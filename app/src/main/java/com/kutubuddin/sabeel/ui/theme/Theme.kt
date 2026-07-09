package com.kutubuddin.sabeel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import android.app.Activity
import androidx.core.view.WindowCompat

/**
 * "Sakīnah Night" — the real Sabeel scheme, built from [SabeelColors].
 *
 * Teal is the primary accent (M3 ripples, selection handles, switches all
 * inherit it); gold is demoted to [SabeelColors.GoldPrimary] for milestone
 * accents only. No leftover template purple, no wallpaper-driven dynamic color.
 */
private val SakinahDarkScheme = darkColorScheme(
    primary = DarkSabeelColors.AccentTeal,
    onPrimary = DarkSabeelColors.OnAccentTeal,
    secondary = DarkSabeelColors.GoldPrimary,
    onSecondary = DarkSabeelColors.OnGoldPrimary,
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
    onPrimary = LightSabeelColors.OnAccentTeal,
    secondary = LightSabeelColors.GoldPrimary,
    onSecondary = LightSabeelColors.OnGoldPrimary,
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
    language: String = "en",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SakinahDarkScheme else SakinahLightScheme
    val tokens = if (darkTheme) DarkSabeelColors else LightSabeelColors
    val typography = getScaledTypography(language)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalAbsoluteTonalElevation provides 0.dp,
        LocalSabeelColors provides tokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
