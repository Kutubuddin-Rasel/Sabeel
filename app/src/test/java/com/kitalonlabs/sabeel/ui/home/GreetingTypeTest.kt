package com.kitalonlabs.sabeel.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class GreetingTypeTest {

    @Test
    fun mapsHourBoundaries() {
        assertEquals(GreetingType.FAJR, greetingTypeForHour(4))
        assertEquals(GreetingType.FAJR, greetingTypeForHour(6))
        assertEquals(GreetingType.MORNING, greetingTypeForHour(7))
        assertEquals(GreetingType.MORNING, greetingTypeForHour(11))
        assertEquals(GreetingType.DHUHR, greetingTypeForHour(12))
        assertEquals(GreetingType.AFTERNOON, greetingTypeForHour(14))
        assertEquals(GreetingType.ASR, greetingTypeForHour(16))
        assertEquals(GreetingType.MAGHRIB, greetingTypeForHour(18))
        assertEquals(GreetingType.ISHA, greetingTypeForHour(20))
        assertEquals(GreetingType.DEFAULT, greetingTypeForHour(2))
        assertEquals(GreetingType.DEFAULT, greetingTypeForHour(23))
    }
}
