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

    private val _effect = MutableSharedFlow<TasbihSideEffect>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
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
                repository.streak,
                repository.activeStepIndex,
                settingsRepository.hapticsLevel   // THREAD-02: flow through state, not @Volatile field
            ) { values ->
                val count = values[0] as Int
                val dhikr = values[1] as ActiveDhikr
                val smartFlow = values[2] as Boolean
                val variant = values[3] as SmartFlowVariant
                val streak = values[4] as? com.kutubuddin.sabeel.domain.model.Streak
                val repoStepIndex = values[5] as Int
                val hapticStrength = HapticStrength.fromSetting(values[6] as String)

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
                    is TasbihIntent.SetDhikr -> handleSetDhikr(intent.key, intent.target, intent.origin, intent.preserveCount)
                    is TasbihIntent.SetSmartFlowEnabled -> handleSetSmartFlowEnabled(intent.enabled)
                    is TasbihIntent.SetSmartFlowVariant -> handleSetSmartFlowVariant(intent.variant)
                    is TasbihIntent.SyncProgress -> syncCurrentProgressToRoom()
                    is TasbihIntent.ClearError -> _state.update { it.copy(error = null) }
                }
            }
        }
    }

    // ─── Increment ────────────────────────────────────────────────────────────

    private suspend fun handleIncrement() {
        val currentState = _state.value
        val sequence = currentState.sequence

        if (currentState.isTransitioning) {
            // Tap buffering during the 600ms breathing room
            val nextCount = currentState.count + 1
            _state.update { it.copy(count = nextCount) }
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.TICK, currentState.hapticStrength))
            val dateString = LocalDate.now().toString()
            repoWriteChannel.trySend { repository.incrementCount(dateString) }
            return
        }

        val nextCount = currentState.count + 1
        val isTargetReached = nextCount == currentState.target
        
        val isSmartFlow = currentState.isSmartFlowEnabled && sequence != null
        val isDailyGoal = currentState.sessionOrigin == SessionOrigin.DAILY_GOAL

        if (isTargetReached) {
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.THUD, currentState.hapticStrength))
            _effect.tryEmit(TasbihSideEffect.TriggerGoldenBloom)
            
            val completedKey = if (sequence != null) sequence.key else currentState.currentDhikr.key
            val completedTarget = currentState.target
            val dateString = LocalDate.now().toString()
            
            repoWriteChannel.trySend {
                repository.incrementCount(dateString)
                repository.completeDhikrTarget(dateString, completedKey, completedTarget)
            }
            
            _state.update { it.copy(count = nextCount) }
            
            if (isSmartFlow || isDailyGoal) {
                // Enter transition phase (600ms Breathing Room)
                _state.update { it.copy(isTransitioning = true) }
                
                viewModelScope.launch {
                    kotlinx.coroutines.delay(600)
                    
                    val stateAfterDelay = _state.value
                    // Calculate any taps that happened during the 600ms window
                    val remainder = stateAfterDelay.count - completedTarget
                    
                    if (isSmartFlow && sequence != null) {
                        val result = advance(sequence, stateAfterDelay.stepIndex, completedTarget - 1)
                        if (result.sequenceCompleted) {
                            _effect.emit(TasbihSideEffect.ShowSessionSummary)
                            _state.update { it.copy(isTransitioning = false, count = remainder, stepIndex = 0, target = sequence.steps.first().target) }
                            repoWriteChannel.send { 
                                repository.setStepIndex(0)
                                repository.setCount(remainder)
                            }
                        } else {
                            val nextTarget = sequence.steps[result.stepIndex].target
                            _state.update { it.copy(isTransitioning = false, count = remainder, stepIndex = result.stepIndex, target = nextTarget) }
                            repoWriteChannel.send { 
                                repository.setStepIndex(result.stepIndex)
                                repository.setCount(remainder)
                            }
                        }
                    } else if (isDailyGoal) {
                        // Tell UI to request next daily goal item
                        _effect.emit(TasbihSideEffect.AutoProgressDailyGoal)
                        _state.update { it.copy(isTransitioning = false, count = remainder) }
                        repoWriteChannel.send { repository.setCount(remainder) }
                    }
                }
            } else {
                // Infinite flow (Library)
                // Just continue counting, no transition delay needed!
            }
        } else {
            // Normal increment
            _state.update { it.copy(count = nextCount) }
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.TICK, currentState.hapticStrength))
            val dateString = LocalDate.now().toString()
            val result = repoWriteChannel.trySend { repository.incrementCount(dateString) }
            if (!result.isSuccess) {
                Log.w(TAG, "repoWriteChannel full (capacity=64) — write dropped. Tap rate exceeded drain speed.")
            }
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
            
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.TICK, _state.value.hapticStrength))
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
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.RESET, _state.value.hapticStrength))
            repoWriteChannel.trySend {
                repository.setStepIndex(0)
                repository.resetCount()
            }
        } else {
            _state.update { it.copy(count = 0) }
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.RESET, _state.value.hapticStrength))
            repoWriteChannel.trySend {
                repository.resetCount()
            }
        }
    }

    // ─── Settings ─────────────────────────────────────────────────────────────

    private suspend fun handleSetDhikr(key: String, target: Int? = null, origin: SessionOrigin = SessionOrigin.LIBRARY, preserveCount: Boolean = false) {
        // Reset the sequence cursor so a freshly-selected dhikr starts from step 1,
        // and capture the session origin for context-aware completion UI.
        _state.update { it.copy(stepIndex = 0, sessionOrigin = origin) }
        
        val currentCount = _state.value.count
        repoWriteChannel.send {
            repository.setDhikr(key, target)
            if (preserveCount) {
                repository.setCount(currentCount)
            }
        }
    }

    private suspend fun handleSetSmartFlowEnabled(enabled: Boolean) {
        repository.setSmartFlowEnabled(enabled)
    }

    private suspend fun handleSetSmartFlowVariant(variant: SmartFlowVariant) {
        repository.setSmartFlowVariant(variant)
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
