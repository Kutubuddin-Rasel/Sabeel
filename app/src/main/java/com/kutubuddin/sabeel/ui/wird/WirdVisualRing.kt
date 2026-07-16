package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import kotlinx.collections.immutable.ImmutableList

/**
 * FIX 7 — a tasbih string, not an activity ring.
 *
 * The old visual was a multi-hue segmented donut (an Apple/Fitbit "close your
 * rings" metaphor) that reframed worship as a fitness KPI. This replaces it
 * with two calm, single-hue elements:
 *
 * 1. **One [SabeelColors.AccentTeal] arc** over [SabeelColors.ArcTrack], swept
 *    by the completed fraction of [itemStates] — overall progress at a glance.
 * 2. **A row of bead dots**, one per item — teal-filled = done, hollow ring =
 *    pending. Because the encoding is presence/absence (not hue), it needs no
 *    colour legend and works for colourblind users by construction (retires the
 *    old CT-02 palette/legend problem entirely).
 *
 * [centerContent] lets each screen place whatever number matters to it
 * (completed/total on the read-only screen, target total on Edit) inside the
 * arc. On Edit every item is passed as complete, so the arc reads full and the
 * beads all fill — allocation, not daily progress, is the point there.
 */
@Composable
fun WirdVisualRing(
    itemStates: ImmutableList<Boolean>,
    modifier: Modifier = Modifier,
    centerContent: @Composable () -> Unit
) {
    val fraction =
        if (itemStates.isEmpty()) 0f
        else itemStates.count { it }.toFloat() / itemStates.size

    val trackColor = SabeelColors.ArcTrack
    val arcColor = SabeelColors.AccentTeal

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val strokeWidth = androidx.compose.runtime.remember(density) { with(density) { 10.dp.toPx() } }
        val strokeStyle = androidx.compose.runtime.remember(strokeWidth) { Stroke(width = strokeWidth, cap = StrokeCap.Round) }
        
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = strokeStyle
                )
                if (fraction > 0f) {
                    drawArc(
                        color = arcColor,
                        startAngle = -90f,
                        sweepAngle = 360f * fraction,
                        useCenter = false,
                        style = strokeStyle
                    )
                }
            }
            centerContent()
        }

        if (itemStates.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            BeadRow(states = itemStates)
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/**
 * The tasbih beads: one small dot per wird item. [FlowRow] wraps to a second
 * line so a long wird (many items) never overflows the width.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BeadRow(states: ImmutableList<Boolean>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        states.forEach { BeadDot(filled = it) }
    }
}

@Composable
private fun BeadDot(filled: Boolean) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .then(
                if (filled) Modifier.background(SabeelColors.AccentTeal)
                else Modifier.border(1.5.dp, SabeelColors.BorderIdle, CircleShape)
            )
    )
}
