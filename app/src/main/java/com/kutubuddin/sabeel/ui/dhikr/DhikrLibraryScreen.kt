package com.kutubuddin.sabeel.ui.dhikr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
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
import com.kutubuddin.sabeel.ui.theme.arabicStyle

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
    viewModel: DhikrViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DhikrLibraryContent(
        state = state,
        onSearch = viewModel::onSearch,
        onToggleExpand = viewModel::onToggleExpand,
        onCountNow = { key ->
            tasbihViewModel.processIntent(TasbihIntent.SetDhikr(key))
            onCountNow()
        }
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
    onCountNow: (key: String) -> Unit
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
                            item = item,
                            language = state.language,
                            isExpanded = state.expandedKey == item.key,
                            onToggle = { onToggleExpand(item.key) },
                            onCountNow = { onCountNow(item.key) }
                        )
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
            textStyle = TextStyle(color = SabeelColors.TextPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(SabeelColors.AccentTeal),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(strings.dhikrSearchPlaceholder, fontSize = 15.sp, color = SabeelColors.TextHint)
                }
                inner()
            }
        )
    }
}

@Composable
private fun CategoryHeader(name: String) {
    Text(
        text = name.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.8.sp,
        color = SabeelColors.TextSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .background(SabeelColors.Background)
            .padding(vertical = 8.dp)
    )
}

@Composable
private fun DhikrCard(
    item: DhikrItem,
    language: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onCountNow: () -> Unit
) {
    val strings = LocalStrings.current
    val layoutDirection = LocalLayoutDirection.current
    // IX-06 fix: this border alone used to be the only signal that a card was
    // "open", using the same AccentTeal language as Home's hero CTA card (a
    // teal-tinted fill + teal border) — a user who's learned "teal outline =
    // the important thing to do next" from Home could misread an expanded
    // list item as similarly featured. The fill now shifts to a neutral
    // elevation tone instead of a teal tint, so "currently open" reads as its
    // own state rather than a dimmer copy of "primary/recommended".
    val borderColor = if (isExpanded) SabeelColors.AccentTeal.copy(alpha = 0.6f)
    else SabeelColors.BorderIdle
    val backgroundColor = if (isExpanded) SabeelColors.SurfaceElevated else SabeelColors.Surface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Explicit chevron so the card visibly announces "tap to expand"
                // instead of relying on the whole surface being silently clickable.
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = strings.wirdExpandRowA11y,
                    tint = SabeelColors.TextSecondary,
                    modifier = Modifier.padding(top = 2.dp).size(18.dp)
                )
                Text(
                    text = item.arabicText,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    color = SabeelColors.ArabicText,
                    textAlign = TextAlign.End,
                    style = if (isExpanded) arabicStyle else arabicStyle.copy(fontSize = 20.sp, lineHeight = 32.sp),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.displayName.get(language),
                    fontSize = 14.sp,
                    color = SabeelColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                // Plain secondary text instead of a filled AccentTealSurface pill —
                // a colored pill reads as "tap me", but this is the static default
                // target, not an action. Filled/colored pills are reserved for real
                // controls (Count Now below).
                Text(
                    text = "${strings.dhikrTargetLabel} ${item.defaultTarget.toLocalizedNumerals(language)}×",
                    fontSize = 12.sp,
                    color = SabeelColors.TextSecondary
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
            exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(color = SabeelColors.Divider)

                // Transliteration + meaning are one reading cluster (pronunciation
                // aid, then the payload) — tight 4dp spacing groups them visually
                // instead of both reading as equally-weighted, unrelated lines.
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // TY-03: italics measurably slow reading versus upright text;
                    // stacking that with secondary-gray + 13sp on what's
                    // functionally instructional text (not a quotation) spent
                    // italic's "this is different" signal in the wrong place.
                    item.transliteration?.let {
                        Text(it, fontSize = 13.sp, color = SabeelColors.TextSecondary)
                    }

                    val meaning = when (language) {
                        "ur" -> item.meaning.ur.ifBlank { item.meaning.en }
                        "bn" -> item.meaning.bn.ifBlank { item.meaning.en }
                        else -> item.meaning.en
                    }
                    Text(meaning, fontSize = 14.sp, color = SabeelColors.TextPrimary)
                }

                val reward = item.spiritualReward.get(language)
                val hasReward = reward.isNotBlank()
                val hasRef = item.hadithRef.isNotBlank()
                if (hasReward || hasRef) {
                    // Reward + reference are their own "context" cluster, set apart
                    // from the translation above with a distinct surface instead of
                    // sitting in the same flat stack — five same-weight lines in a
                    // row was hard to parse at a glance.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SabeelColors.SurfaceElevated)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (hasReward) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    tint = SabeelColors.SageGreen,
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(14.dp)
                                )
                                Text(
                                    text = reward,
                                    fontSize = 12.sp,
                                    color = SabeelColors.SageGreen,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        if (hasRef) {
                            Text(
                                text = strings.dhikrRef.format(localizeHadithRef(item.hadithRef, language)),
                                fontSize = 11.sp,
                                color = SabeelColors.TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = onCountNow,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val arrow = if (layoutDirection == LayoutDirection.Rtl) "←" else "→"
                    Text(
                        text = "${strings.dhikrCountNow}  $arrow",
                        color = SabeelColors.Background,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
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
        Text(strings.dhikrNoResults.format(query), fontSize = 14.sp, color = SabeelColors.TextSecondary)
        Text(strings.dhikrSearchHint, fontSize = 12.sp, color = SabeelColors.TextHint)
    }
}