package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import com.kutubuddin.sabeel.ui.i18n.localizeDigits
import com.kutubuddin.sabeel.ui.theme.SabeelMotion

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
    // FIX-POST-2 (Flaw 4): At count=0 (pre-tap resting state) the unconditional
    // padStart rendered "00" for any target ≥ 10 — reads as a display error before
    // any counting has begun. The padding serves animation stability at digit-boundary
    // crossings (9→10), but at count=0 there is no boundary to protect. Skip padding
    // at rest; resume padding from count=1 onward. The 0→1 width transition for
    // 2-digit targets is animated gracefully by the key(index) slot mechanism below.
    val countString = if (count == 0) "0"
                      else count.toString().padStart(padWidth, '0')
    // Numbers read left-to-right in every language (Arabic-Indic digits too), so
    // pin the digit row to LTR — otherwise under the app-wide RTL direction a
    // two-digit count like ۱۲ would render mirrored as ۲۱.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier = modifier) {
            countString.forEachIndexed { index, char ->
            // JANK-03: key(index) gives Compose a stable slot identity per digit.
            // Without it, going from a 1-digit to 2-digit count (9 → 10) caused all
            // AnimatedContent instances to rebuild from scratch because slot positions shifted.
            key(index) {
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (targetState > initialState) {
                        // POLISH-01 + token compliance: SabeelMotion.Spring.CounterSlide() is the
                        // generic counterpart of Counter for IntOffset-typed slide transitions.
                        // Physics identical to the arc sweep — one shared clock.
                        slideInVertically(animationSpec = SabeelMotion.Spring.CounterSlide()) { it } togetherWith
                        slideOutVertically(animationSpec = SabeelMotion.Spring.CounterSlide()) { -it }
                    } else {
                        slideInVertically(animationSpec = SabeelMotion.Spring.CounterSlide()) { -it } togetherWith
                        slideOutVertically(animationSpec = SabeelMotion.Spring.CounterSlide()) { it }
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
            } // key
            }
        }
    }
}
