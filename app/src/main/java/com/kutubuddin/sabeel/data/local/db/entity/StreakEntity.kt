package com.kutubuddin.sabeel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey
    val id: String = "current_streak",
    val count: Int,
    val lastActiveDate: String,
    val longestStreak: Int
)
