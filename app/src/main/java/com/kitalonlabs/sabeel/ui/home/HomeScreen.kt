package com.kitalonlabs.sabeel.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kitalonlabs.sabeel.ui.components.AccentCard
import com.kitalonlabs.sabeel.ui.components.CollapsibleSectionHeader
import com.kitalonlabs.sabeel.ui.components.InfoCard
import com.kitalonlabs.sabeel.ui.components.ProgressFractionText
import com.kitalonlabs.sabeel.domain.model.LocalizedText
import com.kitalonlabs.sabeel.ui.components.SabeelSectionHeader
import com.kitalonlabs.sabeel.ui.i18n.LocalStrings
import com.kitalonlabs.sabeel.ui.i18n.UiStrings
import com.kitalonlabs.sabeel.ui.i18n.localizeDigits
import com.kitalonlabs.sabeel.ui.i18n.toGroupedLocalizedNumerals
import com.kitalonlabs.sabeel.ui.i18n.toLocalizedNumerals
import com.kitalonlabs.sabeel.ui.theme.SabeelColors
import com.kitalonlabs.sabeel.ui.theme.SabeelMotion
import com.kitalonlabs.sabeel.ui.theme.arabicStyle
import com.kitalonlabs.sabeel.ui.wird.WirdSegmentedProgress
import java.util.Locale

