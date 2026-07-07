package com.kutubuddin.sabeel.domain.usecase

import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.DhikrMeaning
import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.WirdItem
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.TasbihCounterObserver
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveWirdProgressTest {

    private fun catalogItem(key: String, target: Int) = DhikrItem(
        key = key,
        arabicText = "ar-$key",
        displayName = LocalizedText(en = "name-$key"),
        transliteration = "tr-$key",
        meaning = DhikrMeaning(en = "m"),
        defaultTarget = target,
        spiritualReward = LocalizedText(en = "r"),
        hadithRef = "",
        category = DhikrCategory.DAILY
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

    /** ISP payoff: only [TasbihCounterObserver]'s two members need stubbing now. */
    private fun fakeCounterObserver(active: ActiveDhikr, count: Int) = object : TasbihCounterObserver {
        override val activeCount = flowOf(count)
        override val activeDhikr = flowOf(active)
    }

    private fun active(key: String, target: Int) =
        ActiveDhikr(
            key = key,
            arabicText = "ar",
            displayName = LocalizedText(en = "name"),
            target = target,
            spiritualReward = LocalizedText(en = ""),
            hadithRef = ""
        )

    @Test
    fun projects_savedPlusLive_resolvesDisplay_ordersByPlan() = runTest {
        val useCase = ObserveWirdProgress(
            wirdRepository = fakeWirdRepo(listOf(WirdItem("A", 33, 0), WirdItem("B", 100, 1))),
            dhikrRepository = fakeDhikrRepo(listOf(catalogItem("B", 999), catalogItem("A", 999))),
            sessionRepository = fakeSessionRepo(mapOf("A" to 33, "B" to 10)),
            counterObserver = fakeCounterObserver(active("B", 100), count = 15)
        )
        val progress = useCase("2026-07-03").first()

        assertEquals(listOf("A", "B"), progress.items.map { it.dhikrKey })
        assertEquals("name-A", progress.items[0].displayName.en)
        assertEquals(33, progress.items[0].target)
        assertEquals(100, progress.items[1].target)
        assertEquals(0, progress.items[0].position)
        assertEquals(1, progress.items[1].position)
        assertEquals(33, progress.items[0].countToday)
        assertEquals(25, progress.items[1].countToday)
        assertTrue(progress.items[0].isComplete)
    }

    @Test
    fun filtersOut_wirdItemsMissingFromCatalog() = runTest {
        val useCase = ObserveWirdProgress(
            wirdRepository = fakeWirdRepo(listOf(WirdItem("GONE", 33, 0))),
            dhikrRepository = fakeDhikrRepo(emptyList()),
            sessionRepository = fakeSessionRepo(emptyMap()),
            counterObserver = fakeCounterObserver(active("X", 33), count = 0)
        )
        assertTrue(useCase("2026-07-03").first().items.isEmpty())
    }
}