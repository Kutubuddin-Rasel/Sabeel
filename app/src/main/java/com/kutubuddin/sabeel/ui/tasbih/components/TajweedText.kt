package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
 * Tasbīḥ-after-Salah sequence. Crossfades on the Arabic text as the content changes.
 */
@Composable
fun TajweedText(
    arabicText: String,
    showTransliteration: Boolean = true,
    transliteration: String? = null,
    meaning: String? = null,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = Triple(arabicText, transliteration, meaning),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        modifier = modifier,
        label = "TajweedTextCrossfade"
    ) { (arabic, trans, mn) ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = arabic,
                color = SabeelColors.ArabicText,
                textAlign = TextAlign.Center,
                style = arabicStyle.copy(fontSize = 36.sp, lineHeight = 68.sp), // 36 × 1.9 = 68.4sp — diacritic safety rule
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (showTransliteration && trans != null && trans.isNotBlank()) {
                Text(
                    text = trans,
                    style = MaterialTheme.typography.displaySmall,
                    color = SabeelColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (mn != null && mn.isNotBlank()) {
                Text(
                    text = mn,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SabeelColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
