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
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.domain.model.DhikrItem
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
        onToggleExpand = viewModel::onToggleExpand,
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
    onToggleExpand: (String) -> Unit,
    onCountNow: (key: String) -> Unit,
    onOpenGallery: () -> Unit
) {
    val strings = LocalStrings.current

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
                            // animateItem() is the fix for the "one card animates, its
                            // neighbours teleport" gap: whenever this card's own height
                            // changes (expand/collapse) or the list is filtered by search,
                            // every sibling in the LazyColumn animates to its new position
                            // instead of snapping there in the same frame.
                            modifier = Modifier.animateItem(),
                            item = item,
                            language = state.language,
                            isExpanded = state.expandedKey == item.key,
                            onToggle = { onToggleExpand(item.key) },
                            onCountNow = { onCountNow(item.key) }
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
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onCountNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val stripeWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { 3.dp.toPx() }

    val borderColor by animateColorAsState(
        targetValue = if (isExpanded) SabeelColors.AccentTeal.copy(alpha = 0.6f)
                      else SabeelColors.BorderIdle,
        animationSpec = tween(durationMillis = SabeelMotion.Duration.ColorTransition), label = "border"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isExpanded) SabeelColors.SurfaceElevated else SabeelColors.Surface,
        animationSpec = tween(durationMillis = SabeelMotion.Duration.ColorTransition), label = "bg"
    )
    val stripeColor by animateColorAsState(
        targetValue = if (isExpanded) SabeelColors.AccentTeal else Color.Transparent,
        animationSpec = tween(durationMillis = SabeelMotion.Duration.ColorTransition), label = "stripe"
    )

    // Chevron: rotates 0° (collapsed) → 180° (expanded).
    // Spring spec matches the AnimatedVisibility body expansion so icon and
    // content reach their final position at the same time — they feel coupled.
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = SabeelMotion.Spring.ChevronRotate,
        label = "chevron_rotation"
    )
    // Tint transitions with the same colour-transition duration as border/bg/stripe —
    // consistent animation language across all card state changes.
    val chevronTint by animateColorAsState(
        targetValue = if (isExpanded) SabeelColors.AccentTeal else SabeelColors.TextHint,
        animationSpec = tween(SabeelMotion.Duration.ColorTransition),
        label = "chevron_tint"
    )

    // Arabic text: font size and line height animate on the same spring as the
    // card body so the text grows in sync with the expand animation.
    // Using animateFloatAsState (raw float → .sp) is the correct approach:
    // Compose cannot interpolate TextStyle objects, so we animate the scalar
    // values directly and apply them each frame.
    val arabicFontSize by animateFloatAsState(
        targetValue = if (isExpanded) 28f else 20f,
        animationSpec = SabeelMotion.Spring.CardExpand(),
        label = "arabic_font_size"
    )
    val arabicLineHeight by animateFloatAsState(
        targetValue = if (isExpanded) 53.2f else 38f,
        animationSpec = SabeelMotion.Spring.CardExpand(),
        label = "arabic_line_height"
    )

    // FIX 6: gold demoted to teal here — it’s per-row chrome, not a milestone.
    // Gold now survives only on the 99-Names hero card and completion victories.
    val accentColor = SabeelColors.AccentTeal

    // drawBehind draws the stripe onto the canvas AFTER layout, so size.height is always
    // the real current height — it follows AnimatedVisibility frames naturally.
    // matchParentSize() was wrong: it overrides width(3.dp) and filled the entire card.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .drawBehind {
                drawRect(
                    color = stripeColor,
                    size = androidx.compose.ui.geometry.Size(stripeWidthPx, size.height)
                )
            }
            .clickable(onClick = onToggle)
            .padding(start = 17.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
    ) {
            // Arabic — full width, right-aligned. Expands to 28sp when open.
            Text(
                text = item.arabicText,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                color = SabeelColors.ArabicText,
                textAlign = TextAlign.End,
                style = arabicStyle.copy(
                    fontSize = arabicFontSize.sp,
                    lineHeight = arabicLineHeight.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Name row — chevron here, semantically paired with the name it expands.
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
                    Text(
                        text = "· ${item.defaultTarget.toLocalizedNumerals(language)}×",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 0.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = SabeelColors.AccentTeal
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = strings.wirdExpandRowA11y,
                        tint = chevronTint,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer { rotationZ = chevronRotation }
                    )
                }
            }

            // AnimatedVisibility: fade+expand on open, fade+shrink on close.
            // Works correctly here because the outer Column (not a Row+IntrinsicSize)
            // measures height from its children's actual layout height each frame.
            AnimatedVisibility(
                visible = isExpanded,
                enter = androidx.compose.animation.expandVertically(
                    animationSpec = SabeelMotion.Spring.CardExpand()
                ) + androidx.compose.animation.fadeIn(
                    animationSpec = tween(200)
                ),
                exit = androidx.compose.animation.shrinkVertically(
                    animationSpec = SabeelMotion.Spring.CardExpand()
                ) + androidx.compose.animation.fadeOut(
                    animationSpec = tween(150)
                )
            ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    color = SabeelColors.AccentTeal.copy(alpha = 0.25f),
                    thickness = 0.5.dp
                )
                Spacer(Modifier.height(6.dp))

                // Transliteration + meaning
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    item.transliteration?.let {
                        Text(
                            it.get(language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                letterSpacing = 0.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = SabeelColors.TextSecondary
                        )
                    }
                    Text(
                        item.meaning.get(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = SabeelColors.TextPrimary,
                        lineHeight = 18.sp
                    )
                }

                // Reward box — text stays TextPrimary/TextSecondary for contrast.
                // FIX 6: the stripe + border accent is now teal (accentColor),
                // matching the card's expanded stripe; gold is milestone-only.
                val reward = item.spiritualReward.get(language)
                val hasReward = reward.isNotBlank()
                val hasRef = item.hadithRef.isNotBlank()
                if (hasReward || hasRef) {
                    Spacer(Modifier.height(2.dp))
                    // Reward box: drawBehind for the gold stripe (same fix as card stripe).
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SabeelColors.SurfaceElevated)
                            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .drawBehind {
                                drawRect(
                                    color = accentColor,
                                    size = androidx.compose.ui.geometry.Size(stripeWidthPx, size.height)
                                )
                            }
                            .padding(start = 15.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (hasReward) {
                            Text(
                                text = reward,
                                style = MaterialTheme.typography.bodySmall,
                                color = SabeelColors.TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                        if (hasRef) {
                            Text(
                                text = strings.dhikrRef.format(
                                    localizeHadithRef(item.hadithRef, language)
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = SabeelColors.TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Count Now button
                Button(
                    onClick = onCountNow,
                    modifier = Modifier.fillMaxWidth(),
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
            } // Column (expanded body)
            } // AnimatedVisibility
        } // Card Column
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
