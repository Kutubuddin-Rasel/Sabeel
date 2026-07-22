package com.kutubuddin.sabeel.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kutubuddin.sabeel.ui.theme.SabeelColors

enum class TooltipPosition {
    Top, Bottom, Start, End
}

@Composable
fun SabeelTooltip(
    visible: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    position: TooltipPosition = TooltipPosition.Top,
    pointerSize: Dp = 8.dp
) {
    val bubbleColor = SabeelColors.AccentTeal
    val textColor = SabeelColors.Background

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            initialScale = 0.9f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        ),
        exit = fadeOut() + scaleOut(targetScale = 0.9f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(pointerSize),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bubbleColor)
                    .drawBehind {
                        val path = Path()
                        val pointerPx = pointerSize.toPx()
                        
                        when (position) {
                            TooltipPosition.Top -> {
                                // Pointer at the bottom, pointing down
                                path.moveTo(size.width / 2 - pointerPx, size.height)
                                path.lineTo(size.width / 2, size.height + pointerPx)
                                path.lineTo(size.width / 2 + pointerPx, size.height)
                            }
                            TooltipPosition.Bottom -> {
                                // Pointer at the top, pointing up
                                path.moveTo(size.width / 2 - pointerPx, 0f)
                                path.lineTo(size.width / 2, -pointerPx)
                                path.lineTo(size.width / 2 + pointerPx, 0f)
                            }
                            TooltipPosition.Start -> {
                                // Pointer at the end, pointing right
                                path.moveTo(size.width, size.height / 2 - pointerPx)
                                path.lineTo(size.width + pointerPx, size.height / 2)
                                path.lineTo(size.width, size.height / 2 + pointerPx)
                            }
                            TooltipPosition.End -> {
                                // Pointer at the start, pointing left
                                path.moveTo(0f, size.height / 2 - pointerPx)
                                path.lineTo(-pointerPx, size.height / 2)
                                path.lineTo(0f, size.height / 2 + pointerPx)
                            }
                        }
                        path.close()
                        drawPath(path, bubbleColor)
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
