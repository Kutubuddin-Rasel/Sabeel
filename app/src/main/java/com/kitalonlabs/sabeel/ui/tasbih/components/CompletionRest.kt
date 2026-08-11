package com.kitalonlabs.sabeel.ui.tasbih.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kitalonlabs.sabeel.ui.i18n.LocalStrings
import com.kitalonlabs.sabeel.ui.i18n.toLocalizedNumerals
import com.kitalonlabs.sabeel.ui.theme.SabeelColors
import com.kitalonlabs.sabeel.ui.theme.arabicStyle

/**
 * The context of the current completion, determining the UI state.
 */
enum class CompletionContext {
    /** Doing a daily goal, but more dhikrs remain. */
    FLOW,
    /** Completed the final dhikr of the entire daily goal. */
    VICTORY,
    /** Completed the daily goal from a Library flow (transient, auto-dismissing, non-blocking). */
    VICTORY_TRANSIENT,
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
            // Consume all taps so they never reach the count surface beneath, EXCEPT for transient contexts
            .then(
                if (context == CompletionContext.VICTORY_TRANSIENT) Modifier
                else Modifier.pointerInput(Unit) { detectTapGestures { } }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            val isVictory = context == CompletionContext.VICTORY || context == CompletionContext.VICTORY_TRANSIENT
            val iconTint = if (isVictory) SabeelColors.GoldPrimary else SabeelColors.AccentTeal
            val arabicText = if (isVictory) strings.countAlhamdulillah else "تَمَّ"
            
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(if (isVictory) 72.dp else 56.dp)
            )
            
            Text(
                text = arabicText,
                color = iconTint,
                style = arabicStyle.copy(
                    // 1.9× diacritic-safety rule: 48×1.9=91sp, 40×1.9=76sp
                    fontSize = if (isVictory) 48.sp else 40.sp,
                    lineHeight = if (isVictory) 91.sp else 76.sp
                ),
                textAlign = TextAlign.Center
            )
            
            val statusText = if (isVictory) {
                strings.countDailyGoalCompleted
            } else {
                strings.countComplete.format(total.toLocalizedNumerals(language))
            }
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleLarge,
                color = SabeelColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            
            if (!isVictory) {
                Text(
                    text = dhikrName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SabeelColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(12.dp))

            if (context == CompletionContext.FLOW && nextWirdItemName != null) {
                Text(
                    text = "${strings.countContinue}: $nextWirdItemName",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = SabeelColors.GoldPrimary,
                    textAlign = TextAlign.Center
                )
            }

            if (context != CompletionContext.VICTORY_TRANSIENT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (onSecondaryAction != null) {
                    // Press-scale: each button gets its own interactionSource so
                    // pressing one doesn't scale the other. 0.95f is slightly more
                    // pronounced than AccentCard (0.96f) — smaller target, needs
                    // stronger confirmation at an emotionally significant moment.
                    val secondarySource = remember { MutableInteractionSource() }
                    val secondaryPressed by secondarySource.collectIsPressedAsState()
                    val secondaryScale by animateFloatAsState(
                        targetValue = if (secondaryPressed) 0.95f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness    = Spring.StiffnessMedium
                        ),
                        label = "secondary_btn_scale"
                    )
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        interactionSource = secondarySource,
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { scaleX = secondaryScale; scaleY = secondaryScale },
                        border = BorderStroke(1.dp, SabeelColors.BorderIdle),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SabeelColors.TextSecondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val secondaryText = if (context == CompletionContext.AD_HOC) strings.countFinish else strings.countFinish
                        Text(secondaryText, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                val primaryColor = if (context == CompletionContext.VICTORY) SabeelColors.GoldPrimary else SabeelColors.AccentTeal
                val primaryTextColor = if (context == CompletionContext.VICTORY) SabeelColors.Background else SabeelColors.Background

                val primarySource = remember { MutableInteractionSource() }
                val primaryPressed by primarySource.collectIsPressedAsState()
                val primaryScale by animateFloatAsState(
                    targetValue = if (primaryPressed) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness    = Spring.StiffnessMedium
                    ),
                    label = "primary_btn_scale"
                )
                Button(
                    onClick = onPrimaryAction,
                    interactionSource = primarySource,
                    modifier = (if (onSecondaryAction != null) Modifier.weight(1f) else Modifier.fillMaxWidth())
                        .graphicsLayer { scaleX = primaryScale; scaleY = primaryScale },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val primaryText = when (context) {
                        CompletionContext.VICTORY -> strings.wirdDone
                        CompletionContext.AD_HOC -> strings.countAgain
                        CompletionContext.FLOW -> if (nextWirdItemName != null) "${strings.countContinue} →" else strings.countContinue
                        CompletionContext.VICTORY_TRANSIENT -> "" // Unused
                    }
                    Text(primaryText, style = MaterialTheme.typography.titleSmall, color = primaryTextColor)
                }
            }
        }
    }
}
}
