package com.kutubuddin.sabeel.ui.dhikr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.DhikrSequence
import com.kutubuddin.sabeel.domain.model.DhikrStep
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.Shield
import com.kutubuddin.sabeel.ui.tasbih.TasbihIntent
import com.kutubuddin.sabeel.ui.tasbih.TasbihViewModel
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.localizeHadithRef
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.SabeelMotion
import com.kutubuddin.sabeel.ui.theme.arabicStyle
import com.kutubuddin.sabeel.ui.components.CategoryHeader as SharedCategoryHeader

/**
 * Route-level wrapper (SRP/DIP): sole owner of [viewModel] injection and state
 * collection, and the only place that knows [tasbihViewModel] exists — the
 * cross-screen "Count Now" wiring is resolved here, not inside the reusable
 * rendering below.
 */
@Composable
fun DhikrLibraryScreen(
    tasbihViewModel: TasbihViewModel,
    onCountNow: () -> Unit,
    onOpenGallery: () -> Unit,
    viewModel: DhikrViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DhikrLibraryContent(
        state = state,
        onSearch = viewModel::onSearch,
        onSelectDhikr = viewModel::onSelectDhikr,
        onCountNow = { key ->
            tasbihViewModel.processIntent(TasbihIntent.SetDhikr(key))
            onCountNow() // Calls the parameter
        },
        onOpenGallery = onOpenGallery
    )
}

/**
 * Pure, stateless catalog rendering (SRP): a function of [state] only,
 * emitting intent via the trailing lambdas. Has no knowledge of
 * [TasbihViewModel] or any other screen's ViewModel type.
 */
