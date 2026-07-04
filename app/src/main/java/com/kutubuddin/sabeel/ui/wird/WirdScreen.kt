package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

@Composable
fun WirdScreen(
    onCountItem: (key: String, target: Int) -> Unit,
    onEdit: () -> Unit,
    viewModel: WirdViewModel = hiltViewModel()
) {
    val progress by viewModel.state.collectAsState()
    val language by viewModel.language.collectAsState()
    val strings = LocalStrings.current

    Column(Modifier.fillMaxSize().background(SabeelColors.Background)) {
        // Header: title + completed/total + sum-based bar
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(strings.wirdTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = SabeelColors.TextPrimary)
                Text(
                    strings.wirdDoneOf.format(
                        progress.completed.toLocalizedNumerals(language),
                        progress.total.toLocalizedNumerals(language)
                    ),
                    fontSize = 13.sp, color = SabeelColors.TextSecondary
                )
            }
            if (progress.total > 0) {
                val fraction = if (progress.targetSum > 0)
                    (progress.countedSum.toFloat() / progress.targetSum).coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = SabeelColors.AccentTeal, trackColor = SabeelColors.ArcTrack,
                    strokeCap = StrokeCap.Round, gapSize = 0.dp, drawStopIndicator = {}
                )
            }
        }
        HorizontalDivider(color = SabeelColors.Divider)

        if (progress.items.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(strings.wirdEmpty, fontSize = 15.sp, color = SabeelColors.TextSecondary)
                Spacer(Modifier.height(6.dp))
                Text(strings.wirdEmptyHint, fontSize = 12.sp, color = SabeelColors.TextHint)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onEdit, colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal)) {
                    Text(strings.wirdAddDhikr, color = SabeelColors.Background)
                }
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(progress.items, key = { it.dhikrKey }) { item ->
                    WirdItemRow(item, language) { onCountItem(item.dhikrKey, item.target) }
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable(onClick = onEdit).padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = SabeelColors.AccentTeal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.wirdEditTitle, color = SabeelColors.AccentTeal, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun WirdItemRow(item: WirdProgressItem, language: String, onClick: () -> Unit) {
    val done = item.isComplete
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .clickable(enabled = !done, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (done) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (done) SabeelColors.SageGreen else SabeelColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(item.displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                color = if (done) SabeelColors.SageGreen else SabeelColors.TextPrimary)
            Text(item.arabicText, maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = arabicStyle.copy(fontSize = 16.sp, lineHeight = 24.sp), color = SabeelColors.ArabicText,
                modifier = Modifier.fillMaxWidth())
        }
        Text(
            "${item.countToday.toLocalizedNumerals(language)} / ${item.target.toLocalizedNumerals(language)}",
            fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = if (done) SabeelColors.SageGreen else SabeelColors.AccentTeal
        )
    }
}
