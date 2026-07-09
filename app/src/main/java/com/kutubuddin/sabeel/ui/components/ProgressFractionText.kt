package com.kutubuddin.sabeel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * Renders a "count / target" reps figure the same way everywhere it appears
 * (Today's Wird rows, the Home resume card, ...) instead of each call site
 * interpolating its own raw string (the old pattern — see WirdItemRow and
 * ResumeCard before this file existed).
 *
 * DT-01 fix: dhikr counts can legitimately exceed target (looping a wird
 * more than once in a sitting is completely normal), but a raw fraction like
 * "102/34" reads as broken math, not "completed 3 times over" — especially
 * for tasbih counts, where 33, 100 etc. are specific, meaningful numbers.
 * When count > target, the fraction is capped at target/target and the
 * overflow is carried by a small gold "×N" badge instead — gold because
 * completing extra rounds is genuinely the kind of milestone moment
 * [SabeelColors.GoldPrimary] is reserved for elsewhere in this app.
 */
@Composable
fun ProgressFractionText(
    count: Int,
    target: Int,
    language: String,
    isComplete: Boolean,
    style: TextStyle,
    normalColor: Color,
    completeColor: Color
) {
    val textColor = if (isComplete) completeColor else normalColor

    if (target > 0 && count > target) {
        val rounds = count / target
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${target.toLocalizedNumerals(language)} / ${target.toLocalizedNumerals(language)}",
                style = style.copy(color = textColor)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SabeelColors.GoldSurface)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "×${rounds.toLocalizedNumerals(language)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SabeelColors.GoldPrimary
                )
            }
        }
    } else {
        Text(
            text = "${count.toLocalizedNumerals(language)} / ${target.toLocalizedNumerals(language)}",
            style = style.copy(color = textColor)
        )
    }
}
