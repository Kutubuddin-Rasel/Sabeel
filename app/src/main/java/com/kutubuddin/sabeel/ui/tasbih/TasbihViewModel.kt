package com.kutubuddin.sabeel.ui.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import com.kutubuddin.sabeel.domain.haptic.TasbihHapticController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val repository: TasbihRepository,
    private val hapticController: TasbihHapticController
) : ViewModel() {

    private val _state = MutableStateFlow(TasbihState())
    val state: StateFlow<TasbihState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TasbihSideEffect>(replay = 0)
    val effect: Flow<TasbihSideEffect> = _effect.asSharedFlow()

    init {
        observeRepositoryState()
    }

    private fun observeRepositoryState() {
        viewModelScope.launch {
            combine(
                repository.activeCount,
                repository.activeDhikr,
                repository.isSmartFlowEnabled,
                repository.isPocketModeActive,
                repository.streak
            ) { count, dhikr, smartFlow, pocketActive, streak ->
                _state.update { currentState ->
                    currentState.copy(
                        count = count,
                        currentDhikr = dhikr,
                        isSmartFlowEnabled = smartFlow,
                        isPocketModeActive = pocketActive,
                        target = dhikr.defaultTarget,
                        currentStreak = streak?.count ?: 0,
                        longestStreak = streak?.longestStreak ?: 0
                    )
                }
            }.collect()
        }
    }

    fun processIntent(intent: TasbihIntent) {
        viewModelScope.launch {
            when (intent) {
                is TasbihIntent.Increment -> handleIncrement()
                is TasbihIntent.Reset -> handleReset()
                is TasbihIntent.SetDhikr -> handleSetDhikr(intent.type)
                is TasbihIntent.SetSmartFlowEnabled -> handleSetSmartFlowEnabled(intent.enabled)
                is TasbihIntent.SetPocketModeActive -> handleSetPocketModeActive(intent.active)
                is TasbihIntent.SyncProgress -> syncCurrentProgressToRoom()
                is TasbihIntent.ClearError -> _state.update { it.copy(error = null) }
            }
        }
    }

    private fun handleIncrement() {
        var hapticTypeToPlay: HapticType = HapticType.TICK
        var showCelebration = false
        var completedDhikr: DhikrType? = null
        var completedTarget: Int = 0
        var nextDhikrToSet: DhikrType? = null
        var shouldResetRepoCount = false
        var shouldIncrementRepoCount = false

        _state.update { currentState ->
            val nextCount = currentState.count + 1
            val target = currentState.target
            val currentDhikr = currentState.currentDhikr
            val smartFlow = currentState.isSmartFlowEnabled

            if (smartFlow && isPostSalahDhikr(currentDhikr)) {
                when (currentDhikr) {
                    DhikrType.SUBHANALLAH -> {
                        if (nextCount >= 33) {
                            hapticTypeToPlay = HapticType.CLICK
                            completedDhikr = DhikrType.SUBHANALLAH
                            completedTarget = 33
                            nextDhikrToSet = DhikrType.ALHAMDULILLAH
                            shouldResetRepoCount = true
                            currentState.copy(
                                count = 0,
                                currentDhikr = DhikrType.ALHAMDULILLAH,
                                target = DhikrType.ALHAMDULILLAH.defaultTarget
                            )
                        } else {
                            hapticTypeToPlay = HapticType.TICK
                            shouldIncrementRepoCount = true
                            currentState.copy(count = nextCount)
                        }
                    }
                    DhikrType.ALHAMDULILLAH -> {
                        if (nextCount >= 33) {
                            hapticTypeToPlay = HapticType.CLICK
                            completedDhikr = DhikrType.ALHAMDULILLAH
                            completedTarget = 33
                            nextDhikrToSet = DhikrType.ALLAHU_AKBAR
                            shouldResetRepoCount = true
                            currentState.copy(
                                count = 0,
                                currentDhikr = DhikrType.ALLAHU_AKBAR,
                                target = DhikrType.ALLAHU_AKBAR.defaultTarget
                            )
                        } else {
                            hapticTypeToPlay = HapticType.TICK
                            shouldIncrementRepoCount = true
                            currentState.copy(count = nextCount)
                        }
                    }
                    DhikrType.ALLAHU_AKBAR -> {
                        if (nextCount >= 34) {
                            hapticTypeToPlay = HapticType.THUD
                            showCelebration = true
                            completedDhikr = DhikrType.ALLAHU_AKBAR
                            completedTarget = 34
                            nextDhikrToSet = DhikrType.SUBHANALLAH
                            shouldResetRepoCount = true
                            currentState.copy(
                                count = 0,
                                currentDhikr = DhikrType.SUBHANALLAH,
                                target = DhikrType.SUBHANALLAH.defaultTarget
                            )
                        } else {
                            hapticTypeToPlay = HapticType.TICK
                            shouldIncrementRepoCount = true
                            currentState.copy(count = nextCount)
                        }
                    }
                    else -> currentState
                }
            } else {
                if (nextCount >= target) {
                    hapticTypeToPlay = HapticType.THUD
                    showCelebration = true
                    completedDhikr = currentDhikr
                    completedTarget = target
                    shouldResetRepoCount = true
                    currentState.copy(count = 0)
                } else {
                    hapticTypeToPlay = HapticType.TICK
                    shouldIncrementRepoCount = true
                    currentState.copy(count = nextCount)
                }
            }
        }

        // Apply haptic side-effects immediately
        when (hapticTypeToPlay) {
            HapticType.TICK -> hapticController.playIncrementTick()
            HapticType.CLICK -> hapticController.playMilestoneClick()
            HapticType.THUD -> hapticController.playCompletionThud()
        }
        viewModelScope.launch {
            _effect.emit(TasbihSideEffect.PlayHaptic(hapticTypeToPlay))
            if (showCelebration) {
                _effect.emit(TasbihSideEffect.ShowCelebration)
            }
        }

        // Persist to repository in background
        val dateString = LocalDate.now().toString()
        
        // Launch completion persistence separately so it doesn't block dhikr transition
        completedDhikr?.let { dhikr ->
            viewModelScope.launch {
                repository.completeDhikrTarget(dateString, dhikr, completedTarget)
            }
        }
        
        viewModelScope.launch {
            nextDhikrToSet?.let {
                repository.setDhikr(it)
            } ?: run {
                if (shouldResetRepoCount) {
                    repository.resetCount()
                } else if (shouldIncrementRepoCount) {
                    repository.incrementCount(dateString)
                }
            }
        }
    }

    private suspend fun handleReset() {
        hapticController.playCompletionThud()
        _effect.emit(TasbihSideEffect.PlayHaptic(HapticType.THUD))
        repository.resetCount()
    }

    private suspend fun handleSetDhikr(type: DhikrType) {
        repository.setDhikr(type)
    }

    private suspend fun handleSetSmartFlowEnabled(enabled: Boolean) {
        repository.setSmartFlowEnabled(enabled)
    }

    private suspend fun handleSetPocketModeActive(active: Boolean) {
        repository.setPocketModeActive(active)
    }

    private suspend fun syncCurrentProgressToRoom() {
        val currentState = _state.value
        val dateString = LocalDate.now().toString()
        repository.completeDhikrTarget(
            date = dateString,
            dhikrType = currentState.currentDhikr,
            targetCount = currentState.count
        )
    }

    private fun isPostSalahDhikr(dhikr: DhikrType): Boolean {
        return dhikr == DhikrType.SUBHANALLAH ||
               dhikr == DhikrType.ALHAMDULILLAH ||
               dhikr == DhikrType.ALLAHU_AKBAR
    }
}
