package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * Segmented progress indicator for a multi-step Tasbīḥ-after-Salah sequence.
 *
 * A pure function of the cursor position — one segment per step:
 * - completed (`i < stepIndex`)  → teal tick
 * - current   (`i == stepIndex`) → enlarged filled teal dot
 * - upcoming  (`i > stepIndex`)  → idle outlined dot
 *
 * Conveys *position* only; the current step's Arabic/name is rendered by
 * [TajweedText] directly below, so no per-dot labels are duplicated here.
 */
@Composable
fun SequenceTracker(
    stepIndex: Int,
    stepCount: Int,
    language: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Step ${(stepIndex + 1).toLocalizedNumerals(language)} of " +
                stepCount.toLocalizedNumerals(language),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SabeelColors.TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(stepCount) { i ->
                SequenceSegment(
                    completed = i < stepIndex,
                    current = i == stepIndex
                )
            }
        }
    }
}

@Composable
private fun SequenceSegment(completed: Boolean, current: Boolean) {
    // Current step swells to a filled dot; completed shows a tick; upcoming is idle.
    val dotSize by animateDpAsState(
        targetValue = if (current) 12.dp else 8.dp,
        animationSpec = tween(250),
        label = "segmentSize"
    )
    val fill by animateColorAsState(
        targetValue = if (completed || current) SabeelColors.AccentTeal else SabeelColors.Surface,
        animationSpec = tween(250),
        label = "segmentFill"
    )

    Box(
        modifier = Modifier.size(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (completed) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = SabeelColors.AccentTeal,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(fill)
                    .border(1.dp, if (current) SabeelColors.AccentTeal else SabeelColors.BorderIdle, CircleShape)
            )
        }
    }
}
