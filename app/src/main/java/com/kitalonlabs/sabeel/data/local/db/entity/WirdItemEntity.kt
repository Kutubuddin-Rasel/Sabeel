package com.kitalonlabs.sabeel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One entry in the user's daily wird — the PLAN only (which dhikr, what target,
 * what order). Actual counts live in dhikr_sessions; progress is derived.
 * @PrimaryKey dhikrKey enforces one entry per dhikr at the DB level.
 */
@Entity(tableName = "wird_items")
data class WirdItemEntity(
    @PrimaryKey val dhikrKey: String,
    val target: Int,
    val position: Int
)
