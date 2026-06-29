package com.kutubuddin.sabeel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SakinahDao {

    @Query("SELECT * FROM daily_targets WHERE date = :date")
    fun getDailyTargetFlow(date: String): Flow<DailyTargetEntity?>

    @Query("SELECT * FROM daily_targets WHERE date = :date")
    suspend fun getDailyTarget(date: String): DailyTargetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyTarget(target: DailyTargetEntity)

    @Query("SELECT * FROM streaks WHERE id = :id")
    fun getStreakFlow(id: String = "current_streak"): Flow<StreakEntity?>

    @Query("SELECT * FROM streaks WHERE id = :id")
    suspend fun getStreak(id: String = "current_streak"): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)

    @Transaction
    suspend fun updateProgressAndStreak(
        date: String,
        progress: Int,
        target: Int,
        streakCount: Int
    ) {
        insertDailyTarget(DailyTargetEntity(date = date, target = target, progress = progress))
        insertStreak(StreakEntity(id = "current_streak", count = streakCount, lastActiveDate = date))
    }
}
