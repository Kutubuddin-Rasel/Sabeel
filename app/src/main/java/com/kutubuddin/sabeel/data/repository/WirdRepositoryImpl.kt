package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.db.dao.WirdDao
import com.kutubuddin.sabeel.data.local.db.entity.WirdItemEntity
import com.kutubuddin.sabeel.di.IoDispatcher
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.WirdItem
import com.kutubuddin.sabeel.domain.repository.WirdRepository
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
        val position = wirdDao.maxPosition() + 1
        wirdDao.upsert(WirdItemEntity(dhikrKey, target.coerceAtLeast(1), position))
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
        if (wirdDao.count() == 0) {
            DEFAULT_WIRD.forEachIndexed { index, (key, target) ->
                wirdDao.upsert(WirdItemEntity(key, target, index))
            }
        }
    }

    companion object {
        /** Canonical post-Salah tasbih — 33 / 33 / 34. Keys exist in DhikrCatalog. */
        private val DEFAULT_WIRD = listOf(
            DhikrType.SUBHANALLAH.name to 33,
            DhikrType.ALHAMDULILLAH.name to 33,
            DhikrType.ALLAHU_AKBAR.name to 34
        )
    }
}
