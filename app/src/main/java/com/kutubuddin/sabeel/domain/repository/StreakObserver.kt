package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.domain.model.DailyTarget
import com.kutubuddin.sabeel.domain.model.Streak
import kotlinx.coroutines.flow.Flow

/**
 * ISP: historical/derived progress reads — the current consistency streak and
 * the legacy per-dhikr daily target row. Kept separate from
 * [TasbihCounterObserver] because it answers a different question ("how am I
 * doing over time") than the live in-memory counter ("what am I on right now").
 */
interface StreakObserver {
    val streak: Flow<Streak?>
    fun getDailyTargetFlow(date: String, dhikrKey: String): Flow<DailyTarget?>
}