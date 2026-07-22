package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
 * @param onDecrement Called on swipe-down to undo. Optional.
 * @param scale       Reserved for external override (defaults to 1f; internal
 *                    pressScale takes precedence via graphicsLayer).
 * @param diameter    Circle diameter; default 280.dp
 */
@Composable
fun TasbihCircle(
    count: Int,
    target: Int,
    triggerGoldenBloom: Boolean = false,
    onGoldenBloomEnd: () -> Unit = {},
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    onDecrement: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    diameter: Dp = 280.dp,
    language: String = "en"
) {
    val strings = LocalStrings.current
    val scope   = rememberCoroutineScope()

    // ── Hoist color tokens — read inside non-composable Canvas lambdas ────────
    // FIX-FLAW7: At past-target (count >= target), the full 360° arc was rendered
    // at 100% brightness — visually jarring and compounded the box-shape glow.
    // Drop arc alpha to 0.65f when past-target; circle still signals completion.
    val isPastTarget  = target > 0 && count >= target
    val arcAlpha      = if (isPastTarget) 0.65f else 1.0f
    val arcTrackColor = SabeelColors.ArcTrack
    val arcStartColor = SabeelColors.AccentTeal.copy(alpha = arcAlpha)
    val arcEndColor   = SabeelColors.AccentTealBright.copy(alpha = arcAlpha)

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

    // ── Pre-allocate brushes and strokes — avoid per-frame allocation in draw phase ────────
    val mainStrokeWidth = remember(density) { with(density) { 5.dp.toPx() } }
    val mainStroke = remember(mainStrokeWidth) { Stroke(width = mainStrokeWidth, cap = StrokeCap.Round) }
    
    val ringStrokeWidth = remember(density) { with(density) { 3.dp.toPx() } }
    val ringStroke = remember(ringStrokeWidth) { Stroke(width = ringStrokeWidth, cap = StrokeCap.Round) }

    val sweepGradient = remember(arcStartColor, arcEndColor) {
        // FIX-3.2 (corrected): Same-endpoint sweep gradient — no seam artifact possible.
        //
        // The previous transparent-endpoint approach broke the ring at 100% count:
        // the transparent stop at 1.0f created a visible gap at 3 o'clock when the
        // full 360° arc was drawn (sweep = 360°).
        //
        // Root cause of the original seam: 2-stop gradient jumps from arcEndColor
        // back to arcStartColor at the 0°/360° junction (3 o'clock). On Mali GPUs
        // (Redmi/MIUI) the sub-pixel AA doesn't smooth this — visible sharp line.
        //
        // Correct fix: make BOTH endpoints the same color (arcStartColor). There is
        // now zero color difference at the seam junction on either side. The gradient
        // flows:  arcStartColor (3 o'clock) → arcEndColor (9 o'clock) → arcStartColor
        // (3 o'clock). No jump, no gap, no artifact — at any count value including 33/33.
        Brush.sweepGradient(
            colorStops = arrayOf(
                0.0f  to arcStartColor,  // 3 o'clock — seam START (matches end)
                0.5f  to arcEndColor,    // 9 o'clock — gradient peak brightness
                1.0f  to arcStartColor   // 3 o'clock — seam END (same as start = no jump)
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

    // ── Layer 5: Golden Bloom — triggers on milestone (33, 100, etc.) ─────────
    val bloomRadius = remember { Animatable(0f) }
    val bloomAlpha  = remember { Animatable(0f) }
    val bloomSweep  = remember { Animatable(0f) }
    
    val goldStartColor = SabeelColors.GoldPrimary
    val goldEndColor   = SabeelColors.GoldLuminous
    
    val bloomGradient = remember(goldStartColor, goldEndColor) {
        // FIX-POST-2 (Flaw 3): Same-endpoint bloom gradient — no seam artifact.
        // The original 2-stop version (goldStartColor → goldEndColor) creates a hard
        // color jump at the 360°/0° seam when bloomSweep reaches 360° on milestone
        // completion. Same bug pattern as the progress arc seam we already fixed.
        // Same fix: make both endpoints goldStartColor so there is zero color difference
        // at the junction — on any GPU, at any sweep angle including the full 360° bloom.
        Brush.sweepGradient(
            colorStops = arrayOf(
                0.0f to goldStartColor,  // 3 o'clock — seam START (matches end)
                0.5f to goldEndColor,    // 9 o'clock — peak gold brightness
                1.0f to goldStartColor   // 3 o'clock — seam END (same as start = no jump)
            )
        )
    }

    LaunchedEffect(triggerGoldenBloom) {
        if (triggerGoldenBloom) {
            bloomRadius.snapTo(ringMaxRadius) // Start at arc boundary
            bloomAlpha.snapTo(0.85f)
            bloomSweep.snapTo(0f)
            
            launch {
                bloomRadius.animateTo(
                    targetValue   = ringMaxRadius + diameterPx * 0.5f,
                    animationSpec = tween(
                        durationMillis = 800,
                        easing         = FastOutSlowInEasing
                    )
                )
            }
            launch {
                bloomAlpha.animateTo(
                    targetValue   = 0f,
                    animationSpec = tween(
                        durationMillis = 800,
                        easing         = LinearEasing
                    )
                )
            }
            launch {
                bloomSweep.animateTo(
                    targetValue   = 360f,
                    animationSpec = tween(
                        durationMillis = 600,
                        easing         = FastOutSlowInEasing
                    )
                )
            }
            
            // Wait for 800ms
            kotlinx.coroutines.delay(800)
            onGoldenBloomEnd()
        }
    }

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
            // ── Gesture handler: Swipe Down to Undo ───────────────────────────
            .pointerInput(Unit) {
                var totalDragY = 0f
                detectVerticalDragGestures(
                    onDragStart = { _ -> totalDragY = 0f },
                    onDragEnd = {
                        if (totalDragY > 60f) { // Swipe threshold
                            onDecrement?.invoke()
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        totalDragY += dragAmount
                    }
                )
            }
            // ── Gesture handler: Tap & Hold ───────────────────────────────────
            .pointerInput(Unit) {
                var wasReset = false
                detectTapGestures(
                    onPress = { _ ->
                        wasReset = false
                        // Layer 3a — sequenced press animations and delayed reset
                        val pressJob = scope.launch {
                            // 1. Immediate dip on finger-down (PressFeedback)
                            pressScale.animateTo(
                                targetValue   = 0.93f,
                                animationSpec = SabeelMotion.Spring.PressFeedback
                            )
                            
                            // 2. Slow, continuous shrink to indicate "keep holding to reset"
                            pressScale.animateTo(
                                targetValue   = 0.82f,
                                animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
                            )
                            
                            // 3. Reached 2.5s without being cancelled — trigger reset!
                            wasReset = true
                            onLongPress?.invoke()
                            
                            // 4. "Pop" back to 1f to visually confirm reset to the user
                            pressScale.animateTo(
                                targetValue   = 1f,
                                animationSpec = SabeelMotion.Spring.PressRelease
                            )
                        }

                        // Wait until the user lifts their finger or drags out
                        tryAwaitRelease()
                        
                        // User released finger. Cancel the press sequence (if it hasn't finished)
                        pressJob.cancel()
                        
                        // Layer 3b — spring back if they let go before reset
                        scope.launch {
                            pressScale.animateTo(
                                targetValue   = 1f,
                                animationSpec = SabeelMotion.Spring.PressRelease
                            )
                        }
                    },
                    onTap = { _ ->
                        // Only increment if we didn't just trigger a reset
                        if (!wasReset) {
                            onTap()
                            // SMOOTH-02: cancel any in-flight ring before starting a new one.
                            ringJob.value?.cancel()
                            ringJob.value = scope.launch {
                                ringRadius.snapTo(0f)
                                ringAlpha.snapTo(0.55f)
                                launch {
                                    ringRadius.animateTo(
                                        targetValue   = ringMaxRadius,
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
                        }
                    }
                    // onLongPress is completely removed. We handle it manually in onPress.
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
            val arcDiameter = size.minDimension - mainStrokeWidth
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
                style      = mainStroke
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
                    style      = mainStroke
                )
            }

            // Layer 4 — expanding ring pulse (AccentTeal ring, fades to 0)
            // Drawn on top of the arc so it's clearly visible.
            if (ringAlpha.value > 0f) {
                drawCircle(
                    color  = arcStartColor.copy(alpha = ringAlpha.value),
                    radius = ringRadius.value,
                    style  = ringStroke
                )
            }
            
            // Layer 5 — Golden Bloom arc flash
            if (bloomAlpha.value > 0f) {
                // Expanding golden aura
                drawCircle(
                    color  = goldStartColor.copy(alpha = bloomAlpha.value * 0.5f),
                    radius = bloomRadius.value,
                    style  = Stroke(width = ringStrokeWidth * 2, cap = StrokeCap.Round)
                )
                
                // Fast sweep arc flash
                drawArc(
                    brush      = bloomGradient,
                    startAngle = -90f,
                    sweepAngle = bloomSweep.value,
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = mainStroke
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
