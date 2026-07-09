package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import kotlinx.coroutines.delay

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
        onMove = viewModel::move
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
    onMove: (key: String, up: Boolean) -> Unit
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
                    color = SabeelColors.AccentTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(onClick = onBack)
                )
            }
        )
        HorizontalDivider(color = SabeelColors.Divider)

        // Allocation only — every slice is `isComplete = true` so the ring
        // reads as pure composition here, not daily progress. Compare with
        // WirdScreen, where the same composable is fed real completion state.
        //
        // IA-01 fix: this ring shows the target total here, but a completion
        // count (completed/total) on WirdScreen — a caption now says which,
        // and every slice is labeled below via WirdRingLegend (CT-02).
        WirdVisualRing(
            slices = state.rows.map { WirdRingSlice(target = it.target, label = it.displayName) },
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
        ) {
            val totalTarget = state.rows.sumOf { it.target }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = totalTarget.toLocalizedNumerals(state.language),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = SabeelColors.TextPrimary
                )
                Text(
                    text = strings.wirdRingCaptionTarget,
                    fontSize = 12.sp,
                    color = SabeelColors.TextSecondary
                )
            }
        }
        if (state.rows.isNotEmpty()) {
            WirdRingLegend(
                slices = state.rows.map { WirdRingSlice(target = it.target, label = it.displayName) },
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )
        }

        if (state.rows.isEmpty()) {
            Column(
                Modifier.weight(1f).fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Spa, contentDescription = null, tint = SabeelColors.GoldPrimary, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text(strings.wirdGoalHintTitle, style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(strings.wirdGoalHintBody, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.rows, key = { it.dhikrKey }) { row ->
                    val isExpanded = expandedKey == row.dhikrKey
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SabeelColors.Surface)
                            .clickable { expandedKey = if (isExpanded) null else row.dhikrKey }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = row.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = SabeelColors.TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            // CP-01 fix: rows already had reorder/delete
                            // controls behind a tap-to-expand, but nothing
                            // on-screen hinted that tapping the row would
                            // reveal them. This chevron is that hint.
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${row.target.toLocalizedNumerals(state.language)}×",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SabeelColors.AccentTeal
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = strings.wirdExpandRowA11y,
                                    tint = SabeelColors.TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically(animationSpec = tween(300)),
                            exit = shrinkVertically(animationSpec = tween(300))
                        ) {
                            Column(Modifier.padding(top = 16.dp)) {
                                HorizontalDivider(color = SabeelColors.Divider)
                                Spacer(Modifier.height(16.dp))

                                // Target Adjuster row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(strings.wirdTargetA11y, style = MaterialTheme.typography.labelMedium, color = SabeelColors.TextSecondary)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        PresetChip(33, state.language) { onUpdateTarget(row.dhikrKey, 33) }
                                        PresetChip(100, state.language) { onUpdateTarget(row.dhikrKey, 100) }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SabeelColors.SurfaceElevated)
                                        ) {
                                            Icon(Icons.Filled.Remove, contentDescription = null, tint = SabeelColors.AccentTeal, modifier = Modifier.clickable { onUpdateTarget(row.dhikrKey, row.target - 1) }.padding(8.dp).size(20.dp))
                                            Icon(Icons.Filled.Add, contentDescription = null, tint = SabeelColors.AccentTeal, modifier = Modifier.clickable { onUpdateTarget(row.dhikrKey, row.target + 1) }.padding(8.dp).size(20.dp))
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // Action row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = strings.wirdReorder, tint = SabeelColors.TextSecondary, modifier = Modifier.clip(CircleShape).clickable { onMove(row.dhikrKey, true) }.padding(8.dp).size(24.dp))
                                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = strings.wirdReorder, tint = SabeelColors.TextSecondary, modifier = Modifier.clip(CircleShape).clickable { onMove(row.dhikrKey, false) }.padding(8.dp).size(24.dp))
                                    }
                                    Icon(Icons.Outlined.Delete, contentDescription = strings.wirdRemove, tint = SabeelColors.Danger, modifier = Modifier.clip(CircleShape).clickable { onRemove(row.dhikrKey) }.padding(8.dp).size(24.dp))
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
                items(state.pickable, key = { it.key }) { d ->
                    Row(
                        Modifier.fillMaxWidth()
                            .clickable { onAddDhikr(d.key, d.defaultTarget) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(d.displayName.get(state.language), fontSize = 14.sp, color = SabeelColors.TextPrimary)
                        Text("${d.defaultTarget.toLocalizedNumerals(state.language)}×",
                            fontSize = 13.sp, color = SabeelColors.AccentTeal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(target: Int, language: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SabeelColors.SurfaceElevated)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("${target.toLocalizedNumerals(language)}×", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SabeelColors.TextPrimary)
    }
}