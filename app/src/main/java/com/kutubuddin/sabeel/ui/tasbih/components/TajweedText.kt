package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

/**
 * Renders the current dhikr's Arabic and its Latin name.
 *
 * Takes plain strings rather than a [com.kutubuddin.sabeel.domain.model.DhikrType]
 * so it can display any of the catalog entries — or an individual step inside a
 * Tasbīḥ-after-Salah sequence.
 *
 * FIX-2.2: Removed the internal Crossfade. This composable is always called from
 * inside [StaticDhikrInfo]'s AnimatedContent, which already owns the dhikr-change
 * transition (slide + fade). The previous Crossfade fired concurrently with the parent
 * AnimatedContent on every dhikr switch — two competing animations on the same content
 * caused a dropped frame on mid-range GPUs (Exynos 9611 on M21, Helio G96 on Redmi).
 * TajweedText is now a pure, stateless layout composable. Zero self-owned animation.
 */
@Composable
fun TajweedText(
    arabicText: String,
    showTransliteration: Boolean = true,
    transliteration: String? = null,
    meaning: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = arabicText,
            color = SabeelColors.ArabicText,
            textAlign = TextAlign.Center,
            // 36 × 1.9 = 68.4sp — diacritic safety rule: lineHeight must be ≥ 1.9× fontSize
            // so stacked tashkil marks never clip at the top of the line box.
            style = arabicStyle.copy(fontSize = 36.sp, lineHeight = 68.sp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (showTransliteration && transliteration != null && transliteration.isNotBlank()) {
            Text(
                text = transliteration,
                style = MaterialTheme.typography.displaySmall,
                color = SabeelColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        if (meaning != null && meaning.isNotBlank()) {
            Text(
                text = meaning,
                // FIX-FLAW10: bodyMedium (14sp) instead of bodyLarge (16sp) prevents
                // long meanings from wrapping to 2 lines on M21's 363dp usable width.
                style = MaterialTheme.typography.bodyMedium,
                color = SabeelColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
