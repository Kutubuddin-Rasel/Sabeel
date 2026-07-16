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
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SabeelColors.TextPrimary
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp
                        ),
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

/**
 * A 4-dot intensity stepper for haptic magnitude.
 */
@Composable
fun SabeelIntensityStepper(
    currentLevel: String,
    onLevelSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf("off", "light", "medium", "strong")
    val levelLabels = mapOf(
        "off" to "Off",
        "light" to "Light",
        "medium" to "Medium",
        "strong" to "Strong"
    )
    val currentIndex = levels.indexOf(currentLevel).coerceAtLeast(0)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        levels.indices.forEach { index ->
            val isOn = index <= currentIndex && currentLevel != "off"
            val color by animateColorAsState(
                targetValue = if (isOn) SabeelColors.AccentTeal else SabeelColors.SurfaceElevated,
                label = "stepper_dot_color"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onLevelSelected(levels[index]) }
            )
        }
        Text(
            text = levelLabels[currentLevel] ?: "Medium",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = SabeelColors.TextSecondary,
            modifier = Modifier.widthIn(min = 52.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Right
        )
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
        label = "switch_thumb_pos"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) SabeelColors.AccentTeal else SabeelColors.SurfaceElevated,
        label = "switch_track_color"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) SabeelColors.OnAccentTeal else SabeelColors.TextSecondary,
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
                .offset(x = thumbPosition.dp)
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
