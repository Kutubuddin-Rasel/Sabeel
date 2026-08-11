package com.kitalonlabs.sabeel.domain.model

/**
 * One countable phrase inside a [DhikrSequence] (e.g. "SubhanAllah × 33").
 *
 * A step is pure data — the counting engine ([TasbihViewModel.advance]) reads
 * [target] to decide when to move on, so adding a variant never touches engine code.
 */
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class DhikrStep(
    val arabicText: String,
    val displayName: LocalizedText,
    val transliteration: LocalizedText,
    val meaning: DhikrMeaning,
    val target: Int
)

/**
 * An ordered list of [DhikrStep]s counted one after another — the generic
 * replacement for the old hardcoded post-Salah state machine. The user counts
 * three (or four) distinct phrases, not one, and the tracker shows where they are.
 */
@Immutable
data class DhikrSequence(
    val key: String,
    val displayName: LocalizedText,
    val steps: ImmutableList<DhikrStep>
)
