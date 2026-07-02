package com.kutubuddin.sabeel.ui.tasbih

import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the pure sequence-advance function. It has no Android or
 * coroutine dependency, so the whole post-Salah state machine is verified here
 * without a ViewModel or a device.
 */
class SequenceEngineTest {
    private val classic = DhikrCatalog.sequenceFor("SMART_FLOW_CLASSIC")!!
    private val withTahlil = DhikrCatalog.sequenceFor("SMART_FLOW_WITH_TAHLIL")!!

    @Test fun tick_within_step() {
        val r = TasbihViewModel.advance(classic, stepIndex = 0, stepCount = 5)
        assertEquals(0, r.stepIndex)
        assertEquals(6, r.stepCount)
        assertFalse(r.stepCompleted)
        assertFalse(r.sequenceCompleted)
    }

    @Test fun completing_step_advances() {
        val r = TasbihViewModel.advance(classic, stepIndex = 0, stepCount = 32) // -> 33
        assertTrue(r.stepCompleted)
        assertEquals(1, r.stepIndex)
        assertEquals(0, r.stepCount)
        assertFalse(r.sequenceCompleted)
    }

    @Test fun completing_last_step_finishes() {
        val r = TasbihViewModel.advance(classic, stepIndex = 2, stepCount = 33) // Akbar -> 34
        assertTrue(r.stepCompleted)
        assertTrue(r.sequenceCompleted)
    }

    @Test fun tahlil_single_count_finishes_sequence() {
        val r = TasbihViewModel.advance(withTahlil, stepIndex = 3, stepCount = 0) // Tahlil -> 1
        assertTrue(r.stepCompleted)
        assertTrue(r.sequenceCompleted)
    }
}
