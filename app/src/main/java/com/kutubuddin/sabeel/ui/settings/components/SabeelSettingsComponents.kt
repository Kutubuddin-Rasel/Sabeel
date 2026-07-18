package com.kutubuddin.sabeel.ui.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import kotlinx.collections.immutable.ImmutableList
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.graphicsLayer
import com.kutubuddin.sabeel.ui.theme.SabeelMotion

/**
 * A grouped container for settings rows.
 *
 * Provides the 16dp rounded corners and Surface background.
 * Rows passed in [content] are responsible for their own internal padding
 * and dividers (via [SettingsBaseRow]).
 */
@Composable
fun SabeelSettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .border(1.dp, SabeelColors.BorderIdle, RoundedCornerShape(16.dp)),
        content = content
    )
}

/**
 * Base row for settings items.
 *
 * Features:
 * - Leading icon in a SurfaceElevated box.
 * - Title and optional description.
 * - Hairline divider at the top (enabled by default, usually disabled for first row).
 */
@Composable
fun SettingsBaseRow(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp),
                thickness = 0.5.dp,
                color = SabeelColors.Divider
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(SabeelColors.SurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SabeelColors.TextSecondary
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = SabeelColors.TextPrimary
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SabeelColors.TextHint,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            if (action != null) {
                action()
            }
        }
    }
}

@Immutable
data class SegmentOption(
    val id: String,
    val display: String,
    val icon: ImageVector? = null
)

@Composable
fun SettingsSegmentRow(
    options: ImmutableList<SegmentOption>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 13.dp, top = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        options.forEach { option ->
            val isSelected = selected == option.id
            Box(
                modifier = Modifier
                    .weight(1f)
                    .width(0.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) SabeelColors.AccentTeal else SabeelColors.SurfaceElevated)
                    .clickable { onSelect(option.id) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (option.icon != null) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.display,
                        modifier = Modifier.size(20.dp),
                        tint = if (isSelected) SabeelColors.OnAccentTeal else SabeelColors.TextSecondary
                    )
                } else {
                    Text(
                        text = option.display,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = if (isSelected) SabeelColors.OnAccentTeal else SabeelColors.TextSecondary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Custom switch matching the mockup's 42x25px proportions and ON-only checkmark.
 */
@Composable
fun SabeelSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbPosition by animateFloatAsState(
        targetValue = if (checked) 19f else 2f,
        animationSpec = com.kutubuddin.sabeel.ui.theme.SabeelMotion.Spring.SwitchSnap,
        label = "switch_thumb_pos"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) SabeelColors.AccentTeal else SabeelColors.SurfaceElevated,
        animationSpec = com.kutubuddin.sabeel.ui.theme.SabeelMotion.Tween.ColorTransition,
        label = "switch_track_color"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) SabeelColors.OnAccentTeal else SabeelColors.TextSecondary,
        animationSpec = com.kutubuddin.sabeel.ui.theme.SabeelMotion.Tween.ColorTransition,
        label = "switch_thumb_color"
    )

    Box(
        modifier = modifier
            .size(width = 42.dp, height = 25.dp)
            .clip(CircleShape)
            .background(trackColor)
            .border(
                width = 1.dp,
                color = if (checked) SabeelColors.AccentTeal else SabeelColors.BorderIdle,
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { translationX = thumbPosition.dp.toPx() }
                .size(19.dp)
                .clip(CircleShape)
                .background(thumbColor),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                    tint = SabeelColors.AccentTeal
                )
            }
        }
    }
}
