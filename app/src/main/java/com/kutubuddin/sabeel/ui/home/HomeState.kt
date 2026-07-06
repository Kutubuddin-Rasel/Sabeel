package com.kutubuddin.sabeel.ui.home

import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.WirdProgress

data class HomeState(
    val todaysSessions: List<DhikrSessionEntity> = emptyList(),
    val totalToday: Int = 0,
    val wird: WirdSummary = WirdSummary(),
    val currentStreak: Int = 0,
    val totalAllTime: Int = 0,
    val totalSessionCount: Int = 0,
    val resumeSession: ResumeSession? = null,
    val greeting: GreetingType = GreetingType.DEFAULT,
    val showStreaks: Boolean = true,
    val language: String = "en"
)

/** Non-null only when the user has an in-progress (incomplete) session. */
data class ResumeSession(
    val dhikrKey: String,
    val displayName: LocalizedText,
    val lastCount: Int,
    val target: Int
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
