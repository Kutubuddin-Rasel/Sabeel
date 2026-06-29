package com.kutubuddin.sabeel.ui.tasbih

import com.kutubuddin.sabeel.domain.model.DhikrType

/**
 * Represents the immutable visual state of the Tasbih screen.
 */
data class TasbihState(
    val count: Int = 0,
    val target: Int = 33,
    val currentDhikr: DhikrType = DhikrType.SUBHANALLAH,
    val isSessionComplete: Boolean = false,
    val isSmartFlowEnabled: Boolean = true,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isPocketModeActive: Boolean = false,
    val dailyProgress: Map<DhikrType, Int> = emptyMap(),
    val error: String? = null
)

/**
 * Actions initiated by the user or hardware keys that update state.
 */
sealed interface TasbihIntent {
    object Increment : TasbihIntent
    object Reset : TasbihIntent
    data class SetDhikr(val type: DhikrType) : TasbihIntent
    data class SetSmartFlowEnabled(val enabled: Boolean) : TasbihIntent
    data class SetPocketModeActive(val active: Boolean) : TasbihIntent
    object SyncProgress : TasbihIntent
    object ClearError : TasbihIntent
}

/**
 * Transient, one-shot events that do not modify persistent state.
 */
sealed interface TasbihSideEffect {
    data class PlayHaptic(val type: HapticType) : TasbihSideEffect
    object ShowCelebration : TasbihSideEffect
    data class ShowToast(val message: String) : TasbihSideEffect
}

/**
 * Standardized haptic pulses used in the application.
 */
enum class HapticType {
    TICK,      // Short, light tap (regular increment)
    CLICK,     // Distinct, sharp pulse (milestone reached - 33, 66)
    THUD       // Low-frequency resonance (session completed or manual reset)
}
