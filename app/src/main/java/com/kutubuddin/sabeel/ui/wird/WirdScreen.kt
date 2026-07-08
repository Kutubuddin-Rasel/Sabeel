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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 *
 * Edit has exactly one entry point on this screen: the labeled action in the
 * top bar, shown only once there's a plan to edit. The old always-visible
 * bottom button that duplicated it has been removed — two "Edit" affordances
 * for one action was clutter, not a safety net. The empty state below has
 * its own single CTA instead, so there's never more than one way to start
 * editing from any given moment on this screen.
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
                if (progress.items.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(onClick = onEdit)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = SabeelColors.AccentTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.wirdEditCta,
                            color = SabeelColors.AccentTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        )

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
            // The ring now carries the "how much is done" job that the old
            // header Text + LinearProgressIndicator did — real per-item
            // completion drives slice opacity, and the completed/total
            // count sits in the center, so there's one progress statement
            // on this screen instead of two that could disagree.
            WirdVisualRing(
                slices = progress.items.map { WirdRingSlice(target = it.target, isComplete = it.isComplete) },
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "${progress.completed.toLocalizedNumerals(language)}/${progress.total.toLocalizedNumerals(language)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SabeelColors.TextPrimary
                )
            }
            HorizontalDivider(color = SabeelColors.Divider)

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