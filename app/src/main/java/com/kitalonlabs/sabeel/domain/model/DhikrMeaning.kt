package com.kitalonlabs.sabeel.domain.model

/**
 * Translation of a dhikr's meaning in multiple languages.
 * All translations are bundled — no network required.
 */import androidx.compose.runtime.Immutable

@Immutable
data class DhikrMeaning(
    val en: String,
    val bn: String = ""    // Bengali
) {
    fun get(language: String): String = when (language) {
        "bn" -> bn.ifBlank { en }
        else -> en
    }
}
