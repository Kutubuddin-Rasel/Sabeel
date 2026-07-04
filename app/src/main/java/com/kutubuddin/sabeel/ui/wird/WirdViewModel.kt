package com.kutubuddin.sabeel.ui.wird

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WirdViewModel @Inject constructor(
    observeWirdProgress: ObserveWirdProgress,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val today = LocalDate.now().toString()

    val state: StateFlow<WirdProgress> = observeWirdProgress(today).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WirdProgress(emptyList())
    )

    val language: StateFlow<String> = settingsRepository.language.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "en"
    )
}
