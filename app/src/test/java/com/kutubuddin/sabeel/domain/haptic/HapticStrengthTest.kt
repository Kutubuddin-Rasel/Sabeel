package com.kutubuddin.sabeel.domain.haptic

import org.junit.Assert.assertEquals
import org.junit.Test

class HapticStrengthTest {

    @Test
    fun fromSetting_mapsKnownLevels() {
        assertEquals(HapticStrength.OFF, HapticStrength.fromSetting("off"))
        assertEquals(HapticStrength.LIGHT, HapticStrength.fromSetting("light"))
        assertEquals(HapticStrength.MEDIUM, HapticStrength.fromSetting("medium"))
        assertEquals(HapticStrength.STRONG, HapticStrength.fromSetting("strong"))
    }

    @Test
    fun fromSetting_isCaseInsensitive() {
        assertEquals(HapticStrength.STRONG, HapticStrength.fromSetting("STRONG"))
        assertEquals(HapticStrength.OFF, HapticStrength.fromSetting("Off"))
    }

    @Test
    fun fromSetting_defaultsToMedium_forUnknownOrBlank() {
        assertEquals(HapticStrength.MEDIUM, HapticStrength.fromSetting(""))
        assertEquals(HapticStrength.MEDIUM, HapticStrength.fromSetting("bogus"))
    }

    @Test
    fun off_hasZeroScaleOnEveryPrimitive() {
        assertEquals(0f, HapticStrength.OFF.tick, 0f)
        assertEquals(0f, HapticStrength.OFF.click, 0f)
        assertEquals(0f, HapticStrength.OFF.thud, 0f)
    }
}
