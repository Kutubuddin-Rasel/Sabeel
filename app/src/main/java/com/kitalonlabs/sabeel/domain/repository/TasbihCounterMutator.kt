package com.kitalonlabs.sabeel.domain.repository

/**
 * ISP: the minimal write surface for driving the live counter. This is the
 * ENTIRE contract [com.kitalonlabs.sabeel.service.PocketModeService] needs —
 * it only ever calls [incrementCount] — so it no longer depends on Smart Flow
 * settings, Pocket Mode settings, or streak reads it never touches.
 */
interface TasbihCounterMutator {
    suspend fun incrementCount(date: String): Int
    suspend fun decrementCount(): Int
    suspend fun resetCount()
    suspend fun setCount(value: Int)
    suspend fun setDhikr(key: String, targetOverride: Int? = null)
    suspend fun completeDhikrTarget(date: String, dhikrKey: String, targetCount: Int)
    suspend fun flushToDisk()
}