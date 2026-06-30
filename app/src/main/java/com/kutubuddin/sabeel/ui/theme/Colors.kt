package com.kutubuddin.sabeel.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Sabeel design token system — "Mushaf Night" palette.
 *
 * Design philosophy: OLED true black base, antique gold accents,
 * white for maximum-contrast content. Inspired by illuminated
 * Quranic manuscripts: gold calligraphy on black void.
 *
 * RULE: Never reference raw Color(0xFF...) in composables.
 *       Always use these named tokens so the palette can be
 *       updated from one place.
 */
object SabeelColors {

    // ── Backgrounds ────────────────────────────────────────────────────────────
    /** True OLED black — zero pixel emission on OLED displays */
    val Background = Color(0xFF000000)

    /** Near-black surface for cards and sheets */
    val Surface = Color(0xFF0D0D0D)

    /** Elevated surface — subtle distinction from Surface */
    val SurfaceElevated = Color(0xFF141414)

    // ── Circle button ──────────────────────────────────────────────────────────
    /** Outer edge of the dark glass circle */
    val CircleEdge = Color(0xFF1C1C1C)

    /** Center of the circle (radial gradient center) */
    val CircleCenter = Color(0xFF0D0D0D)

    /** Border ring of the circle */
    val CircleBorder = Color(0xFF2A2A2A)

    // ── Gold accent system ──────────────────────────────────────────────────────
    /** Antique gold — primary brand color: title, selection borders, arc start */
    val GoldPrimary = Color(0xFFC9A84C)

    /** Luminous gold — arc fill end, active states, milestone flash */
    val GoldLuminous = Color(0xFFE8C547)

    /** Gold at 15% opacity — subtle tint for badge backgrounds */
    val GoldSurface = Color(0x26C9A84C)

    // ── Text hierarchy ──────────────────────────────────────────────────────────
    /** Counter digits — maximum contrast on OLED black */
    val CounterWhite = Color(0xFFFFFFFF)

    /** Arabic dhikr text — soft white, less harsh than pure white for long sessions */
    val ArabicText = Color(0xFFF0F0F0)

    /** English display names, section headers */
    val TextPrimary = Color(0xFFD4D4D4)

    /** Secondary labels, hints, "of 33" sub-labels */
    val TextSecondary = Color(0xFF888888)

    /** Ghost text — disabled, placeholder hints */
    val TextHint = Color(0xFF444444)

    // ── Semantic colors ─────────────────────────────────────────────────────────
    /** Hadith references and Spiritual Reward label */
    val SageGreen = Color(0xFF5A7A5A)

    /** Streak indicator */
    val StreakAmber = Color(0xFFFF8C42)

    /** Smart Flow active indicator */
    val SmartFlowGold = Color(0xFFC9A84C)

    // ── Structural ─────────────────────────────────────────────────────────────
    /** Arc track (unfilled progress ring) */
    val ArcTrack = Color(0xFF1E1E1E)

    /** Selected card left-border accent */
    val BorderActive = Color(0xFFC9A84C)

    /** Idle card border — near invisible */
    val BorderIdle = Color(0xFF1A1A1A)

    /** Subtle background blob (lapis blue, used at 6% opacity) */
    val WaveLapis = Color(0xFF0A1628)

    /** Spiritual Reward card left border */
    val RewardBorder = Color(0xFF5A7A5A)

    /** Divider lines */
    val Divider = Color(0xFF1E1E1E)
}
