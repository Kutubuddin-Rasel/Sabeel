package com.kutubuddin.sabeel.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.R

/**
 * KFGQPC Uthmanic Script (Hafs) — the official font from the King Fahd
 * Complex for the Printing of the Holy Quran (qurancomplex.gov.sa).
 *
 * Bundle the .ttf file at: res/font/kfgqpc_uthmanic_hafs.ttf
 * Download (free, open license): https://fonts.qurancomplex.gov.sa/
 *
 * The Google Fonts fallback ("Amiri Quran") is used only if the local asset
 * is not found — this ensures offline functionality when the font is bundled.
 *
 * To activate the local font:
 * 1. Download KFGQPC Uthmanic Script Hafs.ttf from the URL above
 * 2. Place it at: app/src/main/res/font/kfgqpc_uthmanic_hafs.ttf
 * 3. Uncomment the Font(R.font.kfgqpc_uthmanic_hafs) line below
 * 4. Remove or comment out the GoogleFont fallback block
 */
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val UthmanicHafsFontFamily: FontFamily = try {
    // PRIMARY: KFGQPC Uthmanic Script Hafs — official font from the King Fahd Complex
    // File: app/src/main/res/font/kfgqpc_uthmanic_hafs.otf
    // Source: https://fonts.qurancomplex.gov.sa/ (free, open license)
    // Supported: API 26+ natively. For API 24–25 falls back to Google Fonts below.
    FontFamily(
        Font(R.font.kfgqpc_uthmanic_hafs, weight = FontWeight.Normal)
    )
} catch (e: Exception) {
    // FALLBACK for API 24–25 or missing asset: Google Fonts "Amiri Quran"
    FontFamily(
        Font(
            googleFont = GoogleFont("Amiri Quran"),
            fontProvider = provider,
            weight = FontWeight.Normal
        )
    )
}


/**
 * A single harmonic type scale. Screens should pull from
 * MaterialTheme.typography.* instead of hardcoding raw .sp values, so the
 * rhythm is predictable and the brain gets a consistent scanning grid.
 */
val Typography = Typography(
    // The 80sp counter — Medium (not Bold) to reduce OLED bloom.
    displayLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 80.sp,
        lineHeight = 84.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.8.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

/**
 * Dynamically scales the entire typography set for languages with smaller
 * optical sizes (like Urdu or Bengali) to maintain consistent readability.
 */
fun getScaledTypography(languageCode: String): Typography {
    val scale = when (languageCode) {
        "ur" -> 2
        "bn" -> 1
        else -> 0
    }
    
    if (scale == 0) return Typography

    return Typography(
        displayLarge = Typography.displayLarge.copy(
            fontSize = (Typography.displayLarge.fontSize.value + scale).sp,
            lineHeight = (Typography.displayLarge.lineHeight.value + scale).sp
        ),
        titleLarge = Typography.titleLarge.copy(
            fontSize = (Typography.titleLarge.fontSize.value + scale).sp,
            lineHeight = (Typography.titleLarge.lineHeight.value + scale).sp
        ),
        titleMedium = Typography.titleMedium.copy(
            fontSize = (Typography.titleMedium.fontSize.value + scale).sp,
            lineHeight = (Typography.titleMedium.lineHeight.value + scale).sp
        ),
        titleSmall = Typography.titleSmall.copy(
            fontSize = (Typography.titleSmall.fontSize.value + scale).sp,
            lineHeight = (Typography.titleSmall.lineHeight.value + scale).sp
        ),
        headlineSmall = Typography.headlineSmall.copy(
            fontSize = (Typography.headlineSmall.fontSize.value + scale).sp,
            lineHeight = (Typography.headlineSmall.lineHeight.value + scale).sp
        ),
        bodyLarge = Typography.bodyLarge.copy(
            fontSize = (Typography.bodyLarge.fontSize.value + scale).sp,
            lineHeight = (Typography.bodyLarge.lineHeight.value + scale).sp
        ),
        bodyMedium = Typography.bodyMedium.copy(
            fontSize = (Typography.bodyMedium.fontSize.value + scale).sp,
            lineHeight = (Typography.bodyMedium.lineHeight.value + scale).sp
        ),
        bodySmall = Typography.bodySmall.copy(
            fontSize = (Typography.bodySmall.fontSize.value + scale).sp,
            lineHeight = (Typography.bodySmall.lineHeight.value + scale).sp
        ),
        labelLarge = Typography.labelLarge.copy(
            fontSize = (Typography.labelLarge.fontSize.value + scale).sp,
            lineHeight = (Typography.labelLarge.lineHeight.value + scale).sp
        ),
        labelMedium = Typography.labelMedium.copy(
            fontSize = (Typography.labelMedium.fontSize.value + scale).sp,
            lineHeight = (Typography.labelMedium.lineHeight.value + scale).sp
        )
    )
}

/**
 * Arabic / Qur'anic text style. Uthmanic diacritics need ~1.9× leading or the
 * marks clip — a *trust* failure on sacred text, not just a visual one.
 * Always right-aligned (RTL). Use this everywhere Arabic is rendered.
 */
val arabicStyle = TextStyle(
    fontFamily = UthmanicHafsFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 42.sp
)