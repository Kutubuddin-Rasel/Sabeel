package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

@Composable
fun TajweedText(
    currentDhikr: DhikrType,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = currentDhikr,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        modifier = modifier,
        label = "TajweedTextCrossfade"
    ) { dhikr ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dhikr.arabicText,
                color = SabeelColors.ArabicText,
                textAlign = TextAlign.Center,
                style = arabicStyle.copy(fontSize = 36.sp, lineHeight = 58.sp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = dhikr.displayName,
                fontSize = 18.sp,
                color = SabeelColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
