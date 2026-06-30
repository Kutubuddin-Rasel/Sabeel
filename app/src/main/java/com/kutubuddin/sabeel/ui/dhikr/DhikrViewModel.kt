package com.kutubuddin.sabeel.ui.dhikr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the Dhikr Library tab.
 *
 * Threading: all Flow operators are cold — Room emits on IoDispatcher via
 * DhikrRepositoryImpl. No explicit dispatcher needed here.
 *
 * Performance:
 * - Search query is debounced 300ms to avoid recompose on every keystroke.
 * - DhikrCategory.values() is called once per emission, not in composition.
 * - expandedKey is a separate StateFlow so toggling a card doesn't refilter the list.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class DhikrViewModel @Inject constructor(
    private val dhikrRepository: DhikrRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _searchQuery  = MutableStateFlow("")
    private val _expandedKey  = MutableStateFlow<String?>(null)

    // Debounce search to avoid filtering on every keystroke
    private val debouncedQuery = _searchQuery.debounce(300L)

    // Filter catalog based on debounced query; recompute only when query or catalog changes
    private val filteredDhikr = dhikrRepository.getAllDhikr()
        .combine(debouncedQuery) { allDhikr, query ->
            if (query.isBlank()) allDhikr
            else allDhikr.filter { it.matches(query) }
        }

    // Group filtered list by category — done on collection thread, not main
    private val categorized = filteredDhikr.map { items ->
        DhikrCategory.values()
            .associateWith { cat -> items.filter { it.category == cat } }
            .filterValues { it.isNotEmpty() }
    }

    val state: StateFlow<DhikrLibraryState> = combine(
        categorized,
        _searchQuery,       // raw (for display), not debounced
        settingsRepository.language,
        _expandedKey
    ) { cat, query, language, expandedKey ->
        DhikrLibraryState(
            categorized = cat,
            searchQuery = query,
            language    = language,
            expandedKey = expandedKey
        )
    }.stateIn(
        scope   = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DhikrLibraryState()
    )

    fun onSearch(query: String) {
        _searchQuery.value = query
    }

    /** Toggle expand: same key collapses, different key expands. */
    fun onToggleExpand(key: String) {
        _expandedKey.value = if (_expandedKey.value == key) null else key
    }
}
