package com.kutubuddin.sabeel.domain.model
import androidx.compose.runtime.Immutable

@Immutable
data class DailyTarget(
    val id: String,
    val date: String,
    val dhikrType: String,
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean
)
