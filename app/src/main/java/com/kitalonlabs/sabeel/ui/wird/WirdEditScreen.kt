package com.kitalonlabs.sabeel.ui.wird

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kitalonlabs.sabeel.ui.components.SabeelTopBar
import com.kitalonlabs.sabeel.ui.i18n.LocalStrings
import com.kitalonlabs.sabeel.ui.i18n.toLocalizedNumerals
import com.kitalonlabs.sabeel.ui.theme.SabeelColors
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.rememberLazyListState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.foundation.border
import com.kitalonlabs.sabeel.ui.components.CategoryHeader
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import com.kitalonlabs.sabeel.ui.theme.SabeelMotion
import com.kitalonlabs.sabeel.domain.model.DhikrCategory
import com.kitalonlabs.sabeel.domain.model.DhikrItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Route-level wrapper (DIP): sole owner of ViewModel injection, state
 * collection, and the ephemeral (non-business) picker-visibility flag.
 * Rendering is delegated to the stateless [WirdEditContent].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WirdEditScreen(
    onBack: () -> Unit,
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {},
    viewModel: WirdEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var hasAutoOpened by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.rows.isEmpty()) {
        if (state.rows.isEmpty() && !hasAutoOpened) {
            delay(350) // Wait for 300ms screen enter transition to finish to prevent double-slide collision
            if (state.rows.isEmpty()) {
                showPicker = true
                hasAutoOpened = true
            }
        }
    }

    WirdEditContent(
        state = state,
        showPicker = showPicker,
        onBack = onBack,
        onRequestPicker = { showPicker = true },
        onDismissPicker = { showPicker = false },
        onAddDhikr = { key, target ->
            if (showTooltip) onTooltipDismiss()
            viewModel.addDhikr(key, target)
            showPicker = false
        },
        onUpdateTarget = viewModel::updateTarget,
        onRemove = viewModel::remove,
        onReorder = viewModel::reorder,
        showTooltip = showTooltip,
        onTooltipDismiss = onTooltipDismiss
    )
}

