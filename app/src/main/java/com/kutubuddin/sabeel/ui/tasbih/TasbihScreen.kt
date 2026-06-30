package com.kutubuddin.sabeel.ui.tasbih

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.ui.tasbih.components.SpiritualRewardCard
import com.kutubuddin.sabeel.ui.tasbih.components.TasbihCircle
import com.kutubuddin.sabeel.ui.tasbih.components.TajweedText
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.UthmanicHafsFontFamily

@Composable
fun TasbihScreen(
    viewModel: TasbihViewModel,
    hapticEngine: HapticEngine,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
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
        onCelebrationEnd = { showCelebration = false },
        onIncrement = { viewModel.processIntent(TasbihIntent.Increment) },
        onDecrement = { viewModel.processIntent(TasbihIntent.Decrement) },
        onReset = { viewModel.processIntent(TasbihIntent.Reset) },
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
    modifier: Modifier = Modifier
) {
    // Auto-clear celebration after a short delay
    LaunchedEffect(showCelebration) {
        if (showCelebration) {
            kotlinx.coroutines.delay(1200)
            onCelebrationEnd()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SabeelColors.Background)
            // Full-screen tap still counts — "eyes-free" fallback
            .semantics {
                contentDescription =
                    "${state.currentDhikr.displayName}. Count: ${state.count} of ${state.target}. " +
                    "Tap anywhere to count. Long press anywhere to reset."
                onClick(label = "Count") { onIncrement(); true }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onIncrement() },
                    onLongPress = { onReset() }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
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
                    Text(
                        text = "⚡ Smart Flow",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SabeelColors.SmartFlowGold
                    )
                }

                // Streak
                Text(
                    text = "🔥 ${state.currentStreak}",
                    fontSize = 14.sp,
                    color = SabeelColors.StreakAmber.copy(alpha = 0.7f),
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "Streak: ${state.currentStreak} days"
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Arabic Dhikr Name ────────────────────────────────────────────
            TajweedText(
                currentDhikr = state.currentDhikr,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Circle Tap Button ────────────────────────────────────────────
            TasbihCircle(
                count = state.count,
                target = state.target,
                onTap = onIncrement,
                modifier = Modifier
                    // Block the tap from propagating to the outer full-screen tap
                    // so we don't double-count. The circle handles its own tap.
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { /* consumed by TasbihCircle internally */ },
                            onLongPress = { onReset() }
                        )
                    }
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Spiritual Reward Card ────────────────────────────────────────
            SpiritualRewardCard(
                dhikr = state.currentDhikr,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Bottom Bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrement pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SabeelColors.CounterWhite.copy(alpha = 0.04f))
                        .semantics {
                            contentDescription = "Undo last count"
                            onClick(label = "Decrement") { onDecrement(); true }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { onDecrement() })
                        }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "−1",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = SabeelColors.TextHint.copy(alpha = 1.5f)
                    )
                }

                Text(
                    text = "Tap anywhere to count",
                    fontSize = 12.sp,
                    color = SabeelColors.TextHint,
                    textAlign = TextAlign.Center
                )

                // Balanced spacer
                Spacer(modifier = Modifier.width(56.dp))
            }
        }
    }
}
