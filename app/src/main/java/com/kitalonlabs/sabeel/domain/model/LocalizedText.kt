package com.kitalonlabs.sabeel.domain.model

import androidx.compose.runtime.Immutable

/**
 * Encapsulates text available in multiple languages.
 *
 * For features like custom dhikr that don't have translations yet,
 * both fields can point to the same user-provided text.
 */
@Immutable
data class LocalizedText(
    val en: String,
    val bn: String = ""
) {
    fun get(lang: String): String = when (lang) {
        "bn" -> bn.ifBlank { en }
        else -> en
    }
}