/**
 * Pure, stateless edit-the-plan UI — a function of [state] and the ephemeral
 * [showPicker] flag only. Every mutation is emitted through a callback; this
 * composable never references [WirdEditViewModel] (SRP).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WirdEditContent(
    state: WirdEditState,
    showPicker: Boolean,
    onBack: () -> Unit,
    onRequestPicker: () -> Unit,
    onDismissPicker: () -> Unit,
    onAddDhikr: (key: String, target: Int) -> Unit,
    onUpdateTarget: (key: String, target: Int) -> Unit,
    onRemove: (key: String) -> Unit,
    onReorder: (keys: ImmutableList<String>) -> Unit,
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {}
) {
    val strings = LocalStrings.current

    var expandedKey by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
            .navigationBarsPadding()
    ) {
        SabeelTopBar(
            title = strings.wirdEditTitle,
            onBack = onBack,
            backContentDescription = strings.a11yBack,
            actions = {
                Text(
                    text = strings.wirdDone,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SabeelColors.AccentTeal,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(onClick = onBack)
                )
            }
        )
        HorizontalDivider(color = SabeelColors.Divider)

        // FIX 7: allocation preview — every bead is filled (all `true`) so the
        // arc reads full and the dots all fill. This screen shows the plan's
        // composition (target total in the centre), not daily progress; compare
        // WirdScreen, where the same composable is fed real completion state.
        WirdVisualRing(
            itemStates = state.rows.map { true }.toImmutableList(),
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
        ) {
            val totalTarget = state.rows.sumOf { it.target }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = totalTarget.toLocalizedNumerals(state.language),
                    style = MaterialTheme.typography.displayMedium,
                    color = SabeelColors.TextPrimary
                )
                Text(
                    text = strings.wirdRingCaptionTarget,
                    style = MaterialTheme.typography.bodySmall,
                    color = SabeelColors.TextSecondary
                )
            }
        }

        if (state.rows.isEmpty()) {
            Column(
                Modifier.weight(1f).fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Spa, contentDescription = null, tint = SabeelColors.AccentTeal, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text(strings.wirdGoalHintTitle, style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(strings.wirdGoalHintBody, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            var localRows by remember(state.rows) { mutableStateOf(state.rows) }
            val lazyListState = rememberLazyListState()
            val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
                localRows = localRows.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }.toImmutableList()
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(localRows, key = { it.dhikrKey }) { row ->
                    val isExpanded = expandedKey == row.dhikrKey
                    
                    ReorderableItem(reorderState, key = row.dhikrKey) { isDragging ->
                        val elevation by animateFloatAsState(if (isDragging) 8f else 0f)
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    onRemove(row.dhikrKey)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) SabeelColors.Danger else SabeelColors.Surface
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(16.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Outlined.Delete, contentDescription = strings.a11yDelete, tint = Color.White)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SabeelColors.Surface)
                                    .border(1.dp, SabeelColors.BorderIdle, RoundedCornerShape(16.dp))
                                    .clickable { expandedKey = if (isExpanded) null else row.dhikrKey }
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Explicit chevron so the row visibly announces
                                        // "tap to expand" instead of relying on the whole
                                        // surface being silently clickable.
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = strings.wirdExpandRowA11y,
                                            tint = SabeelColors.TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = row.displayName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = SabeelColors.TextPrimary
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // Plain secondary text, not a bold AccentTeal
                                        // number — filled/bold accent styling is now
                                        // reserved for real controls (the segmented
                                        // target picker below), so this reads as info,
                                        // not as a button.
                                        Text(
                                            text = "${strings.wirdTargetA11y} ${row.target.toLocalizedNumerals(state.language)}×",
                                            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp),
                                            color = SabeelColors.TextSecondary
                                        )
                                        // Drag handle: a distinct dot-grip icon instead of
                                        // a two-bar glyph that reads as a menu button, so it
                                        // doesn't compete with the chevron for meaning.
                                        Icon(
                                            imageVector = Icons.Filled.DragIndicator,
                                            contentDescription = strings.wirdReorder,
                                            tint = SabeelColors.TextHint,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .draggableHandle(
                                                    onDragStopped = { 
                                                        onReorder(localRows.map { it.dhikrKey }.toImmutableList())
                                                    }
                                                )
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                                    exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                                ) {
                                    Column(Modifier.padding(top = 16.dp)) {
                                        HorizontalDivider(color = SabeelColors.Divider)
                                        Spacer(Modifier.height(16.dp))

                                        // Target Adjuster row — presets now show which
                                        // value is active (filled AccentTeal) instead of
                                        // looking identical whether selected or not, and
                                        // the stepper shows the live number inline so
                                        // adjusting a value doesn't require looking back
                                        // up at the header row to see what changed.
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(strings.wirdTargetA11y, style = MaterialTheme.typography.labelMedium, color = SabeelColors.TextSecondary)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                PresetChip(33, row.target == 33, state.language) { onUpdateTarget(row.dhikrKey, 33) }
                                                PresetChip(100, row.target == 100, state.language) { onUpdateTarget(row.dhikrKey, 100) }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SabeelColors.SurfaceElevated)
                                                ) {
                                                    Icon(Icons.Filled.Remove, contentDescription = null, tint = SabeelColors.AccentTeal, modifier = Modifier.clickable { onUpdateTarget(row.dhikrKey, row.target - 1) }.padding(8.dp).size(20.dp))
                                                    Text(
                                                        text = row.target.toLocalizedNumerals(state.language),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = SabeelColors.TextPrimary,
                                                        modifier = Modifier.padding(horizontal = 4.dp)
                                                    )
                                                    Icon(Icons.Filled.Add, contentDescription = null, tint = SabeelColors.AccentTeal, modifier = Modifier.clickable { onUpdateTarget(row.dhikrKey, row.target + 1) }.padding(8.dp).size(20.dp))
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        // Explicit, always-visible remove action — swipe-
                                        // to-dismiss on the collapsed row still works, but
                                        // it's an easy-to-never-discover gesture, especially
                                        // for TalkBack users. This gives every user a
                                        // visible path to the same outcome.
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = strings.wirdRemove,
                                                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp),
                                                color = SabeelColors.Danger,
                                                modifier = Modifier.clickable { onRemove(row.dhikrKey) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onRequestPicker,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = SabeelColors.Background)
            Spacer(Modifier.width(6.dp))
            Text(strings.wirdAddDhikr, color = SabeelColors.Background, fontWeight = FontWeight.Bold)
        }
    }

    if (showPicker) {
        // Ephemeral, sheet-local UI state — reset every time the sheet opens
        // (keyed on showPicker) so a stale search/expansion never lingers
        // into the next "Add dhikr" tap.
        var pickerQuery by rememberSaveable(showPicker) { mutableStateOf("") }
        var asmaExpanded by rememberSaveable(showPicker) { mutableStateOf(false) }

        ModalBottomSheet(
            onDismissRequest = onDismissPicker,
            containerColor = SabeelColors.Surface,
            // IX-01 fix: the default M3 scrim reads as almost imperceptible
            // over an already near-black Background, so the sheet doesn't
            // clearly read as modal. A stronger, explicit scrim fixes the
            // perceptibility regardless of how dark the base theme is.
            scrimColor = SabeelColors.Background.copy(alpha = 0.75f)
        ) {
            val query = pickerQuery.trim()

            // IX-PICKER-1: grouped by DhikrCategory.values() DECLARATION order —
            // the same order DhikrViewModel already uses for the main Dhikr
            // Library tab — instead of catalog list-concatenation order. The
            // old .groupBy{} put "99 Names of Allah" FIRST, because its single
            // "Sequence" summary entry happens to be the very first item in
            // DhikrCatalog.all — an accident of data layout, not a design
            // choice. Enum order puts Asma-ul-Husna second-to-last, matching
            // how the rest of the app already treats this category.
            //
            // IX-PICKER-2: individual Asma-ul-Husna names are excluded from
            // this default (non-search) grouping — the identical rule
            // DhikrViewModel already applies to the main Dhikr Library ("EXCLUDE
            // individual Asma Ul Husna items from the default view"). Only the
            // gold "explore" toggle below reveals them, on demand.
            val grouped = remember(state.pickable) {
                DhikrCategory.values().associateWith { cat ->
                    state.pickable.filter {
                        it.category == cat && (cat != DhikrCategory.ASMA_UL_HUSNA || it.key == "ASMA_ALL_99")
                    }
                }.filterValues { it.isNotEmpty() }
            }
            val asmaIndividualNames = remember(state.pickable) {
                state.pickable.filter { it.category == DhikrCategory.ASMA_UL_HUSNA && it.key != "ASMA_ALL_99" }
            }
            // Search reaches the FULL pickable catalog (all 99 names included) —
            val searchResults = remember(state.pickable, query) {
                if (query.isBlank()) {
                    emptyList()
                } else {
                    state.pickable.map { it to it.searchScore(query) }
                        .filter { it.second > 0 }
                        .sortedByDescending { it.second }
                        .map { it.first }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(key = "picker_header") {
                    Column(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        Text(
                            text = strings.wirdAddDhikr,
                            style = MaterialTheme.typography.titleLarge,
                            color = SabeelColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        PickerSearchBar(
                            query = pickerQuery,
                            onQueryChange = { pickerQuery = it }
                        )
                        com.kitalonlabs.sabeel.ui.components.SabeelTooltip(
                            visible = showTooltip,
                            text = if (state.language == "bn") "আপনার প্রতিদিনের লক্ষ্যের জন্য যে কোনো যিকির যোগ করুন।" else "Add any dhikr for your daily goal.",
                            position = com.kitalonlabs.sabeel.ui.components.TooltipPosition.Top,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                if (query.isBlank()) {
                    grouped.forEach { (category, items) ->
                        item(key = "header_${category.name}") {
                            PickerCategoryHeader(
                                name = strings.categoryLabel(category),
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                        item(key = "card_${category.name}") {
                            PickerCategoryCard(
                                items = items.toImmutableList(),
                                language = state.language,
                                onAdd = onAddDhikr,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                        if (category == DhikrCategory.ASMA_UL_HUSNA) {
                            item(key = "asma_explore_toggle") {
                                AsmaExploreToggle(
                                    expanded = asmaExpanded,
                                    remainingCount = asmaIndividualNames.size,
                                    categoryLabel = strings.categoryLabel(DhikrCategory.ASMA_UL_HUSNA),
                                    onClick = { asmaExpanded = !asmaExpanded },
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                            if (asmaExpanded) {
                                itemsIndexed(asmaIndividualNames, key = { _, d -> "asma_${d.key}" }) { index, d ->
                                    PickerRow(
                                        item = d,
                                        language = state.language,
                                        onAdd = onAddDhikr,
                                        shape = pickerRowShape(index, asmaIndividualNames.size),
                                        modifier = Modifier.padding(horizontal = 20.dp)
                                    )
                                }
                            }
                        }
                    }
                } else if (searchResults.isEmpty()) {
                    item(key = "no_results") {
                        PickerEmptyResult(
                            query = query,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp)
                        )
                    }
                } else {
                    itemsIndexed(searchResults, key = { _, d -> "search_${d.key}" }) { index, d ->
                        PickerRow(
                            item = d,
                            language = state.language,
                            onAdd = onAddDhikr,
                            shape = pickerRowShape(index, searchResults.size),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(target: Int, isActive: Boolean, language: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) SabeelColors.AccentTeal else SabeelColors.SurfaceElevated)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "${target.toLocalizedNumerals(language)}×",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp),
            color = if (isActive) SabeelColors.OnAccentTeal else SabeelColors.TextPrimary
        )
    }
}

// ── Add-Dhikr picker: premium redesign ────────────────────────────────────────────────
//
// Design goals (see the WirdEditContent picker block above for the data-side
// fixes — category ordering + Asma-ul-Husna exclusion):
//  1. Bigger, calmer typography: row names move from bodyMedium (14sp) to
//     bodyLarge (16sp); category labels move from labelMedium (12sp) to
//     titleSmall (14sp) — both read as considered choices, not default text.
//  2. Rows are grouped into soft "cards" per category (M3/iOS grouped-list
//     idiom: shared corners at the top/bottom of a run, small gaps between)
//     instead of a flat, undifferentiated list — this is what makes the
//     sheet feel premium rather than like a raw settings menu.
//  3. A comfortable 16dp vertical row padding (up from 14dp) keeps every row
//     comfortably above the 48dp minimum touch target with room to breathe.

/** Section label above a grouped picker card. Larger and bolder than the
 *  shared [CategoryHeader] used elsewhere — this sheet is the one place in
 *  the app doing double duty as a "menu," so its headers can afford more
 *  visual weight without fighting content elsewhere. */
