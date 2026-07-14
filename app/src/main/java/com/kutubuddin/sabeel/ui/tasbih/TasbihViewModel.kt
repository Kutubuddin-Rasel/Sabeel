package com.kutubuddin.sabeel.ui.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.kutubuddin.sabeel.domain.haptic.HapticStrength
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.DhikrSequence
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    private val repository: TasbihRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TasbihState())
    val state: StateFlow<TasbihState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TasbihSideEffect>(replay = 0)
    val effect: Flow<TasbihSideEffect> = _effect.asSharedFlow()

    private val intentMutex = Mutex()
    
    // LEAK-04: capped at 64; trySend failures are logged rather than silently dropped.
    // Channel.UNLIMITED was removed to prevent unbounded lambda accumulation on rapid taps
    // across step boundaries (completeDhikrTarget + resetCount queued in quick succession).
    private val repoWriteChannel = Channel<suspend () -> Unit>(64)

    init {
        observeRepositoryState()
        viewModelScope.launch {
            for (action in repoWriteChannel) {
                action()
            }
        }
    }

    private fun observeRepositoryState() {
        viewModelScope.launch {
            combine(
                repository.activeCount,
                repository.activeDhikr,
                repository.isSmartFlowEnabled,
                repository.smartFlowVariant,
                repository.isPocketModeActive,
                repository.streak,
                repository.activeStepIndex,
                settingsRepository.hapticsLevel   // THREAD-02: flow through state, not @Volatile field
            ) { values ->
                val count = values[0] as Int
                val dhikr = values[1] as ActiveDhikr
                val smartFlow = values[2] as Boolean
                val variant = values[3] as SmartFlowVariant
                val pocketActive = values[4] as Boolean
                @Suppress("UNCHECKED_CAST")
                val streak = values[5] as? com.kutubuddin.sabeel.domain.model.Streak
                val repoStepIndex = values[6] as Int
                val hapticStrength = HapticStrength.fromSetting(values[7] as String)

                // A sequence is active only when the entry declares one AND the
                // user has Smart Flow enabled; otherwise it counts as a single dhikr.
                val sequence = if (smartFlow && dhikr.sequenceKey != null)
                    DhikrCatalog.sequenceFor(dhikr.sequenceKey) else null

                _state.update { currentState ->
                    val stepIndex = if (sequence != null)
                        repoStepIndex.coerceIn(0, sequence.steps.lastIndex) else 0
                    // Sequence step targets are catalog-fixed and intentionally ignore any active
                    // target override; the override only shapes the single-dhikr `dhikr.target` fallback.
                    val target = sequence?.steps?.get(stepIndex)?.target ?: dhikr.target
                    
                    val displayedDhikr = if (dhikr.key == "ASMA_ALL_99") {
                        val index = count.coerceIn(0, DhikrCatalog.asmaUlHusnaList.lastIndex)
                        val item = DhikrCatalog.asmaUlHusnaList[index]
                        ActiveDhikr(
                            key = dhikr.key,
                            arabicText = item.arabicText,
                            displayName = item.displayName,
                            transliteration = item.transliteration,
                            meaning = item.meaning,
                            target = dhikr.target,
                            spiritualReward = item.spiritualReward,
                            hadithRef = item.hadithRef
                        )
                    } else {
                        dhikr
                    }

                    currentState.copy(
                        count = count,
                        currentDhikr = dhikr,
                        displayedDhikr = displayedDhikr,
                        sequence = sequence,
                        stepIndex = stepIndex,
                        isSmartFlowEnabled = smartFlow,
                        smartFlowVariant = variant,
                        isPocketModeActive = pocketActive,
                        target = target,
                        currentStreak = streak?.count ?: 0,
                        longestStreak = streak?.longestStreak ?: 0,
                        hapticStrength = hapticStrength  // THREAD-02: atomic state, no @Volatile
                    )
                }
            }
            // THREAD-03 note: the combine upstream (DataStore flows) already publish on
            // Dispatchers.IO internally. The mapping lambda here is pure O(1) cast/copy
            // work running on viewModelScope (Main). No flowOn needed — adding it would
            // move the UPSTREAM producers onto IO, breaking StandardTestDispatcher tests
            // without a meaningful production benefit for this O(1) mapping work.
            .collect()
        }
    }

    fun processIntent(intent: TasbihIntent) {
        viewModelScope.launch {
            intentMutex.withLock {
                when (intent) {
                    is TasbihIntent.Increment -> handleIncrement()
                    is TasbihIntent.Decrement -> handleDecrement()
                    is TasbihIntent.Reset -> handleReset()
                    is TasbihIntent.SetDhikr -> handleSetDhikr(intent.key, intent.target, intent.origin)
                    is TasbihIntent.SetSmartFlowEnabled -> handleSetSmartFlowEnabled(intent.enabled)
                    is TasbihIntent.SetSmartFlowVariant -> handleSetSmartFlowVariant(intent.variant)
                    is TasbihIntent.SetPocketModeActive -> handleSetPocketModeActive(intent.active)
                    is TasbihIntent.SyncProgress -> syncCurrentProgressToRoom()
                    is TasbihIntent.ClearError -> _state.update { it.copy(error = null) }
                }
            }
        }
    }

    // ─── Increment ────────────────────────────────────────────────────────────

    private suspend fun handleIncrement() {
        var hapticTypeToPlay: HapticType = HapticType.TICK
        var showCelebration = false
        var completedKey: String? = null
        var completedTarget = 0
        var shouldResetRepoCount = false
        var shouldIncrementRepoCount = false
        var stepIndexToSet: Int? = null

        val currentState = _state.value
        val sequence = currentState.sequence
        val newState = if (currentState.isSmartFlowEnabled && sequence != null) {
            // ── Multi-step Tasbīḥ after Salah ──────────────────────────────
            val result = advance(sequence, currentState.stepIndex, currentState.count)
            when {
                !result.stepCompleted -> {
                    hapticTypeToPlay = HapticType.TICK
                    shouldIncrementRepoCount = true
                    currentState.copy(count = result.stepCount)
                }
                result.sequenceCompleted -> {
                    // The whole sequence is done — record one session for it.
                    hapticTypeToPlay = HapticType.THUD; showCelebration = true
                    completedKey = sequence.key
                    completedTarget = sequence.steps.sumOf { it.target }
                    shouldResetRepoCount = true
                    stepIndexToSet = 0
                    currentState.copy(count = 0, stepIndex = 0, target = sequence.steps.first().target)
                }
                else -> {
                    // An intermediate step finished — advance the tracker, no session yet.
                    hapticTypeToPlay = HapticType.CLICK
                    shouldResetRepoCount = true
                    val nextTarget = sequence.steps[result.stepIndex].target
                    stepIndexToSet = result.stepIndex
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
                currentState.copy(count = 0)
            } else {
                hapticTypeToPlay = HapticType.TICK
                shouldIncrementRepoCount = true
                currentState.copy(count = nextCount)
            }
        }

        _state.value = newState

        _effect.emit(TasbihSideEffect.PlayHaptic(hapticTypeToPlay, _state.value.hapticStrength))
        if (showCelebration) _effect.emit(TasbihSideEffect.ShowCelebration)

        val dateString = LocalDate.now().toString()
        val result = repoWriteChannel.trySend {
            stepIndexToSet?.let { repository.setStepIndex(it) }
            completedKey?.let { repository.completeDhikrTarget(dateString, it, completedTarget) }
            if (shouldResetRepoCount) repository.resetCount()
            else if (shouldIncrementRepoCount) repository.incrementCount(dateString)
        }
        // LEAK-04: log channel saturation in debug builds so it's observable.
        if (!result.isSuccess) {
            Log.w(TAG, "repoWriteChannel full (capacity=64) — write dropped. Tap rate exceeded drain speed.")
        }
    }

    // ─── Decrement ────────────────────────────────────────────────────────────

    private suspend fun handleDecrement() {
        val currentState = _state.value
        val sequence = currentState.sequence

        if (currentState.isSmartFlowEnabled && sequence != null && currentState.count == 0 && currentState.stepIndex > 0) {
            val prevIndex = currentState.stepIndex - 1
            val prevTarget = sequence.steps[prevIndex].target
            val prevCount = prevTarget - 1
            
            _state.update { it.copy(stepIndex = prevIndex, count = prevCount, target = prevTarget) }
            
            _effect.emit(TasbihSideEffect.PlayHaptic(HapticType.TICK, _state.value.hapticStrength))
            repoWriteChannel.trySend {
                repository.setStepIndex(prevIndex)
                repository.setCount(prevCount)
            }
        } else {
            _state.update { state ->
                val newCount = maxOf(0, state.count - 1)
                state.copy(count = newCount)
            }
            repoWriteChannel.trySend {
                repository.decrementCount()
            }
        }
    }

    // ─── Reset ────────────────────────────────────────────────────────────────

    private suspend fun handleReset() {
        val currentState = _state.value
        val sequence = currentState.sequence

        if (currentState.isSmartFlowEnabled && sequence != null) {
            _state.update { it.copy(count = 0, stepIndex = 0, target = sequence.steps.first().target) }
            _effect.emit(TasbihSideEffect.PlayHaptic(HapticType.RESET, _state.value.hapticStrength))
            repoWriteChannel.trySend {
                repository.setStepIndex(0)
                repository.resetCount()
            }
        } else {
            _state.update { it.copy(count = 0) }
            _effect.emit(TasbihSideEffect.PlayHaptic(HapticType.RESET, _state.value.hapticStrength))
            repoWriteChannel.trySend {
                repository.resetCount()
            }
        }
    }

    // ─── Settings ─────────────────────────────────────────────────────────────

    private suspend fun handleSetDhikr(key: String, target: Int? = null, origin: SessionOrigin = SessionOrigin.LIBRARY) {
        // Reset the sequence cursor so a freshly-selected dhikr starts from step 1,
        // and capture the session origin for context-aware completion UI.
        _state.update { it.copy(stepIndex = 0, sessionOrigin = origin) }
        repository.setDhikr(key, target)
    }

    private suspend fun handleSetSmartFlowEnabled(enabled: Boolean) {
        repository.setSmartFlowEnabled(enabled)
    }

    private suspend fun handleSetSmartFlowVariant(variant: SmartFlowVariant) {
        repository.setSmartFlowVariant(variant)
    }

    private suspend fun handleSetPocketModeActive(active: Boolean) {
        repository.setPocketModeActive(active)
        // Instruct MainActivity to start/stop the foreground service
        _effect.emit(
            if (active) TasbihSideEffect.StartPocketModeService
            else TasbihSideEffect.StopPocketModeService
        )
    }

    private suspend fun syncCurrentProgressToRoom() {
        repository.flushToDisk()
    }

    companion object {
        private const val TAG = "TasbihViewModel"
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
