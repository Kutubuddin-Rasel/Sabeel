package com.kutubuddin.sabeel.data.local.db.dao

/**
 * Projection for aggregating dhikr sessions grouped by dhikrKey.
 * Used by HomeViewModel to render the "Today's Sessions" list efficiently.
 */
data class AggregatedSessionRow(
    val dhikrKey: String,
    val totalCount: Int,
    val lastInteraction: Long
)
