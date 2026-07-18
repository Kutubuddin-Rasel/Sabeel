package com.kutubuddin.sabeel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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

    Text(
        text = "${count.toLocalizedNumerals(language)} / ${target.toLocalizedNumerals(language)}",
        style = style.copy(color = textColor)
    )
}