@Composable
private fun PickerCategoryHeader(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name.uppercase(),
        style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold),
        color = SabeelColors.TextSecondary,
        modifier = modifier.fillMaxWidth()
    )
}

/** Corner treatment for a row at [index] within a group of [size] — full
 *  16dp rounding at the top/bottom edge of the group, a quiet 4dp everywhere
 *  else, with a 2dp gap between rows (see [PickerCategoryCard]). This reads
 *  as one cohesive card made of near-touching rows, the same grouped-list
 *  language iOS/M3 settings screens use, without needing to manage a single
 *  giant non-lazy container for what may become a 99-row expansion. */
private fun pickerRowShape(index: Int, size: Int): RoundedCornerShape {
    val top = if (index == 0) 16.dp else 4.dp
    val bottom = if (index == size - 1) 16.dp else 4.dp
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

/** A small, non-lazy "card" of rows for one category (After Prayer, Salawat,
 *  etc.) — always ≤ 5 items in practice, so a plain Column is cheap and lets
 *  every row in the group share one continuous visual block. */
@Composable
private fun PickerCategoryCard(
    items: ImmutableList<DhikrItem>,
    language: String,
    onAdd: (key: String, target: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, d ->
            PickerRow(
                item = d,
                language = language,
                onAdd = onAdd,
                shape = pickerRowShape(index, items.size)
            )
            if (index != items.lastIndex) {
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

/** One selectable dhikr row — used both inside [PickerCategoryCard] (as part
 *  of a grouped run) and standalone for search results / the expanded
 *  Asma-ul-Husna list (where [shape] is fully rounded on a singleton, or
 *  group-shaped across a virtualized run of many). */
@Composable
private fun PickerRow(
    item: DhikrItem,
    language: String,
    onAdd: (key: String, target: Int) -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SabeelColors.Surface)
            .border(1.dp, SabeelColors.BorderIdle, shape)
            .clickable { 
                // We don't have a direct onTooltipDismiss passed here, but clicking adds dhikr.
                // The parent could handle dismiss, or we can just let onAdd trigger a dismiss in the parent.
                onAdd(item.key, item.defaultTarget) 
            }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.displayName.get(language),
            style = MaterialTheme.typography.bodyLarge,
            color = SabeelColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(12.dp))
        // Target badge as a small filled pill (matching PresetChip's visual
        // language above) instead of bare accent-colored text — makes the
        // number read as metadata, not as a second competing action.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SabeelColors.AccentTealSurface)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "${item.defaultTarget.toLocalizedNumerals(language)}×",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp),
                color = SabeelColors.AccentTeal
            )
        }
    }
}

