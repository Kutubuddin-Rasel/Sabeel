package com.kutubuddin.sabeel.domain.model
import androidx.compose.runtime.Immutable

@Immutable
data class Streak(
    val id: String,
    val count: Int,
    val lastActiveDate: String,
    val longestStreak: Int
)
