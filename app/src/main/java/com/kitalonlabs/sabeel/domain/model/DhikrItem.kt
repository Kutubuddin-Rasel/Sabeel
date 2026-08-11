package com.kitalonlabs.sabeel.domain.model

import androidx.compose.runtime.Immutable

/**
 * Unified domain model for the Dhikr Library catalog.
 *
 * Bridges both the built-in catalog (DhikrCatalog) and user-created
 * custom dhikr (CustomDhikrEntity) into a single UI-ready type.
 *
 * [key] maps to DhikrType.name for built-in entries, or UUID for custom ones.
 * This allows TasbihViewModel to resolve the correct DhikrType on "Count Now".
 */
@Immutable
data class DhikrItem(
    val key: String,
    val arabicText: String,
    val displayName: LocalizedText,
    val transliteration: LocalizedText?,
    val meaning: DhikrMeaning,
    val defaultTarget: Int,
    val spiritualReward: LocalizedText,
    val hadithRef: String,
    val category: DhikrCategory,
    val isSmartFlow: Boolean = false,
    val smartFlowVariant: SmartFlowVariant? = null,
    val isCustom: Boolean = false
) {
    val searchableWords: List<String> = run {
        val allText = buildString {
            append(displayName.en).append(" ")
            append(displayName.bn).append(" ")
            append(arabicText).append(" ")
            if (transliteration != null) {
                append(transliteration.en).append(" ")
                append(transliteration.bn).append(" ")
            }
        }
        com.kitalonlabs.sabeel.domain.util.FuzzySearchUtils.normalize(allText)
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
    }

    /** Returns true if the search query matches this item. */
    fun matches(query: String): Boolean {
        return searchScore(query) > 0
    }

    /** Returns a relevance score for the query, 0 if no match. */
    fun searchScore(query: String): Int {
        val queryWords = com.kitalonlabs.sabeel.domain.util.FuzzySearchUtils.normalize(query)
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            
        if (queryWords.isEmpty()) return 0
        
        var totalScore = 0
        
        for (qWord in queryWords) {
            var bestWordScore = 0
            
            for (tWord in searchableWords) {
                if (tWord == qWord) {
                    bestWordScore = maxOf(bestWordScore, 100)
                } else if (tWord.startsWith(qWord)) {
                    bestWordScore = maxOf(bestWordScore, 80)
                } else if (tWord.contains(qWord)) {
                    bestWordScore = maxOf(bestWordScore, 60)
                } else if (qWord.length > 3) {
                    val dist = com.kitalonlabs.sabeel.domain.util.FuzzySearchUtils.levenshteinDistance(qWord, tWord)
                    val allowedDist = if (qWord.length > 5) 2 else 1
                    if (dist <= allowedDist) {
                        val fuzzyScore = if (dist == 1) 50 else 30
                        bestWordScore = maxOf(bestWordScore, fuzzyScore)
                    }
                }
            }
            
            // If any word in the query has absolutely no match (bestWordScore == 0), the whole search fails.
            if (bestWordScore == 0) return 0
            totalScore += bestWordScore
        }
        return totalScore
    }
}
