package com.kutubuddin.sabeel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.SabeelMotion

/**
 * Canonical section header used across the app.
 *
 * Renders an ALL-CAPS overline label using [MaterialTheme.typography.labelMedium]
 * with configurable [letterSpacing] so each screen's visual rhythm is preserved
 * without forking a separate composable:
 * - Home screen uses 1.5sp (a lighter cadence matching the card-heavy layout)
 * - Settings and Dhikr library use 1.8sp (tighter, more structured feel)
 *
 * Previously duplicated as `SectionHeader` (private in HomeScreen) and
 * `SettingsHeader` (private in SettingsScreen), and as the category label
 * inline in DhikrLibraryScreen. All three now point here.
 */
@Composable
fun SabeelSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    letterSpacing: Dp = 1.5.dp
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = letterSpacing.value.sp),
        color = SabeelColors.TextSecondary,
        modifier = modifier
    )
}

/**
 * Sticky list header for categorised library / picker lists.
 *
 * Renders the same labelMedium overline as [SabeelSectionHeader] but also
 * fills the full row width with a Background-colored backdrop so it reads
 * cleanly over scrolling content when used as a `stickyHeader {}` in a
 * [androidx.compose.foundation.lazy.LazyColumn].
 */
@Composable
fun CategoryHeader(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.8.sp),
        color = SabeelColors.TextSecondary,
        modifier = modifier
            .fillMaxWidth()
            .background(SabeelColors.Background)
            .padding(vertical = 8.dp)
    )
}

/**
 * Collapsible section header with expand/collapse chevron.
 *
 * Wraps [SabeelSectionHeader] with a clickable [Row] and an animated chevron.
 * Used on Home for the "Today's Sessions" section and Settings for "Advanced".
 */
@Composable
fun CollapsibleSectionHeader(
    text: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SabeelSectionHeader(text)
        // Previously swapped between two different vector assets (ExpandLess /
        // ExpandMore) on every click — an instant, unanimated cut. Now a single
        // icon rotates 0°→180°, same spring + same visual language as the
        // DhikrCard chevron (DhikrLibraryScreen.kt), so every disclosure
        // indicator in the app moves the same way.
        val rotation by animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = SabeelMotion.Spring.ChevronRotate,
            label = "section_chevron_rotation"
        )
        Icon(
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = SabeelColors.TextSecondary,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer { rotationZ = rotation }
        )
    }
}
