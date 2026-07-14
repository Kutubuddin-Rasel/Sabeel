package com.kutubuddin.sabeel.ui.tasbih

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
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
import com.kutubuddin.sabeel.ui.theme.SabeelMotion
import kotlinx.coroutines.launch
import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.DhikrMeaning

/**
 * SMOOTH-03: Stable wrapper for the dhikr identity key used by AnimatedContent.
 * Defined at file level so equals/hashCode are compiler-generated (no @Stable needed).
 * Keying on Arabic text ensures the crossfade fires exactly when the dhikr changes,
 * not on every tap. Avoids the Triple allocation the original code made per recomposition.
 */
private data class DhikrTexts(val arabic: String)

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
 *
 * JANK-01: Decomposed into two private sub-composables:
 *   • [StaticDhikrInfo] — Arabic text + spiritual reward card. Only recomposes
 *     when the *dhikr* changes (state.currentDhikr changes), never on a count tap.
 *   • [CountingLayer]   — TasbihCircle + undo pill. Recomposes on every tap, but
 *     these are already cheap Canvas + Text paints. Isolating them means Compose
 *     skips StaticDhikrInfo entirely on the 60fps hot path.
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
    // IX-CR: hoisted out of the StaticDhikrInfo call so the SAME stable
    // String values can be handed to the SpiritualRewardCard call below the
    // circle too — the reward now lives in the counting cluster, not with
    // the Arabic/meaning block above it.
    val rewardText = state.displayedDhikr.spiritualReward.get(language)
    val rewardRef = state.displayedDhikr.hadithRef
        .takeIf { it.isNotBlank() }
        ?.let { strings.dhikrRef.format(localizeHadithRef(it, language)) }
    val currentDhikrKey = state.currentDhikr.key
    val wasDailyGoalCompleteBeforeSession = remember(currentDhikrKey) { isDailyGoalFinished }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
    ) {
        // ── Layer 0: Living background ─────────────────────────────────────────
        FluidWaveBackground(count = state.count, target = state.target)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // JANK-01: StaticDhikrInfo is its own recomposition scope. It reads
            // displayArabic/displayTransliteration/displayMeaning which only change
            // when state.currentDhikr or state.sequence changes — not on every tap.
            StaticDhikrInfo(
                displayArabic = displayArabic,
                displayTransliteration = displayTransliteration,
                displayMeaning = displayMeaning,
                language = language
            )

            Spacer(modifier = Modifier.weight(1f))

            // JANK-01: CountingLayer owns the hot-path (every tap). Sequence tracker
            // is here because it reads stepIndex (also changes per-step, not per-tap).
            CountingLayer(
                count = state.count,
                target = state.target,
                stepIndex = state.stepIndex,
                stepCount = state.sequence?.steps?.size,
                onIncrement = onIncrement,
                onReset = onReset,
                language = language
            )

            // IX-CR: "show the spiritual reward properly under the circle" —
            // this used to live up with the Arabic text (StaticDhikrInfo),
            // visually detached from the counter and stranding the undo pill
            // in a huge empty gap below. Reward + undo now sit directly under
            // the circle as one cohesive "counting cluster": tap → see why it
            // matters → undo if needed, all in one glance, no scanning back
            // up the screen. The reward call is a plain, stable-arg composable
            // call (same rewardText/rewardRef values every recomposition
            // unless the dhikr changes), so it keeps the same tap-skip
            // guarantee StaticDhikrInfo had — it does not join the hot path.
            Spacer(modifier = Modifier.height(20.dp))
            SpiritualRewardCard(
                reward = rewardText,
                reference = rewardRef,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            UndoPill(
                onDecrement = onDecrement,
                language = language,
                leftHanded = leftHanded
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── CompletionRest overlay ────────────────────────────────────────────
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
                // POLISH-02: SabeelMotion.Spring.Completion token — LowBouncy meditative settle.
                // See Motion.kt for full tuning rationale.
                animationSpec = SabeelMotion.Spring.Completion
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
                        CompletionContext.VICTORY -> { onCelebrationEnd(); onNavigateHome() }
                        CompletionContext.AD_HOC  -> { onReset(); onCelebrationEnd() }
                        CompletionContext.FLOW    -> {
                            if (onContinueWird != null) onContinueWird() else onReset()
                            onCelebrationEnd()
                        }
                    }
                },
                onSecondaryAction = if (completionContext == CompletionContext.VICTORY) null else {
                    {
                        onCelebrationEnd()
                        if (completionContext == CompletionContext.AD_HOC) onNavigateLibrary()
                    }
                },
                nextWirdItemName = nextWirdItemName
            )
        }
    }
}

// ── JANK-01: StaticDhikrInfo ──────────────────────────────────────────────────
// Recomposition guard: only recomposes when dhikr identity changes, not on taps.
// All text reads are stable vals passed from TasbihContent.
@Composable
private fun StaticDhikrInfo(
    displayArabic: String,
    displayTransliteration: LocalizedText?,
    displayMeaning: DhikrMeaning?,
    language: String
) {
    // SMOOTH-03: stable key — AnimatedContent only re-runs its content block
    // when displayArabic changes (different dhikr), not on every count tap.
    val dhikrKey = remember(displayArabic) { DhikrTexts(displayArabic) }
    AnimatedContent(
        targetState = dhikrKey,
        transitionSpec = {
            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 4 }))
                .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it / 4 }))
        },
        label = "dhikr_transition"
    ) {
        TajweedText(
            arabicText = displayArabic,
            transliteration = displayTransliteration?.get(language),
            meaning = displayMeaning?.get(language),
            modifier = Modifier.fillMaxWidth()
        )
    }
    // IX-CR: SpiritualRewardCard used to render here, directly under the
    // Arabic block. It now anchors under the counter instead (see
    // TasbihContent) — this composable's only job is "what to say."
}

// ── JANK-01: CountingLayer ────────────────────────────────────────────────────
// The hot recomposition path: recomposes on every count tap.
// Intentionally lean — only Canvas arcs, scale animations, and Text. No text
// layout for the main dhikr, no rewards lookup, no undo pill (that moved out
// to its own sibling call — see UndoPill below — since it isn't part of the
// tap-driven hot path either). Pure count-driven rendering.
@Composable
private fun CountingLayer(
    count: Int,
    target: Int,
    stepIndex: Int,
    stepCount: Int?,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    language: String
) {
    if (stepCount != null) {
        SequenceTracker(
            stepIndex = stepIndex,
            stepCount = stepCount,
            language = language,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    TasbihCircle(
        count = count,
        target = target,
        language = language,
        onTap = onIncrement,
        onLongPress = onReset,
    )
}

// ── Undo pill ──────────────────────────────────────────────────────────────────
// IX-CR: extracted from CountingLayer so it can be positioned AFTER the
// spiritual reward in the flow (circle → reward → undo, per design review),
// while still not depending on count/target — it never needed to be inside
// the hot-path composable in the first place.
@Composable
private fun UndoPill(
    onDecrement: () -> Unit,
    language: String,
    leftHanded: Boolean
) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier.fillMaxWidth(),
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