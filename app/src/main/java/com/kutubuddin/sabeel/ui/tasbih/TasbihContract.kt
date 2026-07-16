package com.kutubuddin.sabeel.ui.tasbih

import com.kutubuddin.sabeel.domain.haptic.HapticStrength
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.DhikrSequence
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant

import androidx.compose.runtime.Immutable

/**
 * Represents the immutable visual state of the Tasbih screen.
 * All fields are derived from repository observables — the UI is a pure
 * function of this state object (MVI pattern).
 *
 * [currentDhikr] is an [ActiveDhikr] resolved from a String key, so any catalog
 * entry can be counted. When [sequence] is non-null the screen is running a
 * multi-step Tasbīḥ-after-Salah, and [stepIndex] is the current step.
 */
@Immutable
data class TasbihState(
    val count: Int = 0,
    val target: Int = 33,
    val currentDhikr: ActiveDhikr = DhikrCatalog.resolve(DhikrType.SUBHANALLAH.name),
    val displayedDhikr: ActiveDhikr = currentDhikr,
    val sequence: DhikrSequence? = null,
    val stepIndex: Int = 0,
    val isSessionComplete: Boolean = false,
    val isSmartFlowEnabled: Boolean = true,
    val smartFlowVariant: SmartFlowVariant = SmartFlowVariant.CLASSIC,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isPocketModeActive: Boolean = false,
    val error: String? = null,
    val sessionOrigin: SessionOrigin = SessionOrigin.LIBRARY,
    // THREAD-02: hapticStrength is part of atomic state — no @Volatile field needed.
    // Updated via the 7-flow combine in TasbihViewModel.observeRepositoryState().
    val hapticStrength: HapticStrength = HapticStrength.MEDIUM
)

/**
 * Actions initiated by the user or hardware keys that update state.
 * Each intent represents one discrete user intention — no compound intents.
 */
sealed interface TasbihIntent {
    object Increment : TasbihIntent
    object Decrement : TasbihIntent
    object Reset : TasbihIntent
    data class SetDhikr(val key: String, val target: Int? = null, val origin: SessionOrigin = SessionOrigin.LIBRARY) : TasbihIntent
    data class SetSmartFlowEnabled(val enabled: Boolean) : TasbihIntent
    data class SetSmartFlowVariant(val variant: SmartFlowVariant) : TasbihIntent
    data class SetPocketModeActive(val active: Boolean) : TasbihIntent
    object SyncProgress : TasbihIntent
    object ClearError : TasbihIntent
}

/**
 * Transient, one-shot events that do not modify persistent state.
 * These are consumed exactly once by the UI layer and never replayed.
 */
sealed interface TasbihSideEffect {
    data class PlayHaptic(val type: HapticType, val strength: HapticStrength) : TasbihSideEffect
    object ShowCelebration : TasbihSideEffect
    data class ShowToast(val message: String) : TasbihSideEffect
    /** Instructs MainActivity to start the PocketModeService foreground service. */
    object StartPocketModeService : TasbihSideEffect
    /** Instructs MainActivity to stop the PocketModeService foreground service. */
    object StopPocketModeService : TasbihSideEffect
}

/**
 * Standardized haptic pulses used throughout the application.
 * ISP-compliant: kept separate from audio/visual feedback interfaces.
 */
enum class HapticType {
    TICK,      // Short, light tap (regular increment)
    CLICK,     // Distinct, sharp pulse (milestone reached — 33, 66)
    THUD,      // Low-frequency resonance (session completed)
    RESET      // Firm double-thud — manual reset, distinct from completion
}

/**
 * Tracks where the counting session was initiated from to provide context-aware UX.
 */
enum class SessionOrigin {
    DAILY_GOAL,
    LIBRARY
}
