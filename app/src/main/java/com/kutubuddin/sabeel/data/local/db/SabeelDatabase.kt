package com.kutubuddin.sabeel.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kutubuddin.sabeel.data.local.db.dao.CustomDhikrDao
import com.kutubuddin.sabeel.data.local.db.dao.DhikrSessionDao
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity

@Database(
    entities = [
        DailyTargetEntity::class,
        StreakEntity::class,
        DhikrSessionEntity::class,
        CustomDhikrEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SabeelDatabase : RoomDatabase() {

    abstract fun sakinahDao(): SakinahDao
    abstract fun dhikrSessionDao(): DhikrSessionDao
    abstract fun customDhikrDao(): CustomDhikrDao

    companion object {
        /**
         * Migration from v1 → v2: adds dhikr_sessions and custom_dhikr tables.
         * Existing daily_targets and streaks data is preserved.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS dhikr_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        dhikrKey TEXT NOT NULL,
                        count INTEGER NOT NULL,
                        target INTEGER NOT NULL,
                        isComplete INTEGER NOT NULL,
                        dateKey TEXT NOT NULL,
                        endedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS custom_dhikr (
                        id TEXT PRIMARY KEY NOT NULL,
                        arabicText TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        transliteration TEXT,
                        target INTEGER NOT NULL,
                        spiritualReward TEXT,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
