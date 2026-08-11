package com.kitalonlabs.sabeel.domain.model

/** One item in the daily wird plan: which dhikr, its per-day target, its order. */
import androidx.compose.runtime.Immutable

@Immutable
data class WirdItem(
    val dhikrKey: String,
    val target: Int,
    val position: Int
)