/**
 * Route-level wrapper (DIP): the ONLY place in this file that injects a
 * ViewModel or collects a Flow. All rendering is delegated to the stateless
 * [HomeContent].
 *
 * `onEditWird` has been removed from this screen's API: editing the plan is
 * no longer initiated from Home. The one thing Home does with the daily goal
 * is open it via [onOpenWird] — [com.kitalonlabs.sabeel.ui.wird.WirdScreen] is
 * where "edit" actually lives now, with a single entry point of its own. If
 * your navigation graph still wires an `onEditWird` callback into this
 * route, that wiring can be deleted along with this parameter.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onResumeCounting: (String?, Int?) -> Unit,
    onResumeSession: (String) -> Unit,
    onOpenWird: () -> Unit,
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onResumeCounting = onResumeCounting,
        onResumeSession = onResumeSession,
        onOpenWird = onOpenWird,
        showTooltip = showTooltip,
        onTooltipDismiss = onTooltipDismiss
    )
}

/**
 * Pure, stateless Home rendering — a function of [state] only, emitting
 * intent via the trailing lambdas. No ViewModel reference, no Flow
 * collection; fully previewable/testable in isolation (SRP).
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeContent(
    state: HomeState,
    onResumeCounting: (String?, Int?) -> Unit,
    onResumeSession: (String) -> Unit,
    onOpenWird: () -> Unit,
    showTooltip: Boolean = false,
    onTooltipDismiss: () -> Unit = {}
) {
    var isSessionsExpanded by rememberSaveable { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Greeting ──────────────────────────────────────────────────────────
        item {
            val strings = LocalStrings.current
            Column {
                Text(
                    text = strings.greetingText(state.greeting),
                    style = MaterialTheme.typography.titleLarge,
                    color = SabeelColors.TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                // JANK-04: was remember(state.language) { LocalDate.now()... } which cached
                // the date until the language changed. Now owned by HomeViewModel and
                // refreshed via a midnight-aligned ticker flow — always correct.
                Text(
                    text = state.todayLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SabeelColors.TextSecondary
                )
            }
        }

        // ── Primary action (hero, above the fold) ─────────────────────────────
        // AnimatedContent crossfades between the four hero variants (Resume /
        // GoalComplete / SmartPlay / HeroStart) keyed on `heroCardState`. Before
        // this fix, each branch was a *different composable in the same
        // LazyColumn slot* — Compose has no way to animate between unrelated
        // composable identities, so finishing a session and coming back to
        // Home was a hard cut. Modifier.animateItem() on top additionally
        // animates this item's own position if something above it changes size.
        // JANK-FIX: We use a sealed class HeroCardState as targetState to capture
        // the required data (like resumeSession) at the moment the state is determined.
        // This prevents NPEs when AnimatedContent recomposes outgoing content with
        // a newer, null state.
        item(key = "hero_card") {
            AnimatedContent(
                targetState = state.heroState,
                transitionSpec = {
                    fadeIn(tween(SabeelMotion.Duration.HeroCardCrossfadeIn)) togetherWith
                        fadeOut(tween(SabeelMotion.Duration.HeroCardCrossfadeOut))
                },
                modifier = Modifier.animateItem(),
                label = "home_hero_card"
            ) { cardState ->
                when (cardState) {
                    is HomeHeroState.Resume -> HomeHeroCard(
                        state = cardState,
                        language = state.language,
                        onClick = { onResumeCounting(cardState.dhikrKey, cardState.target) }
                    )
                    HomeHeroState.SetupGoal -> SetupGoalCard(
                        onClick = onOpenWird
                    )
                    HomeHeroState.None -> GoalCompleteCard(
                        onStart = { onResumeCounting(null, null) }
                    )
                }
            }
        }

        // ── Stats (demoted below the hero) ────────────────────────────────────
        item(key = "streak_goal_card") {
            val strings = LocalStrings.current
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StreakGoalCard(
                    state = state,
                    onOpenWird = {
                        if (showTooltip) onTooltipDismiss()
                        onOpenWird()
                    },
                    modifier = Modifier.animateItem()
                )
                
                com.kitalonlabs.sabeel.ui.components.SabeelTooltip(
                    visible = showTooltip,
                    text = if (state.language == "bn") "আপনার প্রতিদিনের লক্ষ্য সেট করতে 'আপনার প্রতিদিনের উর্দ সেট করুন'-এ ক্লিক করুন।" else "Click 'Set your daily wird' to set your daily goal.",
                    position = com.kitalonlabs.sabeel.ui.components.TooltipPosition.Bottom, // pointer at top
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // ── Today's Sessions ──────────────────────────────────────────────────
        if (state.todaysSessions.isNotEmpty()) {
            item(key = "sessions_header") {
                CollapsibleSectionHeader(
                    text = LocalStrings.current.homeTodaysSessions,
                    isExpanded = isSessionsExpanded,
                    onClick = { isSessionsExpanded = !isSessionsExpanded },
                    modifier = Modifier.animateItem()
                )
            }
            // SMOOTH-04 + POLISH-03: replaced single AnimatedVisibility item (forEach inside)
            // with items{} so each row is lazily composed and has its own animateItem() slot.
            // Previous approach:
            //   • Eagerly composed ALL session rows into one LazyColumn slot
            //   • animateItem() on the AnimatedVisibility wrapper caused a double-animation:
            //     slot position animated at the same time as shrinkVertically, at different rates.
            // Now each row exits individually (iOS-like per-item exit) with zero group re-measure.
            if (isSessionsExpanded) {
                items(
                    items = state.todaysSessions,
                    key = { session -> session.dhikrKey }
                ) { session ->
                    SessionRow(
                        session = session,
                        language = state.language,
                        modifier = Modifier
                            .animateItem()
                            .clickable { onResumeSession(session.dhikrKey) }
                    )
                }
            }
        }

        // ── All Time stats ────────────────────────────────────────────────────
        // IX-08: a first-ever launch used to show "0 Total Counted / 0
        // Sessions" side by side — accurate, but it's the very first thing a
        // new user's stats say about them, and two zeroes read as "behind"
        // rather than "about to begin." Swap in a short encouragement card
        // until there's at least one real session to report on.
        if (state.totalSessionCount > 0) {
            item(key = "all_time_header") {
                SectionHeader(LocalStrings.current.homeAllTime, modifier = Modifier.animateItem())
            }
            item(key = "all_time_card") {
                AllTimeCard(state = state, modifier = Modifier.animateItem())
            }
        } else {
            item(key = "first_time_encouragement") {
                FirstTimeEncouragementCard(modifier = Modifier.animateItem())
            }
        }

        // CP-02 fix: a short session's content used to leave a large, mute
        // gap of pure Background between the last card and the nav bar,
        // which reads as an unfinished/loading screen rather than deliberate
        // whitespace. A small closing wordmark — the same gold-on-teal
        // treatment HeroStartCard already uses — gives the screen a
        // deliberate ending instead.
        item { Spacer(Modifier.height(32.dp)) }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "سَبِيل",
                    // Arabic wordmark: arabicStyle ensures Uthmanic font; 20×1.9=38sp diacritic safety
                    style = arabicStyle.copy(fontSize = 20.sp, lineHeight = 38.sp),
                    // FIX 6: a faint signature, not gold — gold is milestone-only now.
                    color = SabeelColors.TextSecondary.copy(alpha = 0.35f)
                )
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun HomeHeroCard(state: HomeHeroState.Resume, language: String, onClick: () -> Unit) {
    val strings = LocalStrings.current
    
    val icon = if (state.isDailyGoal) Icons.Outlined.TrackChanges else Icons.Filled.PlayArrow
    val title = if (state.isDailyGoal) {
        if (state.lastCount == 0) strings.homeStartGoal else strings.homeContinueGoal
    } else {
        strings.homeResume
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SabeelColors.AccentTealSurface)
            .border(1.dp, SabeelColors.AccentTeal.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SabeelColors.AccentTeal,
                    modifier = Modifier.size(16.dp)
                )
                Text(title, style = MaterialTheme.typography.labelMedium, color = SabeelColors.AccentTeal)
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = state.displayName.get(language),
                style = MaterialTheme.typography.titleMedium,
                color = SabeelColors.TextPrimary
            )
        }
        ProgressFractionText(
            count = state.lastCount,
            target = state.target,
            language = language,
            isComplete = state.lastCount >= state.target,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            normalColor = SabeelColors.AccentTeal,
            completeColor = SabeelColors.AccentTeal
        )
    }
}

/**
 * The single Home-tab entry point into the daily goal — and now the *only*
 * thing you can do with it from Home. One clickable surface, one state-driven
 * label, no separate edit affordance:
 *
 *  - Empty: the whole card reads as a call to action ("Set your daily wird")
 *    with a trailing chevron. There is no bare "Setup" text sitting next to
 *    an unrelated "Edit" button anymore.
 *  - Has a plan: the label and the [WirdSegmentedProgress] bar are driven by
 *    the exact same `completed`/`total` numbers, so they can never disagree
 *    the way the old text (`completed`/`total`) and bar
 *    (`countedSum`/`targetSum`) sometimes did.
 *
 * Tapping anywhere on the card opens [com.kitalonlabs.sabeel.ui.wird.WirdScreen]
 * via [onOpenWird] — that's where the breakdown and the single "edit" action
 * live now.
 */
