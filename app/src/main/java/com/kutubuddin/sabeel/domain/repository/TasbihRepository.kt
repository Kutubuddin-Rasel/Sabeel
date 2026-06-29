package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import com.kutubuddin.sabeel.domain.model.DhikrType
import kotlinx.coroutines.flow.Flow

interface TasbihRepository {
    val activeCount: Flow<Int>
    val activeDhikr: Flow<DhikrType>
    val isSmartFlowEnabled: Flow<Boolean>
    val isPocketModeActive: Flow<Boolean>
    val streak: Flow<StreakEntity?>
    
    fun getDailyTargetFlow(date: String, dhikrType: DhikrType): Flow<DailyTargetEntity?>
    
    suspend fun incrementCount(date: String): Int
    suspend fun resetCount()
    suspend fun setDhikr(dhikr: DhikrType)
    suspend fun setSmartFlowEnabled(enabled: Boolean)
    suspend fun setPocketModeActive(active: Boolean)
    suspend fun completeDhikrTarget(date: String, dhikrType: DhikrType, targetCount: Int)
}
