package com.kutubuddin.sabeel

import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomEntityTest {

    @Test
    fun testDailyTargetEntityProperties() {
        val entity = DailyTargetEntity(
            id = "SUBHANALLAH_2026-06-29",
            date = "2026-06-29",
            dhikrType = "SUBHANALLAH",
            currentCount = 42,
            targetCount = 100,
            isCompleted = false
        )
        assertEquals("SUBHANALLAH_2026-06-29", entity.id)
        assertEquals("2026-06-29", entity.date)
        assertEquals("SUBHANALLAH", entity.dhikrType)
        assertEquals(42, entity.currentCount)
        assertEquals(100, entity.targetCount)
        assertEquals(false, entity.isCompleted)
    }

    @Test
    fun testStreakEntityProperties() {
        val entity = StreakEntity(
            id = "current_streak",
            count = 5,
            lastActiveDate = "2026-06-29",
            longestStreak = 10
        )
        assertEquals("current_streak", entity.id)
        assertEquals(5, entity.count)
        assertEquals("2026-06-29", entity.lastActiveDate)
        assertEquals(10, entity.longestStreak)
    }
}
