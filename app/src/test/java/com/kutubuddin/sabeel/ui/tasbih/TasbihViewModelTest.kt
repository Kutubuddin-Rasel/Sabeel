package com.kutubuddin.sabeel.ui.tasbih

import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.model.DailyTarget
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasbihViewModelTest {

    private val repository: TasbihRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: TasbihViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock repository flows for initialization
        every { repository.activeCount } returns flowOf(0)
        every { repository.activeDhikr } returns flowOf(DhikrType.SUBHANALLAH)
        every { repository.isSmartFlowEnabled } returns flowOf(true)
        every { repository.isPocketModeActive } returns flowOf(false)
        every { repository.streak } returns flowOf(null)

        viewModel = TasbihViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateLoadedFromRepository() {
        val state = viewModel.state.value
        assertEquals(0, state.count)
        assertEquals(DhikrType.SUBHANALLAH, state.currentDhikr)
        assertTrue(state.isSmartFlowEnabled)
        assertEquals(33, state.target)
    }

    @Test
    fun testIncrementNormalTick() = runTest {
        // Given state count = 5
        every { repository.activeCount } returns flowOf(5)
        viewModel = TasbihViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch {
            viewModel.effect.toList(effects)
        }

        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        // State updates optimistically
        assertEquals(6, viewModel.state.value.count)
        assertEquals(1, effects.size)
        assertTrue(effects[0] is TasbihSideEffect.PlayHaptic && (effects[0] as TasbihSideEffect.PlayHaptic).type == HapticType.TICK)

        coVerify(exactly = 1) { repository.incrementCount(any()) }

        job.cancel()
    }

    @Test
    fun testSmartFlowTransition_SubhanAllahToAlhamdulillah() = runTest {
        // Given count = 32
        every { repository.activeCount } returns flowOf(32)
        every { repository.activeDhikr } returns flowOf(DhikrType.SUBHANALLAH)
        viewModel = TasbihViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch {
            viewModel.effect.toList(effects)
        }

        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        // SubhanAllah milestone reached (33) -> resets local count, changes to Alhamdulillah
        assertEquals(0, viewModel.state.value.count)
        assertEquals(DhikrType.ALHAMDULILLAH, viewModel.state.value.currentDhikr)
        assertEquals(1, effects.size)
        assertTrue(effects[0] is TasbihSideEffect.PlayHaptic && (effects[0] as TasbihSideEffect.PlayHaptic).type == HapticType.CLICK)

        coVerify(exactly = 1) { repository.completeDhikrTarget(any(), DhikrType.SUBHANALLAH, 33) }
        coVerify(exactly = 1) { repository.setDhikr(DhikrType.ALHAMDULILLAH) }

        job.cancel()
    }

    @Test
    fun testSmartFlowTransition_AlhamdulillahToAllahuAkbar() = runTest {
        // Given count = 32
        every { repository.activeCount } returns flowOf(32)
        every { repository.activeDhikr } returns flowOf(DhikrType.ALHAMDULILLAH)
        viewModel = TasbihViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch {
            viewModel.effect.toList(effects)
        }

        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        // Alhamdulillah milestone reached (33) -> resets local count, changes to Allahu Akbar
        assertEquals(0, viewModel.state.value.count)
        assertEquals(DhikrType.ALLAHU_AKBAR, viewModel.state.value.currentDhikr)
        assertEquals(1, effects.size)
        assertTrue(effects[0] is TasbihSideEffect.PlayHaptic && (effects[0] as TasbihSideEffect.PlayHaptic).type == HapticType.CLICK)

        coVerify(exactly = 1) { repository.completeDhikrTarget(any(), DhikrType.ALHAMDULILLAH, 33) }
        coVerify(exactly = 1) { repository.setDhikr(DhikrType.ALLAHU_AKBAR) }

        job.cancel()
    }

    @Test
    fun testSmartFlowTransition_AllahuAkbarCompletion() = runTest {
        // Given count = 33
        every { repository.activeCount } returns flowOf(33)
        every { repository.activeDhikr } returns flowOf(DhikrType.ALLAHU_AKBAR)
        viewModel = TasbihViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch {
            viewModel.effect.toList(effects)
        }

        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        // Allahu Akbar milestone reached (34) -> resets local count, wraps back to SubhanAllah, triggers celebration
        assertEquals(0, viewModel.state.value.count)
        assertEquals(DhikrType.SUBHANALLAH, viewModel.state.value.currentDhikr)
        assertEquals(2, effects.size)
        assertTrue(effects.any { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.THUD })
        assertTrue(effects.any { it is TasbihSideEffect.ShowCelebration })

        coVerify(exactly = 1) { repository.completeDhikrTarget(any(), DhikrType.ALLAHU_AKBAR, 34) }
        coVerify(exactly = 1) { repository.setDhikr(DhikrType.SUBHANALLAH) }

        job.cancel()
    }

    @Test
    fun testResetIntent() = runTest {
        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch {
            viewModel.effect.toList(effects)
        }

        viewModel.processIntent(TasbihIntent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, effects.size)
        assertTrue(effects[0] is TasbihSideEffect.PlayHaptic && (effects[0] as TasbihSideEffect.PlayHaptic).type == HapticType.THUD)
        coVerify(exactly = 1) { repository.resetCount() }
        job.cancel()
    }

    @Test
    fun testSmartFlowFullTransitionSequence() = runTest {
        val fakeRepo = FakeTasbihRepository()
        val viewModel = TasbihViewModel(fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch {
            viewModel.effect.toList(effects)
        }

        // 1. SubhanAllah (0 -> 33)
        assertEquals(DhikrType.SUBHANALLAH, viewModel.state.value.currentDhikr)
        for (i in 1..32) {
            viewModel.processIntent(TasbihIntent.Increment)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(i, viewModel.state.value.count)
            assertEquals(DhikrType.SUBHANALLAH, viewModel.state.value.currentDhikr)
        }
        
        // The 33rd increment: should transition to Alhamdulillah (count 0)
        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.state.value.count)
        assertEquals(DhikrType.ALHAMDULILLAH, viewModel.state.value.currentDhikr)
        
        // Verify SubhanAllah completion persisted and dhikr updated in repository
        assertEquals(1, fakeRepo.completedTargets.size)
        assertEquals(DhikrType.SUBHANALLAH, fakeRepo.completedTargets[0].second)
        assertEquals(33, fakeRepo.completedTargets[0].third)
        assertEquals(DhikrType.ALHAMDULILLAH, fakeRepo._activeDhikr.value)

        // 2. Alhamdulillah (0 -> 33)
        for (i in 1..32) {
            viewModel.processIntent(TasbihIntent.Increment)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(i, viewModel.state.value.count)
            assertEquals(DhikrType.ALHAMDULILLAH, viewModel.state.value.currentDhikr)
        }
        
        // The 33rd increment: should transition to Allahu Akbar (count 0)
        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.state.value.count)
        assertEquals(DhikrType.ALLAHU_AKBAR, viewModel.state.value.currentDhikr)
        
        // Verify Alhamdulillah completion persisted and dhikr updated in repository
        assertEquals(2, fakeRepo.completedTargets.size)
        assertEquals(DhikrType.ALHAMDULILLAH, fakeRepo.completedTargets[1].second)
        assertEquals(33, fakeRepo.completedTargets[1].third)
        assertEquals(DhikrType.ALLAHU_AKBAR, fakeRepo._activeDhikr.value)

        // 3. Allahu Akbar (0 -> 34)
        for (i in 1..33) {
            viewModel.processIntent(TasbihIntent.Increment)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(i, viewModel.state.value.count)
            assertEquals(DhikrType.ALLAHU_AKBAR, viewModel.state.value.currentDhikr)
        }
        
        // The 34th increment: should transition back to SubhanAllah (count 0)
        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.state.value.count)
        assertEquals(DhikrType.SUBHANALLAH, viewModel.state.value.currentDhikr)

        // Verify Allahu Akbar completion persisted and dhikr updated in repository
        assertEquals(3, fakeRepo.completedTargets.size)
        assertEquals(DhikrType.ALLAHU_AKBAR, fakeRepo.completedTargets[2].second)
        assertEquals(34, fakeRepo.completedTargets[2].third)
        assertEquals(DhikrType.SUBHANALLAH, fakeRepo._activeDhikr.value)

        job.cancel()
        val ticks = effects.count { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.TICK }
        val clicks = effects.count { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.CLICK }
        val thuds = effects.count { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.THUD }
        assertEquals(97, ticks)
        assertEquals(2, clicks)
        assertEquals(1, thuds)
    }

    @Test
    fun testHighFrequencyStateRace() = runTest {
        val fakeRepo = FakeTasbihRepository()
        // Simulate repository database/datastore latency (50ms)
        fakeRepo.delayMs = 50 
        
        val viewModel = TasbihViewModel(fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Set count to 32 SubhanAllah
        fakeRepo._activeCount.value = 32
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Confirm initial state
        assertEquals(32, viewModel.state.value.count)
        assertEquals(DhikrType.SUBHANALLAH, viewModel.state.value.currentDhikr)
        
        // User clicks Increment (which will trigger transition because nextCount = 33)
        viewModel.processIntent(TasbihIntent.Increment)
        
        // Advance time by 20ms (less than 50ms delay of repository write)
        testDispatcher.scheduler.advanceTimeBy(20)
        
        // UI should optimistically transition to Alhamdulillah count 0 immediately
        assertEquals(0, viewModel.state.value.count)
        assertEquals(DhikrType.ALHAMDULILLAH, viewModel.state.value.currentDhikr)
        
        // User clicks Increment again immediately (high frequency update, 20ms later)
        viewModel.processIntent(TasbihIntent.Increment)
        
        // Advance time by another 20ms (total 40ms)
        testDispatcher.scheduler.advanceTimeBy(20)
        
        // UI should optimistically update to Alhamdulillah count 1
        assertEquals(1, viewModel.state.value.count)
        assertEquals(DhikrType.ALHAMDULILLAH, viewModel.state.value.currentDhikr)
        
        // Now advance time fully so all pending repository tasks complete
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert the final state. If the race condition exists, it might rollback to 33 SubhanAllah or get corrupted
        val finalState = viewModel.state.value
        println("RACE TEST FINAL STATE: count=${finalState.count}, dhikr=${finalState.currentDhikr}")
        
        // Expected correct state: count = 1, currentDhikr = ALHAMDULILLAH
        assertEquals(DhikrType.ALHAMDULILLAH, finalState.currentDhikr)
        assertEquals(1, finalState.count)
    }

    @Test
    fun testHighFrequencyOutofOrderRace() = runTest {
        val fakeRepo = object : FakeTasbihRepository() {
            override suspend fun setDhikr(dhikr: DhikrType) {
                // Simulate slower setDhikr operation (80ms)
                kotlinx.coroutines.delay(80)
                _activeDhikr.value = dhikr
                _activeCount.value = 0
            }

            override suspend fun incrementCount(date: String): Int {
                // Simulate faster increment operation (40ms)
                kotlinx.coroutines.delay(40)
                _activeCount.value += 1
                return _activeCount.value
            }
        }
        
        val viewModel = TasbihViewModel(fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()
        
        fakeRepo._activeCount.value = 32
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Tap 1 (SubhanAllah 32 -> 33, triggers setDhikr(ALHAMDULILLAH))
        viewModel.processIntent(TasbihIntent.Increment)
        
        // Tap 2 happens 20ms later (triggers incrementCount())
        testDispatcher.scheduler.advanceTimeBy(20)
        viewModel.processIntent(TasbihIntent.Increment)
        
        // Advance time fully
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert the final state. If out-of-order race exists, it will be 0 instead of 1
        val finalState = viewModel.state.value
        assertEquals(DhikrType.ALHAMDULILLAH, finalState.currentDhikr)
        assertEquals(1, finalState.count)
    }
}

open class FakeTasbihRepository : TasbihRepository {
    val _activeCount = kotlinx.coroutines.flow.MutableStateFlow(0)
    override val activeCount: kotlinx.coroutines.flow.Flow<Int> = _activeCount

    val _activeDhikr = kotlinx.coroutines.flow.MutableStateFlow(DhikrType.SUBHANALLAH)
    override val activeDhikr: kotlinx.coroutines.flow.Flow<DhikrType> = _activeDhikr

    val _isSmartFlowEnabled = kotlinx.coroutines.flow.MutableStateFlow(true)
    override val isSmartFlowEnabled: kotlinx.coroutines.flow.Flow<Boolean> = _isSmartFlowEnabled

    val _isPocketModeActive = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val isPocketModeActive: kotlinx.coroutines.flow.Flow<Boolean> = _isPocketModeActive

    val _streak = kotlinx.coroutines.flow.MutableStateFlow<com.kutubuddin.sabeel.domain.model.Streak?>(null)
    override val streak: kotlinx.coroutines.flow.Flow<com.kutubuddin.sabeel.domain.model.Streak?> = _streak

    var delayMs: Long = 0

    override fun getDailyTargetFlow(date: String, dhikrType: DhikrType): kotlinx.coroutines.flow.Flow<com.kutubuddin.sabeel.domain.model.DailyTarget?> =
        kotlinx.coroutines.flow.flowOf(null)

    val completedTargets = mutableListOf<Triple<String, DhikrType, Int>>()

    override suspend fun incrementCount(date: String): Int {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _activeCount.value += 1
        return _activeCount.value
    }

    override suspend fun resetCount() {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _activeCount.value = 0
    }

    override suspend fun setDhikr(dhikr: DhikrType) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _activeDhikr.value = dhikr
        _activeCount.value = 0
    }

    override suspend fun setSmartFlowEnabled(enabled: Boolean) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _isSmartFlowEnabled.value = enabled
    }

    override suspend fun setPocketModeActive(active: Boolean) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _isPocketModeActive.value = active
    }

    override suspend fun completeDhikrTarget(date: String, dhikrType: DhikrType, targetCount: Int) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        completedTargets.add(Triple(date, dhikrType, targetCount))
    }
}
