package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.domain.model.WirdItem
import kotlinx.coroutines.flow.Flow

/** CRUD over the daily-wird plan. Progress is derived elsewhere (ObserveWirdProgress). */
interface WirdRepository {
    fun observeWird(): Flow<List<WirdItem>>
    suspend fun addToWird(dhikrKey: String, target: Int)
    suspend fun updateTarget(dhikrKey: String, target: Int)
    suspend fun removeFromWird(dhikrKey: String)
    suspend fun reorder(orderedKeys: List<String>)
    suspend fun seedDefaultIfEmpty()
}