@Composable
private fun StreakGoalCard(state: HomeState, onOpenWird: () -> Unit, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current

    // 3B: Split tap targets — streak row is informational only (no nav),
    // wird progress row navigates. Outer column is non-clickable.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .border(1.dp, SabeelColors.BorderIdle, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.Spa, contentDescription = null,
                tint = SabeelColors.AccentTeal, modifier = Modifier.size(18.dp))
            Text(strings.homeConsistency, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary)
            Spacer(Modifier.weight(1f))
            val streakDigits = state.currentStreak.toLocalizedNumerals(state.language)
            Text(
                text = when {
                    // IX-07: "0 days" on a scoreboard reads as a deficit,
                    // whether this is a brand-new install or a streak
                    // that just broke. "Start today" is true either way
                    // and doesn't frame either case as a failure.
                    state.currentStreak == 0 -> strings.homeStreakStart
                    state.currentStreak == 1 -> strings.homeDayOne.format(streakDigits)
                    else -> strings.homeDayOther.format(streakDigits)
                },
                style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary
            )
        }
        HorizontalDivider(color = SabeelColors.Divider)

        // 3B: Wird progress section — the ONLY tappable part of this card.
        // Clicking it opens WirdScreen. The streak section above is intentionally
        // non-clickable; it is an informational badge, not a navigation affordance.
        if (state.wird.isEmpty) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenWird)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Outlined.TrackChanges, contentDescription = null,
                    tint = SabeelColors.AccentTeal, modifier = Modifier.size(20.dp))
                Column(Modifier.weight(1f)) {
                    Text(strings.wirdSetupTitle, style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary)
                    Text(strings.wirdSetupSubtitle, style = MaterialTheme.typography.bodySmall, color = SabeelColors.TextSecondary)
                }
                Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                    tint = SabeelColors.TextSecondary, modifier = Modifier.size(18.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenWird)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.TrackChanges, contentDescription = null,
                            tint = SabeelColors.TextSecondary, modifier = Modifier.size(16.dp))
                        Text(strings.wirdTitle, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                        tint = SabeelColors.TextSecondary, modifier = Modifier.size(18.dp))
                }

                val allDone = state.wird.completed == state.wird.total
                Text(
                    text = strings.wirdDoneOf.format(
                        state.wird.completed.toLocalizedNumerals(state.language),
                        state.wird.total.toLocalizedNumerals(state.language)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (allDone) SabeelColors.SageGreen else SabeelColors.TextPrimary
                )
                WirdSegmentedProgress(completed = state.wird.completed, total = state.wird.total)
            }
        }
    }
}

