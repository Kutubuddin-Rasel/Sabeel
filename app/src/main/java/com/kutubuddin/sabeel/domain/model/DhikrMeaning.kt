package com.kutubuddin.sabeel.domain.model

/**
 * Translation of a dhikr's meaning in multiple languages.
 * All translations are bundled — no network required.
 */
data class DhikrMeaning(
    val en: String,
    val ur: String = "",   // Urdu
    val bn: String = ""    // Bengali
)
