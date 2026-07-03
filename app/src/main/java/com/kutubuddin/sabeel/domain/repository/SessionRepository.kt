package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSessionsForDate(dateKey: String): Flow<List<DhikrSessionEntity>>
    fun getTotalCountForDate(dateKey: String): Flow<Int>
    fun getTotalAllTime(): Flow<Int>
    fun getTotalSessionCount(): Flow<Int>
    suspend fun insertSession(session: DhikrSessionEntity)

    /** Map of dhikrKey → total counted for the given date. */
    fun getCountsByKeyForDate(dateKey: String): Flow<Map<String, Int>>
}
