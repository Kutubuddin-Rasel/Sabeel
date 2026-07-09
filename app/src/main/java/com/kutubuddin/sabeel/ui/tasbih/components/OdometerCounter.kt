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
    target: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    language: String = "en"
) {
    // IX-05 fix: was a hardcoded 2-digit pad regardless of target, so a
    // small target like 3 rendered as "00" at rest — read as a display
    // glitch rather than "zero of three". Width now tracks the target's own
    // digit count (1 digit for a target of 3, 3 digits for a target of 100),
    // so the padding never implies a scale the target doesn't have.
    val padWidth = target.toString().length.coerceAtLeast(1)
    // ASCII digits drive the roll animation (stable ordering); the localized
    // glyph is substituted only at paint time inside the content lambda.
    val countString = count.toString().padStart(padWidth, '0')
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
