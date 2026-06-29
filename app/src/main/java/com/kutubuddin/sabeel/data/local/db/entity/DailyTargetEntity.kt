package com.kutubuddin.sabeel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_targets")
data class DailyTargetEntity(
    @PrimaryKey
    val date: String,
    val target: Int,
    val progress: Int
)
