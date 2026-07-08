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
    // Structural
    val ArcTrack: Color,
    val BorderActive: Color,
    val BorderIdle: Color,
    val WaveLapis: Color,
    val RewardBorder: Color,
    val Divider: Color,
    // Destructive actions (delete, remove) — its own token so it gets a
    // properly tuned Light/Dark pair instead of a raw hex at the call site.
    val Danger: Color,
    // "On" colors for the two brand accents used as a solid fill (e.g.
    // MaterialTheme's onPrimary/onSecondary) — named tokens instead of raw
    // hex so both themes get the same verification discipline as everything
    // else in this file.
    val OnAccentTeal: Color,
    val OnGoldPrimary: Color,
    // Data Visualization
    val RingPalette: List<Color>
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
    ArcTrack          = Color(0xFF1E2723),
    BorderActive      = Color(0xFF4FA88B),
    BorderIdle        = Color(0xFF243029),
    WaveLapis         = Color(0xFF0A1F16),
    RewardBorder      = Color(0xFF4E7A6A),
    Divider           = Color(0xFF1E2723),
    // ~5.7:1 against dark Surface (#161D1A) — unchanged value, just named
    // now instead of being a raw hex at the WirdEditScreen call site.
    Danger            = Color(0xFFE57373),
    OnAccentTeal      = Color(0xFF0E1311), // = Background
    OnGoldPrimary     = Color(0xFF0E1311), // = Background
    // Spread across the wheel (teal, terracotta, slate blue, plum) instead of
    // four shades of green — with 4 items (the common case, see the wird
    // screenshots) every slice now reads as visually distinct. Gold sits 5th
    // so it doesn't crowd that common case. Each value tuned independently
    // for contrast against Background (#0E1311) at ~50-70% lightness, rather
    // than reused from other semantic tokens.
    // 7 distinct hues so a 7-item wird (the common case) never repeats a
    // color via modulo (the old 5-color list did). Terracotta is gone: it
    // read as a near-universal "warning" hue and, by coincidence of list
    // order, often landed on the largest slice. Color is now a secondary
    // cue anyway — WirdRingLegend labels every slice by name.
    RingPalette       = listOf(
        Color(0xFF4FA88B), // teal
        Color(0xFF7798C5), // slate blue
        Color(0xFFB474AF), // plum
        Color(0xFFD9B26A), // gold
        Color(0xFFC98CA0), // dusty rose
        Color(0xFF6FA5A0), // deep aqua
        Color(0xFFB3A26A)  // warm sand
    )
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
    ArcTrack          = Color(0xFFE0DACB),
    BorderActive      = Color(0xFF2C6E5A),
    BorderIdle        = Color(0xFFD8D2C2),
    WaveLapis         = Color(0xFFE6EFE6),
    RewardBorder      = Color(0xFF4E7A6A),
    Divider           = Color(0xFFE4DFD0),
    // ~5.3:1 against light Surface (#FBF9F3). The old raw hex here was
    // literally the dark-theme value (0xFFE57373), which only computed
    // ~2.8:1 in light mode — below the 3:1 floor for icons (WCAG 1.4.11).
    Danger            = Color(0xFFB3413A),
    OnAccentTeal      = Color(0xFFEAF3EE), // was inlined in Theme.kt
    OnGoldPrimary     = Color(0xFFFBF6EA), // was inlined in Theme.kt
    // Same identities as the dark set, retuned to ~30-45% lightness so they
    // hold contrast against the warm cream Background (#F4F1E9) instead of
    // washing out the way pale greens would.
    RingPalette       = listOf(
        Color(0xFF2C6E5A), // teal
        Color(0xFF355A8C), // slate blue
        Color(0xFF7F3979), // plum
        Color(0xFFB08D43), // gold
        Color(0xFF8C4B5C), // dusty rose
        Color(0xFF2E6E68), // deep aqua
        Color(0xFF7A6A3A)  // warm sand
    )
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