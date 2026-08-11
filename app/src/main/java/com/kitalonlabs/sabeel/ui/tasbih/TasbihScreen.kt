package com.kitalonlabs.sabeel.ui.tasbih

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.kitalonlabs.sabeel.domain.haptic.HapticEngine
import com.kitalonlabs.sabeel.ui.i18n.LocalStrings
import com.kitalonlabs.sabeel.ui.i18n.localizeHadithRef
import com.kitalonlabs.sabeel.ui.i18n.toLocalizedNumerals
import com.kitalonlabs.sabeel.ui.tasbih.components.CompletionContext
import com.kitalonlabs.sabeel.ui.tasbih.components.CompletionRest
import com.kitalonlabs.sabeel.ui.tasbih.components.SequenceTracker
import com.kitalonlabs.sabeel.ui.tasbih.components.SpiritualRewardCard
import com.kitalonlabs.sabeel.ui.tasbih.components.FluidWaveBackground
import com.kitalonlabs.sabeel.ui.tasbih.components.TasbihCircle
import com.kitalonlabs.sabeel.ui.tasbih.components.TajweedText
import com.kitalonlabs.sabeel.ui.theme.SabeelColors
import com.kitalonlabs.sabeel.ui.theme.SabeelMotion
import kotlinx.coroutines.launch
import com.kitalonlabs.sabeel.domain.model.LocalizedText
import com.kitalonlabs.sabeel.domain.model.DhikrMeaning
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/**
 * SMOOTH-03: Stable wrapper for the dhikr identity key used by AnimatedContent.
 * Defined at file level so equals/hashCode are compiler-generated (no @Stable needed).
 * Keying on Arabic text ensures the crossfade fires exactly when the dhikr changes,
 * not on every tap. Avoids the Triple allocation the original code made per recomposition.
 */
private data class DhikrTexts(
    val arabic: String,
    val transliteration: String?,
    val meaning: String?
)

/**
 * Route-level wrapper (SRP/DIP): owns [viewModel] (injected by the caller —
 * [com.kitalonlabs.sabeel.ui.navigation.SabeelNavHost] — never defaulted here)
 * collects — this screen no longer instantiates its own second
 * `SettingsViewModel` to re-derive values the caller already has.
 */
