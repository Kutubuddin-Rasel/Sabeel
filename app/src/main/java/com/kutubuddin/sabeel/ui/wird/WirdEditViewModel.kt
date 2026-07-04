package com.kutubuddin.sabeel.ui.wird

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Edit-the-plan screen state + mutations. All view logic lives in the pure
 * [resolveWirdEditState]/[swapAdjacent] helpers (unit-tested); this class is the
 * thin Hilt/coroutine wiring around them. The target≥1 invariant is enforced by
 * the repository (`coerceAtLeast(1)`), so the steppers can pass `target ± 1`
 * freely without duplicating the floor here.
 */
@HiltViewModel
class WirdEditViewModel @Inject constructor(
    private val wirdRepository: WirdRepository,
    dhikrRepository: DhikrRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val state: StateFlow<WirdEditState> = combine(
        wirdRepository.observeWird(),
        dhikrRepository.getAllDhikr(),
        settingsRepository.language
    ) { plan, catalog, language ->
        resolveWirdEditState(plan, catalog, language)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WirdEditState())

    fun addDhikr(key: String, target: Int) = viewModelScope.launch { wirdRepository.addToWird(key, target) }
    fun updateTarget(key: String, target: Int) = viewModelScope.launch { wirdRepository.updateTarget(key, target) }
    fun remove(key: String) = viewModelScope.launch { wirdRepository.removeFromWird(key) }

    fun move(key: String, up: Boolean) = viewModelScope.launch {
        val order = state.value.rows.map { it.dhikrKey }
        swapAdjacent(order, key, up)?.let { wirdRepository.reorder(it) }
    }
}
