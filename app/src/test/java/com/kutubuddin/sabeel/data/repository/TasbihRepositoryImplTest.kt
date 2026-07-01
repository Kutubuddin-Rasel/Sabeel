package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.datastore.CounterDataStore
import com.kutubuddin.sabeel.data.local.db.dao.DhikrSessionDao
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasbihRepositoryImplTest {

    private val counterDataStore: CounterDataStore = mockk(relaxed = true)
    private val sakinahDao: SakinahDao = mockk(relaxed = true)
    private val dhikrSessionDao: DhikrSessionDao = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TasbihRepositoryImpl

    @Before
    fun setUp() {
        repository = TasbihRepositoryImpl(
            counterDataStore = counterDataStore,
            sakinahDao = sakinahDao,
            dhikrSessionDao = dhikrSessionDao,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun testIncrementCount() = runTest(testDispatcher) {
        val date = "2026-06-30"
        coEvery { counterDataStore.counterValueFlow } returns flowOf(5)
        coEvery { counterDataStore.activeDhikrKeyFlow } returns flowOf("SUBHANALLAH")
        coEvery { counterDataStore.incrementCounter() } just Runs

        val count = repository.incrementCount(date)

        assertEquals(5, count)
        coVerify(exactly = 1) { counterDataStore.incrementCounter() }
        coVerify(exactly = 1) {
            sakinahDao.insertDailyTarget(
                DailyTargetEntity(
                    id = "SUBHANALLAH_$date",
                    date = date,
                    dhikrType = "SUBHANALLAH",
                    currentCount = 5,
                    targetCount = 33,
                    isCompleted = false
                )
            )
        }
    }

    @Test
    fun testResetCount() = runTest(testDispatcher) {
        coEvery { counterDataStore.resetCounter() } just Runs
        repository.resetCount()
        coVerify(exactly = 1) { counterDataStore.resetCounter() }
    }

    @Test
    fun testSetDhikr() = runTest(testDispatcher) {
        coEvery { counterDataStore.setDhikrKey("ALHAMDULILLAH") } just Runs
        coEvery { counterDataStore.resetCounter() } just Runs

        repository.setDhikr("ALHAMDULILLAH")

        coVerify(exactly = 1) { counterDataStore.setDhikrKey("ALHAMDULILLAH") }
        coVerify(exactly = 1) { counterDataStore.resetCounter() }
    }

    @Test
    fun testStreakCompletionToday_firstStreak() = runTest(testDispatcher) {
        val date = "2026-06-30"
        coEvery { sakinahDao.getStreak("current_streak") } returns null
        coEvery { sakinahDao.updateProgressAndStreak(any(), any(), any(), any(), any(), any()) } just Runs

        repository.completeDhikrTarget(date, "SUBHANALLAH", 33)

        coVerify(exactly = 1) {
            sakinahDao.updateProgressAndStreak(
                date = date,
                progress = 33,
                target = 33,
                streakCount = 1,
                dhikrType = "SUBHANALLAH",
                longestStreak = 1
            )
        }
    }

    @Test
    fun testStreakCompletionConsecutiveDay() = runTest(testDispatcher) {
        val date = "2026-06-30"
        val yesterday = "2026-06-29"
        coEvery { sakinahDao.getStreak("current_streak") } returns StreakEntity(
            count = 4,
            lastActiveDate = yesterday,
            longestStreak = 4
        )
        coEvery { sakinahDao.updateProgressAndStreak(any(), any(), any(), any(), any(), any()) } just Runs

        repository.completeDhikrTarget(date, "SUBHANALLAH", 33)

        coVerify(exactly = 1) {
            sakinahDao.updateProgressAndStreak(
                date = date,
                progress = 33,
                target = 33,
                streakCount = 5,
                dhikrType = "SUBHANALLAH",
                longestStreak = 5
            )
        }
    }

    @Test
    fun testStreakCompletionGapReset() = runTest(testDispatcher) {
        val date = "2026-06-30"
        val longAgo = "2026-06-25"
        coEvery { sakinahDao.getStreak("current_streak") } returns StreakEntity(
            count = 4,
            lastActiveDate = longAgo,
            longestStreak = 4
        )
        coEvery { sakinahDao.updateProgressAndStreak(any(), any(), any(), any(), any(), any()) } just Runs

        repository.completeDhikrTarget(date, "SUBHANALLAH", 33)

        coVerify(exactly = 1) {
            sakinahDao.updateProgressAndStreak(
                date = date,
                progress = 33,
                target = 33,
                streakCount = 1,
                dhikrType = "SUBHANALLAH",
                longestStreak = 4
            )
        }
    }
}
