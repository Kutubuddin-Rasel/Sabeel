package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * A single slice of the ring: how large a share of the daily plan this dhikr
 * accounts for ([target] as a share of the total), whether it's finished
 * today ([isComplete]), and its display [label] — used by [WirdRingLegend]
 * so a slice can be identified by name, not color alone (CT-02: the palette
 * repeats past a handful of items, and color-only encoding never works for
 * colorblind users regardless of how many hues are in the palette).
 *
 * Deliberately its own type rather than `WirdEditRow` or
 * [com.kutubuddin.sabeel.domain.model.WirdProgressItem] (DIP) — this file
 * should never need to know about either screen's model. [WirdScreen] and
 * `WirdEditScreen` each map their own rows into this shape.
 */
data class WirdRingSlice(
    val target: Int,
    val label: String = "",
    val isComplete: Boolean = true
)

/**
 * The "Visual Ring" (Concept 3).
 * A dynamic, data-driven visualization of the daily plan's composition.
 * Slices are sized by [WirdRingSlice.target] as a share of the total; an
 * incomplete slice is drawn at reduced opacity, so on the read-only screen
 * the same ring communicates both what the plan is made of *and* how much of
 * it is done, without a second progress element competing for attention. On
 * the Edit screen every slice is passed `isComplete = true` — allocation,
 * not daily progress, is the point there.
 *
 * [centerContent] lets each screen show whatever number matters to it (total
 * target on Edit, completed/total on the read-only screen) without this file
 * needing to know which — IA-01: pair it with a short caption at the call
 * site so the two screens' different numbers don't look like the same stat
 * disagreeing with itself.
 */
@Composable
fun WirdVisualRing(
    slices: List<WirdRingSlice>,
    modifier: Modifier = Modifier,
    centerContent: @Composable () -> Unit
) {
    val totalTarget = slices.sumOf { it.target }
    val ringColors = SabeelColors.RingPalette

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        if (slices.isEmpty() || totalTarget == 0) {
            val emptyColor = SabeelColors.BorderIdle
            Canvas(modifier = Modifier.size(140.dp)) {
                drawArc(
                    color = emptyColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        } else {
            Canvas(modifier = Modifier.size(140.dp)) {
                var currentStartAngle = -90f // Start from the top (12 o'clock)

                slices.forEachIndexed { index, slice ->
                    val sweep = (slice.target.toFloat() / totalTarget) * 360f
                    // Small gap between slices for visual separation; none
                    // needed when there's only one (it forms a full circle).
                    val gap = if (slices.size > 1) 4f else 0f

                    // coerceAtLeast(1f) ensures even a tiny target draws a
                    // visible sliver.
                    val actualSweep = (sweep - gap).coerceAtLeast(1f)

                    val baseColor = ringColors[index % ringColors.size]
                    val color = if (slice.isComplete) baseColor else baseColor.copy(alpha = 0.3f)

                    drawArc(
                        color = color,
                        startAngle = currentStartAngle + (gap / 2f),
                        sweepAngle = actualSweep,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )

                    currentStartAngle += sweep
                }
            }
        }

        centerContent()
    }
}

/**
 * Two-column legend of dot + name pairs, one per [WirdVisualRing] slice, in
 * the same index order the ring assigns colors — so a slice's dot here is
 * always the same color as its arc there.
 *
 * CT-02 fix: [SabeelColors.RingPalette] is finite and cycles via modulo past
 * its length, so a 6th/7th item necessarily repeats an earlier item's color.
 * Even at palette length, colorblind users can't reliably map hue → item.
 * Naming every slice here makes color a secondary cue instead of the only
 * way to identify one.
 */
@Composable
fun WirdRingLegend(
    slices: List<WirdRingSlice>,
    modifier: Modifier = Modifier
) {
    if (slices.isEmpty()) return
    val ringColors = SabeelColors.RingPalette

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        slices.withIndex().toList().chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { (index, slice) ->
                    LegendEntry(
                        color = ringColors[index % ringColors.size],
                        label = slice.label,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LegendEntry(color: androidx.compose.ui.graphics.Color, label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = SabeelColors.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
