package com.kutubuddin.sabeel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
        val KEY_LEFT_HANDED      = booleanPreferencesKey("settings_left_handed")
        val KEY_TRANSLIT_ENABLED = booleanPreferencesKey("settings_translit")
        val KEY_AUTO_RESET       = booleanPreferencesKey("settings_auto_reset")
        val KEY_SOUND_ENABLED    = booleanPreferencesKey("settings_sound")
        val KEY_SHOW_STREAKS     = booleanPreferencesKey("settings_show_streaks")
        val KEY_AUTO_PROGRESS_WIRD = booleanPreferencesKey("settings_auto_progress_wird")
        val KEY_SMART_FLOW_ENABLED = booleanPreferencesKey("smart_flow_enabled")
        val KEY_DAILY_REMINDER_ENABLED = booleanPreferencesKey("settings_daily_reminder_enabled")
        val KEY_DAILY_REMINDER_TIME = stringPreferencesKey("settings_daily_reminder_time")
        val KEY_APP_LAUNCH_COUNT = androidx.datastore.preferences.core.intPreferencesKey("app_launch_count")
    }

    override val theme: Flow<String> = dataStore.data.map { it[KEY_THEME] ?: "dark" }
    override val language: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "en" }
    override val hapticsLevel: Flow<String> = dataStore.data.map { it[KEY_HAPTICS] ?: "medium" }
    override val leftHanded: Flow<Boolean> = dataStore.data.map { it[KEY_LEFT_HANDED] ?: false }
    override val translitEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_TRANSLIT_ENABLED] ?: true }
    override val autoReset: Flow<Boolean> = dataStore.data.map { it[KEY_AUTO_RESET] ?: false }
    override val soundEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_SOUND_ENABLED] ?: true }
    override val showStreaks: Flow<Boolean> = dataStore.data.map { it[KEY_SHOW_STREAKS] ?: true }
    override val autoProgressWird: Flow<Boolean> = dataStore.data.map { it[KEY_AUTO_PROGRESS_WIRD] ?: true }
    override val isSmartFlowEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_SMART_FLOW_ENABLED] ?: true }
    override val dailyReminderEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_DAILY_REMINDER_ENABLED] ?: true }
    override val dailyReminderTime: Flow<String> = dataStore.data.map { it[KEY_DAILY_REMINDER_TIME] ?: "20:30" }
    
    override val appLaunchCount: Flow<Int> = dataStore.data.map { it[KEY_APP_LAUNCH_COUNT] ?: 0 }

    override suspend fun setTheme(theme: String) = dataStore.edit { it[KEY_THEME] = theme }.let {}
    override suspend fun setLanguage(lang: String) = dataStore.edit { it[KEY_LANGUAGE] = lang }.let {}
    override suspend fun setHaptics(level: String) = dataStore.edit { it[KEY_HAPTICS] = level }.let {}
    override suspend fun setLeftHanded(on: Boolean) = dataStore.edit { it[KEY_LEFT_HANDED] = on }.let {}
    override suspend fun setTranslitEnabled(on: Boolean) = dataStore.edit { it[KEY_TRANSLIT_ENABLED] = on }.let {}
    override suspend fun setAutoReset(on: Boolean) = dataStore.edit { it[KEY_AUTO_RESET] = on }.let {}
    override suspend fun setSoundEnabled(on: Boolean) = dataStore.edit { it[KEY_SOUND_ENABLED] = on }.let {}
    override suspend fun setShowStreaks(on: Boolean) = dataStore.edit { it[KEY_SHOW_STREAKS] = on }.let {}
    override suspend fun setAutoProgressWird(on: Boolean) = dataStore.edit { it[KEY_AUTO_PROGRESS_WIRD] = on }.let {}
    override suspend fun setSmartFlowEnabled(on: Boolean) = dataStore.edit { it[KEY_SMART_FLOW_ENABLED] = on }.let {}
    override suspend fun setDailyReminderEnabled(on: Boolean) = dataStore.edit { it[KEY_DAILY_REMINDER_ENABLED] = on }.let {}
    override suspend fun setDailyReminderTime(time: String) = dataStore.edit { it[KEY_DAILY_REMINDER_TIME] = time }.let {}
    
    override suspend fun incrementAppLaunchCount() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_APP_LAUNCH_COUNT] ?: 0
            preferences[KEY_APP_LAUNCH_COUNT] = current + 1
        }
    }
}