@Composable
fun DhikrLibraryContent(
    state: DhikrLibraryState,
    onSearch: (String) -> Unit,
    onSelectDhikr: (String?) -> Unit,
    onCountNow: (key: String) -> Unit,
    onOpenGallery: () -> Unit
) {
    val strings = LocalStrings.current

    val selectedItem = remember(state.selectedDhikrKey, state.categorized) {
        state.selectedDhikrKey?.let { key ->
            state.categorized.values.flatten().find { it.key == key }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
    ) {
        SearchBar(
            query = state.searchQuery,
            onQueryChange = onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (state.categorized.isEmpty()) {
            EmptySearchResult(query = state.searchQuery)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categorized.forEach { (category, items) ->
                    stickyHeader(key = "header_${category.name}") {
                        CategoryHeader(strings.categoryLabel(category))
                    }
                    items(items, key = { "item_${it.key}" }) { item ->
                        DhikrCard(
                            modifier = Modifier.animateItem(),
                            item = item,
                            language = state.language,
                            onSelect = { onSelectDhikr(item.key) }
                        )
                    }
                    if (category == com.kutubuddin.sabeel.domain.model.DhikrCategory.ASMA_UL_HUSNA && state.searchQuery.isBlank()) {
                        item(key = "explore_gallery_card") {
                            ExploreGalleryCard(onClick = onOpenGallery)
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    if (selectedItem != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val configuration = LocalConfiguration.current
        val maxSheetHeight = configuration.screenHeightDp.dp * 0.85f

        ModalBottomSheet(
            onDismissRequest = { onSelectDhikr(null) },
            sheetState = sheetState,
            containerColor = SabeelColors.SurfaceElevated,
            dragHandle = { BottomSheetDefaults.DragHandle(color = SabeelColors.BorderIdle) }
        ) {
            Box(modifier = Modifier.heightIn(max = maxSheetHeight)) {
                DhikrDetailSheetContent(
                    item = selectedItem,
                    language = state.language,
                    showTransliteration = state.showTransliteration,
                    onCountNow = {
                        onSelectDhikr(null)
                        onCountNow(selectedItem.key)
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier) {
    val strings = LocalStrings.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Filled.Search, contentDescription = null, tint = SabeelColors.TextSecondary, modifier = Modifier.size(18.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = SabeelColors.TextPrimary),
            cursorBrush = SolidColor(SabeelColors.AccentTeal),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(strings.dhikrSearchPlaceholder, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextHint)
                }
                inner()
            }
        )
    }
}

// Delegates to the shared CategoryHeader (SabeelComponents.kt).
// Kept as a private alias so existing call-sites in this file need no changes.
@Composable
private fun CategoryHeader(name: String) = SharedCategoryHeader(name = name)

@Composable
private fun DhikrCard(
    item: DhikrItem,
    language: String,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val isSmartFlow = item.isSmartFlow
    val sequence = remember(item.key, isSmartFlow) { if (isSmartFlow) DhikrCatalog.sequenceFor(item.key) else null }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .border(1.dp, SabeelColors.BorderIdle, RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect)
            .padding(start = 17.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
    ) {
        // Shared Header Block
        if (!isSmartFlow) {
            Text(
                text = item.arabicText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = SabeelColors.ArabicText,
                textAlign = TextAlign.End,
                style = arabicStyle.copy(
                    fontSize = 20.sp,
                    lineHeight = 38.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.displayName.get(language),
                style = MaterialTheme.typography.titleSmall,
                color = SabeelColors.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CountPill(target = item.defaultTarget, language = language)
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = strings.wirdExpandRowA11y,
                    tint = SabeelColors.TextHint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (isSmartFlow && sequence != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                sequence.steps.forEach { step ->
                    MiniTargetChip(target = step.target, language = language)
                }
            }
        }
    }
}

@Composable
private fun DhikrDetailSheetContent(
    item: DhikrItem,
    language: String,
    showTransliteration: Boolean,
    onCountNow: () -> Unit
) {
    val strings = LocalStrings.current
    val isSmartFlow = item.isSmartFlow
    val sequence = remember(item.key, isSmartFlow) { if (isSmartFlow) DhikrCatalog.sequenceFor(item.key) else null }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            if (!isSmartFlow) {
                Text(
                    text = item.arabicText,
                    color = SabeelColors.ArabicText,
                    textAlign = TextAlign.End,
                    style = arabicStyle.copy(
                        fontSize = 28.sp,
                        lineHeight = 53.2.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            Text(
                text = item.displayName.get(language),
                style = MaterialTheme.typography.titleLarge,
                color = SabeelColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!isSmartFlow) {
                SinglePhraseBody(item = item, language = language, showTransliteration = showTransliteration)
            } else if (sequence != null) {
                SequenceBody(sequence = sequence, meaning = item.meaning.get(language), language = language)
            }

            val reward = item.spiritualReward.get(language)
            val hasReward = reward.isNotBlank()
            val hasRef = item.hadithRef.isNotBlank()

            if (hasReward || hasRef) {
                Spacer(Modifier.height(24.dp))
                RewardCallout(
                    reward = reward,
                    hadithRef = if (hasRef) strings.dhikrRef.format(com.kutubuddin.sabeel.ui.i18n.localizeHadithRef(item.hadithRef, language)) else ""
                )
            }
            Spacer(Modifier.height(16.dp)) // padding at bottom of scroll content
        }

        // Fixed Bottom Action
        Button(
            onClick = onCountNow,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SabeelColors.AccentTeal
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = strings.dhikrCountNow,
                color = SabeelColors.OnAccentTeal,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/** Single-phrase expanded content: quiet transliteration caption + promoted meaning. */
@Composable
private fun SinglePhraseBody(item: DhikrItem, language: String, showTransliteration: Boolean = true) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (showTransliteration) {
            item.transliteration?.let {
                Text(
                    text = it.get(language),
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = SabeelColors.TextSecondary
                )
            }
        }
        Text(
            text = item.meaning.get(language),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = SabeelColors.TextPrimary
        )
    }
}

/** Sequence expanded content: short caption + numbered step-by-step breakdown. */
@Composable
private fun SequenceBody(sequence: DhikrSequence, meaning: String, language: String) {
    val connectorColor = SabeelColors.BorderIdle

    Text(
        text = meaning,
        style = MaterialTheme.typography.bodyMedium,
        color = SabeelColors.TextSecondary,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // MEMORY CHURN OPTIMIZATION: Use drawWithCache instead of drawBehind
            // to avoid allocating Offsets on every single frame rendering pass.
            .drawWithCache {
                val circleRadius = 13.dp.toPx()
                val strokeWidthPx = 1.5.dp.toPx()
                val startOffset = Offset(circleRadius, circleRadius)
                val endOffset = Offset(circleRadius, size.height - circleRadius)
                
                onDrawBehind {
                    drawLine(
                        color = connectorColor,
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = strokeWidthPx
                    )
                }
            }
    ) {
        sequence.steps.forEachIndexed { index, step ->
            SequenceStepRow(number = index + 1, step = step, language = language)
        }
    }
}
@Composable
private fun SequenceStepRow(number: Int, step: DhikrStep, language: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(SabeelColors.AccentTealSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toLocalizedNumerals(language),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = SabeelColors.AccentTeal,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = step.displayName.get(language),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = SabeelColors.TextPrimary,
            modifier = Modifier.weight(0.35f)
        )

        Text(
            text = step.arabicText,
            color = SabeelColors.ArabicText,
            textAlign = TextAlign.End,
            style = arabicStyle.copy(fontSize = 19.sp, lineHeight = 30.sp),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(0.65f)
        )

        CountPill(target = step.target, language = language)
    }
}

/** The teal count badge — used for both the header total and per-step targets. */
@Composable
private fun CountPill(target: Int, language: String) {
    Text(
        text = "${target.toLocalizedNumerals(language)}\u00D7",
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp, fontWeight = FontWeight.SemiBold),
        color = SabeelColors.AccentTeal,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(SabeelColors.AccentTealSurface)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/** Muted collapsed-state chip — previews a sequence step's target without the teal accent. */
@Composable
private fun MiniTargetChip(target: Int, language: String) {
    Text(
        text = "${target.toLocalizedNumerals(language)}\u00D7",
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        color = SabeelColors.TextSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(SabeelColors.SurfaceElevated)
            .padding(horizontal = 9.dp, vertical = 3.dp)
    )
}

/** Flat, icon-led reward block — replaces the old bordered/striped nested box. */
@Composable
private fun RewardCallout(reward: String, hadithRef: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SabeelColors.Background)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            tint = SabeelColors.AccentTeal,
            modifier = Modifier
                .size(17.dp)
                .padding(top = 1.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            if (reward.isNotBlank()) {
                Text(
                    text = reward,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SabeelColors.TextPrimary
                )
            }
            if (hadithRef.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = hadithRef,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.2.sp
                        ),
                        color = SabeelColors.TextSecondary
                    )
                }
            }
        }
    }
}



@Composable
private fun EmptySearchResult(query: String) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            tint = SabeelColors.TextSecondary,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(strings.dhikrNoResults.format(query), style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary)
        Text(strings.dhikrSearchHint, style = MaterialTheme.typography.bodySmall, color = SabeelColors.TextHint)
    }
}
@Composable
private fun ExploreGalleryCard(onClick: () -> Unit) {
    val strings = LocalStrings.current

    // Gold-tinted surface + Arabic "٩٩" numeral makes this card
    // unambiguously distinct from the dhikr list cards around it.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.GoldSurface)
            .border(
                1.dp,
                SabeelColors.GoldPrimary.copy(alpha = 0.40f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gold left stripe — mirrors DhikrCard's expanded accent stripe
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(72.dp)
                .background(SabeelColors.GoldPrimary.copy(alpha = 0.7f))
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = strings.exploreGalleryTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = SabeelColors.GoldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = strings.exploreGallerySubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SabeelColors.TextSecondary
                )
            }
            // Arabic "٩٩" — the 99 Names, rendered in Uthmanic script
            // as the card's identity icon. No search icon needed.
            Text(
                text = "\u06F9\u06F9",  // ٩٩ (Extended Arabic-Indic digits)
                style = arabicStyle.copy(
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = SabeelColors.GoldPrimary.copy(alpha = 0.85f)
            )
        }
    }
}
