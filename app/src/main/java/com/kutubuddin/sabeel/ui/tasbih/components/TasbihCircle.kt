package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import kotlinx.coroutines.launch

/**
 * The primary tap affordance for the Sabeel counting screen.
 *
 * Renders a large dark-glass circle with:
 * - A circular gold arc progress indicator (sweeps clockwise from top)
 * - A rolling odometer counter inside (white, large)
 * - "of {target}" sub-label
 * - Spring-physics press/release animation
 *
 * SRP: owns only its own visual rendering and press animation.
 *      All counting logic stays in TasbihViewModel.
 *
 * @param count         Current count value (0..target)
 * @param target        Target to complete (33, 34, 100, etc.)
 * @param onTap         Called on tap — increments count in ViewModel
 * @param diameter      Circle diameter; default 280.dp
 */
@Composable
fun TasbihCircle(
    count: Int,
    target: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    diameter: Dp = 280.dp,
    language: String = "en"
) {
    val coroutineScope = rememberCoroutineScope()
    val pressScale = remember { Animatable(1f) }

    // Hoist tokens read inside the (non-composable) Canvas draw lambda.
    val arcTrackColor = SabeelColors.ArcTrack
    val arcStartColor = SabeelColors.AccentTeal
    val arcEndColor = SabeelColors.AccentTealBright

    // Progress fraction — clamped to [0, 1]
    val progress = (count.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    // Animate sweep angle with spring physics
    val sweepAngle = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        sweepAngle.animateTo(
            targetValue = progress * 360f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(diameter + 24.dp) // Extra space for the arc ring
            .graphicsLayer {
                scaleX = pressScale.value
                scaleY = pressScale.value
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        coroutineScope.launch {
                            pressScale.animateTo(
                                0.96f,
                                spring(Spring.DampingRatioNoBouncy, Spring.StiffnessHigh)
                            )
                        }
                        val released = tryAwaitRelease()
                        coroutineScope.launch {
                            pressScale.animateTo(
                                if (released) 1.03f else 1f,
                                spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium)
                            )
                            pressScale.animateTo(
                                1f,
                                spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
                            )
                        }
                    },
                    onTap = { onTap() }
                )
            }
            .clearAndSetSemantics {
                contentDescription = "Count ${count.toLocalizedNumerals(language)} of " +
                    "${target.toLocalizedNumerals(language)}. Tap to count."
            }
    ) {
        // ── Layer 1: Arc track + gold progress arc ────────────────────────────
        Canvas(modifier = Modifier.size(diameter + 20.dp)) {
            val strokeWidth = 5.dp.toPx()
            val arcDiameter = size.minDimension - strokeWidth
            val topLeft = androidx.compose.ui.geometry.Offset(
                x = (size.width - arcDiameter) / 2f,
                y = (size.height - arcDiameter) / 2f
            )
            val arcSize = androidx.compose.ui.geometry.Size(arcDiameter, arcDiameter)

            // Track (full 360°)
            drawArc(
                color = arcTrackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc — teal (calm, everyday). Gold is reserved for the
            // milestone completion flash so it stays meaningful.
            if (sweepAngle.value > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colorStops = arrayOf(
                            0.0f to arcStartColor,
                            1.0f to arcEndColor
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // ── Layer 2: Dark glass circle ────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(diameter)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SabeelColors.CircleEdge,  // Center — slightly lighter
                            SabeelColors.CircleCenter  // Edge — deepest black
                        )
                    )
                )
                .border(
                    width = 0.5.dp,
                    color = SabeelColors.CircleBorder,
                    shape = CircleShape
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Counter — 3-digit zero-padded odometer
                OdometerCounter(
                    count = count,
                    language = language,
                    style = TextStyle(
                        color = SabeelColors.CounterWhite,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Medium  // Medium (not Bold) reduces OLED bloom at 80sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // "of 33" sub-label
                Text(
                    text = "of ${target.toLocalizedNumerals(language)}",
                    style = TextStyle(
                        color = SabeelColors.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}
