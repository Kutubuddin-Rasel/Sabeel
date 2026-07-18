package com.kutubuddin.sabeel.ui.settings

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
    val isOnboardingComplete: Boolean = false
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
}
