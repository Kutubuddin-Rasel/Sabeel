package com.kitalonlabs.sabeel.data.repository

import com.kitalonlabs.sabeel.data.local.db.dao.WirdDao
import com.kitalonlabs.sabeel.data.local.db.entity.WirdItemEntity
import com.kitalonlabs.sabeel.di.IoDispatcher
import com.kitalonlabs.sabeel.domain.model.DhikrType
import com.kitalonlabs.sabeel.domain.model.WirdItem
import com.kitalonlabs.sabeel.domain.repository.WirdRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WirdRepositoryImpl @Inject constructor(
    private val wirdDao: WirdDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : WirdRepository {

    override fun observeWird(): Flow<List<WirdItem>> =
        wirdDao.observeWird().map { rows ->
            rows.map { WirdItem(it.dhikrKey, it.target, it.position) }
        }

    override suspend fun addToWird(dhikrKey: String, target: Int) = withContext(io) {
        wirdDao.insertAtEnd(dhikrKey, target.coerceAtLeast(1))
    }

    override suspend fun updateTarget(dhikrKey: String, target: Int) = withContext(io) {
        val existing = wirdDao.getByKey(dhikrKey) ?: return@withContext
        wirdDao.upsert(existing.copy(target = target.coerceAtLeast(1)))
    }

    override suspend fun removeFromWird(dhikrKey: String) = withContext(io) {
        wirdDao.delete(dhikrKey)
    }

    override suspend fun reorder(orderedKeys: List<String>) = withContext(io) {
        val byKey = wirdDao.getAllOnce().associateBy { it.dhikrKey }
        val reordered = orderedKeys.mapIndexedNotNull { index, key ->
            byKey[key]?.copy(position = index)
        }
        wirdDao.reorder(reordered)
    }

    override suspend fun seedDefaultIfEmpty() = withContext(io) {
        // No-op. The user should build their own goal from scratch.
    }
}
