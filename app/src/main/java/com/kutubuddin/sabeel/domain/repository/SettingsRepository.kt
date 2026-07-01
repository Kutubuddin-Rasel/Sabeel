package com.kutubuddin.sabeel.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<String>          // "dark" | "light"
    val language: Flow<String>       // "en" | "ur" | "bn"
    val hapticsLevel: Flow<String>   // "light" | "medium" | "strong" | "off"
    val dailyGoal: Flow<Int>
    val translitEnabled: Flow<Boolean>
    val autoReset: Flow<Boolean>
    val soundEnabled: Flow<Boolean>
    val showStreaks: Flow<Boolean>   // consistency counts visible, or hidden for pure ibadah

    suspend fun setTheme(theme: String)
    suspend fun setLanguage(lang: String)
    suspend fun setHaptics(level: String)
    suspend fun setDailyGoal(count: Int)
    suspend fun setTranslitEnabled(on: Boolean)
    suspend fun setAutoReset(on: Boolean)
    suspend fun setSoundEnabled(on: Boolean)
    suspend fun setShowStreaks(on: Boolean)
}
