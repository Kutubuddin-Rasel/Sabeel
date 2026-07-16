package com.kutubuddin.sabeel.ui.wird

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * Discrete, per-wird progress: one segment per item in the plan, filled once
 * that item is complete.
 *
 * This replaces the old `countedSum / targetSum` continuous fraction used on
 * both [com.kutubuddin.sabeel.ui.home.HomeScreen]'s summary card and
 * [WirdScreen] — that ratio is a *different number* than the "completed of
 * total" text shown alongside it, so the two could visually disagree (e.g.
 * "0 of 4 done" next to a bar that looks 80% full). A segmented bar can only
 * ever show the same [completed]/[total] the text already states.
 */
@Composable
fun WirdSegmentedProgress(
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier,
    filledColor: Color = SabeelColors.AccentTeal,
    trackColor: Color = SabeelColors.ArcTrack
) {
    if (total <= 0) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(total) { index ->
            val isFilled = index < completed
            val color by animateColorAsState(
                targetValue = if (isFilled) filledColor else trackColor,
                animationSpec = com.kutubuddin.sabeel.ui.theme.SabeelMotion.Tween.ColorTransition,
                label = "wird_segment_$index"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}