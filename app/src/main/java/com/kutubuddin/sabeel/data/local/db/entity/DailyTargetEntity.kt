package com.kutubuddin.sabeel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_targets")
data class DailyTargetEntity(
    @PrimaryKey val id: String,
    val date: String,
    val dhikrType: String,
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean
)
