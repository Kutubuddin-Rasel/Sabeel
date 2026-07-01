package com.kutubuddin.sabeel.ui.home

import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity

data class HomeState(
    val todaysSessions: List<DhikrSessionEntity> = emptyList(),
    val totalToday: Int = 0,
    val dailyGoal: Int = 200,
    val currentStreak: Int = 0,
    val totalAllTime: Int = 0,
    val totalSessionCount: Int = 0,
    val resumeSession: ResumeSession? = null,
    val greeting: String = "Assalamu alaikum",
    val showStreaks: Boolean = true
)

/** Non-null only when the user has an in-progress (incomplete) session. */
data class ResumeSession(
    val dhikrKey: String,
    val displayName: String,
    val lastCount: Int,
    val target: Int
)
