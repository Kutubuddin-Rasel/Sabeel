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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
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
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import java.text.NumberFormat

@Composable
fun HomeScreen(
    onResumeCounting: () -> Unit,
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
            Column {
                Text(
                    text = "${state.greetingIcon}  ${state.greeting}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SabeelColors.TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                val todayLabel = remember {
                    java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
                }
                Text(
                    text = todayLabel,
                    fontSize = 13.sp,
                    color = SabeelColors.TextSecondary
                )
            }
        }

        // ── Resume button ─────────────────────────────────────────────────────
        if (state.resumeSession != null) {
            item {
                ResumeCard(
                    session = state.resumeSession!!,
                    onClick = onResumeCounting
                )
            }
        }

        // ── Streak + Daily Goal ───────────────────────────────────────────────
        item {
            StreakGoalCard(state = state)
        }

        // ── Today's Sessions header ───────────────────────────────────────────
        if (state.todaysSessions.isNotEmpty()) {
            item {
                SectionHeader("Today's Sessions")
            }
            items(state.todaysSessions) { session ->
                SessionRow(session)
            }
        } else {
            item {
                EmptyTodayCard()
            }
        }

        // ── All Time stats ────────────────────────────────────────────────────
        item {
            SectionHeader("All Time")
        }
        item {
            AllTimeCard(state = state)
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ResumeCard(session: ResumeSession, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.GoldSurface)
            .border(1.dp, SabeelColors.GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("▶  Resume", fontSize = 12.sp, color = SabeelColors.GoldPrimary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Text(
                text = session.dhikrType.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SabeelColors.TextPrimary
            )
        }
        Text(
            text = "${session.lastCount} / ${session.target}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SabeelColors.GoldPrimary
        )
    }
}

@Composable
private fun StreakGoalCard(state: HomeState) {
    val goalFraction = if (state.dailyGoal > 0)
        (state.totalToday.toFloat() / state.dailyGoal).coerceIn(0f, 1f)
    else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = goalFraction,
        animationSpec = tween(800),
        label = "goal_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Streak row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatPill(label = "🔥 Streak", value = "${state.currentStreak} days")
            StatPill(label = "🏆 Best", value = "${state.longestStreak} days")
        }

        HorizontalDivider(color = SabeelColors.Divider)

        // Daily goal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎯 Daily Goal", fontSize = 13.sp, color = SabeelColors.TextSecondary)
            Text(
                text = "${state.totalToday} / ${state.dailyGoal}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SabeelColors.TextPrimary
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = SabeelColors.GoldPrimary,
            trackColor = SabeelColors.ArcTrack,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = SabeelColors.TextSecondary)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SabeelColors.TextPrimary)
    }
}

@Composable
private fun SessionRow(session: DhikrSessionEntity) {
    val displayName = try {
        DhikrType.valueOf(session.dhikrKey).displayName
    } catch (e: Exception) {
        session.dhikrKey
    }

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
            Text(displayName, fontSize = 14.sp, color = SabeelColors.TextPrimary, fontWeight = FontWeight.Medium)
            Text(
                text = if (session.isComplete) "Completed ✓" else "Partial",
                fontSize = 11.sp,
                color = if (session.isComplete) SabeelColors.SageGreen else SabeelColors.TextSecondary
            )
        }
        Text(
            text = "${session.count}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SabeelColors.GoldPrimary
        )
    }
}

@Composable
private fun AllTimeCard(state: HomeState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AllTimeStat("Total Counted", NumberFormat.getInstance().format(state.totalAllTime))
        AllTimeStat("Sessions", "${state.totalSessionCount}")
    }
}

@Composable
private fun AllTimeStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SabeelColors.TextPrimary)
        Text(label, fontSize = 11.sp, color = SabeelColors.TextSecondary)
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        color = SabeelColors.TextSecondary
    )
}

@Composable
private fun EmptyTodayCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("سَبِيل", fontSize = 28.sp, color = SabeelColors.GoldPrimary.copy(alpha = 0.5f))
        Text(
            text = "Begin your first session today",
            fontSize = 14.sp,
            color = SabeelColors.TextSecondary
        )
    }
}
