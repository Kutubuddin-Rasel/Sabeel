package com.kutubuddin.sabeel.ui.settings

data class SettingsState(
    val theme: String = "dark",
    val language: String = "en",
    val hapticsLevel: String = "medium",
    val leftHanded: Boolean = false,
    val translitEnabled: Boolean = true,
    val autoReset: Boolean = false,
    val soundEnabled: Boolean = true,
    val showStreaks: Boolean = true,
    val autoProgressWird: Boolean = true,
    val isSmartFlowEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderTime: String = "20:30"
)

sealed class SettingsIntent {
    data class SetTheme(val theme: String) : SettingsIntent()
    data class SetLanguage(val lang: String) : SettingsIntent()
    data class SetHaptics(val level: String) : SettingsIntent()
    data class SetLeftHanded(val on: Boolean) : SettingsIntent()
    data class SetTranslit(val on: Boolean) : SettingsIntent()
    data class SetAutoReset(val on: Boolean) : SettingsIntent()
    data class SetSoundOn(val on: Boolean) : SettingsIntent()
    data class SetShowStreaks(val on: Boolean) : SettingsIntent()
    data class SetAutoProgressWird(val on: Boolean) : SettingsIntent()
    data class SetSmartFlowEnabled(val on: Boolean) : SettingsIntent()
    data class SetDailyReminderEnabled(val on: Boolean) : SettingsIntent()
    data class SetDailyReminderTime(val time: String) : SettingsIntent()
}
