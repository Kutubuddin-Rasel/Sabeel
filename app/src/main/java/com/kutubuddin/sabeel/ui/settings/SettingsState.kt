package com.kutubuddin.sabeel.ui.settings

data class SettingsState(
    val theme: String = "dark",
    val language: String = "en",
    val hapticsLevel: String = "medium",
    val dailyGoal: Int = 200,
    val translitEnabled: Boolean = true,
    val autoReset: Boolean = true,
    val soundEnabled: Boolean = false
)

sealed class SettingsIntent {
    data class SetTheme(val theme: String) : SettingsIntent()
    data class SetLanguage(val lang: String) : SettingsIntent()
    data class SetHaptics(val level: String) : SettingsIntent()
    data class SetDailyGoal(val count: Int) : SettingsIntent()
    data class SetTranslit(val on: Boolean) : SettingsIntent()
    data class SetAutoReset(val on: Boolean) : SettingsIntent()
    data class SetSoundOn(val on: Boolean) : SettingsIntent()
}
