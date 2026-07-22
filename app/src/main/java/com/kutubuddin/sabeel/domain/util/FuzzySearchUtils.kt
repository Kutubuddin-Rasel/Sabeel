package com.kutubuddin.sabeel.domain.util

object FuzzySearchUtils {

    /**
     * Calculates the Levenshtein distance between two strings using an optimized 2-row array approach.
     * Time Complexity: O(n * m)
     * Space Complexity: O(min(n, m))
     */
    fun levenshteinDistance(s1: String, s2: String): Int {
        if (s1 == s2) return 0
        if (s1.isEmpty()) return s2.length
        if (s2.isEmpty()) return s1.length

        // Swap to ensure s1 is the shorter string to minimize space
        val shortStr = if (s1.length <= s2.length) s1 else s2
        val longStr = if (s1.length <= s2.length) s2 else s1

        var v0 = IntArray(shortStr.length + 1)
        var v1 = IntArray(shortStr.length + 1)

        for (i in 0..shortStr.length) {
            v0[i] = i
        }

        for (i in 0 until longStr.length) {
            v1[0] = i + 1
            for (j in 0 until shortStr.length) {
                val cost = if (longStr[i] == shortStr[j]) 0 else 1
                v1[j + 1] = minOf(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost)
            }
            // Swap arrays
            val temp = v0
            v0 = v1
            v1 = temp
        }
        return v0[shortStr.length]
    }

    /**
     * Normalizes text for searching by lowercasing and removing diacritics
     * (both Latin marks and Arabic Tashkeel).
     */
    fun normalize(text: String): String {
        val temp = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
        // Remove combining diacritical marks (Latin) and Arabic Tashkeel (\u064B-\u065F)
        val regex = "[\\p{InCombiningDiacriticalMarks}\\u064B-\\u065F]+".toRegex()
        return regex.replace(temp, "").lowercase().trim()
    }
}
