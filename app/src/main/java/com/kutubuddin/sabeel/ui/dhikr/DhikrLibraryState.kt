package com.kutubuddin.sabeel.ui.dhikr

import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem

data class DhikrLibraryState(
    val categorized: Map<DhikrCategory, List<DhikrItem>> = emptyMap(),
    val searchQuery: String = "",
    val language: String = "en",
    val expandedKey: String? = null  // key of the currently expanded card
)
