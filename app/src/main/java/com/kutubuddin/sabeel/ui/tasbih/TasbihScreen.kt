package com.kutubuddin.sabeel.ui.tasbih

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.settings.SettingsViewModel
import com.kutubuddin.sabeel.ui.tasbih.components.CompletionRest
import com.kutubuddin.sabeel.ui.tasbih.components.SequenceTracker
import com.kutubuddin.sabeel.ui.tasbih.components.SpiritualRewardCard
import com.kutubuddin.sabeel.ui.tasbih.components.TasbihCircle
import com.kutubuddin.sabeel.ui.tasbih.components.TajweedText
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.UthmanicHafsFontFamily

@Composable
fun TasbihScreen(
    viewModel: TasbihViewModel,
    hapticEngine: HapticEngine,
    nextWirdItemName: String? = null,
    onContinueWird: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()
    var showCelebration by remember { mutableStateOf(false) }

    // Collect transient side-effects
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TasbihSideEffect.PlayHaptic -> {
                    when (effect.type) {
                        HapticType.TICK  -> hapticEngine.playIncrementTick()
                        HapticType.CLICK -> hapticEngine.playMilestoneClick()
                        HapticType.THUD  -> hapticEngine.playCompletionThud()
                    }
                }
                is TasbihSideEffect.ShowCelebration -> showCelebration = true
                is TasbihSideEffect.ShowToast -> {}
                is TasbihSideEffect.StartPocketModeService -> {}
                is TasbihSideEffect.StopPocketModeService -> {}
            }
        }
    }

    TasbihContent(
        state = state,
        showCelebration = showCelebration,
        showStreaks = settingsState.showStreaks,
        language = settingsState.language,
        onCelebrationEnd = { showCelebration = false },
        onIncrement = { viewModel.processIntent(TasbihIntent.Increment) },
        onDecrement = { viewModel.processIntent(TasbihIntent.Decrement) },
        onReset = { viewModel.processIntent(TasbihIntent.Reset) },
        nextWirdItemName = nextWirdItemName,
        onContinueWird = onContinueWird,
        modifier = modifier
    )
}

@Composable
fun TasbihContent(
    state: TasbihState,
    showCelebration: Boolean,
    onCelebrationEnd: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    nextWirdItemName: String? = null,
    onContinueWird: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showStreaks: Boolean = true,
    language: String = "en"
) {
    val strings = LocalStrings.current
    // When a Tasbīḥ-after-Salah sequence is active, the header tracks the current
    // step; otherwise it shows the single selected dhikr.
    val activeStep = state.sequence?.steps?.getOrNull(state.stepIndex)
    val displayArabic = activeStep?.arabicText ?: state.currentDhikr.arabicText
    val displayName = activeStep?.displayName ?: state.currentDhikr.displayName

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
            // Counting is intentionally confined to the circle (below) — a single
            // focal point. Eyes-free counting is served by Pocket Mode's volume
            // keys, so the whole screen no longer acts as a tap target.
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top Bar ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spacer to preserve top-bar layout (back button removed — bottom nav handles navigation)
                Spacer(Modifier.size(48.dp))

                // Smart Flow indicator
                AnimatedVisibility(
                    visible = state.isSmartFlowEnabled,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)),
                    exit = fadeOut(spring(stiffness = Spring.StiffnessMedium))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = SabeelColors.SmartFlowGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = strings.countSmartFlow,
                            style = MaterialTheme.typography.titleSmall,
                            color = SabeelColors.SmartFlowGold
                        )
                    }
                }

                // Consistency — calm icon, no fire/loss framing. Hidden at zero so
                // a first-timer is never greeted by a cold "0", and hidden entirely
                // when the worshipper opts for pure ibadah (Settings › Show Streaks).
                if (showStreaks && state.currentStreak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clearAndSetSemantics {
                            contentDescription = strings.countConsistencyA11y
                                .format(state.currentStreak.toLocalizedNumerals(language))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Spa,
                            contentDescription = null,
                            tint = SabeelColors.AccentTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = strings.countStreakShort
                                .format(state.currentStreak.toLocalizedNumerals(language)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SabeelColors.TextSecondary
                        )
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Sequence progress (post-Salah only) ──────────────────────────
            state.sequence?.let { seq ->
                SequenceTracker(
                    stepIndex = state.stepIndex,
                    stepCount = seq.steps.size,
                    language = language,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Arabic Dhikr Name ────────────────────────────────────────────
            TajweedText(
                arabicText = displayArabic,
                displayName = displayName.get(language),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Circle Tap Button ────────────────────────────────────────────
            // The sole counting affordance: tap to count, long press to reset.
            TasbihCircle(
                count = state.count,
                target = state.target,
                language = language,
                onTap = onIncrement,
                onLongPress = onReset
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Spiritual Reward Card ────────────────────────────────────────
            SpiritualRewardCard(
                reward = state.currentDhikr.spiritualReward.get(language),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Bottom Bar ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Decrement pill — hit area ≥48dp (WCAG/Material floor) for
                // eyes-free use; the visible pill stays small via inner padding.
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SabeelColors.CounterWhite.copy(alpha = 0.10f))
                        .border(1.dp, SabeelColors.BorderIdle, RoundedCornerShape(14.dp))
                        .semantics {
                            contentDescription = strings.countUndo
                            onClick(label = strings.countDecrementAction) { onDecrement(); true }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { onDecrement() })
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−" + 1.toLocalizedNumerals(language),
                        style = MaterialTheme.typography.labelLarge,
                        color = SabeelColors.TextSecondary
                    )
                }

                Text(
                    text = strings.countTapHint,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SabeelColors.TextHint,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ── Completion rest — calm, dismissible (replaces the 1200ms flash) ───
        if (showCelebration) {
            CompletionRest(
                dhikrName = state.currentDhikr.displayName.get(language),
                // NOTE: completion always reports the whole dhikr/sequence, not a step.
                total = state.target,
                language = language,
                onContinue = { 
                    if (onContinueWird != null) {
                        onContinueWird()
                    } else {
                        onReset() 
                    }
                    onCelebrationEnd() 
                },
                onFinish = onCelebrationEnd,
                nextWirdItemName = nextWirdItemName
            )
        }
    }
}