/** Gold toggle that reveals the 99 individual Asma-ul-Husna names on demand.
 *  Mirrors the "Explore Individual Names" gallery CTA already used on the
 *  Dhikr Library tab (ExploreGalleryCard) so the visual language for "there's
 *  more behind this card" is consistent across the app — here it expands
 *  inline instead of navigating, since this picker lives inside a bottom
 *  sheet, not a nav destination. */
@Composable
private fun AsmaExploreToggle(
    expanded: Boolean,
    remainingCount: Int,
    categoryLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = SabeelMotion.Spring.ChevronRotate,
        label = "asma_toggle_rotation"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.GoldSurface)
            .border(1.dp, SabeelColors.GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = strings.exploreGalleryTitle,
                style = MaterialTheme.typography.titleSmall,
                color = SabeelColors.GoldPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$remainingCount · $categoryLabel",
                style = MaterialTheme.typography.bodySmall,
                color = SabeelColors.TextSecondary
            )
        }
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = strings.wirdExpandRowA11y,
            tint = SabeelColors.GoldPrimary,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer { rotationZ = rotation }
        )
    }
}

/** Empty-search state, mirroring DhikrLibraryScreen's EmptySearchResult so a
 *  "no matches" moment looks and reads identically everywhere in the app. */
@Composable
private fun PickerEmptyResult(query: String, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            tint = SabeelColors.TextSecondary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = strings.dhikrNoResults.format(query),
            style = MaterialTheme.typography.bodyMedium,
            color = SabeelColors.TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = strings.dhikrSearchHint,
            style = MaterialTheme.typography.bodySmall,
            color = SabeelColors.TextHint,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/** Search field for the Add-Dhikr sheet — mirrors DhikrLibraryScreen's private
 *  SearchBar (same visual language, larger bodyLarge text to match this
 *  sheet's bumped-up type scale). Duplicated rather than shared because the
 *  library's SearchBar is private to that file; promoting it to a shared
 *  component is a reasonable follow-up if a third call site ever appears. */
@Composable
private fun PickerSearchBar(query: String, onQueryChange: (String) -> Unit) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.SurfaceElevated)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = SabeelColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = SabeelColors.TextPrimary),
            cursorBrush = SolidColor(SabeelColors.AccentTeal),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = strings.dhikrSearchPlaceholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = SabeelColors.TextHint
                    )
                }
                inner()
            }
        )
    }
}