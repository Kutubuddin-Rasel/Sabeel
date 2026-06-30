package com.kutubuddin.sabeel.domain.model

/**
 * Unified domain model for the Dhikr Library catalog.
 *
 * Bridges both the built-in catalog (DhikrCatalog) and user-created
 * custom dhikr (CustomDhikrEntity) into a single UI-ready type.
 *
 * [key] maps to DhikrType.name for built-in entries, or UUID for custom ones.
 * This allows TasbihViewModel to resolve the correct DhikrType on "Count Now".
 */
data class DhikrItem(
    val key: String,
    val arabicText: String,
    val displayName: String,
    val transliteration: String?,
    val meaning: DhikrMeaning,
    val defaultTarget: Int,
    val spiritualReward: String,
    val hadithRef: String,
    val category: DhikrCategory,
    val isSmartFlow: Boolean = false,
    val smartFlowVariant: SmartFlowVariant? = null,
    val isCustom: Boolean = false
) {
    /** Returns true if the search query matches this item. */
    fun matches(query: String): Boolean {
        val q = query.trim()
        return displayName.contains(q, ignoreCase = true) ||
               arabicText.contains(q) ||
               transliteration?.contains(q, ignoreCase = true) == true ||
               category.displayName.contains(q, ignoreCase = true)
    }
}