@Composable
private fun SessionRow(session: SessionSummary, language: String, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SabeelColors.Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(session.displayName.get(language), style = MaterialTheme.typography.labelLarge, color = SabeelColors.TextPrimary)
            Text(
                text = if (session.isComplete) strings.homeCompleted else strings.homePartial,
                style = MaterialTheme.typography.bodySmall,
                color = if (session.isComplete) SabeelColors.SageGreen else SabeelColors.TextSecondary
            )
        }
        Text(
            text = session.count.toLocalizedNumerals(language),
            style = MaterialTheme.typography.headlineSmall,
            color = SabeelColors.AccentTeal
        )
    }
}

@Composable
private fun AllTimeCard(state: HomeState, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    InfoCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            AllTimeStat(strings.homeTotalCounted, state.totalAllTime.toGroupedLocalizedNumerals(state.language))
            AllTimeStat(strings.homeSessions, state.totalSessionCount.toLocalizedNumerals(state.language))
        }
    }
}

@Composable
private fun AllTimeStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = SabeelColors.TextPrimary)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary)
    }
}

/**
 * IX-08: stands in for [AllTimeCard] before the user's first-ever session, so
 * the zero-state screen says something encouraging instead of reporting two
 * zeroes. Also softens the CP-02 empty-state gap a little further, since it
 * occupies real vertical space with actual content rather than nothing.
 */
@Composable
private fun FirstTimeEncouragementCard(modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    InfoCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Spa,
                contentDescription = null,
                tint = SabeelColors.SageGreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = strings.homeFirstTimeEncouragement,
                style = MaterialTheme.typography.bodyMedium,
                color = SabeelColors.TextSecondary
            )
        }
    }
}

@Composable
private fun SetupGoalCard(onClick: () -> Unit) {
    val strings = LocalStrings.current
    AccentCard(onClick = onClick, accentColor = SabeelColors.AccentTeal) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = strings.homeSetupGoalSubtitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = SabeelColors.TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = SabeelColors.AccentTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.homeSetupGoalTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = SabeelColors.AccentTeal
                    )
                }
            }
        }
    }
}

// Delegated to shared SabeelSectionHeader — kept as a local alias for zero call-site churn
@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) = SabeelSectionHeader(text = text, modifier = modifier, letterSpacing = 1.5.dp)

// Delegated to shared CollapsibleSectionHeader — kept as a local alias for zero call-site churn
@Composable
private fun CollapsibleSectionHeader(text: String, isExpanded: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) =
    CollapsibleSectionHeader(text, isExpanded, onClick, modifier)

@Composable
private fun GoalCompleteCard(onStart: () -> Unit) {
    val strings = LocalStrings.current
    AccentCard(onClick = onStart, accentColor = SabeelColors.SageGreen) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = strings.homeGoalCompleteTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = SabeelColors.SageGreen
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = SabeelColors.SageGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.homeGoalCompleteAction,
                        style = MaterialTheme.typography.labelLarge,
                        color = SabeelColors.SageGreen
                    )
                }
            }
        }
    }
}

private fun UiStrings.greetingText(type: GreetingType): String = when (type) {
    GreetingType.FAJR -> greetingFajr
    GreetingType.MORNING -> greetingMorning
    GreetingType.DHUHR -> greetingDhuhr
    GreetingType.AFTERNOON -> greetingAfternoon
    GreetingType.ASR -> greetingAsr
    GreetingType.MAGHRIB -> greetingMaghrib
    GreetingType.ISHA -> greetingIsha
    GreetingType.DEFAULT -> greetingDefault
}

private fun localeFor(lang: String): Locale = Locale.forLanguageTag(lang)