package com.kutubuddin.sabeel.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for Home's live-aggregation helpers.
 *
 * Key invariant: the in-progress count is only added while the current round is
 * UNFINISHED (activeCount < target). Once a round completes, a session is saved
 * (and counted in savedToday/savedAllTime), so the still-live activeCount must
 * NOT be added again — otherwise a completed 33-round shows as 66.
 */
class HomeAggregationTest {

    @Test
    fun inProgressCount_addsToSavedToday() {
        // Counting 13 of 33, nothing saved yet → 13.
        assertEquals(13, HomeViewModel.displayedToday(savedToday = 0, activeCount = 13, target = 33))
    }

    @Test
    fun completedRound_notDoubleCounted() {
        // Round complete: session saved (33), activeCount still 33 (auto-reset off).
        // Must show 33, not 66.
        assertEquals(33, HomeViewModel.displayedToday(savedToday = 33, activeCount = 33, target = 33))
    }

    @Test
    fun afterReset_showsSavedOnly() {
        assertEquals(33, HomeViewModel.displayedToday(savedToday = 33, activeCount = 0, target = 33))
    }

    @Test
    fun multipleSavedRounds_plusInProgress() {
        // Two saved rounds (66) + 10 into the third → 76.
        assertEquals(76, HomeViewModel.displayedToday(savedToday = 66, activeCount = 10, target = 33))
    }

    @Test
    fun allTime_followsSameGuard() {
        assertEquals(13, HomeViewModel.displayedAllTime(savedAllTime = 0, activeCount = 13, target = 33))
        assertEquals(100, HomeViewModel.displayedAllTime(savedAllTime = 100, activeCount = 33, target = 33))
    }

    @Test
    fun streak_promotedWhenActiveButNonePersisted() {
        assertEquals(1, HomeViewModel.displayedStreak(streakCount = 0, activeCount = 13))
    }

    @Test
    fun streak_unchangedWhenAlreadyCounted() {
        assertEquals(5, HomeViewModel.displayedStreak(streakCount = 5, activeCount = 13))
    }

    @Test
    fun streak_zeroWhenIdle() {
        assertEquals(0, HomeViewModel.displayedStreak(streakCount = 0, activeCount = 0))
    }
}
