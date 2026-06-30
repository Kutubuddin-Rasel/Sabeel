package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kutubuddin.sabeel.domain.model.DhikrItem
import kotlinx.coroutines.flow.Flow

interface DhikrRepository {
    /** Built-in catalog merged with user's custom dhikr, grouped-ready. */
    fun getAllDhikr(): Flow<List<DhikrItem>>
    fun getCustomDhikr(): Flow<List<CustomDhikrEntity>>
    suspend fun saveCustomDhikr(dhikr: CustomDhikrEntity)
    suspend fun deleteCustomDhikr(dhikr: CustomDhikrEntity)
}
