package com.kutubuddin.sabeel.ui.tasbih

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
import com.kutubuddin.sabeel.ui.tasbih.components.FluidWaveBackground
import com.kutubuddin.sabeel.ui.tasbih.components.TasbihCircle
import com.kutubuddin.sabeel.ui.tasbih.components.TajweedText
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import kotlinx.coroutines.launch

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
    leftHanded: Boolean = false,
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
                            HapticType.TICK  -> hapticEngine.playIncrementTick(effect.strength)
                            HapticType.CLICK -> hapticEngine.playMilestoneClick(effect.strength)
                            HapticType.THUD  -> hapticEngine.playCompletionThud(effect.strength)
                            HapticType.RESET -> hapticEngine.playReset(effect.strength)
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
        leftHanded = leftHanded,
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
    language: String = "en",
    leftHanded: Boolean = false
) {
    val strings = LocalStrings.current
    val activeStep = state.sequence?.steps?.getOrNull(state.stepIndex)
    val displayArabic = activeStep?.arabicText ?: state.displayedDhikr.arabicText
    val displayTransliteration = activeStep?.transliteration ?: state.displayedDhikr.transliteration
    val displayMeaning = activeStep?.meaning ?: state.displayedDhikr.meaning
    val currentDhikrKey = state.currentDhikr.key
    val wasDailyGoalCompleteBeforeSession = remember(currentDhikrKey) { isDailyGoalFinished }


    Box(
        modifier = modifier
            .fillMaxSize()
            // FIX 10 — true black behind the dark-glass circle so it reads as an
            // illuminated void, not a card on a near-black panel.
            .background(SabeelColors.Background)
    ) {
        // ── Layer 0: Living background ────────────────────────────────────────
        // FluidWaveBackground morphs a circle → 8-point star as count/target
        // progress increases. Subtle (alpha=0.15), never distracting — it
        // simply makes the background feel alive during dhikr.
        FluidWaveBackground(
            count  = state.count,
            target = state.target
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            state.sequence?.let { seq ->
                SequenceTracker(
                    stepIndex = state.stepIndex,
                    stepCount = seq.steps.size,
                    language = language,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            AnimatedContent(
                targetState = Triple(displayArabic, displayTransliteration, displayMeaning),
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 4 }))
                        .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it / 4 }))
                },
                label = "dhikr_transition"
            ) { (arabic, trans, meaning) ->
                TajweedText(
                    arabicText = arabic,
                    transliteration = trans?.get(language),
                    meaning = meaning?.get(language),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Symmetric weight spacers centre the circle vertically between
            // the Arabic text above and the reward card below.
            Spacer(modifier = Modifier.weight(1f))

            TasbihCircle(
                count = state.count,
                target = state.target,
                language = language,
                onTap = onIncrement,
                onLongPress = onReset,
            )

            Spacer(modifier = Modifier.weight(1f))

            SpiritualRewardCard(
                    reward = state.displayedDhikr.spiritualReward.get(language),
                    reference = state.displayedDhikr.hadithRef
                        .takeIf { it.isNotBlank() }
                        ?.let { strings.dhikrRef.format(localizeHadithRef(it, language)) },
                    modifier = Modifier.fillMaxWidth()
                )

            Spacer(modifier = Modifier.height(16.dp))

            // IX-04: undo pill biased toward the active thumb (right by
            // default, left in left-handed mode).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = if (leftHanded) Arrangement.Start else Arrangement.End
            ) {
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

        // ── CompletionRest overlay ────────────────────────────────────────────
        // AnimatedVisibility gives the completion screen weight:
        //   Enter: scale 0.88→1.0 (spring) + fade in (400ms) — "materialises"
        //   Exit:  scale 1.0→0.95 (tween)  + fade out (220ms) — "dissolves"
        // The asymmetry (enter is springy, exit is a short tween) is intentional:
        // completion should feel earned (slow arrival) but dismissal should be
        // instant and not delay the next action.
        val completionContext = when {
            state.sessionOrigin == SessionOrigin.DAILY_GOAL && !wasDailyGoalCompleteBeforeSession && isDailyGoalFinished -> CompletionContext.VICTORY
            state.sessionOrigin == SessionOrigin.DAILY_GOAL && !isDailyGoalFinished -> CompletionContext.FLOW
            state.sessionOrigin == SessionOrigin.LIBRARY && !wasDailyGoalCompleteBeforeSession && isDailyGoalFinished -> CompletionContext.VICTORY
            else -> CompletionContext.AD_HOC
        }
        AnimatedVisibility(
            visible = showCelebration,
            enter = scaleIn(
                initialScale  = 0.88f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessMediumLow
                )
            ) + fadeIn(animationSpec = tween(400)),
            exit = scaleOut(
                targetScale   = 0.95f,
                animationSpec = tween(220)
            ) + fadeOut(animationSpec = tween(220))
        ) {
            CompletionRest(
                dhikrName = state.currentDhikr.displayName.get(language),
                total = state.target,
                context = completionContext,
                language = language,
                onPrimaryAction = {
                    when (completionContext) {
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
                onSecondaryAction = if (completionContext == CompletionContext.VICTORY) null else {
                    {
                        onCelebrationEnd()
                        if (completionContext == CompletionContext.AD_HOC) {
                            onNavigateLibrary()
                        }
                    }
                },
                nextWirdItemName = nextWirdItemName
            )
        }
    }
}