package com.kutubuddin.sabeel.domain.repository

import kotlinx.coroutines.flow.Flow

/** ISP: Pocket Mode (screen-off volume-key counting) activation — a distinct concern from counting itself. */
interface PocketModeSettings {
    val isPocketModeActive: Flow<Boolean>
    suspend fun setPocketModeActive(active: Boolean)
}