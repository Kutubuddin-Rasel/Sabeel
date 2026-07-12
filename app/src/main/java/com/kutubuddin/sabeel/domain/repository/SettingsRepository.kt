package com.kutubuddin.sabeel.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<String>          // "dark" | "light"
    val language: Flow<String>       // "en" | "ur" | "bn"
    val hapticsLevel: Flow<String>   // "light" | "medium" | "strong" | "off"
    val leftHanded: Flow<Boolean>    // bias the count target toward the left thumb
    val translitEnabled: Flow<Boolean>
    val autoReset: Flow<Boolean>
    val soundEnabled: Flow<Boolean>
    val showStreaks: Flow<Boolean>   // consistency counts visible, or hidden for pure ibadah
    val autoProgressWird: Flow<Boolean>
    val isSmartFlowEnabled: Flow<Boolean>
    val dailyReminderEnabled: Flow<Boolean>
    val dailyReminderTime: Flow<String>
    
    val appLaunchCount: Flow<Int>

    suspend fun setTheme(theme: String)
    suspend fun setLanguage(lang: String)
    suspend fun setHaptics(level: String)
    suspend fun setLeftHanded(on: Boolean)
    suspend fun setTranslitEnabled(on: Boolean)
    suspend fun setAutoReset(on: Boolean)
    suspend fun setSoundEnabled(on: Boolean)
    suspend fun setShowStreaks(on: Boolean)
    suspend fun setAutoProgressWird(on: Boolean)
    suspend fun setSmartFlowEnabled(on: Boolean)
    suspend fun setDailyReminderEnabled(on: Boolean)
    suspend fun setDailyReminderTime(time: String)
    suspend fun incrementAppLaunchCount()
}
