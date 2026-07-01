package com.kutubuddin.sabeel.ui.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.DhikrSequence
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Tasbih counting screen.
 *
 * Owns the full counting lifecycle:
 * - Optimistic state updates (UI is never blocked waiting for I/O)
 * - Async persistence via Mutex-guarded repository writes
 * - Smart Flow transitions for both Post-Salah variations
 * - Side-effect emission for haptics and service lifecycle
 *
 * DIP: depends only on the TasbihRepository interface, never the concrete impl.
 * SRP: owns state management and intent routing only — no vibration, no DB logic.
 */
@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val repository: TasbihRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TasbihState())
    val state: StateFlow<TasbihState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TasbihSideEffect>(replay = 0)
    val effect: Flow<TasbihSideEffect> = _effect.asSharedFlow()

    private val repositoryMutex = Mutex()
    private var lastSentCount: Int = -1
    private var expectingReset: Boolean = false

    init {
        observeRepositoryState()
    }

    private fun observeRepositoryState() {
        viewModelScope.launch {
            combine(
                repository.activeCount,
                repository.activeDhikr,
                repository.isSmartFlowEnabled,
                repository.smartFlowVariant,
                repository.isPocketModeActive,
                repository.streak
            ) { values ->
                val count = values[0] as Int
                val dhikr = values[1] as ActiveDhikr
                val smartFlow = values[2] as Boolean
                val variant = values[3] as SmartFlowVariant
                val pocketActive = values[4] as Boolean
                @Suppress("UNCHECKED_CAST")
                val streak = values[5] as? com.kutubuddin.sabeel.domain.model.Streak

                // A sequence is active only when the entry declares one AND the
                // user has Smart Flow enabled; otherwise it counts as a single dhikr.
                val sequence = if (smartFlow && dhikr.sequenceKey != null)
                    DhikrCatalog.sequenceFor(dhikr.sequenceKey) else null

                _state.update { currentState ->
                    val resolvedCount = when {
                        expectingReset -> {
                            if (count == 0) { expectingReset = false; 0 }
                            else currentState.count
                        }
                        lastSentCount != -1 -> {
                            if (count < lastSentCount) currentState.count
                            else { if (count >= lastSentCount) lastSentCount = -1; count }
                        }
                        else -> count
                    }
                    val stepIndex = if (sequence != null)
                        currentState.stepIndex.coerceIn(0, sequence.steps.lastIndex) else 0
                    val target = sequence?.steps?.get(stepIndex)?.target ?: dhikr.target
                    currentState.copy(
                        count = resolvedCount,
                        currentDhikr = dhikr,
                        sequence = sequence,
                        stepIndex = stepIndex,
                        isSmartFlowEnabled = smartFlow,
                        smartFlowVariant = variant,
                        isPocketModeActive = pocketActive,
                        target = target,
                        currentStreak = streak?.count ?: 0,
                        longestStreak = streak?.longestStreak ?: 0
                    )
                }
            }.collect()
        }
    }

    fun processIntent(intent: TasbihIntent) {
        when (intent) {
            is TasbihIntent.Increment -> handleIncrement()
            is TasbihIntent.Decrement -> handleDecrement()
            is TasbihIntent.Reset -> handleReset()
            is TasbihIntent.SetDhikr -> handleSetDhikr(intent.key)
            is TasbihIntent.SetSmartFlowEnabled -> handleSetSmartFlowEnabled(intent.enabled)
            is TasbihIntent.SetSmartFlowVariant -> handleSetSmartFlowVariant(intent.variant)
            is TasbihIntent.SetPocketModeActive -> handleSetPocketModeActive(intent.active)
            is TasbihIntent.SyncProgress -> syncCurrentProgressToRoom()
            is TasbihIntent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    // ─── Increment ────────────────────────────────────────────────────────────

    private fun handleIncrement() {
        var hapticTypeToPlay: HapticType = HapticType.TICK
        var showCelebration = false
        var completedKey: String? = null
        var completedTarget = 0
        var shouldResetRepoCount = false
        var shouldIncrementRepoCount = false

        _state.update { currentState ->
            val sequence = currentState.sequence
            if (currentState.isSmartFlowEnabled && sequence != null) {
                // ── Multi-step Tasbīḥ after Salah ──────────────────────────────
                val result = advance(sequence, currentState.stepIndex, currentState.count)
                when {
                    !result.stepCompleted -> {
                        hapticTypeToPlay = HapticType.TICK
                        shouldIncrementRepoCount = true
                        lastSentCount = result.stepCount; expectingReset = false
                        currentState.copy(count = result.stepCount)
                    }
                    result.sequenceCompleted -> {
                        // The whole sequence is done — record one session for it.
                        hapticTypeToPlay = HapticType.THUD; showCelebration = true
                        completedKey = sequence.key
                        completedTarget = sequence.steps.sumOf { it.target }
                        shouldResetRepoCount = true
                        expectingReset = true; lastSentCount = -1
                        currentState.copy(count = 0, stepIndex = 0, target = sequence.steps.first().target)
                    }
                    else -> {
                        // An intermediate step finished — advance the tracker, no session yet.
                        hapticTypeToPlay = HapticType.CLICK
                        shouldResetRepoCount = true
                        expectingReset = true; lastSentCount = -1
                        val nextTarget = sequence.steps[result.stepIndex].target
                        currentState.copy(count = 0, stepIndex = result.stepIndex, target = nextTarget)
                    }
                }
            } else {
                // ── Standard single-dhikr counting ─────────────────────────────
                val nextCount = currentState.count + 1
                if (nextCount >= currentState.target) {
                    hapticTypeToPlay = HapticType.THUD; showCelebration = true
                    completedKey = currentState.currentDhikr.key
                    completedTarget = currentState.target
                    shouldResetRepoCount = true
                    expectingReset = true; lastSentCount = -1
                    currentState.copy(count = 0)
                } else {
                    hapticTypeToPlay = HapticType.TICK
                    shouldIncrementRepoCount = true
                    lastSentCount = nextCount; expectingReset = false
                    currentState.copy(count = nextCount)
                }
            }
        }

        viewModelScope.launch {
            _effect.emit(TasbihSideEffect.PlayHaptic(hapticTypeToPlay))
            if (showCelebration) _effect.emit(TasbihSideEffect.ShowCelebration)
        }

        val dateString = LocalDate.now().toString()
        viewModelScope.launch {
            repositoryMutex.withLock {
                completedKey?.let { repository.completeDhikrTarget(dateString, it, completedTarget) }
                if (shouldResetRepoCount) repository.resetCount()
                else if (shouldIncrementRepoCount) repository.incrementCount(dateString)
            }
        }
    }

    // ─── Decrement ────────────────────────────────────────────────────────────

    private fun handleDecrement() {
        _state.update { currentState ->
            val newCount = maxOf(0, currentState.count - 1)
            lastSentCount = newCount
            currentState.copy(count = newCount)
        }
        viewModelScope.launch {
            repositoryMutex.withLock { repository.decrementCount() }
        }
    }

    // ─── Reset ────────────────────────────────────────────────────────────────

    private fun handleReset() {
        expectingReset = true
        lastSentCount = -1
        _state.update { it.copy(count = 0) }
        viewModelScope.launch {
            _effect.emit(TasbihSideEffect.PlayHaptic(HapticType.THUD))
        }
        viewModelScope.launch {
            repositoryMutex.withLock { repository.resetCount() }
        }
    }

    // ─── Settings ─────────────────────────────────────────────────────────────

    private fun handleSetDhikr(key: String) {
        expectingReset = true
        lastSentCount = -1
        // Reset the sequence cursor so a freshly-selected dhikr starts from step 1.
        _state.update { it.copy(stepIndex = 0) }
        viewModelScope.launch {
            repositoryMutex.withLock { repository.setDhikr(key) }
        }
    }

    private fun handleSetSmartFlowEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repositoryMutex.withLock { repository.setSmartFlowEnabled(enabled) }
        }
    }

    private fun handleSetSmartFlowVariant(variant: SmartFlowVariant) {
        viewModelScope.launch {
            repositoryMutex.withLock { repository.setSmartFlowVariant(variant) }
        }
    }

    private fun handleSetPocketModeActive(active: Boolean) {
        viewModelScope.launch {
            repositoryMutex.withLock { repository.setPocketModeActive(active) }
            // Instruct MainActivity to start/stop the foreground service
            _effect.emit(
                if (active) TasbihSideEffect.StartPocketModeService
                else TasbihSideEffect.StopPocketModeService
            )
        }
    }

    private fun syncCurrentProgressToRoom() {
        val currentState = _state.value
        val dateString = LocalDate.now().toString()
        viewModelScope.launch {
            repositoryMutex.withLock {
                repository.completeDhikrTarget(
                    date = dateString,
                    dhikrKey = currentState.currentDhikr.key,
                    targetCount = currentState.count
                )
            }
        }
    }

    companion object {
        /**
         * Pure state transition for one tap inside a [DhikrSequence].
         *
         * Given the current step and its within-step count, returns the next
         * cursor position and whether a step / the whole sequence just completed.
         * No Android or coroutine dependency, so it is unit-tested directly.
         */
        internal fun advance(seq: DhikrSequence, stepIndex: Int, stepCount: Int): SeqResult {
            val target = seq.steps[stepIndex].target
            val next = stepCount + 1
            if (next < target) {
                return SeqResult(stepIndex, next, stepCompleted = false, sequenceCompleted = false)
            }
            val isLast = stepIndex == seq.steps.lastIndex
            return SeqResult(
                stepIndex = if (isLast) stepIndex else stepIndex + 1,
                stepCount = if (isLast) target else 0,
                stepCompleted = true,
                sequenceCompleted = isLast
            )
        }
    }
}

/** Outcome of one [TasbihViewModel.advance] tick. */
data class SeqResult(
    val stepIndex: Int,
    val stepCount: Int,
    val stepCompleted: Boolean,
    val sequenceCompleted: Boolean
)
