package com.kutubuddin.sabeel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        val KEY_THEME            = stringPreferencesKey("settings_theme")
        val KEY_LANGUAGE         = stringPreferencesKey("settings_language")
        val KEY_HAPTICS          = stringPreferencesKey("settings_haptics")
        val KEY_DAILY_GOAL       = intPreferencesKey("settings_daily_goal")
        val KEY_TRANSLIT_ENABLED = booleanPreferencesKey("settings_translit")
        val KEY_AUTO_RESET       = booleanPreferencesKey("settings_auto_reset")
        val KEY_SOUND_ENABLED    = booleanPreferencesKey("settings_sound")
        val KEY_SHOW_STREAKS     = booleanPreferencesKey("settings_show_streaks")
    }

    override val theme: Flow<String> = dataStore.data.map { it[KEY_THEME] ?: "dark" }
    override val language: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "en" }
    override val hapticsLevel: Flow<String> = dataStore.data.map { it[KEY_HAPTICS] ?: "medium" }
    override val dailyGoal: Flow<Int> = dataStore.data.map { it[KEY_DAILY_GOAL] ?: 200 }
    override val translitEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_TRANSLIT_ENABLED] ?: true }
    override val autoReset: Flow<Boolean> = dataStore.data.map { it[KEY_AUTO_RESET] ?: false }
    override val soundEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_SOUND_ENABLED] ?: false }
    override val showStreaks: Flow<Boolean> = dataStore.data.map { it[KEY_SHOW_STREAKS] ?: true }

    override suspend fun setTheme(theme: String) = dataStore.edit { it[KEY_THEME] = theme }.let {}
    override suspend fun setLanguage(lang: String) = dataStore.edit { it[KEY_LANGUAGE] = lang }.let {}
    override suspend fun setHaptics(level: String) = dataStore.edit { it[KEY_HAPTICS] = level }.let {}
    override suspend fun setDailyGoal(count: Int) = dataStore.edit { it[KEY_DAILY_GOAL] = count }.let {}
    override suspend fun setTranslitEnabled(on: Boolean) = dataStore.edit { it[KEY_TRANSLIT_ENABLED] = on }.let {}
    override suspend fun setAutoReset(on: Boolean) = dataStore.edit { it[KEY_AUTO_RESET] = on }.let {}
    override suspend fun setSoundEnabled(on: Boolean) = dataStore.edit { it[KEY_SOUND_ENABLED] = on }.let {}
    override suspend fun setShowStreaks(on: Boolean) = dataStore.edit { it[KEY_SHOW_STREAKS] = on }.let {}
}
