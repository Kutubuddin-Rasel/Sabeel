package com.kutubuddin.sabeel.domain.model

/** One item in the daily wird plan: which dhikr, its per-day target, its order. */
data class WirdItem(
    val dhikrKey: String,
    val target: Int,
    val position: Int
)
