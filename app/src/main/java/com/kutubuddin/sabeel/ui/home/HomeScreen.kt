package com.kutubuddin.sabeel.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.UiStrings
import com.kutubuddin.sabeel.ui.i18n.localizeDigits
import com.kutubuddin.sabeel.ui.i18n.toGroupedLocalizedNumerals
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import java.util.Locale

@Composable
fun HomeScreen(
    onResumeCounting: () -> Unit,
    onOpenWird: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

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
                // Locale-aware day/month names, then localized digits so the
                // whole date follows the app language (not the device locale).
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
                    onClick = onResumeCounting
                )
            }
        } else {
            item {
                HeroStartCard(onStart = onResumeCounting)
            }
        }

        // ── Stats (demoted below the hero) ────────────────────────────────────
        item {
            StreakGoalCard(state = state, onOpenWird = onOpenWird)
        }

        // ── Today's Sessions ──────────────────────────────────────────────────
        if (state.todaysSessions.isNotEmpty()) {
            item {
                SectionHeader(LocalStrings.current.homeTodaysSessions)
            }
            items(state.todaysSessions) { session ->
                SessionRow(session, state.language)
            }
        }

        // ── All Time stats ────────────────────────────────────────────────────
        item {
            SectionHeader(LocalStrings.current.homeAllTime)
        }
        item {
            AllTimeCard(state = state)
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
        Text(
            text = "${session.lastCount.toLocalizedNumerals(language)} / ${session.target.toLocalizedNumerals(language)}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = SabeelColors.AccentTeal
        )
    }
}

@Composable
private fun StreakGoalCard(state: HomeState, onOpenWird: () -> Unit) {
    val strings = LocalStrings.current
    val fraction = if (state.wird.targetSum > 0)
        (state.wird.countedSum.toFloat() / state.wird.targetSum).coerceIn(0f, 1f)
    else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = fraction, animationSpec = tween(800), label = "wird_progress"
    )

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
                    text = if (state.currentStreak == 1) strings.homeDayOne.format(streakDigits)
                           else strings.homeDayOther.format(streakDigits),
                    style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary
                )
            }
            HorizontalDivider(color = SabeelColors.Divider)
        }

        // Today's Wird summary (tappable → Wird screen)
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
            Text(
                text = if (state.wird.isEmpty) strings.wirdSetup
                       else strings.wirdDoneOf.format(
                           state.wird.completed.toLocalizedNumerals(state.language),
                           state.wird.total.toLocalizedNumerals(state.language)
                       ),
                style = MaterialTheme.typography.labelLarge, color = SabeelColors.TextPrimary
            )
        }
        if (!state.wird.isEmpty) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = SabeelColors.AccentTeal, trackColor = SabeelColors.ArcTrack,
                strokeCap = StrokeCap.Round, gapSize = 0.dp, drawStopIndicator = {}
            )
        }
    }
}

@Composable
private fun SessionRow(session: DhikrSessionEntity, language: String) {
    val strings = LocalStrings.current
    val displayName = DhikrCatalog.displayNameFor(session.dhikrKey).get(language)

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
            Text(displayName, style = MaterialTheme.typography.labelLarge, color = SabeelColors.TextPrimary)
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

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
        color = SabeelColors.TextSecondary
    )
}

@Composable
private fun HeroStartCard(onStart: () -> Unit) {
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
        // سَبِيل flourish — a rare, accepted gold accent.
        Text("سَبِيل", fontSize = 30.sp, color = SabeelColors.GoldPrimary.copy(alpha = 0.55f))
        Text(
            text = strings.homeBeginToday,
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

/**
 * Maps the resolved [GreetingType] to its localized copy. Lives in the UI layer
 * (not the domain/state) so [HomeState] carries only the language-agnostic enum;
 * the exhaustive `when` makes the compiler flag any GreetingType we forget.
 */
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

/**
 * App-language → JVM [Locale] for date formatting (day/month names). The codes
 * `en`/`ur`/`bn` are valid BCP-47 tags, so a single lookup covers all three.
 */
private fun localeFor(lang: String): Locale = Locale.forLanguageTag(lang)
