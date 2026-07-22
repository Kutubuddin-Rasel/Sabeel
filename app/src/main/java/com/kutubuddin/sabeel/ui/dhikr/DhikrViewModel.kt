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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineDispatcher
import com.kutubuddin.sabeel.di.DefaultDispatcher
import kotlinx.coroutines.flow.flowOn
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
    private val settingsRepository: SettingsRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _searchQuery  = MutableStateFlow("")
    private val _selectedDhikrKey  = MutableStateFlow<String?>(null)

    // Debounce search to avoid filtering on every keystroke
    private val debouncedQuery = _searchQuery.debounce(300L)

    // Filter catalog based on debounced query; recompute only when query or catalog changes
    private val filteredDhikr = dhikrRepository.getAllDhikr()
        .combine(debouncedQuery) { allDhikr, query ->
            if (query.isBlank()) {
                // EXCLUDE individual Asma Ul Husna items from the default view
                // Only keep ASMA_ALL_99 and other categories
                allDhikr.filter { it.category != DhikrCategory.ASMA_UL_HUSNA || it.key == "ASMA_ALL_99" }
            } else {
                allDhikr.map { it to it.searchScore(query) }
                    .filter { it.second > 0 }
                    .sortedByDescending { it.second }
                    .map { it.first }
            }
        }.flowOn(defaultDispatcher)

    // Group filtered list by category — done on collection thread, not main
    private val categorized = filteredDhikr.map { items ->
        DhikrCategory.entries
            .associateWith { cat -> items.filter { it.category == cat }.toImmutableList() }
            .filterValues { it.isNotEmpty() }
            .toImmutableMap()
    }.flowOn(defaultDispatcher)

    val state: StateFlow<DhikrLibraryState> = combine(
        categorized,
        _searchQuery,       // raw (for display), not debounced
        settingsRepository.language,
        settingsRepository.translitEnabled,
        _selectedDhikrKey
    ) { cat, query, language, translit, selectedDhikrKey ->
        DhikrLibraryState(
            categorized = cat,
            searchQuery = query,
            language    = language,
            showTransliteration = translit,
            selectedDhikrKey = selectedDhikrKey
        )
    }.stateIn(
        scope   = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DhikrLibraryState()
    )

    fun onSearch(query: String) {
        _searchQuery.value = query
    }

    /** Toggle selection for bottom sheet */
    fun onSelectDhikr(key: String?) {
        _selectedDhikrKey.value = if (_selectedDhikrKey.value == key) null else key
    }
}
