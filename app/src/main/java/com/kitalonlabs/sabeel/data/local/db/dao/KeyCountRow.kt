package com.kitalonlabs.sabeel.data.local.db.dao

/** Projection row for "SUM(count) per dhikrKey" aggregation. */
data class KeyCountRow(
    val dhikrKey: String,
    val total: Int
)
