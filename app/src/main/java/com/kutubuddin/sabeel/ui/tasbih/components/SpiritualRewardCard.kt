package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * Displays the Hadith-sourced spiritual reward for the current dhikr.
 *
 * Animates smoothly when the reward changes (dhikr switch / sequence step).
 * SRP: purely presentational — receives the already-localized reward string,
 * renders it. Language resolution happens at the call site.
 */
@Composable
fun SpiritualRewardCard(
    reward: String,
    // IX-12: DhikrItem/ActiveDhikr already carry a hadithRef, and the Dhikr
    // Library's expanded card cites it. This card — the one screen a user
    // actually spends dhikr time on, every session — rendered the reward
    // with no source at all, which is backwards for a religious app's
    // credibility: citation should be most present where engagement is
    // highest, not least. Optional so callers with no ref (or a blank one)
    // render exactly as before.
    reference: String? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    // Key on the full identity (reward + reference) so the animation fires on
    // every dhikr switch, even when two dhikrs share the same reward text but
    // have different references — or vice versa.
    AnimatedContent(
        targetState = Pair(reward, reference),
        transitionSpec = {
            (fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
             slideInVertically(
                 animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
             ) { it / 4 })
                .togetherWith(
                    fadeOut(spring(stiffness = Spring.StiffnessMedium)) +
                    slideOutVertically(
                        animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
                    ) { -it / 4 }
                )
        },
        label = "SpiritualRewardTransition",
        modifier = modifier
    ) { (rewardText, ref) ->
        // FIX 7: Typographic overhaul matching the profound weight of Dhikr.
        // Introduces an eyebrow label, classical serif italics for the promise,
        // and a subtle visual break before the theological citation.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // FIX-1.4: Removed padding(horizontal = 24.dp) that was here.
                // TasbihContent's outer Column already applies padding(horizontal = 24.dp)
                // globally. Having it here too produced 48dp total on each side, reducing
                // the text line width to 312dp on 360dp screens — causing unnecessary
                // line wrapping, especially with One UI's font scale boost on the M21.
                .semantics {  },  // Readable by TalkBack as plain text container
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Eyebrow Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Eco,
                    contentDescription = null,
                    tint = SabeelColors.SageGreen.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.countReward.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.5.sp
                    ),
                    color = SabeelColors.SageGreen.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Main Reward Text
            // ARCH-FINAL: Reduced from headlineSmall (24sp / 34sp lineHeight) to
            // bodyLarge (16sp / 24sp lineHeight). headlineSmall is a title scale —
            // too large for supporting card text sharing the screen with a 304dp circle.
            // At 34sp lineHeight, 3 lines = 102dp text; at 24sp, 3 lines = 72dp.
            // This saves ~30dp, bringing the card height from ~214dp to ~142dp and
            // making the BOTTOM ZONE content (480dp) fit within its 65% budget.
            Text(
                text = rewardText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 24.sp
                ),
                color = SabeelColors.CounterWhite.copy(alpha = 0.95f),
                textAlign = TextAlign.Center
            )
            
            if (!ref.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                
                // Minimalist Divider ( —   — )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(18.dp).height(1.dp).background(SabeelColors.SageGreen.copy(alpha = 0.25f)))
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.width(18.dp).height(1.dp).background(SabeelColors.SageGreen.copy(alpha = 0.25f)))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Subdued Citation
                Text(
                    text = ref,
                    style = MaterialTheme.typography.labelMedium,
                    color = SabeelColors.SageGreen.copy(alpha = 0.6f),
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
