package com.kutubuddin.sabeel.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: SettingsRepository
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
        combine(repository.theme, repository.language, repository.hapticsLevel) {
            theme, lang, haptics -> Triple(theme, lang, haptics)
        },
        combine(repository.dailyGoal, repository.translitEnabled, repository.autoReset, repository.soundEnabled) {
            goal, translit, autoReset, sound -> listOf<Any>(goal, translit, autoReset, sound)
        }
    ) { (theme, lang, haptics), extras ->
        SettingsState(
            theme           = theme as String,
            language        = lang as String,
            hapticsLevel    = haptics as String,
            dailyGoal       = extras[0] as Int,
            translitEnabled = extras[1] as Boolean,
            autoReset       = extras[2] as Boolean,
            soundEnabled    = extras[3] as Boolean
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
            is SettingsIntent.SetDailyGoal-> repository.setDailyGoal(intent.count)
            is SettingsIntent.SetTranslit -> repository.setTranslitEnabled(intent.on)
            is SettingsIntent.SetAutoReset-> repository.setAutoReset(intent.on)
            is SettingsIntent.SetSoundOn  -> repository.setSoundEnabled(intent.on)
        }
    }
}
