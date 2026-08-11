package com.kitalonlabs.sabeel.data.repository

import com.kitalonlabs.sabeel.data.local.db.dao.AggregatedSessionRow
import com.kitalonlabs.sabeel.data.local.db.dao.DhikrSessionDao
import com.kitalonlabs.sabeel.data.local.db.dao.SakinahDao
import com.kitalonlabs.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kitalonlabs.sabeel.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher
import com.kitalonlabs.sabeel.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val dao: DhikrSessionDao,
    private val sakinahDao: SakinahDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SessionRepository {
    override fun getSessionsForDate(dateKey: String): Flow<List<DhikrSessionEntity>> =
        dao.getSessionsForDate(dateKey)

    override fun getTotalCountForDate(dateKey: String): Flow<Int> =
        dao.getTotalCountForDate(dateKey)

    override fun getTotalAllTime(): Flow<Int> = dao.getTotalAllTime()

    override fun getTotalSessionCount(): Flow<Int> = dao.getTotalSessionCount()

    override suspend fun insertSession(session: DhikrSessionEntity) = withContext(ioDispatcher) {
        dao.insertSession(session)
    }

    override fun getCountsByKeyForDate(dateKey: String): Flow<Map<String, Int>> {
        return dao.getCountsByKeyForDate(dateKey).map { rows -> rows.associate { it.dhikrKey to it.total } }
    }

    override fun getAggregatedSessionsForDate(dateKey: String): Flow<List<com.kitalonlabs.sabeel.data.local.db.dao.AggregatedSessionRow>> {
        return dao.getAggregatedSessionsForDate(dateKey).map { sessions ->
            sessions.sortedByDescending { it.lastInteraction }
        }
    }
}
