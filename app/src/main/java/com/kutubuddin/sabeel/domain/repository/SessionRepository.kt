package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSessionsForDate(dateKey: String): Flow<List<DhikrSessionEntity>>
    fun getTotalCountForDate(dateKey: String): Flow<Int>
    fun getTotalAllTime(): Flow<Int>
    fun getTotalSessionCount(): Flow<Int>
    suspend fun insertSession(session: DhikrSessionEntity)
}
