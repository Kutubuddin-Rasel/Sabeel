package com.kutubuddin.sabeel.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity

@Database(
    entities = [DailyTargetEntity::class, StreakEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SabeelDatabase : RoomDatabase() {
    abstract fun sakinahDao(): SakinahDao
}
