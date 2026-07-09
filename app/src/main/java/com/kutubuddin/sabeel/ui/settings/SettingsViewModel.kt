package com.kutubuddin.sabeel.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.notifications.NotificationScheduler
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
        combine(repository.theme, repository.language, repository.hapticsLevel, repository.dailyReminderTime) {
            theme, lang, haptics, time -> listOf(theme, lang, haptics, time)
        },
        combine(
            repository.translitEnabled, repository.autoReset, repository.soundEnabled, 
            repository.showStreaks, repository.autoProgressWird, repository.isSmartFlowEnabled,
            repository.dailyReminderEnabled
        ) { args: Array<Boolean> -> args.toList() }
    ) { strings, booleans ->
        SettingsState(
            theme           = strings[0],
            language        = strings[1],
            hapticsLevel    = strings[2],
            dailyReminderTime = strings[3],
            translitEnabled = booleans[0],
            autoReset       = booleans[1],
            soundEnabled    = booleans[2],
            showStreaks     = booleans[3],
            autoProgressWird = booleans[4],
            isSmartFlowEnabled = booleans[5],
            dailyReminderEnabled = booleans[6]
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsState()
    )

    fun processIntent(intent: SettingsIntent) = viewModelScope.launch {
        when (intent) {
            is SettingsIntent.SetTheme    -> repository.setTheme(intent.theme)
            is SettingsIntent.SetLanguage -> repository.setLanguage(intent.lang)
            is SettingsIntent.SetHaptics  -> repository.setHaptics(intent.level)
            is SettingsIntent.SetTranslit -> repository.setTranslitEnabled(intent.on)
            is SettingsIntent.SetAutoReset-> repository.setAutoReset(intent.on)
            is SettingsIntent.SetSoundOn  -> repository.setSoundEnabled(intent.on)
            is SettingsIntent.SetShowStreaks -> repository.setShowStreaks(intent.on)
            is SettingsIntent.SetAutoProgressWird -> repository.setAutoProgressWird(intent.on)
            is SettingsIntent.SetSmartFlowEnabled -> repository.setSmartFlowEnabled(intent.on)
            is SettingsIntent.SetDailyReminderEnabled -> {
                repository.setDailyReminderEnabled(intent.on)
                if (intent.on) {
                    notificationScheduler.scheduleDailyReminder(state.value.dailyReminderTime)
                } else {
                    notificationScheduler.cancelDailyReminder()
                }
            }
            is SettingsIntent.SetDailyReminderTime -> {
                repository.setDailyReminderTime(intent.time)
                if (state.value.dailyReminderEnabled) {
                    notificationScheduler.scheduleDailyReminder(intent.time)
                }
            }
        }
    }
}
