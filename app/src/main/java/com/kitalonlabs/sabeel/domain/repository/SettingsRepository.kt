package com.kitalonlabs.sabeel.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<String>          // "dark" | "light"
    val language: Flow<String>       // "en" | "bn"
    val hapticsLevel: Flow<String>   // "light" | "medium" | "strong" | "off"
    val translitEnabled: Flow<Boolean>   // consistency counts visible, or hidden for pure ibadah
    val autoProgressWird: Flow<Boolean>
    val isSmartFlowEnabled: Flow<Boolean>
    val dailyReminderEnabled: Flow<Boolean>
    val dailyReminderTime: Flow<String>
    
    val appLaunchCount: Flow<Int>
    val isOnboardingComplete: Flow<Boolean>
    
    // JIT Onboarding Tooltip Flags
    val hasSeenTasbihTooltip: Flow<Boolean>
    val hasSeenHomeTooltip: Flow<Boolean>
    val hasSeenWirdTooltip: Flow<Boolean>
    val hasSeenWirdPickerTooltip: Flow<Boolean>
    val hasSeenLibraryTooltip: Flow<Boolean>

    suspend fun setTheme(theme: String)
    suspend fun setLanguage(lang: String)
    suspend fun setHaptics(level: String)
    suspend fun setTranslitEnabled(on: Boolean)
    suspend fun setAutoProgressWird(on: Boolean)
    suspend fun setSmartFlowEnabled(on: Boolean)
    suspend fun setDailyReminderEnabled(on: Boolean)
    suspend fun setDailyReminderTime(time: String)
    suspend fun incrementAppLaunchCount()
    suspend fun setOnboardingComplete(complete: Boolean)

    suspend fun setHasSeenTasbihTooltip(seen: Boolean)
    suspend fun setHasSeenHomeTooltip(seen: Boolean)
    suspend fun setHasSeenWirdTooltip(seen: Boolean)
    suspend fun setHasSeenWirdPickerTooltip(seen: Boolean)
    suspend fun setHasSeenLibraryTooltip(seen: Boolean)
}
