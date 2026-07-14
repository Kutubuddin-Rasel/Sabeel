package com.kutubuddin.sabeel.domain.model

import com.kutubuddin.sabeel.data.repository.applyTargetOverride
import org.junit.Assert.assertEquals
import org.junit.Test

class TargetOverrideTest {

    private fun active(target: Int) =
        ActiveDhikr(
            key = "A", 
            arabicText = "ar", 
            displayName = LocalizedText(en = "name"), 
            target = target, 
            spiritualReward = LocalizedText(en = ""), 
            hadithRef = ""
        )

    @Test
    fun override_replacesTarget_whenPresentAndPositive() {
        assertEquals(100, applyTargetOverride(active(33), 100).target)
    }

    @Test
    fun override_ignored_whenNullOrNonPositive() {
        assertEquals(33, applyTargetOverride(active(33), null).target)
        assertEquals(33, applyTargetOverride(active(33), 0).target)
    }
}
