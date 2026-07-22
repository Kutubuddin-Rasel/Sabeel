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
                try {
                    action()
                } catch (e: Exception) {
                    Log.e("TasbihViewModel", "Failed to execute repository action", e)
                }
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
                settingsRepository.hapticsLevel   // THREAD-02: flow through state, not @Volatile field
            ) { values ->
                val count = values[0] as Int
                val dhikr = values[1] as ActiveDhikr
                val smartFlow = values[2] as Boolean
                val variant = values[3] as SmartFlowVariant
                val streak = values[4] as? com.kutubuddin.sabeel.domain.model.Streak
                val hapticStrength = HapticStrength.fromSetting(values[5] as String)

                // A sequence is active only when the entry declares one AND the
                // user has Smart Flow enabled; otherwise it counts as a single dhikr.
                val sequence = if (smartFlow && dhikr.sequenceKey != null)
                    DhikrCatalog.sequenceFor(dhikr.sequenceKey) else null

                _state.update { currentState ->
                    var stepIndex = 0
                    var localCount = count
                    var target = dhikr.target

                    if (sequence != null) {
                        var remaining = count
                        for ((index, step) in sequence.steps.withIndex()) {
                            val isLockedTransition = currentState.isTransitioning && index == currentState.stepIndex
                            
                            if (remaining >= step.target && index < sequence.steps.lastIndex && !isLockedTransition) {
                                remaining -= step.target
                                stepIndex = index + 1
                            } else {
                                localCount = remaining
                                target = step.target
                                stepIndex = index
                                break
                            }
                        }
                    }
                    

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
                        count = localCount,
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
            // Let the repository increment it. The UI state will update automatically!
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.TICK, currentState.hapticStrength))
            val dateString = LocalDate.now().toString()
            repoWriteChannel.trySend { repository.incrementCount(dateString) }
            return
        }

        val nextCount = currentState.count + 1
        val isTargetReached = nextCount == currentState.target
        
        val isSmartFlow = currentState.isSmartFlowEnabled && sequence != null
        val isDailyGoal = currentState.sessionOrigin == SessionOrigin.DAILY_GOAL
        val is99Names = currentState.currentDhikr.key == "ASMA_ALL_99"

        if (isTargetReached) {
            _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.THUD, currentState.hapticStrength))
            _effect.tryEmit(TasbihSideEffect.TriggerGoldenBloom)
            
            val isLastStep = sequence == null || currentState.stepIndex == sequence.steps.lastIndex
            val completedKey = if (sequence != null) sequence.key else currentState.currentDhikr.key
            val dateString = LocalDate.now().toString()
            
            repoWriteChannel.trySend {
                repository.incrementCount(dateString)
                if (isLastStep) {
                    repository.completeDhikrTarget(dateString, completedKey, currentState.currentDhikr.target)
                }
            }
            
            val isSmartFlowComplete = isSmartFlow && isLastStep
            val isFixedEndDhikrComplete = isSmartFlowComplete || is99Names

            if (isFixedEndDhikrComplete && !isDailyGoal) {
                // Enter 1200ms transition phase for completion pop-back
                _state.update { it.copy(isTransitioning = true) }
                viewModelScope.launch {
                    kotlinx.coroutines.delay(1200)
                    if (isSmartFlowComplete) {
                        val stateAfterDelay = _state.value
                        val overTaps = stateAfterDelay.count - stateAfterDelay.target
                        repoWriteChannel.send { repository.setCount(overTaps) }
                    }
                    if (currentState.sessionOrigin == SessionOrigin.LIBRARY) {
                        _effect.emit(TasbihSideEffect.NavigateToLibrary)
                    } else {
                        _effect.emit(TasbihSideEffect.NavigateToHome)
                    }
                    _state.update { it.copy(isTransitioning = false) }
                }
            } else if (isSmartFlow || isDailyGoal) {
                // Enter standard 600ms transition phase (auto-progress or next step)
                _state.update { it.copy(isTransitioning = true) }
                
                viewModelScope.launch {
                    kotlinx.coroutines.delay(600)
                    
                    val stateAfterDelay = _state.value
                    
                    if (isSmartFlow && !isLastStep) {
                        // Enforce a strict boundary reset. We calculate the exact mathematical
                        // target up to the completed step and force the global count to that number,
                        // discarding any overflow taps made during the 600ms visual delay.
                        val sequence = stateAfterDelay.sequence
                        if (sequence != null) {
                            var cumulativeTarget = 0
                            for (i in 0..stateAfterDelay.stepIndex) {
                                cumulativeTarget += sequence.steps[i].target
                            }
                            repoWriteChannel.send { repository.setCount(cumulativeTarget) }
                        }
                        
                        // Advancing to next step! We just unlock the transition.
                        _state.update { it.copy(isTransitioning = false) }
                    } else if (isDailyGoal) {
                        // Tell UI to request next daily goal item
                        _effect.emit(TasbihSideEffect.AutoProgressDailyGoal)
                        val overTaps = stateAfterDelay.count - stateAfterDelay.target
                        repoWriteChannel.send { repository.setCount(overTaps) }
                        _state.update { it.copy(isTransitioning = false) }
                    }
                }
            } else {
                // Infinite flow (Library)
                // Just continue counting, no transition delay needed!
            }
        } else {
            // Normal increment
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

        if (currentState.count == 0 && currentState.stepIndex == 0) return

        _effect.tryEmit(TasbihSideEffect.PlayHaptic(HapticType.TICK, currentState.hapticStrength))
        repoWriteChannel.trySend {
            repository.decrementCount()
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
        // Capture the session origin for context-aware completion UI.
        // We do not reset the step index here because it is dynamically calculated from the global count.
        _state.update { it.copy(sessionOrigin = origin) }
        
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
    }
}
