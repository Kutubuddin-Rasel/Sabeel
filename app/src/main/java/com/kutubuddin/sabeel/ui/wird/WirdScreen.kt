package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.BorderStroke
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import com.kutubuddin.sabeel.ui.components.SabeelTopBar
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

/**
 * Route-level wrapper (DIP): the ONLY place in this file that injects a
 * ViewModel or collects a Flow. Rendering is delegated to [WirdContent].
 */
@Composable
fun WirdScreen(
    onCountItem: (key: String, target: Int) -> Unit,
    onEdit: () -> Unit,
    onBack: () -> Unit,
    viewModel: WirdViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    WirdContent(
        progress = uiState.progress,
        language = uiState.language,
        onCountItem = onCountItem,
        onEdit = {
            viewModel.onWirdEditEntryUsed()
            onEdit()
        },
        onBack = onBack
    )
}

/**
 * Pure, stateless read-only view of today's wird progress — a function of
 * [progress]/[language] only, emitting intent via the trailing lambdas.
 */
@Composable
fun WirdContent(
    progress: WirdProgress,
    language: String,
    onCountItem: (key: String, target: Int) -> Unit,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current

    Column(
        Modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
            .navigationBarsPadding()
    ) {
        SabeelTopBar(
            title = strings.wirdTitle,
            onBack = onBack,
            backContentDescription = strings.a11yBack,
            actions = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = strings.wirdEditTitle,
                    tint = SabeelColors.AccentTeal,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(22.dp)
                        .clickable(onClick = onEdit)
                )
            }
        )

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                strings.wirdDoneOf.format(
                    progress.completed.toLocalizedNumerals(language),
                    progress.total.toLocalizedNumerals(language)
                ),
                fontSize = 13.sp, color = SabeelColors.TextSecondary
            )
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
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.wirdAddDhikr, color = SabeelColors.Background, fontWeight = FontWeight.Bold)
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
                item { Spacer(Modifier.height(8.dp)) }
            }

            // Second, always-visible entry point to the same edit action as the
            // top-bar icon — mirrors the empty-state CTA below so the
            // "add/update daily goal" affordance never depends on noticing a
            // single small icon.
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SabeelColors.AccentTeal),
                border = BorderStroke(1.dp, SabeelColors.AccentTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(strings.wirdEditTitle, fontWeight = FontWeight.Bold)
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
            Text(item.displayName.get(language), fontSize = 14.sp, fontWeight = FontWeight.Medium,
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