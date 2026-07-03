package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import com.kutubuddin.sabeel.ui.i18n.localizeDigits

@Composable
fun OdometerCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    language: String = "en"
) {
    // ASCII digits drive the roll animation (stable ordering); the localized
    // glyph is substituted only at paint time inside the content lambda.
    val countString = count.toString().padStart(2, '0')
    // Numbers read left-to-right in every language (Arabic-Indic digits too), so
    // pin the digit row to LTR — otherwise under the app-wide RTL direction a
    // two-digit count like ۱۲ would render mirrored as ۲۱.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                    text = animatedChar.toString().localizeDigits(language),
                    style = style.copy(
                        fontFeatureSettings = "tnum"
                    )
                )
            }
            }
        }
    }
}
