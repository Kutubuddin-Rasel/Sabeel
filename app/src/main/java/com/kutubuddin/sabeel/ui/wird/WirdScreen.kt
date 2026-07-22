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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.toImmutableList
import androidx.compose.ui.platform.LocalConfiguration

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import com.kutubuddin.sabeel.ui.components.ProgressFractionText
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
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {},
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
        onBack = onBack,
        showTooltip = showTooltip,
        onTooltipDismiss = onTooltipDismiss
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
    onBack: () -> Unit,
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {}
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
                    Box(contentAlignment = Alignment.TopCenter) {
                        Row(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(onClick = {
                                    if (showTooltip) onTooltipDismiss()
                                    onEdit()
                                })
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
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        
                        com.kutubuddin.sabeel.ui.components.SabeelTooltip(
                            visible = showTooltip,
                            text = if (language == "bn") "আপনার উর্দ পরিবর্তন করতে 'এডিট' এ ক্লিক করুন।" else "Click 'Edit' to change your daily wird.",
                            position = com.kutubuddin.sabeel.ui.components.TooltipPosition.Bottom, // pointer at top
                            modifier = Modifier
                                .offset(y = 56.dp)
                                .padding(end = 12.dp)
                        )
                    }
                }
            }
        )

        if (progress.items.isEmpty()) {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(screenHeight * 0.3f))
                Text(strings.wirdEmpty, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary)
                Spacer(Modifier.height(6.dp))
                Text(strings.wirdEmptyHint, style = MaterialTheme.typography.bodySmall, color = SabeelColors.TextHint)
                Spacer(Modifier.height(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    com.kutubuddin.sabeel.ui.components.SabeelTooltip(
                        visible = showTooltip,
                        text = if (language == "bn") "আপনার প্রতিদিনের লক্ষ্যের জন্য ধিকির যোগ করতে এখানে ক্লিক করুন।" else "Click here to add dhikr for your daily goal.",
                        position = com.kutubuddin.sabeel.ui.components.TooltipPosition.Bottom, // pointer at bottom, pointing down to button
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = {
                            if (showTooltip) onTooltipDismiss()
                            onEdit()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.wirdAddDhikr, color = SabeelColors.Background, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // FIX 8: intention framing, not a to-do count. At zero we lead with
            // the invitation ("Your wird awaits") and hold back the numeric
            // fraction — a day of worship isn't a debt of "0/2 tasks". The
            // fraction returns once something's been remembered.
            val wirdHeroLabel = when {
                progress.completed == 0 -> strings.wirdBeginToday
                progress.completed == progress.total -> strings.wirdComplete
                else -> strings.wirdInProgress.format(
                    progress.completed.toLocalizedNumerals(language),
                    progress.total.toLocalizedNumerals(language)
                )
            }

            // FIX 7: a single teal arc (overall completion) with a bead-dot
            // strip below (one per item, filled = done). The arc carries the
            // "how much is done" job that the old header Text +
            // LinearProgressIndicator did, so there's one progress statement on
            // this screen instead of two that could disagree.
            WirdVisualRing(
                itemStates = progress.items.map { it.isComplete }.toImmutableList(),
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (progress.completed > 0) {
                        Text(
                            text = "${progress.completed.toLocalizedNumerals(language)}/${progress.total.toLocalizedNumerals(language)}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = SabeelColors.TextPrimary
                        )
                    }
                    Text(
                        text = wirdHeroLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = SabeelColors.TextSecondary
                    )
                }
            }
            HorizontalDivider(color = SabeelColors.Divider)

            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(progress.items, key = { it.dhikrKey }) { item ->
                    WirdItemRow(
                        item = item,
                        language = language,
                        // animateItem(): Compose 1.7+ placement + fade animation.
                        // Items slide+fade in on first load and animate out on removal.
                        // The stagger emerges naturally from layout pass ordering —
                        // no manual delay loops needed.
                        modifier = Modifier.animateItem(),
                        onClick = { onCountItem(item.dhikrKey, item.target) }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun WirdItemRow(item: WirdProgressItem, language: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val done = item.isComplete
    Row(
        Modifier
            .then(modifier)
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
            Text(item.displayName.get(language), style = MaterialTheme.typography.labelLarge,
                color = if (done) SabeelColors.SageGreen else SabeelColors.TextPrimary)
            Text(item.arabicText, maxLines = 1, overflow = TextOverflow.Ellipsis,
                // 16 × 1.9 = 30.4sp — diacritic safety rule (was 24sp = 1.5×, a violation)
                style = arabicStyle.copy(fontSize = 16.sp, lineHeight = 30.sp), color = SabeelColors.ArabicText,
                modifier = Modifier.fillMaxWidth())
        }
        ProgressFractionText(
            count = item.countToday,
            target = item.target,
            language = language,
            isComplete = done,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            normalColor = SabeelColors.AccentTeal,
            completeColor = SabeelColors.SageGreen
        )
    }
}