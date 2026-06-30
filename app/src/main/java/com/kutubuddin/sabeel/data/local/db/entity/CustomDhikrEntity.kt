package com.kutubuddin.sabeel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-created custom dhikr entries.
 * Shown in the "My Dhikr" category of the Dhikr Library tab.
 */
@Entity(tableName = "custom_dhikr")
data class CustomDhikrEntity(
    @PrimaryKey val id: String,             // UUID
    val arabicText: String,
    val displayName: String,
    val transliteration: String?,
    val target: Int,
    val spiritualReward: String?,
    val createdAt: Long                     // Unix epoch ms
)
