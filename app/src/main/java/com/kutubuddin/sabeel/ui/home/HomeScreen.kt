package com.kutubuddin.sabeel.ui.home

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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.components.ProgressFractionText
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.UiStrings
import com.kutubuddin.sabeel.ui.i18n.localizeDigits
import com.kutubuddin.sabeel.ui.i18n.toGroupedLocalizedNumerals
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.wird.WirdSegmentedProgress
import java.util.Locale

/**
 * Route-level wrapper (DIP): the ONLY place in this file that injects a
 * ViewModel or collects a Flow. All rendering is delegated to the stateless
 * [HomeContent].
 *
 * `onEditWird` has been removed from this screen's API: editing the plan is
 * no longer initiated from Home. The one thing Home does with the daily goal
 * is open it via [onOpenWird] — [com.kutubuddin.sabeel.ui.wird.WirdScreen] is
 * where "edit" actually lives now, with a single entry point of its own. If
 * your navigation graph still wires an `onEditWird` callback into this
 * route, that wiring can be deleted along with this parameter.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onResumeCounting: (String?, Int?) -> Unit,
    onOpenWird: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onResumeCounting = onResumeCounting,
        onOpenWird = onOpenWird
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
    onOpenWird: () -> Unit
) {
    var isSessionsExpanded by rememberSaveable { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SabeelColors.Background),
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
                val todayLabel = remember(state.language) {
                    java.time.LocalDate.now()
                        .format(
                            java.time.format.DateTimeFormatter
                                .ofPattern("EEEE, d MMMM yyyy", localeFor(state.language))
                        )
                        .localizeDigits(state.language)
                }
                Text(
                    text = todayLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SabeelColors.TextSecondary
                )
            }
        }

        // ── Primary action (hero, above the fold) ─────────────────────────────
        if (state.resumeSession != null) {
            item {
                ResumeCard(
                    session = state.resumeSession!!,
                    language = state.language,
                    onClick = { onResumeCounting(state.resumeSession.dhikrKey, state.resumeSession.target) }
                )
            }
        } else if (!state.wird.isEmpty) {
            if (state.wird.completed == state.wird.total) {
                item {
                    GoalCompleteCard(
                        onStart = { onResumeCounting(null, null) }
                    )
                }
            } else {
                item {
                    SmartPlayCard(
                        nextItemName = state.wird.nextItemName!!.get(state.language),
                        onStart = { onResumeCounting(state.wird.nextItemKey, state.wird.nextItemTarget) }
                    )
                }
            }
        } else {
            item {
                HeroStartCard(
                    onStart = { onResumeCounting(null, null) },
                    // IX-09: this card used to read "Begin today's dhikr" even
                    // right after finishing the first session of the day (no
                    // *in-progress* session to resume, but clearly not a
                    // zero-progress day either). Acknowledge it instead.
                    hasProgressToday = state.todaysSessions.isNotEmpty()
                )
            }
        }

        // ── Stats (demoted below the hero) ────────────────────────────────────
        item {
            StreakGoalCard(state = state, onOpenWird = onOpenWird)
        }

        // ── Today's Sessions ──────────────────────────────────────────────────
        if (state.todaysSessions.isNotEmpty()) {
            item {
                CollapsibleSectionHeader(
                    text = LocalStrings.current.homeTodaysSessions,
                    isExpanded = isSessionsExpanded,
                    onClick = { isSessionsExpanded = !isSessionsExpanded }
                )
            }
            item {
                AnimatedVisibility(
                    visible = isSessionsExpanded,
                    enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                    exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        state.todaysSessions.forEach { session ->
                            SessionRow(session, state.language)
                        }
                    }
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
            item {
                SectionHeader(LocalStrings.current.homeAllTime)
            }
            item {
                AllTimeCard(state = state)
            }
        } else {
            item {
                FirstTimeEncouragementCard()
            }
        }

        // CP-02 fix: a short session's content used to leave a large, mute
        // gap of pure Background between the last card and the nav bar,
        // which reads as an unfinished/loading screen rather than deliberate
        // whitespace. A small closing wordmark — the same gold-on-teal
        // treatment HeroStartCard already uses — gives the screen a
        // deliberate ending instead.
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("سَبِيل", fontSize = 20.sp, color = SabeelColors.GoldPrimary.copy(alpha = 0.35f))
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ResumeCard(session: ResumeSession, language: String, onClick: () -> Unit) {
    val strings = LocalStrings.current
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
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = SabeelColors.AccentTeal,
                    modifier = Modifier.size(16.dp)
                )
                Text(strings.homeResume, style = MaterialTheme.typography.labelMedium, color = SabeelColors.AccentTeal)
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = session.displayName.get(language),
                style = MaterialTheme.typography.titleMedium,
                color = SabeelColors.TextPrimary
            )
        }
        ProgressFractionText(
            count = session.lastCount,
            target = session.target,
            language = language,
            isComplete = session.lastCount >= session.target,
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
 * Tapping anywhere on the card opens [com.kutubuddin.sabeel.ui.wird.WirdScreen]
 * via [onOpenWird] — that's where the breakdown and the single "edit" action
 * live now.
 */
@Composable
private fun StreakGoalCard(state: HomeState, onOpenWird: () -> Unit) {
    val strings = LocalStrings.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .clickable(onClick = onOpenWird)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.showStreaks) {
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
        }

        if (state.wird.isEmpty) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun SessionRow(session: SessionSummary, language: String) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
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
private fun AllTimeCard(state: HomeState) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AllTimeStat(strings.homeTotalCounted, state.totalAllTime.toGroupedLocalizedNumerals(state.language))
        AllTimeStat(strings.homeSessions, state.totalSessionCount.toLocalizedNumerals(state.language))
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
private fun FirstTimeEncouragementCard() {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .padding(20.dp),
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

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
        color = SabeelColors.TextSecondary
    )
}

@Composable
private fun CollapsibleSectionHeader(text: String, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SectionHeader(text)
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = SabeelColors.TextSecondary
        )
    }
}

@Composable
private fun HeroStartCard(onStart: () -> Unit, hasProgressToday: Boolean = false) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SabeelColors.AccentTealSurface)
            .border(1.dp, SabeelColors.AccentTeal.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .clickable(onClick = onStart)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (hasProgressToday) strings.homeContinueToday else strings.homeBeginToday,
            style = MaterialTheme.typography.titleMedium,
            color = SabeelColors.TextPrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = SabeelColors.AccentTeal,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = strings.homeStartCounting,
                style = MaterialTheme.typography.labelLarge,
                color = SabeelColors.AccentTeal
            )
        }
    }
}

@Composable
private fun SmartPlayCard(nextItemName: String, onStart: () -> Unit) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SabeelColors.AccentTealSurface)
            .border(1.dp, SabeelColors.AccentTeal.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .clickable(onClick = onStart)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = strings.homeSmartPlayNext.format(nextItemName),
            style = MaterialTheme.typography.titleMedium,
            color = SabeelColors.TextPrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = SabeelColors.AccentTeal,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = strings.homeStartCounting,
                style = MaterialTheme.typography.labelLarge,
                color = SabeelColors.AccentTeal
            )
        }
    }
}

@Composable
private fun GoalCompleteCard(onStart: () -> Unit) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SabeelColors.SageGreen.copy(alpha = 0.1f))
            .border(1.dp, SabeelColors.SageGreen.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .clickable(onClick = onStart)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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