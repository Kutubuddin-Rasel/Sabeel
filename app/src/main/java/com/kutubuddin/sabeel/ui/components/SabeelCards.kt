package com.kutubuddin.sabeel.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * AccentCard — hero / CTA surfaces.
 *
 * 18dp radius, AccentTealSurface (or a custom [accentColor].copy(0.1)) background,
 * matching tinted border. Always clickable.
 *
 * Press-scale: dips to 0.96 on finger-down, springs back on release.
 * Spring spec: DampingRatioMediumBouncy so the release has a tiny overshoot
 * that communicates "the button responded" without being distracting.
 *
 * Used for: HeroStartCard, SmartPlayCard.
 * GoalCompleteCard uses [accentColor] = SabeelColors.SageGreen.
 */
@Composable
fun AccentCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = SabeelColors.AccentTeal,
    content: @Composable ColumnScope.() -> Unit
) {
    // Interaction source tracks press state so we can derive scale from it.
    // Using collectIsPressedAsState + animateFloatAsState is the correct
    // pattern for cards: unlike Animatable it never needs snapTo() because
    // the animation timeline is determined entirely by the Boolean state
    // (pressed = true/false), not by rapid sequential events.
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        // Medium-bouncy spring: natural release "pop" that confirms the tap
        // without feeling like a game button. Dip is instant (pressed=true
        // is set in the same frame as the pointer-down event).
        animationSpec = spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness    = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "accent_card_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(18.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,  // Scale IS the feedback — standard ripple would double up
                onClick = onClick
            )
            .padding(24.dp),
        content = content
    )
}

/**
 * InfoCard — read-only stat / dashboard panels.
 *
 * 16dp radius, plain Surface, no border, not clickable.
 * Used for: AllTimeCard, FirstTimeEncouragementCard, StreakGoalCard (info half).
 */
@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .padding(20.dp),
        content = content
    )
}

/**
 * ListItemCard — browsable / expandable rows.
 *
 * 16dp radius, Surface (idle) / SurfaceElevated (expanded), always bordered.
 * Border sharpens to AccentTeal alpha when [isExpanded] to signal open state.
 *
 * Used for: DhikrCard, WirdItemRow, WirdEdit rows, SessionRow.
 */
@Composable
fun ListItemCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val bg = if (isExpanded) SabeelColors.SurfaceElevated else SabeelColors.Surface
    val borderColor = if (isExpanded) SabeelColors.AccentTeal.copy(alpha = 0.60f)
                      else SabeelColors.BorderIdle
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        content = content
    )
}
