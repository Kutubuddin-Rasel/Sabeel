package com.kutubuddin.sabeel.domain.model

data class Streak(
    val id: String,
    val count: Int,
    val lastActiveDate: String,
    val longestStreak: Int
)
