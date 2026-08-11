package com.kitalonlabs.sabeel.ui.wird

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitalonlabs.sabeel.domain.model.WirdProgress
import com.kitalonlabs.sabeel.domain.repository.SettingsRepository
import com.kitalonlabs.sabeel.domain.usecase.MarkWirdGoalHintSeen
import com.kitalonlabs.sabeel.domain.usecase.ObserveWirdProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

import kotlinx.collections.immutable.persistentListOf

/** Singular, immutable UI state for [WirdScreen] — today's progress + display language. */
data class WirdUiState(
    val progress: WirdProgress = WirdProgress(persistentListOf()),
    val language: String = "en"
)

/**
 * SRP: exposes exactly one [StateFlow] of one immutable [WirdUiState] — no
 * parallel StateFlows for the screen to desync on.
 * DIP: depends only on the [ObserveWirdProgress] use case and the
 * [SettingsRepository] interface, never a concrete DAO/DataStore.
 */
@HiltViewModel
class WirdViewModel @Inject constructor(
    observeWirdProgress: ObserveWirdProgress,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val today = LocalDate.now().toString()

    val state: StateFlow<WirdUiState> = combine(
        observeWirdProgress(today),
        settingsRepository.language
    ) { progress, language -> WirdUiState(progress, language) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WirdUiState()
        )

    /** Fired by this screen's persistent Edit-Wird entry point. */
    fun onWirdEditEntryUsed() {}
}