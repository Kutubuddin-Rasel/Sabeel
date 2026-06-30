package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.domain.model.DailyTarget
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
import com.kutubuddin.sabeel.domain.model.Streak
import kotlinx.coroutines.flow.Flow

interface TasbihRepository {
    val activeCount: Flow<Int>
    val activeDhikr: Flow<DhikrType>
    val isSmartFlowEnabled: Flow<Boolean>
    val smartFlowVariant: Flow<SmartFlowVariant>
    val isPocketModeActive: Flow<Boolean>
    val streak: Flow<Streak?>

    fun getDailyTargetFlow(date: String, dhikrType: DhikrType): Flow<DailyTarget?>

    suspend fun incrementCount(date: String): Int
    suspend fun decrementCount(): Int
    suspend fun resetCount()
    suspend fun setDhikr(dhikr: DhikrType)
    suspend fun setSmartFlowEnabled(enabled: Boolean)
    suspend fun setSmartFlowVariant(variant: SmartFlowVariant)
    suspend fun setPocketModeActive(active: Boolean)
    suspend fun completeDhikrTarget(date: String, dhikrType: DhikrType, targetCount: Int)
}
