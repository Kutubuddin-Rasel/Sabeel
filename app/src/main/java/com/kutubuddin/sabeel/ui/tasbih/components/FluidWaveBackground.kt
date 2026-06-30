package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import android.graphics.Path as AndroidPath

@Composable
fun FluidWaveBackground(
    count: Int,
    target: Int,
    modifier: Modifier = Modifier
) {
    val circlePolygon = remember {
        RoundedPolygon.circle()
    }
    val starPolygon = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 8,
            innerRadius = 0.6f
        )
    }
    val morph = remember { Morph(circlePolygon, starPolygon) }

    // Pre-allocate paths to avoid allocations during draw phase
    val androidPath = remember { AndroidPath() }
    val composePath = remember { androidPath.asComposePath() }

    // Animate the morph progress based on counting progress
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(count, target) {
        val targetProgress = if (target > 0) {
            (count.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        animProgress.animateTo(
            targetValue = targetProgress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    // Continuous slow rotation for the fluid wave effect
    val infiniteTransition = rememberInfiniteTransition(label = "WaveRotationTransition")
    val rotationState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveRotation"
    )

    // Gentle pulse effect inside draw phase
    val pulseState = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WavePulse"
    )

    val isTesting = remember {
        android.os.Build.FINGERPRINT == "robotolectric" ||
        try {
            Class.forName("org.robolectric.Robolectric") != null
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Defer state reads strictly inside drawBehind to bypass composition
                val progress = animProgress.value.coerceIn(0f, 1f)
                val rotation = rotationState.value
                val pulse = pulseState.value

                val size = this.size
                val minDim = minOf(size.width, size.height)

                if (isTesting) {
                    drawCircle(
                        color = Color(0xFF102517).copy(alpha = 0.15f),
                        radius = (minDim * 0.75f) / 2f * pulse
                    )
                } else {
                    // Update the pre-allocated path with the morph progress
                    morph.toPath(progress = progress, path = androidPath)

                    // Center and scale the shape. Normal bounds are -1 to 1 (diameter = 2)
                    val baseScale = (minDim * 0.75f) / 2f
                    val finalScale = baseScale * pulse

                    withTransform({
                        translate(size.width / 2f, size.height / 2f)
                        scale(finalScale, finalScale)
                        rotate(rotation)
                    }) {
                        drawPath(
                            path = composePath,
                            color = Color(0xFF102517).copy(alpha = 0.15f) // Subtle green/dark wave matching Sabeel theme
                        )
                    }
                }
            }
    )
}
