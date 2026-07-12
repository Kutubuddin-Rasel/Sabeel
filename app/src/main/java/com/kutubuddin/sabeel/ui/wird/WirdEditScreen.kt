package com.kutubuddin.sabeel.ui.wird

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.components.SabeelTopBar
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
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
import com.kutubuddin.sabeel.ui.components.CategoryHeader

/**
 * Route-level wrapper (DIP): sole owner of ViewModel injection, state
 * collection, and the ephemeral (non-business) picker-visibility flag.
 * Rendering is delegated to the stateless [WirdEditContent].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WirdEditScreen(
    onBack: () -> Unit,
    viewModel: WirdEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var hasAutoOpened by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.rows.isEmpty()) {
        if (state.rows.isEmpty() && !hasAutoOpened) {
            delay(50) // Prevent flash during initial DB load
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
            viewModel.addDhikr(key, target)
            showPicker = false
        },
        onUpdateTarget = viewModel::updateTarget,
        onRemove = viewModel::remove,
        onReorder = viewModel::reorder
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
    onReorder: (keys: List<String>) -> Unit
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
            itemStates = state.rows.map { true },
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
                }
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
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.White)
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
                                                        onReorder(localRows.map { it.dhikrKey })
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
        ModalBottomSheet(
            onDismissRequest = onDismissPicker,
            containerColor = SabeelColors.Surface,
            // IX-01 fix: the default M3 scrim reads as almost imperceptible
            // over an already near-black Background, so the sheet doesn't
            // clearly read as modal. A stronger, explicit scrim fixes the
            // perceptibility regardless of how dark the base theme is.
            scrimColor = SabeelColors.Background.copy(alpha = 0.75f)
        ) {
            LazyColumn(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                // 3C: Group by category so the picker reads as a structured menu,
                // not an undifferentiated list. groupBy preserves catalog order within
                // each category; no ViewModel change required.
                val grouped = state.pickable.groupBy { it.category }
                grouped.forEach { (category, items) ->
                    stickyHeader(key = "header_${category.name}") {
                        CategoryHeader(
                            name = strings.categoryLabel(category),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    items(items, key = { it.key }) { d ->
                        Row(
                            Modifier.fillMaxWidth()
                                .clickable { onAddDhikr(d.key, d.defaultTarget) }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(d.displayName.get(state.language), style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextPrimary)
                            Text("${d.defaultTarget.toLocalizedNumerals(state.language)}×",
                                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp), color = SabeelColors.AccentTeal)
                        }
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