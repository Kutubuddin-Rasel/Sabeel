package com.kutubuddin.sabeel.ui.settings

data class SettingsState(
    val theme: String = "dark",
    val language: String = "en",
    val hapticsLevel: String = "medium",
    val translitEnabled: Boolean = true,
    val autoReset: Boolean = false,
    val soundEnabled: Boolean = false,
    val showStreaks: Boolean = true,
    val autoProgressWird: Boolean = true
)

sealed class SettingsIntent {
    data class SetTheme(val theme: String) : SettingsIntent()
    data class SetLanguage(val lang: String) : SettingsIntent()
    data class SetHaptics(val level: String) : SettingsIntent()
    data class SetTranslit(val on: Boolean) : SettingsIntent()
    data class SetAutoReset(val on: Boolean) : SettingsIntent()
    data class SetSoundOn(val on: Boolean) : SettingsIntent()
    data class SetShowStreaks(val on: Boolean) : SettingsIntent()
    data class SetAutoProgressWird(val on: Boolean) : SettingsIntent()
}
