package com.kutubuddin.sabeel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Records every counting session — both completed and partial.
 * Queried by HomeViewModel for today's progress and session history.
 */
@Entity(tableName = "dhikr_sessions")
data class DhikrSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dhikrKey: String,       // DhikrType.name or custom UUID
    val count: Int,
    val target: Int,
    val isComplete: Boolean,
    val dateKey: String,        // "2026-06-30" — for daily grouping
    val endedAt: Long           // Unix epoch ms
)
