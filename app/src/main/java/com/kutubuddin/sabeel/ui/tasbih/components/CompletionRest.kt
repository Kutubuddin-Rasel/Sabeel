package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

/**
 * The context of the current completion, determining the UI state.
 */
enum class CompletionContext {
    /** Doing a daily goal, but more dhikrs remain. */
    FLOW,
    /** Completed the final dhikr of the entire daily goal. */
    VICTORY,
    /** Doing an ad-hoc dhikr outside of the daily goal. */
    AD_HOC
}

/**
 * A calm, dismissible completion state shown when a dhikr target is reached.
 *
 * Adapts its UI based on the [CompletionContext]:
 * - [CompletionContext.FLOW]: Offers to continue to the next step.
 * - [CompletionContext.VICTORY]: Celebrates the full daily goal completion.
 * - [CompletionContext.AD_HOC]: Offers to restart or finish the free counting session.
 *
 * Renders as a full-screen scrim that consumes taps, so the underlying
 * tap-to-count surface never fires while resting here.
 */
@Composable
fun CompletionRest(
    dhikrName: String,
    total: Int,
    context: CompletionContext,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: (() -> Unit)?,
    nextWirdItemName: String? = null,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    val strings = LocalStrings.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SabeelColors.Background.copy(alpha = 0.94f))
            .navigationBarsPadding()
            // Consume all taps so they never reach the count surface beneath.
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            val iconTint = if (context == CompletionContext.VICTORY) SabeelColors.GoldPrimary else SabeelColors.AccentTeal
            val arabicText = if (context == CompletionContext.VICTORY) strings.countAlhamdulillah else "تَمَّ"
            
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(if (context == CompletionContext.VICTORY) 72.dp else 56.dp)
            )
            
            Text(
                text = arabicText,
                color = iconTint,
                style = arabicStyle.copy(
                    fontSize = if (context == CompletionContext.VICTORY) 48.sp else 40.sp, 
                    lineHeight = if (context == CompletionContext.VICTORY) 72.sp else 64.sp
                ),
                textAlign = TextAlign.Center
            )
            
            val statusText = if (context == CompletionContext.VICTORY) {
                strings.countDailyGoalCompleted
            } else {
                strings.countComplete.format(total.toLocalizedNumerals(language))
            }
            
            Text(
                text = statusText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SabeelColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            
            if (context != CompletionContext.VICTORY) {
                Text(
                    text = dhikrName,
                    fontSize = 14.sp,
                    color = SabeelColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(12.dp))

            if (context == CompletionContext.FLOW && nextWirdItemName != null) {
                Text(
                    text = "${strings.countContinue}: $nextWirdItemName",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = SabeelColors.GoldPrimary,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onSecondaryAction != null) {
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, SabeelColors.BorderIdle),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SabeelColors.TextSecondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val secondaryText = if (context == CompletionContext.AD_HOC) strings.countFinish else strings.countFinish
                        Text(secondaryText, fontSize = 14.sp)
                    }
                }
                
                val primaryColor = if (context == CompletionContext.VICTORY) SabeelColors.GoldPrimary else SabeelColors.AccentTeal
                val primaryTextColor = if (context == CompletionContext.VICTORY) SabeelColors.Background else SabeelColors.Background
                
                Button(
                    onClick = onPrimaryAction,
                    modifier = if (onSecondaryAction != null) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val primaryText = when (context) {
                        CompletionContext.VICTORY -> strings.wirdDone
                        CompletionContext.AD_HOC -> strings.countAgain
                        CompletionContext.FLOW -> if (nextWirdItemName != null) "${strings.countContinue} →" else strings.countContinue
                    }
                    Text(primaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryTextColor)
                }
            }
        }
    }
}