@Composable
fun TasbihScreen(
    viewModel: TasbihViewModel,
    hapticEngine: HapticEngine,
    language: String = "en",
    showTransliteration: Boolean = true,
    isDailyGoalFinished: Boolean = false,
    isDhikrInDailyGoal: Boolean = false,
    nextWirdItemName: String? = null,
    onContinueWird: (() -> Unit)? = null,
    onNavigateHome: () -> Unit = {},
    onNavigateLibrary: () -> Unit = {},
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCelebration by remember { mutableStateOf(false) }
    var triggerGoldenBloom by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentDhikrKey = state.currentDhikr.key
    val wasDailyGoalCompleteBeforeSession = remember(currentDhikrKey) { isDailyGoalFinished }

    // Instantly sync the in-memory counter to disk when leaving the screen or backgrounding the app.
    // This bypasses the 1.5s write-behind debouncer so the Home screen immediately sees the latest count.
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_PAUSE) {
                viewModel.processIntent(com.kitalonlabs.sabeel.ui.tasbih.TasbihIntent.SyncProgress)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.processIntent(com.kitalonlabs.sabeel.ui.tasbih.TasbihIntent.SyncProgress)
        }
    }

    LaunchedEffect(isDailyGoalFinished) {
        if (isDailyGoalFinished && !wasDailyGoalCompleteBeforeSession) {
            if (state.sessionOrigin == SessionOrigin.DAILY_GOAL) {
                showCelebration = true
            } else if (state.sessionOrigin == SessionOrigin.LIBRARY) {
                showCelebration = true
                kotlinx.coroutines.delay(1200)
                showCelebration = false
            }
        }
    }

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
                    is TasbihSideEffect.NavigateToLibrary -> onNavigateLibrary()
                    is TasbihSideEffect.NavigateToHome -> onNavigateHome()
                    is TasbihSideEffect.TriggerGoldenBloom -> triggerGoldenBloom = true
                    is TasbihSideEffect.AutoProgressDailyGoal -> onContinueWird?.invoke()
                    is TasbihSideEffect.ShowToast -> {}
                }
            }
        }
    }

    TasbihContent(
        state = state,
        showCelebration = showCelebration,
        triggerGoldenBloom = triggerGoldenBloom,
        onGoldenBloomEnd = { triggerGoldenBloom = false },
        language = language,
        onCelebrationEnd = { showCelebration = false },
        onIncrement = { viewModel.processIntent(TasbihIntent.Increment) },
        onDecrement = { viewModel.processIntent(TasbihIntent.Decrement) },
        onReset = { viewModel.processIntent(TasbihIntent.Reset) },
        isDailyGoalFinished = isDailyGoalFinished,
        wasDailyGoalCompleteBeforeSession = wasDailyGoalCompleteBeforeSession,
        isDhikrInDailyGoal = isDhikrInDailyGoal,
        nextWirdItemName = nextWirdItemName,
        onContinueWird = onContinueWird,
        onNavigateHome = onNavigateHome,
        onNavigateLibrary = onNavigateLibrary,
        showTransliteration = showTransliteration,
        showTooltip = showTooltip,
        onTooltipDismiss = onTooltipDismiss,
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihContent(
    state: TasbihState,
    showCelebration: Boolean,
    triggerGoldenBloom: Boolean = false,
    onGoldenBloomEnd: () -> Unit = {},
    onCelebrationEnd: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    isDailyGoalFinished: Boolean = false,
    wasDailyGoalCompleteBeforeSession: Boolean = false,
    isDhikrInDailyGoal: Boolean = false,
    nextWirdItemName: String? = null,
    onContinueWird: (() -> Unit)? = null,
    onNavigateHome: () -> Unit = {},
    onNavigateLibrary: () -> Unit = {},
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    showTransliteration: Boolean = true,
    language: String = "en"
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
        ?.let { localizeHadithRef(it, language) }
    // OPT-B: Transient UI state — not persisted across process death intentionally.
    var showRewardSheet by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        // ── Zone geometry — computed once from actual device constraints ─────
        // maxWidth/maxHeight come from BoxWithConstraints; maxHeight reflects
        // the true Scaffold content area (Sabeel nav already excluded).
        val circleRingH = (maxWidth.value * 0.76f).coerceIn(260f, 300f).dp + 24.dp
        val statusBarH  = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navBarH     = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        // textZoneMax: tallest the text zone can be without encroaching on
        // the circle. Bottom reserved = navBarH + 106dp (bottom padding in
        // BOTTOM ZONE — accounts for 2-line whisper 78dp + 12dp gap + 16dp)
        // + circleRingH + 8dp (minimum breathing gap between text and circle).
        val textZoneMax = (maxHeight - statusBarH - 10.dp - navBarH - 106.dp - circleRingH - 8.dp)
            .coerceAtLeast(120.dp)

        // ── Layer 0: Living background ─────────────────────────────────────────
        FluidWaveBackground(count = state.count, target = state.target)

        Column(
            modifier = Modifier
                .fillMaxSize()
                // FIX-1.2: Consume the status bar inset before applying breathing room.
                // On Stock Android (Pixel/gesture nav) the system already handles this —
                // windowInsetsPadding is a no-op. On One UI (M21) and MIUI (Redmi) the
                // OEM does NOT auto-consume it, so the Arabic text would sit only 10dp
                // from the physical status bar pixels without this.
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── TOP ZONE — dynamic max height ─────────────────────────────
            // Wraps to actual text content, capped at textZoneMax computed
            // from real device constraints via BoxWithConstraints above.
            // This eliminates: the rigid clip boundary ("box shape" Flaw 2),
            // the dead gap for short dhikrs (Flaw 3), and the hard text
            // cut-off for long dhikrs (Flaw 4). Circle Y remains fixed
            // (Flaw 5) because BOTTOM ZONE pins circle via padding(80dp).
            val topScrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = textZoneMax)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(topScrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // JANK-01: StaticDhikrInfo is its own recomposition scope.
                    StaticDhikrInfo(
                        displayArabic = displayArabic,
                        displayTransliteration = displayTransliteration,
                        displayMeaning = displayMeaning,
                        language = language,
                        showTransliteration = showTransliteration
                    )
                }
                // Scroll affordance: fade the bottom edge when content overflows.
                // Appears only when there is more content below the viewport —
                // signals to the user that the text is scrollable.
                if (topScrollState.canScrollForward) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )
                }
            }

            // ── BOTTOM ZONE (65% of usable height) ─────────────────────────
            // Circle-only zone. The RewardWhisper is NOT a layout-flow child
            // of this Column — it is absolutely pinned in the parent Box at
            // Alignment.BottomCenter (see below). This decouples the whisper
            // from the BOTTOM ZONE budget, making it always visible regardless
            // of whether a SequenceTracker step-indicator is present.
            //
            // padding(bottom = 80.dp) reserves exactly the whisper slot:
            //   52dp (whisper) + 12dp (gap between circle and whisper) + 16dp
            //   (original breathing room above nav bar) = 80dp.
            // This ensures the circle never overlaps the pinned whisper.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)                  // takes ALL height remaining after TOP ZONE
                    .navigationBarsPadding()
                    // FIX-B: was 80.dp (calculated for 1-line whisper 52dp).
                    // Now 2-line bodyMedium whisper = 78dp.
                    // Correct reservation: 78dp + 12dp gap + 16dp bottom = 106dp.
                    // Old 80dp caused a 10dp circle/whisper overlap.
                    .padding(bottom = 106.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(contentAlignment = Alignment.TopCenter) {
                    CountingLayer(
                        count = state.count,
                        target = state.target,
                        stepIndex = state.stepIndex,
                        stepCount = state.sequence?.steps?.size,
                        triggerGoldenBloom = triggerGoldenBloom,
                        onGoldenBloomEnd = onGoldenBloomEnd,
                        onIncrement = {
                            if (showTooltip) onTooltipDismiss()
                            onIncrement()
                        },
                        onDecrement = onDecrement,
                        onReset = onReset,
                        language = language
                    )
                    
                    com.kitalonlabs.sabeel.ui.components.SabeelTooltip(
                        visible = showTooltip,
                        text = if (language == "bn") "কাউন্ট করতে এবং হ্যাপটিক ফিডব্যাক অনুভব করতে বৃত্তটিতে ট্যাপ করুন।" else "Tap the circle to count and feel the haptic feedback.",
                        position = com.kitalonlabs.sabeel.ui.components.TooltipPosition.Bottom,
                        modifier = Modifier
                            .offset(y = (-56).dp)
                    )
                }
            }
        }

        // ── RewardWhisper — pinned, always visible ──────────────────────────
        // Absolute in parent BoxWithConstraints — immune to BOTTOM ZONE budget.
        // Hidden during CompletionRest overlay so it doesn't overlap the rest card.
        if (!showCelebration) {
            RewardWhisper(
                rewardText = rewardText,
                onClick = { showRewardSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            )
        }

        // ── Reward Detail Bottom Sheet ─────────────────────────────────────────
        if (showRewardSheet) {
            ModalBottomSheet(
                onDismissRequest = { showRewardSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                SpiritualRewardCard(
                    reward = rewardText,
                    reference = rewardRef,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 40.dp)
                )
            }
        }

        // ── CompletionRest overlay ────────────────────────────────────────────
        val completionContext = when {
            state.sessionOrigin == SessionOrigin.DAILY_GOAL && !wasDailyGoalCompleteBeforeSession && isDailyGoalFinished -> CompletionContext.VICTORY
            state.sessionOrigin == SessionOrigin.DAILY_GOAL && !isDailyGoalFinished -> CompletionContext.FLOW
            state.sessionOrigin == SessionOrigin.LIBRARY && !wasDailyGoalCompleteBeforeSession && isDailyGoalFinished -> CompletionContext.VICTORY_TRANSIENT
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
                        CompletionContext.VICTORY_TRANSIENT -> {} // No primary action button is shown for transient
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
    language: String,
    showTransliteration: Boolean
) {
    val resolvedTransliteration = displayTransliteration?.get(language)
    val resolvedMeaning = displayMeaning?.get(language)

    // SMOOTH-03: stable key — AnimatedContent only re-runs its content block
    // when displayArabic changes (different dhikr), not on every count tap.
    val dhikrKey = remember(displayArabic, resolvedTransliteration, resolvedMeaning) { 
        DhikrTexts(displayArabic, resolvedTransliteration, resolvedMeaning) 
    }
    AnimatedContent(
        targetState = dhikrKey,
        transitionSpec = {
            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 4 }))
                .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it / 4 }))
        },
        label = "dhikr_transition"
    ) { targetState ->
        TajweedText(
            arabicText = targetState.arabic,
            showTransliteration = showTransliteration,
            transliteration = targetState.transliteration,
            meaning = targetState.meaning,
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
    triggerGoldenBloom: Boolean,
    onGoldenBloomEnd: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    language: String
) {
    // CIRCLE SIZE v3: Width-based formula — solves the "small circle" problem on M21.
    //
    // WHY WIDTH, NOT HEIGHT:
    // Height-based (old): screenHeight × 0.30, coerceIn(240, 280)
    //   M21  (780dp tall)  → 234dp → clamped to 240dp  ← felt small
    //   Pixel 9 (924dp)    → 277dp                      ← OK but not ideal
    //
    // Width-based (new): screenWidth × 0.76, coerceIn(260, 300)
    //   M21  (360dp wide)  → 274dp  ← noticeably bigger, feels right
    //   Pixel 9 (411dp)    → 312dp → clamped to 300dp   ← premium feel
    //   Redmi Note (360dp) → 274dp                       ← same as M21
    //   Budget 5.5" (320dp)→ 243dp → clamped to 260dp   ← safe minimum
    //
    // BUDGET IMPACT on M21 (274dp circle → 298dp with ring):
    //   298dp (circle) + 12dp (gap) + 52dp (whisper, 1-line) = 362dp
    //   BOTTOM ZONE available on M21 ≈ 365dp → 3dp spare ✅
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val adaptiveDiameter = (screenWidthDp * 0.76f).coerceIn(260f, 300f).dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            diameter = adaptiveDiameter,
            triggerGoldenBloom = triggerGoldenBloom,
            onGoldenBloomEnd = onGoldenBloomEnd,
            onTap = onIncrement,
            onLongPress = onReset,
            onDecrement = onDecrement,
        )
    }
}

