package com.kutubuddin.sabeel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kutubuddin.sabeel.di.ApplicationScope
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * OPT-01: All settings properties are now [StateFlow] via [stateIn].
 * This means all 13 callers (SettingsViewModel combine, MainActivity direct collect, etc.)
 * share a **single** upstream [DataStore.data] subscription instead of N independent cold flows.
 *
 * OPT-02: Injects the @Named("settings") DataStore — a separate file from the
 * counter DataStore — so tap-driven counter writes never trigger settings re-evaluation.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @Named("settings") private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val scope: CoroutineScope
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
        val KEY_APP_LAUNCH_COUNT = intPreferencesKey("app_launch_count")

        // Sharing config: 5-second upstream keep-alive after last collector drops.
        private val SHARING = SharingStarted.WhileSubscribed(5_000)
    }

    // OPT-01: StateFlow — all collectors share a single dataStore.data subscription.
    override val theme: StateFlow<String> = dataStore.data
        .map { it[KEY_THEME] ?: "dark" }
        .stateIn(scope, SHARING, "dark")

    override val language: StateFlow<String> = dataStore.data
        .map { it[KEY_LANGUAGE] ?: "en" }
        .stateIn(scope, SHARING, "en")

    override val hapticsLevel: StateFlow<String> = dataStore.data
        .map { it[KEY_HAPTICS] ?: "medium" }
        .stateIn(scope, SHARING, "medium")

    override val leftHanded: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_LEFT_HANDED] ?: false }
        .stateIn(scope, SHARING, false)

    override val translitEnabled: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_TRANSLIT_ENABLED] ?: true }
        .stateIn(scope, SHARING, true)

    override val autoReset: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_AUTO_RESET] ?: false }
        .stateIn(scope, SHARING, false)

    override val soundEnabled: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_SOUND_ENABLED] ?: true }
        .stateIn(scope, SHARING, true)

    override val showStreaks: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_SHOW_STREAKS] ?: true }
        .stateIn(scope, SHARING, true)

    override val autoProgressWird: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_AUTO_PROGRESS_WIRD] ?: true }
        .stateIn(scope, SHARING, true)

    override val isSmartFlowEnabled: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_SMART_FLOW_ENABLED] ?: true }
        .stateIn(scope, SHARING, true)

    override val dailyReminderEnabled: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_DAILY_REMINDER_ENABLED] ?: true }
        .stateIn(scope, SHARING, true)

    override val dailyReminderTime: StateFlow<String> = dataStore.data
        .map { it[KEY_DAILY_REMINDER_TIME] ?: "20:30" }
        .stateIn(scope, SHARING, "20:30")

    override val appLaunchCount: StateFlow<Int> = dataStore.data
        .map { it[KEY_APP_LAUNCH_COUNT] ?: 0 }
        .stateIn(scope, SHARING, 0)

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
