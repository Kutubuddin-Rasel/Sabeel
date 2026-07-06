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
 * A calm, dismissible completion state shown when a dhikr target is reached.
 *
 * Replaces the previous 1200ms auto-clearing "flash": worship deserves a pause,
 * not a strobe. The worshipper chooses when to move on — Continue (start a fresh
 * round) or Finish (leave the count and step away).
 *
 * Renders as a full-screen scrim that consumes taps, so the underlying
 * tap-to-count surface never fires while resting here.
 */
@Composable
fun CompletionRest(
    dhikrName: String,
    total: Int,
    onContinue: () -> Unit,
    onFinish: () -> Unit,
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
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = SabeelColors.AccentTeal,
                modifier = Modifier.size(56.dp)
            )
            // "Tamma" — completed.
            Text(
                text = "تَمَّ",
                color = SabeelColors.AccentTeal,
                style = arabicStyle.copy(fontSize = 40.sp, lineHeight = 64.sp),
                textAlign = TextAlign.Center
            )
            Text(
                text = strings.countComplete.format(total.toLocalizedNumerals(language)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SabeelColors.TextPrimary
            )
            Text(
                text = dhikrName,
                fontSize = 14.sp,
                color = SabeelColors.TextSecondary
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onFinish,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, SabeelColors.BorderIdle),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SabeelColors.TextSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.countFinish, fontSize = 14.sp)
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.countContinue, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SabeelColors.Background)
                }
            }
        }
    }
}
