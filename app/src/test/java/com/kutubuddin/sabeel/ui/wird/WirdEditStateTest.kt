package com.kutubuddin.sabeel.ui.wird

import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.DhikrMeaning
import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.WirdItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pure-JVM tests for the framework-free edit projection + reorder helper.
 * No Robolectric / no @Config — these functions touch no Android APIs.
 */
class WirdEditStateTest {

    private fun catalog(
        key: String,
        name: String = "name-$key",
        target: Int = 33,
        smart: Boolean = false
    ) = DhikrItem(
        key = key, arabicText = "ar-$key", displayName = LocalizedText(en = name),
        transliteration = LocalizedText(en = "tr-$key"), meaning = DhikrMeaning(en = "m"),
        defaultTarget = target, spiritualReward = LocalizedText(en = "r"),
        hadithRef = "", category = DhikrCategory.DAILY, isSmartFlow = smart
    )

    // ── resolveWirdEditState ────────────────────────────────────────────

    @Test
    fun pickable_excludesSmartFlowAndAlreadyAdded() {
        val cat = listOf(
            catalog("A"),               // already added → excluded
            catalog("B"),               // addable → kept
            catalog("S", smart = true)  // smart-flow → excluded (Task 7 CARRY, structural)
        )
        val plan = listOf(WirdItem("A", 33, 0))

        val state = resolveWirdEditState(plan, cat, "en")

        assertEquals(listOf("B"), state.pickable.map { it.key })
    }

    @Test
    fun rows_takeTargetFromPlanNotCatalogDefault_andDropDanglingKeys() {
        // catalog default deliberately differs from the plan target so a
        // regression that read the catalog default would be caught.
        val cat = listOf(catalog("A", name = "Alhamd", target = 999))
        val plan = listOf(
            WirdItem("A", 33, 0),
            WirdItem("GONE", 50, 1) // not in catalog → dropped
        )

        val state = resolveWirdEditState(plan, cat, "ur")

        assertEquals(1, state.rows.size)
        val row = state.rows.single()
        assertEquals("A", row.dhikrKey)
        assertEquals("Alhamd", row.displayName) // from catalog (resolveWirdEditState calls .get(lang))
        assertEquals(33, row.target)            // from plan, NOT 999
        assertEquals(0, row.position)
        assertEquals("ur", state.language)
    }

    @Test
    fun rows_followPlanOrder() {
        val cat = listOf(catalog("A"), catalog("B"))
        val plan = listOf(WirdItem("B", 10, 0), WirdItem("A", 20, 1))

        val state = resolveWirdEditState(plan, cat, "en")

        assertEquals(listOf("B", "A"), state.rows.map { it.dhikrKey })
    }

    // ── swapAdjacent ────────────────────────────────────────────────────

    @Test
    fun swap_up_swapsWithPrevious() {
        assertEquals(listOf("A", "C", "B"), swapAdjacent(listOf("A", "B", "C"), "C", up = true))
    }

    @Test
    fun swap_down_swapsWithNext() {
        assertEquals(listOf("B", "A", "C"), swapAdjacent(listOf("A", "B", "C"), "A", up = false))
    }

    @Test
    fun swap_up_onFirst_isNoOp() {
        assertNull(swapAdjacent(listOf("A", "B", "C"), "A", up = true))
    }

    @Test
    fun swap_down_onLast_isNoOp() {
        assertNull(swapAdjacent(listOf("A", "B", "C"), "C", up = false))
    }

    @Test
    fun swap_missingKey_isNoOp() {
        assertNull(swapAdjacent(listOf("A", "B", "C"), "Z", up = true))
    }
}
