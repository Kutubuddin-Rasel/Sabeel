package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.SabeelMotion
import kotlinx.coroutines.launch

/**
 * The visual anchor for the Sabeel counting screen.
 *
 * Renders a large dark-glass circle with:
 * - A circular teal arc progress indicator (sweeps clockwise from top)
 * - A rolling odometer counter inside (white, large)
 * - "of {target}" sub-label
 *
 * Animation layers (all self-contained, no state hoisted to the parent):
 *  1. Odometer digit roll    — AnimatedContent spring on each count change
 *  2. Arc sweep              — Animatable spring on each count change
 *  3. Press-scale dip+bounce — Animatable spring on finger-down / release
 *  4. Expanding ring pulse   — Animatable tween on tap confirm (onTap fires)
 *
 * Tap the circle to increment; long-press to reset.
 *
 * @param count       Current count value (0..target)
 * @param target      Target to complete (33, 34, 100, etc.)
 * @param onTap       Called on each confirmed tap to increment the count.
 * @param onLongPress Called on long-press to reset. Optional.
 * @param scale       Reserved for external override (defaults to 1f; internal
 *                    pressScale takes precedence via graphicsLayer).
 * @param diameter    Circle diameter; default 280.dp
 */
@Composable
fun TasbihCircle(
    count: Int,
    target: Int,
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    diameter: Dp = 280.dp,
    language: String = "en"
) {
    val strings = LocalStrings.current
    val scope   = rememberCoroutineScope()

    // ── Hoist color tokens — read inside non-composable Canvas lambdas ────────
    val arcTrackColor   = SabeelColors.ArcTrack
    val arcStartColor   = SabeelColors.AccentTeal
    val arcEndColor     = SabeelColors.AccentTealBright

    // ── Layer 2: arc sweep — spring-driven, phase-locked with odometer ────────
    val progress   = (count.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val sweepAngle = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        sweepAngle.animateTo(
            targetValue   = progress * 360f,
            // POLISH-01: uses SabeelMotion.Spring.Counter token — critically damped,
            // no overshoot. See Motion.kt for full tuning rationale.
            animationSpec = SabeelMotion.Spring.Counter
        )
    }

    // ── Layer 3: press-scale — dip on finger-down, spring back on release ─────
    val pressScale = remember { Animatable(1f) }

    // ── Layer 4: expanding ring pulse — radius grows, alpha fades on tap ──────
    // Two independent Animatables so we can tune their curves separately.
    val ringRadius = remember { Animatable(0f) }
    val ringAlpha  = remember { Animatable(0f) }
    // Convert diameter to px once; used as the ring's max radius target.
    // JANK-02: diameter is a constant (280.dp); reading LocalDensity without remember
    // caused a redundant toPx() call on every recomposition (every tap).
    val density = androidx.compose.ui.platform.LocalDensity.current
    val diameterPx = remember(diameter) { with(density) { diameter.toPx() } }
    val ringMaxRadius = diameterPx / 2f

    // ── Pre-allocate brushes — avoid per-frame allocation in draw phase ────────
    val sweepGradient = remember(arcStartColor, arcEndColor) {
        Brush.sweepGradient(
            colorStops = arrayOf(
                0.0f to arcStartColor,
                1.0f to arcEndColor
            )
        )
    }
    val circleEdgeColor   = SabeelColors.CircleEdge
    val circleCenterColor = SabeelColors.CircleCenter
    val radialGradient = remember(circleEdgeColor, circleCenterColor) {
        Brush.radialGradient(
            colors = listOf(
                circleEdgeColor,   // centre — slightly lighter
                circleCenterColor  // edge   — deepest black
            )
        )
    }

    // ── SMOOTH-02+07: hoisted Job refs for ring pulse and press scale ────────
    // Without explicit Job tracking, rapid tapping (5 taps/sec) accumulated
    // 15+ concurrent coroutines competing for the ring Animatable. snapTo only
    // cancelled the outer scope, not the two inner launch children.
    val ringJob  = remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(diameter + 24.dp)   // extra room so the ring pulse isn't clipped
            .graphicsLayer {
                // Layer 3 owns scaleX/Y; the external `scale` param is a no-op
                // unless the caller explicitly overrides (no current caller does).
                scaleX = pressScale.value * scale
                scaleY = pressScale.value * scale
            }
            // ── Gesture handler ───────────────────────────────────────────────
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { _ ->
                        // Layer 3a — dip immediately on finger-down.
                        val downJob = scope.launch {
                            pressScale.animateTo(
                                targetValue   = 0.93f,
                                // SabeelMotion.Spring.PressFeedback: NoBouncy+High — instantaneous dip.
                                animationSpec = SabeelMotion.Spring.PressFeedback
                            )
                        }
                        tryAwaitRelease()
                        // Cancel dip animation before spring-back — prevents the two
                        // coroutines from fighting over pressScale mid-gesture.
                        downJob.cancel()
                        // Layer 3b — spring back (mild bounce = "releasing a bead")
                        scope.launch {
                            pressScale.animateTo(
                                targetValue   = 1f,
                                // SabeelMotion.Spring.PressRelease: 0.45 damping, one gentle rebound.
                                animationSpec = SabeelMotion.Spring.PressRelease
                            )
                        }
                    },
                    onTap = { _ ->
                        onTap()
                        // SMOOTH-02: cancel any in-flight ring before starting a new one.
                        // Previous approach used snapTo which only cancelled the outer Job,
                        // leaving the two inner launch children still running.
                        ringJob.value?.cancel()
                        ringJob.value = scope.launch {
                            ringRadius.snapTo(0f)
                            ringAlpha.snapTo(0.55f)
                            launch {
                                ringRadius.animateTo(
                                    targetValue   = ringMaxRadius,
                                    // SabeelMotion.Duration.RingExpand: 600ms — extracted token.
                                    animationSpec = tween(
                                        durationMillis = SabeelMotion.Duration.RingExpand,
                                        easing         = FastOutSlowInEasing
                                    )
                                )
                            }
                            launch {
                                ringAlpha.animateTo(
                                    targetValue   = 0f,
                                    animationSpec = tween(
                                        durationMillis = SabeelMotion.Duration.RingFade,
                                        easing         = LinearEasing
                                    )
                                )
                            }
                        }
                    },
                    onLongPress = { _ -> onLongPress?.invoke() }
                )
            }
            .clearAndSetSemantics {
                contentDescription = strings.countCircleA11y.format(
                    count.toLocalizedNumerals(language),
                    target.toLocalizedNumerals(language)
                )
                onClick(label = strings.countIncrementA11y) { onTap(); true }
            }
    ) {
        // ── Canvas: arc track + progress arc + ring pulse (Layers 1, 2, 4) ────
        Canvas(modifier = Modifier.size(diameter + 20.dp)) {
            val strokeWidth = 5.dp.toPx()
            val arcDiameter = size.minDimension - strokeWidth
            val topLeft = androidx.compose.ui.geometry.Offset(
                x = (size.width  - arcDiameter) / 2f,
                y = (size.height - arcDiameter) / 2f
            )
            val arcSize = androidx.compose.ui.geometry.Size(arcDiameter, arcDiameter)

            // Layer 1 — track (full 360° dim circle)
            drawArc(
                color      = arcTrackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Layer 2 — progress arc (teal; gold reserved for milestone flash)
            if (sweepAngle.value > 0f) {
                drawArc(
                    brush      = sweepGradient,
                    startAngle = -90f,
                    sweepAngle = sweepAngle.value,
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Layer 4 — expanding ring pulse (AccentTeal ring, fades to 0)
            // Drawn on top of the arc so it's clearly visible.
            if (ringAlpha.value > 0f) {
                drawCircle(
                    color  = arcStartColor.copy(alpha = ringAlpha.value),
                    radius = ringRadius.value,
                    style  = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // ── Layer 2: dark glass circle (static visual container) ──────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(diameter)
                .clip(CircleShape)
                .background(radialGradient)
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
                // Layer 1 — odometer digit roll
                OdometerCounter(
                    count    = count,
                    target   = target,
                    language = language,
                    style    = MaterialTheme.typography.displayLarge.copy(
                        color = SabeelColors.CounterWhite
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // "of 33" sub-label
                Text(
                    text  = strings.countOf.format(target.toLocalizedNumerals(language)),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = SabeelColors.TextSecondary
                    )
                )
            }
        }
    }
}
