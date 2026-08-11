package com.kitalonlabs.sabeel.ui.dhikr

import com.kitalonlabs.sabeel.domain.model.DhikrCategory
import com.kitalonlabs.sabeel.domain.model.DhikrItem

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class DhikrLibraryState(
    val categorized: ImmutableMap<DhikrCategory, ImmutableList<DhikrItem>> = persistentMapOf(),
    val searchQuery: String = "",
    val language: String = "en",
    val showTransliteration: Boolean = true,
    val selectedDhikrKey: String? = null  // key of the currently selected card for the bottom sheet
)
