package com.kutubuddin.sabeel.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.notifications.NotificationScheduler
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.domain.haptic.HapticStrength
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
    private val notificationScheduler: NotificationScheduler,
    private val hapticEngine: HapticEngine
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
        combine(repository.theme, repository.language, repository.hapticsLevel, repository.dailyReminderTime) {
            theme, lang, haptics, time -> listOf(theme, lang, haptics, time)
        },
        combine(
            repository.translitEnabled, repository.autoProgressWird, repository.isSmartFlowEnabled,
            repository.dailyReminderEnabled, repository.isOnboardingComplete
        ) { args: Array<Boolean> -> args.toList() },
        combine(
            repository.hasSeenTasbihTooltip, repository.hasSeenHomeTooltip, repository.hasSeenWirdTooltip,
            repository.hasSeenWirdPickerTooltip, repository.hasSeenLibraryTooltip
        ) { args: Array<Boolean> -> args.toList() }
    ) { strings, booleans1, booleans2 ->
        SettingsState(
            theme           = strings[0],
            language        = strings[1],
            hapticsLevel    = strings[2],
            dailyReminderTime = strings[3],
            translitEnabled = booleans1[0],
            autoProgressWird = booleans1[1],
            isSmartFlowEnabled = booleans1[2],
            dailyReminderEnabled = booleans1[3],
            isOnboardingComplete = booleans1[4],
            hasSeenTasbihTooltip = booleans2[0],
            hasSeenHomeTooltip = booleans2[1],
            hasSeenWirdTooltip = booleans2[2],
            hasSeenWirdPickerTooltip = booleans2[3],
            hasSeenLibraryTooltip = booleans2[4]
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
            is SettingsIntent.SetHaptics  -> {
                repository.setHaptics(intent.level)
                hapticEngine.playIncrementTick(HapticStrength.fromSetting(intent.level))
            }
            is SettingsIntent.SetTranslit -> repository.setTranslitEnabled(intent.on)
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
            is SettingsIntent.SetOnboardingComplete -> repository.setOnboardingComplete(intent.complete)
            
            is SettingsIntent.SetHasSeenTasbihTooltip -> repository.setHasSeenTasbihTooltip(intent.seen)
            is SettingsIntent.SetHasSeenHomeTooltip -> repository.setHasSeenHomeTooltip(intent.seen)
            is SettingsIntent.SetHasSeenWirdTooltip -> repository.setHasSeenWirdTooltip(intent.seen)
            is SettingsIntent.SetHasSeenWirdPickerTooltip -> repository.setHasSeenWirdPickerTooltip(intent.seen)
            is SettingsIntent.SetHasSeenLibraryTooltip -> repository.setHasSeenLibraryTooltip(intent.seen)
        }
    }
}
