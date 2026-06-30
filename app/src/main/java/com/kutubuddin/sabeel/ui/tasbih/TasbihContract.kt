package com.kutubuddin.sabeel.ui.tasbih

import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant

/**
 * Represents the immutable visual state of the Tasbih screen.
 * All fields are derived from repository observables — the UI is a pure
 * function of this state object (MVI pattern).
 */
data class TasbihState(
    val count: Int = 0,
    val target: Int = 33,
    val currentDhikr: DhikrType = DhikrType.SUBHANALLAH,
    val isSessionComplete: Boolean = false,
    val isSmartFlowEnabled: Boolean = true,
    val smartFlowVariant: SmartFlowVariant = SmartFlowVariant.CLASSIC,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isPocketModeActive: Boolean = false,
    val dailyProgress: Map<DhikrType, Int> = emptyMap(),
    val error: String? = null
)

/**
 * Actions initiated by the user or hardware keys that update state.
 * Each intent represents one discrete user intention — no compound intents.
 */
sealed interface TasbihIntent {
    object Increment : TasbihIntent
    object Decrement : TasbihIntent
    object Reset : TasbihIntent
    data class SetDhikr(val type: DhikrType) : TasbihIntent
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
    data class PlayHaptic(val type: HapticType) : TasbihSideEffect
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
    THUD       // Low-frequency resonance (session completed or manual reset)
}
