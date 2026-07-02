package com.kutubuddin.sabeel.ui.i18n

import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertEquals
import org.junit.Test

class LayoutDirectionTest {

    @Test
    fun urduIsRtlOthersLtr() {
        assertEquals(LayoutDirection.Rtl, layoutDirectionFor("ur"))
        assertEquals(LayoutDirection.Ltr, layoutDirectionFor("en"))
        assertEquals(LayoutDirection.Ltr, layoutDirectionFor("bn"))
    }
}
