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


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = UthmanicHafsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
)