package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.db.dao.DhikrSessionDao
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val dao: DhikrSessionDao
) : SessionRepository {
    override fun getSessionsForDate(dateKey: String): Flow<List<DhikrSessionEntity>> =
        dao.getSessionsForDate(dateKey)

    override fun getTotalCountForDate(dateKey: String): Flow<Int> =
        dao.getTotalCountForDate(dateKey)

    override fun getTotalAllTime(): Flow<Int> = dao.getTotalAllTime()

    override fun getTotalSessionCount(): Flow<Int> = dao.getTotalSessionCount()

    override suspend fun insertSession(session: DhikrSessionEntity) =
        dao.insertSession(session)
}
