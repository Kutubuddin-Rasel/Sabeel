package com.kutubuddin.sabeel.domain.model

/**
 * A short piece of content available in the app's three languages.
 *
 * Mirrors [DhikrMeaning] but is reusable for any localized copy (spiritual
 * rewards, sequence names, …). Blank translations fall back to English so a
 * partially-translated entry never renders empty.
 */
data class LocalizedText(
    val en: String,
    val ur: String = "",
    val bn: String = ""
) {
    fun get(lang: String): String = when (lang) {
        "ur" -> ur.ifBlank { en }
        "bn" -> bn.ifBlank { en }
        else -> en
    }
}
