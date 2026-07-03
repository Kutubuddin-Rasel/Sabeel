package com.kutubuddin.sabeel.domain.usecase

import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.DhikrMeaning
import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.model.WirdItem
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveWirdProgressTest {

    private fun catalogItem(key: String, target: Int) = DhikrItem(
        key = key, arabicText = "ar-$key", displayName = "name-$key",
        transliteration = "tr-$key",
        meaning = DhikrMeaning(en = "m"), defaultTarget = target,
        spiritualReward = LocalizedText(en = "r"), hadithRef = "", category = DhikrCategory.DAILY
    )

    private fun fakeDhikrRepo(items: List<DhikrItem>) = object : DhikrRepository {
        override fun getAllDhikr() = flowOf(items)
        override fun getCustomDhikr() = flowOf(emptyList<CustomDhikrEntity>())
        override suspend fun saveCustomDhikr(dhikr: CustomDhikrEntity) {}
        override suspend fun deleteCustomDhikr(dhikr: CustomDhikrEntity) {}
    }

    private fun fakeWirdRepo(items: List<WirdItem>) = object : WirdRepository {
        override fun observeWird() = flowOf(items)
        override suspend fun addToWird(dhikrKey: String, target: Int) {}
        override suspend fun updateTarget(dhikrKey: String, target: Int) {}
        override suspend fun removeFromWird(dhikrKey: String) {}
        override suspend fun reorder(orderedKeys: List<String>) {}
        override suspend fun seedDefaultIfEmpty() {}
    }

    private fun fakeSessionRepo(counts: Map<String, Int>) = object : SessionRepository {
        override fun getSessionsForDate(dateKey: String) = flowOf(emptyList<DhikrSessionEntity>())
        override fun getTotalCountForDate(dateKey: String) = flowOf(0)
        override fun getTotalAllTime() = flowOf(0)
        override fun getTotalSessionCount() = flowOf(0)
        override fun getCountsByKeyForDate(dateKey: String) = flowOf(counts)
        override suspend fun insertSession(session: DhikrSessionEntity) {}
    }

    private fun fakeTasbihRepo(active: ActiveDhikr, count: Int) = object : TasbihRepository {
        override val activeCount: Flow<Int> = flowOf(count)
        override val activeDhikr: Flow<ActiveDhikr> = flowOf(active)
        override val isSmartFlowEnabled = flowOf(true)
        override val smartFlowVariant = flowOf(SmartFlowVariant.CLASSIC)
        override val isPocketModeActive = flowOf(false)
        override val streak: Flow<Streak?> = flowOf(null)
        override fun getDailyTargetFlow(date: String, dhikrKey: String) = flowOf(null)
        override suspend fun incrementCount(date: String) = 0
        override suspend fun decrementCount() = 0
        override suspend fun resetCount() {}
        override suspend fun setDhikr(key: String) {}
        override suspend fun setSmartFlowEnabled(enabled: Boolean) {}
        override suspend fun setSmartFlowVariant(variant: SmartFlowVariant) {}
        override suspend fun setPocketModeActive(active: Boolean) {}
        override suspend fun completeDhikrTarget(date: String, dhikrKey: String, targetCount: Int) {}
    }

    private fun active(key: String, target: Int) =
        ActiveDhikr(key, "ar", "name", target, LocalizedText(en = ""), "")

    @Test
    fun projects_savedPlusLive_resolvesDisplay_ordersByPlan() = runTest {
        val useCase = ObserveWirdProgress(
            // Plan order [A, B]; plan targets 33 and 100.
            wirdRepository = fakeWirdRepo(listOf(WirdItem("A", 33, 0), WirdItem("B", 100, 1))),
            // Catalog in a DIFFERENT order [B, A] and with deliberately-wrong defaultTarget (999)
            // so the test discriminates: target must come from the PLAN, order from the PLAN.
            dhikrRepository = fakeDhikrRepo(listOf(catalogItem("B", 999), catalogItem("A", 999))),
            sessionRepository = fakeSessionRepo(mapOf("A" to 33, "B" to 10)),
            tasbihRepository = fakeTasbihRepo(active("B", 100), count = 15) // live +15 on B
        )
        val progress = useCase("2026-07-03").first()

        // Plan order wins over catalog order (catalog was [B, A]).
        assertEquals(listOf("A", "B"), progress.items.map { it.dhikrKey })
        assertEquals("name-A", progress.items[0].displayName)
        // target comes from the PLAN (33, 100), NOT the catalog defaultTarget (999).
        assertEquals(33, progress.items[0].target)
        assertEquals(100, progress.items[1].target)
        // position comes from the PLAN.
        assertEquals(0, progress.items[0].position)
        assertEquals(1, progress.items[1].position)
        assertEquals(33, progress.items[0].countToday)   // saved only
        assertEquals(25, progress.items[1].countToday)   // 10 saved + 15 live
        assertTrue(progress.items[0].isComplete)          // countToday 33 >= plan target 33
    }

    @Test
    fun filtersOut_wirdItemsMissingFromCatalog() = runTest {
        val useCase = ObserveWirdProgress(
            wirdRepository = fakeWirdRepo(listOf(WirdItem("GONE", 33, 0))),
            dhikrRepository = fakeDhikrRepo(emptyList()), // key not resolvable
            sessionRepository = fakeSessionRepo(emptyMap()),
            tasbihRepository = fakeTasbihRepo(active("X", 33), count = 0)
        )
        assertTrue(useCase("2026-07-03").first().items.isEmpty())
    }
}
