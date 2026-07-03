package com.kutubuddin.sabeel.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WirdProgressTest {

    private fun item(key: String, target: Int, count: Int) =
        WirdProgressItem(key, key, "ar", null, target, count, 0)

    @Test
    fun isComplete_atOrAboveTarget() {
        assertFalse(item("A", 33, 32).isComplete)
        assertTrue(item("A", 33, 33).isComplete)
        assertTrue(item("A", 33, 40).isComplete)
    }

    @Test
    fun aggregate_countsAndSums() {
        val p = WirdProgress(listOf(item("A", 33, 33), item("B", 100, 40)))
        assertEquals(1, p.completed)
        assertEquals(2, p.total)
        assertFalse(p.allComplete)
        assertEquals(73, p.countedSum)   // 33 + 40
        assertEquals(133, p.targetSum)   // 33 + 100
    }

    @Test
    fun countedSum_clampsPerItemAtTarget() {
        val p = WirdProgress(listOf(item("A", 33, 99))) // over-count doesn't inflate the bar
        assertEquals(33, p.countedSum)
    }

    @Test
    fun allComplete_isFalseForEmpty() {
        assertFalse(WirdProgress(emptyList()).allComplete)
    }

    @Test
    fun liveContribution_addsOnlyForMatchingInProgressRound() {
        // matches, in progress (1..target-1) -> contributes
        assertEquals(20, liveContribution("A", "A", 20, 33))
        // matches but round finished (>= target) -> excluded (session already saved)
        assertEquals(0, liveContribution("A", "A", 33, 33))
        // different active dhikr -> excluded
        assertEquals(0, liveContribution("A", "B", 20, 33))
        // zero count -> excluded
        assertEquals(0, liveContribution("A", "A", 0, 33))
    }
}
