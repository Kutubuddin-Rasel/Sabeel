package com.kutubuddin.sabeel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * "Sakīnah Night" — the real Sabeel scheme, built from [SabeelColors].
 *
 * Teal is the primary accent (M3 ripples, selection handles, switches all
 * inherit it); gold is demoted to [SabeelColors.GoldPrimary] for milestone
 * accents only. No leftover template purple, no wallpaper-driven dynamic color.
 */
private val SakinahDarkScheme = darkColorScheme(
    primary = DarkSabeelColors.AccentTeal,
    onPrimary = DarkSabeelColors.Background,
    secondary = DarkSabeelColors.GoldPrimary,
    onSecondary = DarkSabeelColors.Background,
    tertiary = DarkSabeelColors.AccentTealBright,
    background = DarkSabeelColors.Background,
    onBackground = DarkSabeelColors.TextPrimary,
    surface = DarkSabeelColors.Surface,
    onSurface = DarkSabeelColors.TextPrimary,
    surfaceVariant = DarkSabeelColors.SurfaceElevated,
    onSurfaceVariant = DarkSabeelColors.TextSecondary,
    outline = DarkSabeelColors.BorderIdle
)

/**
 * "Sakīnah Day" — warm-paper light scheme, sourced from [LightSabeelColors] so
 * the Settings theme toggle is a real choice, not a fall-through to purple.
 */
private val SakinahLightScheme = lightColorScheme(
    primary = LightSabeelColors.AccentTeal,
    onPrimary = Color(0xFFEAF3EE),
    secondary = LightSabeelColors.GoldPrimary,
    onSecondary = Color(0xFFFBF6EA),
    tertiary = LightSabeelColors.RewardBorder,
    background = LightSabeelColors.Background,
    onBackground = LightSabeelColors.TextPrimary,
    surface = LightSabeelColors.Surface,
    onSurface = LightSabeelColors.TextPrimary,
    surfaceVariant = LightSabeelColors.SurfaceElevated,
    onSurfaceVariant = LightSabeelColors.TextSecondary,
    outline = LightSabeelColors.BorderIdle
)

@Composable
fun SabeelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // No dynamicColor: the brand identity is fixed, never wallpaper-tinted.
    val colorScheme = if (darkTheme) SakinahDarkScheme else SakinahLightScheme
    val tokens = if (darkTheme) DarkSabeelColors else LightSabeelColors

    CompositionLocalProvider(
        LocalAbsoluteTonalElevation provides 0.dp,
        LocalSabeelColors provides tokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