// ── OPT-B: RewardWhisper ─────────────────────────────────────────────────────
// A subtle, tappable "whisper" label below the counting circle.
//
// INVISIBLE INTERFACE philosophy:
// The spiritual reward is contextual knowledge — it shouldn't compete with the
// act of counting. This whisper is visible to the attentive eye, invisible to
// those in flow. Tapping it opens a bottom sheet with the full reward text.
//
// HEIGHT BUDGET: ~74dp total
//   8dp (top padding) + 18dp (eyebrow row) + 4dp (gap) + 36dp (2-line preview)
//   + 8dp (bottom padding) = 74dp
// M21 BOTTOM ZONE fit: 264dp (circle+ring) + 12dp + 74dp = 350dp < 355dp ✅
@Composable
private fun RewardWhisper(
    rewardText: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (rewardText.isNullOrBlank()) return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .pointerInput(Unit) { detectTapGestures { onClick() } }
            // WHISPER v4: full-token colors (SageGreen at full opacity, TextSecondary
            // for preview) — no more raw .copy(alpha) calls. bodyMedium (14sp) instead
            // of bodySmall (12sp) for legible italic text on mid-range displays.
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Spiritual reward. Tap to read full."
                onClick(label = "Read spiritual reward") { onClick(); true }
            }
    ) {
        // ── Eyebrow row ───────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Spa,
                contentDescription = null,
                // FIX-D1: full token opacity. SageGreen is already a calm muted
                // sage — copy(alpha=0.55f) was dropping it below readability.
                tint = SabeelColors.SageGreen,
                modifier = Modifier.size(9.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "SPIRITUAL REWARD",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    // FIX-D1: SageGreen at full opacity (token already muted).
                    color = SabeelColors.SageGreen
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Reward preview — 2 lines, bodyMedium ─────────────────────────────
        // FIX-D2: bodyMedium (14sp/20sp) replaces bodySmall (12sp/16sp) —
        // italic at 12sp is too fine to read on M21 mid-range display.
        // TextSecondary replaces CounterWhite.copy(alpha=0.40f) — the token
        // is already theme-calibrated (dark: #A8B0A8, light: #4A5249) and
        // avoids producing an out-of-system ghost-level colour.
        Text(
            text = rewardText,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle = FontStyle.Italic,
                color = SabeelColors.TextSecondary
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}