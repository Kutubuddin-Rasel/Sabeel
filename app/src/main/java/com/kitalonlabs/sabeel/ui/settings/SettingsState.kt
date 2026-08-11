package com.kitalonlabs.sabeel.ui.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val theme: String = "dark",
    val language: String = "en",
    val hapticsLevel: String = "medium",
    val translitEnabled: Boolean = true,
    val autoProgressWird: Boolean = true,
    val isSmartFlowEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderTime: String = "20:30",
    val isOnboardingComplete: Boolean = false,
    
    val hasSeenTasbihTooltip: Boolean = false,
    val hasSeenHomeTooltip: Boolean = false,
    val hasSeenWirdTooltip: Boolean = false,
    val hasSeenWirdPickerTooltip: Boolean = false,
    val hasSeenLibraryTooltip: Boolean = false
)

sealed class SettingsIntent {
    data class SetTheme(val theme: String) : SettingsIntent()
    data class SetLanguage(val lang: String) : SettingsIntent()
    data class SetHaptics(val level: String) : SettingsIntent()
    data class SetTranslit(val on: Boolean) : SettingsIntent()
    data class SetAutoProgressWird(val on: Boolean) : SettingsIntent()
    data class SetSmartFlowEnabled(val on: Boolean) : SettingsIntent()
    data class SetDailyReminderEnabled(val on: Boolean) : SettingsIntent()
    data class SetDailyReminderTime(val time: String) : SettingsIntent()
    data class SetOnboardingComplete(val complete: Boolean) : SettingsIntent()
    
    data class SetHasSeenTasbihTooltip(val seen: Boolean) : SettingsIntent()
    data class SetHasSeenHomeTooltip(val seen: Boolean) : SettingsIntent()
    data class SetHasSeenWirdTooltip(val seen: Boolean) : SettingsIntent()
    data class SetHasSeenWirdPickerTooltip(val seen: Boolean) : SettingsIntent()
    data class SetHasSeenLibraryTooltip(val seen: Boolean) : SettingsIntent()
}
