package com.kitalonlabs.sabeel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kitalonlabs.sabeel.data.local.db.entity.WirdItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WirdDao {

    @Query("SELECT * FROM wird_items ORDER BY position ASC")
    fun observeWird(): Flow<List<WirdItemEntity>>

    @Query("SELECT * FROM wird_items ORDER BY position ASC")
    suspend fun getAllOnce(): List<WirdItemEntity>

    @Query("SELECT * FROM wird_items WHERE dhikrKey = :key")
    suspend fun getByKey(key: String): WirdItemEntity?

    @Query("SELECT COUNT(*) FROM wird_items")
    suspend fun count(): Int

    @Query("SELECT COALESCE(MAX(position), -1) FROM wird_items")
    suspend fun maxPosition(): Int

    @Upsert
    suspend fun upsert(item: WirdItemEntity)

    @Query("INSERT OR REPLACE INTO wird_items (dhikrKey, target, position) VALUES (:key, :target, (SELECT COALESCE(MAX(position), -1) + 1 FROM wird_items))")
    suspend fun insertAtEnd(key: String, target: Int)

    @Query("DELETE FROM wird_items WHERE dhikrKey = :key")
    suspend fun delete(key: String)

    /** Rewrite positions in one transaction (drag-to-reorder). */
    @Transaction
    suspend fun reorder(items: List<WirdItemEntity>) {
        items.forEach { upsert(it) }
    }
}
