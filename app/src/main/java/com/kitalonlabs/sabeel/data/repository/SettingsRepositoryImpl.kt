package com.kitalonlabs.sabeel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kitalonlabs.sabeel.di.ApplicationScope
import com.kitalonlabs.sabeel.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import com.kitalonlabs.sabeel.di.IoDispatcher
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
    @ApplicationScope private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SettingsRepository {

    companion object {
        val KEY_THEME            = stringPreferencesKey("settings_theme")
        val KEY_LANGUAGE         = stringPreferencesKey("settings_language")
        val KEY_HAPTICS          = stringPreferencesKey("settings_haptics")
        val KEY_TRANSLIT_ENABLED = booleanPreferencesKey("settings_translit")
        val KEY_AUTO_PROGRESS_WIRD = booleanPreferencesKey("settings_auto_progress_wird")
        val KEY_SMART_FLOW_ENABLED = booleanPreferencesKey("smart_flow_enabled")
        val KEY_DAILY_REMINDER_ENABLED = booleanPreferencesKey("settings_daily_reminder_enabled")
        val KEY_DAILY_REMINDER_TIME = stringPreferencesKey("settings_daily_reminder_time")
        val KEY_APP_LAUNCH_COUNT = intPreferencesKey("app_launch_count")
        val KEY_IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
        
        val KEY_TOOLTIP_TASBIH = booleanPreferencesKey("tooltip_tasbih")
        val KEY_TOOLTIP_HOME = booleanPreferencesKey("tooltip_home")
        val KEY_TOOLTIP_WIRD = booleanPreferencesKey("tooltip_wird")
        val KEY_TOOLTIP_WIRD_PICKER = booleanPreferencesKey("tooltip_wird_picker")
        val KEY_TOOLTIP_LIBRARY = booleanPreferencesKey("tooltip_library")
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

    override val translitEnabled: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_TRANSLIT_ENABLED] ?: true }
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

    override val isOnboardingComplete: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_IS_ONBOARDING_COMPLETE] ?: false }
        .stateIn(scope, SHARING, false)

    override val hasSeenTasbihTooltip: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_TOOLTIP_TASBIH] ?: false }
        .stateIn(scope, SHARING, false)

    override val hasSeenHomeTooltip: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_TOOLTIP_HOME] ?: false }
        .stateIn(scope, SHARING, false)

    override val hasSeenWirdTooltip: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_TOOLTIP_WIRD] ?: false }
        .stateIn(scope, SHARING, false)

    override val hasSeenWirdPickerTooltip: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_TOOLTIP_WIRD_PICKER] ?: false }
        .stateIn(scope, SHARING, false)

    override val hasSeenLibraryTooltip: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_TOOLTIP_LIBRARY] ?: false }
        .stateIn(scope, SHARING, false)

    override suspend fun setTheme(theme: String) = withContext(ioDispatcher) { dataStore.edit { it[KEY_THEME] = theme }.let {} }
    override suspend fun setLanguage(lang: String) = withContext(ioDispatcher) { dataStore.edit { it[KEY_LANGUAGE] = lang }.let {} }
    override suspend fun setHaptics(level: String) = withContext(ioDispatcher) { dataStore.edit { it[KEY_HAPTICS] = level }.let {} }
    override suspend fun setTranslitEnabled(on: Boolean) = withContext(ioDispatcher) { dataStore.edit { it[KEY_TRANSLIT_ENABLED] = on }.let {} }
    override suspend fun setAutoProgressWird(on: Boolean) = withContext(ioDispatcher) { dataStore.edit { it[KEY_AUTO_PROGRESS_WIRD] = on }.let {} }
    override suspend fun setSmartFlowEnabled(on: Boolean) = withContext(ioDispatcher) { dataStore.edit { it[KEY_SMART_FLOW_ENABLED] = on }.let {} }
    override suspend fun setDailyReminderEnabled(on: Boolean) = withContext(ioDispatcher) { dataStore.edit { it[KEY_DAILY_REMINDER_ENABLED] = on }.let {} }
    override suspend fun setDailyReminderTime(time: String) = withContext(ioDispatcher) { dataStore.edit { it[KEY_DAILY_REMINDER_TIME] = time }.let {} }

    override suspend fun incrementAppLaunchCount() = withContext(ioDispatcher) {
        dataStore.edit { preferences ->
            val current = preferences[KEY_APP_LAUNCH_COUNT] ?: 0
            preferences[KEY_APP_LAUNCH_COUNT] = current + 1
        }
        Unit
    }

    override suspend fun setOnboardingComplete(complete: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[KEY_IS_ONBOARDING_COMPLETE] = complete }
        Unit
    }

    override suspend fun setHasSeenTasbihTooltip(seen: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[KEY_TOOLTIP_TASBIH] = seen }
        Unit
    }

    override suspend fun setHasSeenHomeTooltip(seen: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[KEY_TOOLTIP_HOME] = seen }
        Unit
    }

    override suspend fun setHasSeenWirdTooltip(seen: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[KEY_TOOLTIP_WIRD] = seen }
        Unit
    }

    override suspend fun setHasSeenWirdPickerTooltip(seen: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[KEY_TOOLTIP_WIRD_PICKER] = seen }
        Unit
    }

    override suspend fun setHasSeenLibraryTooltip(seen: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[KEY_TOOLTIP_LIBRARY] = seen }
        Unit
    }
}
