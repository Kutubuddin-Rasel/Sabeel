package com.kitalonlabs.sabeel.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kitalonlabs.sabeel.data.local.db.dao.CustomDhikrDao
import com.kitalonlabs.sabeel.data.local.db.dao.DhikrSessionDao
import com.kitalonlabs.sabeel.data.local.db.dao.SakinahDao
import com.kitalonlabs.sabeel.data.local.db.dao.WirdDao
import com.kitalonlabs.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kitalonlabs.sabeel.data.local.db.entity.DailyTargetEntity
import com.kitalonlabs.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kitalonlabs.sabeel.data.local.db.entity.StreakEntity
import com.kitalonlabs.sabeel.data.local.db.entity.WirdItemEntity

@Database(
    entities = [
        DailyTargetEntity::class,
        StreakEntity::class,
        DhikrSessionEntity::class,
        CustomDhikrEntity::class,
        WirdItemEntity::class
    ],
    version = 3,
    // OPT-06: exportSchema=true — Room generates a JSON schema file per version in
    // the directory configured via `room.schemaLocation` in build.gradle (KSP args).
    // This enables MigrationTestHelper to verify MIGRATION_1_2 and MIGRATION_2_3
    // produce the correct schema on upgrade, preventing silent data corruption.
    exportSchema = true
)
abstract class SabeelDatabase : RoomDatabase() {

    abstract fun sakinahDao(): SakinahDao
    abstract fun dhikrSessionDao(): DhikrSessionDao
    abstract fun customDhikrDao(): CustomDhikrDao
    abstract fun wirdDao(): WirdDao

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

        /** v2 → v3: adds the wird_items table (the daily-wird plan). Purely structural. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS wird_items (
                        dhikrKey TEXT PRIMARY KEY NOT NULL,
                        target INTEGER NOT NULL,
                        position INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
