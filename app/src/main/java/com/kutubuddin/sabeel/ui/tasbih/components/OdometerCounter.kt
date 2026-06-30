package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun OdometerCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default
) {
    val countString = count.toString().padStart(2, '0')
    Row(modifier = modifier) {
        countString.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = 1500f
                            )
                        ) { it } togetherWith slideOutVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = 1500f
                            )
                        ) { -it }
                    } else {
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = 1500f
                            )
                        ) { -it } togetherWith slideOutVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = 1500f
                            )
                        ) { it }
                    }
                },
                label = "OdometerDigit_$index"
            ) { animatedChar ->
                Text(
                    text = animatedChar.toString(),
                    style = style.copy(
                        fontFeatureSettings = "tnum"
                    )
                )
            }
        }
    }
}
