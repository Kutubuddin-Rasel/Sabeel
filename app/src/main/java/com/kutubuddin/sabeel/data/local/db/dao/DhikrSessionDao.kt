package com.kutubuddin.sabeel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: DhikrSessionEntity)

    /** All sessions for a given date key, most recent first. */
    @Query("SELECT * FROM dhikr_sessions WHERE dateKey = :dateKey ORDER BY endedAt DESC")
    fun getSessionsForDate(dateKey: String): Flow<List<DhikrSessionEntity>>

    /** Total count across all sessions for a given date. Used for daily goal progress. */
    @Query("SELECT COALESCE(SUM(count), 0) FROM dhikr_sessions WHERE dateKey = :dateKey")
    fun getTotalCountForDate(dateKey: String): Flow<Int>

    /** Total count across all time — for the Home tab "All Time" stat. */
    @Query("SELECT COALESCE(SUM(count), 0) FROM dhikr_sessions")
    fun getTotalAllTime(): Flow<Int>

    /** Total number of sessions ever. */
    @Query("SELECT COUNT(*) FROM dhikr_sessions")
    fun getTotalSessionCount(): Flow<Int>
}
