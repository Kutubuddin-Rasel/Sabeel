package com.kitalonlabs.sabeel.ui.tasbih.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kitalonlabs.sabeel.ui.theme.SabeelColors
import com.kitalonlabs.sabeel.ui.theme.arabicStyle

/**
 * Renders the current dhikr's Arabic and its Latin name.
 *
 * Takes plain strings rather than a [com.kitalonlabs.sabeel.domain.model.DhikrType]
 * so it can display any of the catalog entries — or an individual step inside a
 * Tasbīḥ-after-Salah sequence.
 *
 * FIX-2.2: Removed the internal Crossfade. This composable is always called from
 * inside [StaticDhikrInfo]'s AnimatedContent, which already owns the dhikr-change
 * transition (slide + fade). The previous Crossfade fired concurrently with the parent
 * AnimatedContent on every dhikr switch — two competing animations on the same content
 * caused a dropped frame on mid-range GPUs (Exynos 9611 on M21, Helio G96 on Redmi).
 * TajweedText is now a pure, stateless layout composable. Zero self-owned animation.
 *
 * PHASE-2.5 (Phonetic typography): The transliteration layer uses [dimmingPhoneticMarks]
 * to render `:` (long-vowel length marker) and `'` (pharyngeal/uvular marker) in
 * [SabeelColors.TextSecondary]. This keeps them as subtle guides rather than
 * visually dominant punctuation — especially important for dense strings like
 * Salawat Ibrahimiyyah that contain 8 colons and 6 apostrophes.
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
            // 36 x 1.9 = 68.4sp — diacritic safety rule: lineHeight must be >= 1.9x fontSize
            // so stacked tashkil marks never clip at the top of the line box.
            style = arabicStyle.copy(fontSize = 36.sp, lineHeight = 68.sp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (showTransliteration && transliteration != null && transliteration.isNotBlank()) {
            Text(
                text = dimmingPhoneticMarks(transliteration),
                style = MaterialTheme.typography.displaySmall,
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

/**
 * Returns an AnnotatedString where every phonetic guide character
 * (`:` for long vowels, `'` for pharyngeal/uvular consonants) is rendered
 * in [SabeelColors.TextSecondary], while all other characters keep
 * [SabeelColors.TextPrimary].
 *
 * Keeps Bangla transliteration readable at a glance: the diacritical guides
 * are visible but subordinate, matching academic phonetics convention.
 */
@Composable
fun dimmingPhoneticMarks(text: String) = buildAnnotatedString {
    val primaryColor = SpanStyle(color = SabeelColors.TextPrimary)
    val dimColor = SpanStyle(color = SabeelColors.TextSecondary)
    for (ch in text) {
        when (ch) {
            ':', '\'' -> {
                pushStyle(dimColor)
                append(ch)
                pop()
            }
            else -> {
                pushStyle(primaryColor)
                append(ch)
                pop()
            }
        }
    }
}
