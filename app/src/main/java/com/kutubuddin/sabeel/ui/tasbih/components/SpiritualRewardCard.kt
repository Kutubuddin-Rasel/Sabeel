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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * Displays the Hadith-sourced spiritual reward for the current dhikr.
 *
 * Animates smoothly when the dhikr changes (Smart Flow transition).
 * SRP: purely presentational — receives the reward string, renders it.
 */
@Composable
fun SpiritualRewardCard(
    dhikr: DhikrType,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = dhikr,
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
    ) { currentDhikr ->
        val borderColor = SabeelColors.RewardBorder

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SabeelColors.Surface)
                .drawBehind {
                    // 3dp left accent border in sage green
                    val strokeWidth = 3.dp.toPx()
                    drawLine(
                        color = borderColor,
                        start = Offset(strokeWidth / 2f, 0f),
                        end = Offset(strokeWidth / 2f, size.height),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                .padding(start = 16.dp, end = 14.dp, top = 12.dp, bottom = 12.dp)
                .semantics {  }  // Readable by TalkBack as plain text container
        ) {
            Column {
                Text(
                    text = "SPIRITUAL REWARD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = SabeelColors.SageGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentDhikr.spiritualReward,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = SabeelColors.TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
