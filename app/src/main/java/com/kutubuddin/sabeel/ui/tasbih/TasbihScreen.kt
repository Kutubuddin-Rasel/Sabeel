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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
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
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.localizeHadithRef
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.tasbih.components.CompletionContext
import com.kutubuddin.sabeel.ui.tasbih.components.CompletionRest
import com.kutubuddin.sabeel.ui.tasbih.components.SequenceTracker
import com.kutubuddin.sabeel.ui.tasbih.components.SpiritualRewardCard
import com.kutubuddin.sabeel.ui.tasbih.components.TasbihCircle
import com.kutubuddin.sabeel.ui.tasbih.components.TajweedText
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * Route-level wrapper (SRP/DIP): owns [viewModel] (injected by the caller —
 * [com.kutubuddin.sabeel.ui.navigation.SabeelNavHost] — never defaulted here)
 * and the effect-collection side of the counting screen. [language]/
 * [showStreaks] are plain display data, not services, so they arrive as
 * simple parameters from the SAME settings state the NavHost already
 * collects — this screen no longer instantiates its own second
 * `SettingsViewModel` to re-derive values the caller already has.
 */
@Composable
fun TasbihScreen(
    viewModel: TasbihViewModel,
    hapticEngine: HapticEngine,
    language: String = "en",
    showStreaks: Boolean = true,
    isDailyGoalFinished: Boolean = false,
    isDhikrInDailyGoal: Boolean = false,
    nextWirdItemName: String? = null,
    onContinueWird: (() -> Unit)? = null,
    onNavigateHome: () -> Unit = {},
    onNavigateLibrary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCelebration by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }

    TasbihContent(
        state = state,
        showCelebration = showCelebration,
        showStreaks = showStreaks,
        language = language,
        onCelebrationEnd = { showCelebration = false },
        onIncrement = { viewModel.processIntent(TasbihIntent.Increment) },
        onDecrement = { viewModel.processIntent(TasbihIntent.Decrement) },
        onReset = { viewModel.processIntent(TasbihIntent.Reset) },
        isDailyGoalFinished = isDailyGoalFinished,
        isDhikrInDailyGoal = isDhikrInDailyGoal,
        nextWirdItemName = nextWirdItemName,
        onContinueWird = onContinueWird,
        onNavigateHome = onNavigateHome,
        onNavigateLibrary = onNavigateLibrary,
        modifier = modifier
    )
}

/**
 * Pure, stateless rendering (SRP): a function of [state] and plain display
 * params only, emitting every intent via a trailing lambda. No ViewModel
 * reference, no DI — fully previewable/testable in isolation.
 */
@Composable
fun TasbihContent(
    state: TasbihState,
    showCelebration: Boolean,
    onCelebrationEnd: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    isDailyGoalFinished: Boolean = false,
    isDhikrInDailyGoal: Boolean = false,
    nextWirdItemName: String? = null,
    onContinueWird: (() -> Unit)? = null,
    onNavigateHome: () -> Unit = {},
    onNavigateLibrary: () -> Unit = {},
    modifier: Modifier = Modifier,
    showStreaks: Boolean = true,
    language: String = "en"
) {
    val strings = LocalStrings.current
    val activeStep = state.sequence?.steps?.getOrNull(state.stepIndex)
    val displayArabic = activeStep?.arabicText ?: state.currentDhikr.arabicText
    val displayName = activeStep?.displayName ?: state.currentDhikr.displayName
    
    val currentDhikrKey = state.currentDhikr.key
    val wasDailyGoalCompleteBeforeSession = remember(currentDhikrKey) { isDailyGoalFinished }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.size(48.dp))

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
                            tint = SabeelColors.GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = strings.countSmartFlow,
                            style = MaterialTheme.typography.titleSmall,
                            color = SabeelColors.GoldPrimary
                        )
                    }
                }

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

            state.sequence?.let { seq ->
                SequenceTracker(
                    stepIndex = state.stepIndex,
                    stepCount = seq.steps.size,
                    language = language,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            TajweedText(
                arabicText = displayArabic,
                displayName = displayName.get(language),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            TasbihCircle(
                count = state.count,
                target = state.target,
                language = language,
                onTap = onIncrement,
                onLongPress = onReset
            )

            Spacer(modifier = Modifier.weight(1f))

            // IX-12: surface the same hadithRef citation the Dhikr Library
            // already shows for this entry, on the screen where it matters
            // most — see SpiritualRewardCard's reference param.
            SpiritualRewardCard(
                reward = state.currentDhikr.spiritualReward.get(language),
                reference = state.currentDhikr.hadithRef
                    .takeIf { it.isNotBlank() }
                    ?.let { strings.dhikrRef.format(localizeHadithRef(it, language)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // IX-04 fix: the undo control used to sit in the bottom-left
            // corner while the tap hint stayed centered above it — far from
            // the primary circle above and easy to miss. Stacking both
            // centered, directly below the circle, puts undo on the same
            // reach path as the thing it corrects, without changing its
            // already-compliant 48dp touch target or its text contrast.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.countTapHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SabeelColors.TextHint,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
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
            }
        }

        if (showCelebration) {
            val context = when {
                state.sessionOrigin == SessionOrigin.DAILY_GOAL && !wasDailyGoalCompleteBeforeSession && isDailyGoalFinished -> CompletionContext.VICTORY
                state.sessionOrigin == SessionOrigin.DAILY_GOAL && !isDailyGoalFinished -> CompletionContext.FLOW
                state.sessionOrigin == SessionOrigin.LIBRARY && !wasDailyGoalCompleteBeforeSession && isDailyGoalFinished -> CompletionContext.VICTORY
                else -> CompletionContext.AD_HOC
            }
            
            CompletionRest(
                dhikrName = state.currentDhikr.displayName.get(language),
                total = state.target,
                context = context,
                language = language,
                onPrimaryAction = {
                    when (context) {
                        CompletionContext.VICTORY -> {
                            onCelebrationEnd()
                            onNavigateHome()
                        }
                        CompletionContext.AD_HOC -> {
                            onReset()
                            onCelebrationEnd()
                        }
                        CompletionContext.FLOW -> {
                            if (onContinueWird != null) {
                                onContinueWird()
                            } else {
                                onReset()
                            }
                            onCelebrationEnd()
                        }
                    }
                },
                onSecondaryAction = if (context == CompletionContext.VICTORY) null else {
                    {
                        onCelebrationEnd()
                        if (context == CompletionContext.AD_HOC) {
                            onNavigateLibrary()
                        }
                    }
                },
                nextWirdItemName = nextWirdItemName
            )
        }
    }
}