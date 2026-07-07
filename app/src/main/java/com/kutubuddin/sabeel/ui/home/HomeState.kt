package com.kutubuddin.sabeel.ui.home

import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.WirdGoalHintState
import com.kutubuddin.sabeel.domain.model.WirdProgress

data class HomeState(
    val todaysSessions: List<SessionSummary> = emptyList(),
    val totalToday: Int = 0,
    val wird: WirdSummary = WirdSummary(),
    val currentStreak: Int = 0,
    val totalAllTime: Int = 0,
    val totalSessionCount: Int = 0,
    val resumeSession: ResumeSession? = null,
    val greeting: GreetingType = GreetingType.DEFAULT,
    val showStreaks: Boolean = true,
    val language: String = "en",
    val wirdGoalHint: WirdGoalHintState = WirdGoalHintState.Hidden
)

/** Non-null only when the user has an in-progress (incomplete) session. */
data class ResumeSession(
    val dhikrKey: String,
    val displayName: LocalizedText,
    val lastCount: Int,
    val target: Int
)

/**
 * UI-safe projection of [com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity].
 *
 * SRP/DIP: the Room entity must never cross into the Composable layer, and
 * resolving a display name from [com.kutubuddin.sabeel.domain.model.DhikrCatalog]
 * is a domain lookup, not rendering — both concerns are handled once, in
 * [HomeViewModel], never inside a Composable.
 */
data class SessionSummary(
    val dhikrKey: String,
    val displayName: LocalizedText,
    val count: Int,
    val isComplete: Boolean
)

/** Lean wird view for the Home summary card — numbers only, no item list. */
data class WirdSummary(
    val completed: Int = 0,
    val total: Int = 0,
    val countedSum: Int = 0,
    val targetSum: Int = 0,
    val isEmpty: Boolean = true
)

fun WirdProgress.toSummary(): WirdSummary = WirdSummary(
    completed = completed,
    total = total,
    countedSum = countedSum,
    targetSum = targetSum,
    isEmpty = items.isEmpty()
)