package com.kutubuddin.sabeel.ui.dhikr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
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
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.ui.tasbih.TasbihIntent
import com.kutubuddin.sabeel.ui.tasbih.TasbihViewModel
import com.kutubuddin.sabeel.ui.i18n.localizeHadithRef
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

@Composable
fun DhikrLibraryScreen(
    tasbihViewModel: TasbihViewModel,
    onCountNow: () -> Unit,
    viewModel: DhikrViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
    ) {
        // ── Search bar ────────────────────────────────────────────────────────
        SearchBar(
            query = state.searchQuery,
            onQueryChange = viewModel::onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // ── Catalog list ──────────────────────────────────────────────────────
        if (state.categorized.isEmpty()) {
            EmptySearchResult(query = state.searchQuery)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categorized.forEach { (category, items) ->
                    // Sticky category header. NOTE: LazyColumn shares ONE key
                    // namespace across headers AND items, so a bare category.name
                    // ("TAHLIL") collides with a dhikr key ("TAHLIL") and crashes.
                    // Prefix to keep the two namespaces disjoint.
                    stickyHeader(key = "header_${category.name}") {
                        CategoryHeader(category.displayName)
                    }
                    items(items, key = { "item_${it.key}" }) { item ->
                        DhikrCard(
                            item = item,
                            language = state.language,
                            isExpanded = state.expandedKey == item.key,
                            onToggle = { viewModel.onToggleExpand(item.key) },
                            onCountNow = {
                                // Any catalog key is countable — the ViewModel resolves
                                // it against the full catalog, no enum coercion needed.
                                tasbihViewModel.processIntent(TasbihIntent.SetDhikr(item.key))
                                onCountNow()
                            }
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
                    Text("Search dhikr…", fontSize = 15.sp, color = SabeelColors.TextHint)
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
    val borderColor = if (isExpanded) SabeelColors.AccentTeal.copy(alpha = 0.6f)
                      else SabeelColors.BorderIdle

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle)
    ) {
        // ── Collapsed card ────────────────────────────────────────────────────
        // Arabic spans the full width on top; the English name and target badge
        // share the bottom baseline — no diagonal dead space.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Arabic — let the engine clip on a grapheme boundary (never .take(40),
            // which cuts mid-ligature), RTL-aligned in both states.
            // One Arabic source of truth: truncates to a single line collapsed,
            // expands to the full (multi-line) text on tap — no duplicate below.
            Text(
                text = item.arabicText,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                color = SabeelColors.ArabicText,
                textAlign = TextAlign.End,
                style = if (isExpanded) arabicStyle else arabicStyle.copy(fontSize = 20.sp, lineHeight = 32.sp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.displayName,
                    fontSize = 14.sp,
                    color = SabeelColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                // Target badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SabeelColors.AccentTealSurface)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("${item.defaultTarget.toLocalizedNumerals(language)}×", fontSize = 13.sp, color = SabeelColors.AccentTeal, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── Expanded details ──────────────────────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalDivider(color = SabeelColors.Divider)

                // (Arabic is rendered once in the collapsed header, which expands
                // to full multi-line text above — no duplicate here.)

                // Transliteration
                item.transliteration?.let {
                    Text(it, fontSize = 13.sp, color = SabeelColors.TextSecondary, fontStyle = FontStyle.Italic)
                }

                // Meaning in selected language
                val meaning = when (language) {
                    "ur" -> item.meaning.ur.ifBlank { item.meaning.en }
                    "bn" -> item.meaning.bn.ifBlank { item.meaning.en }
                    else -> item.meaning.en
                }
                Text(meaning, fontSize = 13.sp, color = SabeelColors.TextPrimary)

                // Spiritual reward — localized to the selected language.
                val reward = item.spiritualReward.get(language)
                if (reward.isNotBlank()) {
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

                // Hadith reference — the trust anchor, so it must be legible.
                if (item.hadithRef.isNotBlank()) {
                    Text(
                        text = "Ref: ${localizeHadithRef(item.hadithRef, language)}",
                        fontSize = 12.sp,
                        color = SabeelColors.TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                }

                // Count Now button
                Button(
                    onClick = onCountNow,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Count Now  →",
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
        Text("No dhikr found for \"$query\"", fontSize = 14.sp, color = SabeelColors.TextSecondary)
        Text("Try searching in Arabic or English", fontSize = 12.sp, color = SabeelColors.TextHint)
    }
}
