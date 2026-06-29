package com.kutubuddin.sabeel

import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomEntityTest {

    @Test
    fun testDailyTargetEntityProperties() {
        val entity = DailyTargetEntity(
            date = "2026-06-29",
            target = 100,
            progress = 42
        )
        assertEquals("2026-06-29", entity.date)
        assertEquals(100, entity.target)
        assertEquals(42, entity.progress)
    }

    @Test
    fun testStreakEntityProperties() {
        val entity = StreakEntity(
            id = "current_streak",
            count = 5,
            lastActiveDate = "2026-06-29"
        )
        assertEquals("current_streak", entity.id)
        assertEquals(5, entity.count)
        assertEquals("2026-06-29", entity.lastActiveDate)
    }
}
