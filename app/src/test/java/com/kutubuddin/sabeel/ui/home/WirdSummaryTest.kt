package com.kutubuddin.sabeel.ui.home

import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WirdSummaryTest {

    private fun item(target: Int, count: Int) =
        WirdProgressItem("k$target", LocalizedText(en = "n"), "a", null, target, count, 0)

    @Test
    fun toSummary_mapsCountsAndEmptiness() {
        val s = WirdProgress(listOf(item(33, 33), item(100, 40))).toSummary()
        assertEquals(1, s.completed)
        assertEquals(2, s.total)
        assertEquals(73, s.countedSum)
        assertEquals(133, s.targetSum)
        assertFalse(s.isEmpty)
    }

    @Test
    fun toSummary_emptyWird() {
        val s = WirdProgress(emptyList()).toSummary()
        assertTrue(s.isEmpty)
        assertEquals(0, s.total)
    }
}
