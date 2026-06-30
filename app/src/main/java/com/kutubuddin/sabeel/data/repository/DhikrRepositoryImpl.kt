package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.db.dao.CustomDhikrDao
import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.DhikrMeaning
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DhikrRepositoryImpl @Inject constructor(
    private val customDhikrDao: CustomDhikrDao
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

    override suspend fun saveCustomDhikr(dhikr: CustomDhikrEntity) =
        customDhikrDao.insertCustomDhikr(dhikr)

    override suspend fun deleteCustomDhikr(dhikr: CustomDhikrEntity) =
        customDhikrDao.deleteCustomDhikr(dhikr)

    private fun CustomDhikrEntity.toDhikrItem() = DhikrItem(
        key = id,
        arabicText = arabicText,
        displayName = displayName,
        transliteration = transliteration,
        meaning = DhikrMeaning(en = spiritualReward ?: displayName),
        defaultTarget = target,
        spiritualReward = spiritualReward ?: "",
        hadithRef = "",
        category = DhikrCategory.CUSTOM,
        isCustom = true
    )
}
