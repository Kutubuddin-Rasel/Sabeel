package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.db.dao.CustomDhikrDao
import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.DhikrMeaning
import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher
import com.kutubuddin.sabeel.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DhikrRepositoryImpl @Inject constructor(
    private val customDhikrDao: CustomDhikrDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DhikrRepository {

    /**
     * Returns built-in catalog + custom entries merged into a single list.
     * Built-in entries are always first; custom entries appear in "My Dhikr" category.
     */
    override fun getAllDhikr(): Flow<List<DhikrItem>> =
        customDhikrDao.getAllCustomDhikr().map { customEntities ->
            val customItems = customEntities.map { it.toDhikrItem() }
            DhikrCatalog.all + customItems
        }

    override fun getCustomDhikr(): Flow<List<CustomDhikrEntity>> =
        customDhikrDao.getAllCustomDhikr()

    override suspend fun saveCustomDhikr(dhikr: CustomDhikrEntity) = withContext(ioDispatcher) {
        customDhikrDao.insertCustomDhikr(dhikr)
    }

    override suspend fun deleteCustomDhikr(dhikr: CustomDhikrEntity) = withContext(ioDispatcher) {
        customDhikrDao.deleteCustomDhikr(dhikr)
    }

    private fun CustomDhikrEntity.toDhikrItem() = DhikrItem(
        key = id,
        arabicText = arabicText,
        displayName = LocalizedText(en = displayName, bn = displayName),
        transliteration = transliteration?.let { LocalizedText(en = it, bn = it) },
        meaning = DhikrMeaning(en = spiritualReward ?: displayName),
        defaultTarget = target,
        spiritualReward = LocalizedText(en = spiritualReward ?: ""),
        hadithRef = "",
        category = DhikrCategory.CUSTOM,
        isCustom = true
    )
}
