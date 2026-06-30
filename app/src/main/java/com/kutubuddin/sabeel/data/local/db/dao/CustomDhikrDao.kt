package com.kutubuddin.sabeel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomDhikrDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomDhikr(dhikr: CustomDhikrEntity)

    @Delete
    suspend fun deleteCustomDhikr(dhikr: CustomDhikrEntity)

    @Query("SELECT * FROM custom_dhikr ORDER BY createdAt DESC")
    fun getAllCustomDhikr(): Flow<List<CustomDhikrEntity>>

    @Query("SELECT * FROM custom_dhikr WHERE id = :id")
    suspend fun getCustomDhikrById(id: String): CustomDhikrEntity?
}
