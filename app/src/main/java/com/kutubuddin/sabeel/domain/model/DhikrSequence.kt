package com.kutubuddin.sabeel.domain.model

/**
 * One countable phrase inside a [DhikrSequence] (e.g. "SubhanAllah × 33").
 *
 * A step is pure data — the counting engine ([TasbihViewModel.advance]) reads
 * [target] to decide when to move on, so adding a variant never touches engine code.
 */
data class DhikrStep(
    val arabicText: String,
    val displayName: String,
    val transliteration: String,
    val target: Int
)

/**
 * An ordered list of [DhikrStep]s counted one after another — the generic
 * replacement for the old hardcoded post-Salah state machine. The user counts
 * three (or four) distinct phrases, not one, and the tracker shows where they are.
 */
data class DhikrSequence(
    val key: String,
    val displayName: String,
    val steps: List<DhikrStep>
)
