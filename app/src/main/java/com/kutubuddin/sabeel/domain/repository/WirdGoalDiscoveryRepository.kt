package com.kutubuddin.sabeel.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * ISP: narrow, single-purpose contract for the one-time "how do I add/update
 * my daily goal (wird)" discoverability signal. Kept separate from
 * [SettingsRepository] so callers that only need to observe/dismiss this
 * signal are never forced to depend on the full settings read/write surface.
 */
interface WirdGoalDiscoveryRepository {
    /** True until any wird-edit entry point has been used, ever. */
    val isGoalEditHintUnseen: Flow<Boolean>

    /** Idempotent — safe to invoke from every entry point that teaches the gesture. */
    suspend fun markGoalEditHintSeen()
}
