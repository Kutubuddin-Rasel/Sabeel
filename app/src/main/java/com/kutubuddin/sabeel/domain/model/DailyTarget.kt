package com.kutubuddin.sabeel.domain.model

data class DailyTarget(
    val id: String,
    val date: String,
    val dhikrType: String,
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean
)
