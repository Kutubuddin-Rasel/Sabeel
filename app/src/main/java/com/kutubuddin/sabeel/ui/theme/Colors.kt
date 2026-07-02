package com.kutubuddin.sabeel.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Sabeel design tokens — theme-aware.
 *
 * Two value sets ("Sakīnah Night" dark + "Sakīnah Day" light) share one set of
 * token NAMES, so every call site is colour-agnostic: `SabeelColors.Background`
 * resolves per-composition to whichever set [SabeelTheme] provides.
 *
 * Design philosophy: a calm base (near-black / warm-paper), paradise-greens for
 * the everyday accent, and gold reserved as a RARE milestone accent. Text tokens
 * clear WCAG AA (4.5:1) on their background. Counter/Arabic use off-white, never
 * pure #FFF, to kill OLED halation.
 *
 * RULE: never reference raw Color(0xFF...) in composables — always a token.
 */
data class SabeelColorTokens(
    // Backgrounds
    val Background: Color,
    val Surface: Color,
    val SurfaceElevated: Color,
    // Accent system
    val AccentTeal: Color,
    val AccentTealBright: Color,
    val AccentTealSurface: Color,
    // Circle button
    val CircleEdge: Color,
    val CircleCenter: Color,
    val CircleBorder: Color,
    // Gold accent (RARE — milestones only)
    val GoldPrimary: Color,
    val GoldLuminous: Color,
    val GoldSurface: Color,
    // Text hierarchy
    val CounterWhite: Color,
    val ArabicText: Color,
    val TextPrimary: Color,
    val TextSecondary: Color,
    val TextHint: Color,
    // Semantic
    val SageGreen: Color,
    val StreakAmber: Color,
    val SmartFlowGold: Color,
    // Structural
    val ArcTrack: Color,
    val BorderActive: Color,
    val BorderIdle: Color,
    val WaveLapis: Color,
    val RewardBorder: Color,
    val Divider: Color
)

/** "Sakīnah Night" — near-black, OLED-friendly, zero halation. */
val DarkSabeelColors = SabeelColorTokens(
    Background        = Color(0xFF0E1311),
    Surface           = Color(0xFF161D1A),
    SurfaceElevated   = Color(0xFF1E2723),
    AccentTeal        = Color(0xFF4FA88B),
    AccentTealBright  = Color(0xFF6FC4A6),
    AccentTealSurface = Color(0x264FA88B),
    CircleEdge        = Color(0xFF243029),
    CircleCenter      = Color(0xFF131A17),
    CircleBorder      = Color(0xFF2C3A33),
    GoldPrimary       = Color(0xFFD9B26A),
    GoldLuminous      = Color(0xFFE5C77E),
    GoldSurface       = Color(0x26D9B26A),
    CounterWhite      = Color(0xFFE8E6DF),
    ArabicText        = Color(0xFFE8E6DF),
    TextPrimary       = Color(0xFFE8E6DF),
    TextSecondary     = Color(0xFFA8B0A8),
    TextHint          = Color(0xFF7E877E),
    SageGreen         = Color(0xFF8FB89E),
    StreakAmber       = Color(0xFF8FB89E),
    SmartFlowGold     = Color(0xFF4FA88B),
    ArcTrack          = Color(0xFF1E2723),
    BorderActive      = Color(0xFF4FA88B),
    BorderIdle        = Color(0xFF243029),
    WaveLapis         = Color(0xFF0A1F16),
    RewardBorder      = Color(0xFF4E7A6A),
    Divider           = Color(0xFF1E2723)
)

/** "Sakīnah Day" — warm parchment, greens + gold on calm cream. */
val LightSabeelColors = SabeelColorTokens(
    Background        = Color(0xFFF4F1E9),
    Surface           = Color(0xFFFBF9F3),
    SurfaceElevated   = Color(0xFFECE7DA),
    AccentTeal        = Color(0xFF2C6E5A),
    AccentTealBright  = Color(0xFF3E8E74),
    AccentTealSurface = Color(0x1A2C6E5A),
    CircleEdge        = Color(0xFFE7E2D4),
    CircleCenter      = Color(0xFFFBF9F3),
    CircleBorder      = Color(0xFFCFC8B8),
    GoldPrimary       = Color(0xFFB08D43),
    GoldLuminous      = Color(0xFFC6A459),
    GoldSurface       = Color(0x1AB08D43),
    CounterWhite      = Color(0xFF1C2421),
    ArabicText        = Color(0xFF1C2421),
    TextPrimary       = Color(0xFF1C2421),
    TextSecondary     = Color(0xFF4A5249),
    TextHint          = Color(0xFF6E776B),
    SageGreen         = Color(0xFF3C6E54),
    StreakAmber       = Color(0xFF3C6E54),
    SmartFlowGold     = Color(0xFF2C6E5A),
    ArcTrack          = Color(0xFFE0DACB),
    BorderActive      = Color(0xFF2C6E5A),
    BorderIdle        = Color(0xFFD8D2C2),
    WaveLapis         = Color(0xFFE6EFE6),
    RewardBorder      = Color(0xFF4E7A6A),
    Divider           = Color(0xFFE4DFD0)
)

/** Provided by [SabeelTheme]; defaults to the dark set. */
val LocalSabeelColors = staticCompositionLocalOf { DarkSabeelColors }

/**
 * Theme-aware accessor. Existing call sites (`SabeelColors.Background`) keep
 * working but now resolve to the active [SabeelColorTokens] set, exactly like
 * `MaterialTheme.colorScheme`.
 */
val SabeelColors: SabeelColorTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalSabeelColors.current
