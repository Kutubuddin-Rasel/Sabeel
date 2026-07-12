package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
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
        // FIX 5: no fill, no leading accent stripe — the reward is quiet
        // supporting text below the counter, not a card competing with it.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .semantics {  },  // Readable by TalkBack as plain text container
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.countReward,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
                color = SabeelColors.SageGreen,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = rewardText,
                style = MaterialTheme.typography.bodyMedium,
                color = SabeelColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            if (!ref.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ref,
                    style = MaterialTheme.typography.bodySmall,
                    color = SabeelColors.TextSecondary,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
