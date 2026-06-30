package com.kutubuddin.sabeel.ui.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.model.DhikrType
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
                val dhikr = values[1] as DhikrType
                val smartFlow = values[2] as Boolean
                val variant = values[3] as SmartFlowVariant
                val pocketActive = values[4] as Boolean
                @Suppress("UNCHECKED_CAST")
                val streak = values[5] as? com.kutubuddin.sabeel.domain.model.Streak

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
                    currentState.copy(
                        count = resolvedCount,
                        currentDhikr = dhikr,
                        isSmartFlowEnabled = smartFlow,
                        smartFlowVariant = variant,
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
        when (intent) {
            is TasbihIntent.Increment -> handleIncrement()
            is TasbihIntent.Decrement -> handleDecrement()
            is TasbihIntent.Reset -> handleReset()
            is TasbihIntent.SetDhikr -> handleSetDhikr(intent.type)
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
            val variant = currentState.smartFlowVariant

            if (smartFlow && isPostSalahDhikr(currentDhikr)) {
                handleSmartFlowIncrement(
                    currentState = currentState,
                    nextCount = nextCount,
                    currentDhikr = currentDhikr,
                    variant = variant,
                    onHaptic = { hapticTypeToPlay = it },
                    onCelebration = { showCelebration = true },
                    onCompleted = { dhikr, cnt -> completedDhikr = dhikr; completedTarget = cnt },
                    onNextDhikr = { nextDhikrToSet = it },
                    onResetRepo = { shouldResetRepoCount = true },
                    onIncrementRepo = { shouldIncrementRepoCount = true }
                )
            } else {
                // Standard (non-Smart-Flow) counting
                if (nextCount >= target) {
                    hapticTypeToPlay = HapticType.THUD
                    showCelebration = true
                    completedDhikr = currentDhikr
                    completedTarget = target
                    shouldResetRepoCount = true
                    expectingReset = true
                    lastSentCount = -1
                    currentState.copy(count = 0)
                } else {
                    hapticTypeToPlay = HapticType.TICK
                    shouldIncrementRepoCount = true
                    lastSentCount = nextCount
                    expectingReset = false
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
                completedDhikr?.let { dhikr ->
                    repository.completeDhikrTarget(dateString, dhikr, completedTarget)
                }
                nextDhikrToSet?.let { repository.setDhikr(it) }
                    ?: run {
                        if (shouldResetRepoCount) repository.resetCount()
                        else if (shouldIncrementRepoCount) repository.incrementCount(dateString)
                    }
            }
        }
    }

    /**
     * Handles Smart Flow transitions for the Post-Salah dhikr sequence.
     *
     * OCP: adding Variation 3 requires only a new SmartFlowVariant entry and
     * a new branch here — the core counting engine in handleIncrement() is unchanged.
     *
     * Variation 1 (CLASSIC):    SubhanAllah→33 → Alhamdulillah→33 → AllahuAkbar→34
     * Variation 2 (WITH_TAHLIL): SubhanAllah→33 → Alhamdulillah→33 → AllahuAkbar→33 → Tahlil→1
     */
    private fun handleSmartFlowIncrement(
        currentState: TasbihState,
        nextCount: Int,
        currentDhikr: DhikrType,
        variant: SmartFlowVariant,
        onHaptic: (HapticType) -> Unit,
        onCelebration: () -> Unit,
        onCompleted: (DhikrType, Int) -> Unit,
        onNextDhikr: (DhikrType) -> Unit,
        onResetRepo: () -> Unit,
        onIncrementRepo: () -> Unit
    ): TasbihState {
        val allahuAkbarTarget = if (variant == SmartFlowVariant.CLASSIC) 34 else 33

        return when (currentDhikr) {
            DhikrType.SUBHANALLAH -> {
                if (nextCount >= 33) {
                    onHaptic(HapticType.CLICK)
                    onCompleted(DhikrType.SUBHANALLAH, 33)
                    onNextDhikr(DhikrType.ALHAMDULILLAH)
                    onResetRepo()
                    expectingReset = true; lastSentCount = -1
                    currentState.copy(count = 0, currentDhikr = DhikrType.ALHAMDULILLAH,
                        target = DhikrType.ALHAMDULILLAH.defaultTarget)
                } else {
                    onHaptic(HapticType.TICK); onIncrementRepo()
                    lastSentCount = nextCount; expectingReset = false
                    currentState.copy(count = nextCount)
                }
            }
            DhikrType.ALHAMDULILLAH -> {
                if (nextCount >= 33) {
                    onHaptic(HapticType.CLICK)
                    onCompleted(DhikrType.ALHAMDULILLAH, 33)
                    onNextDhikr(DhikrType.ALLAHU_AKBAR)
                    onResetRepo()
                    expectingReset = true; lastSentCount = -1
                    currentState.copy(count = 0, currentDhikr = DhikrType.ALLAHU_AKBAR,
                        target = allahuAkbarTarget)
                } else {
                    onHaptic(HapticType.TICK); onIncrementRepo()
                    lastSentCount = nextCount; expectingReset = false
                    currentState.copy(count = nextCount)
                }
            }
            DhikrType.ALLAHU_AKBAR -> {
                if (nextCount >= allahuAkbarTarget) {
                    when (variant) {
                        SmartFlowVariant.CLASSIC -> {
                            // Variation 1 complete — full 33+33+34 = 100
                            onHaptic(HapticType.THUD); onCelebration()
                            onCompleted(DhikrType.ALLAHU_AKBAR, 34)
                            onNextDhikr(DhikrType.SUBHANALLAH)
                            onResetRepo()
                            expectingReset = true; lastSentCount = -1
                            currentState.copy(count = 0, currentDhikr = DhikrType.SUBHANALLAH,
                                target = DhikrType.SUBHANALLAH.defaultTarget)
                        }
                        SmartFlowVariant.WITH_TAHLIL -> {
                            // Variation 2 — transition to Tahlil × 1
                            onHaptic(HapticType.CLICK)
                            onCompleted(DhikrType.ALLAHU_AKBAR, 33)
                            onNextDhikr(DhikrType.TAHLIL)
                            onResetRepo()
                            expectingReset = true; lastSentCount = -1
                            currentState.copy(count = 0, currentDhikr = DhikrType.TAHLIL,
                                target = 1)
                        }
                    }
                } else {
                    onHaptic(HapticType.TICK); onIncrementRepo()
                    lastSentCount = nextCount; expectingReset = false
                    currentState.copy(count = nextCount)
                }
            }
            DhikrType.TAHLIL -> {
                // Only reachable in WITH_TAHLIL variant as the final 1-count step
                if (nextCount >= 1) {
                    onHaptic(HapticType.THUD); onCelebration()
                    onCompleted(DhikrType.TAHLIL, 1)
                    onNextDhikr(DhikrType.SUBHANALLAH)
                    onResetRepo()
                    expectingReset = true; lastSentCount = -1
                    currentState.copy(count = 0, currentDhikr = DhikrType.SUBHANALLAH,
                        target = DhikrType.SUBHANALLAH.defaultTarget)
                } else {
                    onHaptic(HapticType.TICK); onIncrementRepo()
                    lastSentCount = nextCount; expectingReset = false
                    currentState.copy(count = nextCount)
                }
            }
            else -> currentState
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

    private fun handleSetDhikr(type: DhikrType) {
        expectingReset = true
        lastSentCount = -1
        viewModelScope.launch {
            repositoryMutex.withLock { repository.setDhikr(type) }
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
                    dhikrType = currentState.currentDhikr,
                    targetCount = currentState.count
                )
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun isPostSalahDhikr(dhikr: DhikrType): Boolean =
        dhikr == DhikrType.SUBHANALLAH ||
        dhikr == DhikrType.ALHAMDULILLAH ||
        dhikr == DhikrType.ALLAHU_AKBAR ||
        dhikr == DhikrType.TAHLIL  // Reachable in WITH_TAHLIL final step
}
