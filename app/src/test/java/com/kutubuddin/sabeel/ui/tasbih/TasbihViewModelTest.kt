package com.kutubuddin.sabeel.ui.tasbih

import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * ViewModel-level tests for the counting lifecycle.
 *
 * The pure step state machine is covered exhaustively by [SequenceEngineTest];
 * these tests verify how the ViewModel drives the repository and reconciles the
 * optimistic UI count with asynchronous repository emissions.
 *
 * Post-Salah contract (post-refactor):
 * - The active dhikr key stays the SEQUENCE key throughout — it is never mutated
 *   mid-sequence, so [TasbihRepository.setDhikr] is not called while stepping.
 * - Exactly ONE session (the full 100) is persisted when the sequence completes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TasbihViewModelTest {

    private val repository: TasbihRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: TasbihViewModel

    private val subhanAllah = DhikrCatalog.resolve("SUBHANALLAH")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock repository flows for initialization
        every { repository.activeCount } returns flowOf(0)
        every { repository.activeDhikr } returns flowOf(subhanAllah)
        every { repository.isSmartFlowEnabled } returns flowOf(true)
        every { repository.smartFlowVariant } returns flowOf(SmartFlowVariant.CLASSIC)
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
        assertEquals("SUBHANALLAH", state.currentDhikr.key)
        assertTrue(state.isSmartFlowEnabled)
        assertEquals(33, state.target)
        // A plain dhikr (no sequenceKey) never runs as a sequence, even with Smart Flow on.
        assertNull(state.sequence)
    }

    @Test
    fun testIncrementNormalTick() = runTest {
        // Given state count = 5 on a plain (non-sequence) dhikr
        every { repository.activeCount } returns flowOf(5)
        viewModel = TasbihViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(6, viewModel.state.value.count)
        assertEquals(1, effects.size)
        assertTrue(effects[0] is TasbihSideEffect.PlayHaptic && (effects[0] as TasbihSideEffect.PlayHaptic).type == HapticType.TICK)
        coVerify(exactly = 1) { repository.incrementCount(any()) }

        job.cancel()
    }

    @Test
    fun testResetIntent() = runTest {
        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        viewModel.processIntent(TasbihIntent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, effects.size)
        assertTrue(effects[0] is TasbihSideEffect.PlayHaptic && (effects[0] as TasbihSideEffect.PlayHaptic).type == HapticType.THUD)
        coVerify(exactly = 1) { repository.resetCount() }
        job.cancel()
    }

    @Test
    fun testSequenceActivatesForPostSalahEntry() = runTest {
        val fakeRepo = FakeTasbihRepository()
        fakeRepo._activeDhikr.value = DhikrCatalog.resolve("SMART_FLOW_CLASSIC")
        val viewModel = TasbihViewModel(fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.sequence)
        assertEquals("SMART_FLOW_CLASSIC", state.currentDhikr.key)
        assertEquals(0, state.stepIndex)
        assertEquals(33, state.target)                     // first step target
    }

    @Test
    fun testSequenceStepAdvancesWithoutPersistingOrMutatingDhikr() = runTest {
        val fakeRepo = FakeTasbihRepository()
        fakeRepo._activeDhikr.value = DhikrCatalog.resolve("SMART_FLOW_CLASSIC")
        fakeRepo._activeCount.value = 32                    // one tap short of finishing step 0
        val viewModel = TasbihViewModel(fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        // The 33rd tap completes step 0 → advances to step 1, no session yet.
        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.state.value.count)
        assertEquals(1, viewModel.state.value.stepIndex)
        assertEquals(33, viewModel.state.value.target)      // step 1 target
        assertTrue(effects.any { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.CLICK })
        // The dhikr key is unchanged and nothing is persisted mid-sequence.
        assertEquals("SMART_FLOW_CLASSIC", fakeRepo._activeDhikr.value.key)
        assertEquals(0, fakeRepo.setDhikrCalls)
        assertTrue(fakeRepo.completedTargets.isEmpty())

        job.cancel()
    }

    @Test
    fun testFullClassicSequencePersistsOnceWithTotal() = runTest {
        val fakeRepo = FakeTasbihRepository()
        fakeRepo._activeDhikr.value = DhikrCatalog.resolve("SMART_FLOW_CLASSIC")
        val viewModel = TasbihViewModel(fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<TasbihSideEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        // Drive the whole 33 + 33 + 34 = 100 sequence.
        repeat(100) {
            viewModel.processIntent(TasbihIntent.Increment)
            testDispatcher.scheduler.advanceUntilIdle()
        }

        // Back at the start, celebration fired.
        assertEquals(0, viewModel.state.value.count)
        assertEquals(0, viewModel.state.value.stepIndex)
        assertTrue(effects.any { it is TasbihSideEffect.ShowCelebration })

        // Exactly one session for the whole sequence, keyed by the sequence, total 100.
        assertEquals(1, fakeRepo.completedTargets.size)
        assertEquals("SMART_FLOW_CLASSIC", fakeRepo.completedTargets[0].second)
        assertEquals(100, fakeRepo.completedTargets[0].third)
        // The active key was never mutated into individual phrases.
        assertEquals(0, fakeRepo.setDhikrCalls)
        assertEquals("SMART_FLOW_CLASSIC", fakeRepo._activeDhikr.value.key)

        // Haptics: 97 within-step ticks, 2 intermediate clicks, 1 completion thud.
        val ticks = effects.count { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.TICK }
        val clicks = effects.count { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.CLICK }
        val thuds = effects.count { it is TasbihSideEffect.PlayHaptic && it.type == HapticType.THUD }
        assertEquals(97, ticks)
        assertEquals(2, clicks)
        assertEquals(1, thuds)

        job.cancel()
    }

    @Test
    fun testHighFrequencyStepBoundaryRace() = runTest {
        val fakeRepo = FakeTasbihRepository()
        fakeRepo._activeDhikr.value = DhikrCatalog.resolve("SMART_FLOW_CLASSIC")
        fakeRepo.delayMs = 50                               // simulate DataStore/Room latency
        val viewModel = TasbihViewModel(fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        // Sit at step 0, count 32.
        fakeRepo._activeCount.value = 32
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(32, viewModel.state.value.count)
        assertEquals(0, viewModel.state.value.stepIndex)

        // Tap A completes step 0 (triggers a delayed resetCount).
        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceTimeBy(20)          // less than the 50ms write

        // UI optimistically shows step 1, count 0 despite the stale repo count.
        assertEquals(0, viewModel.state.value.count)
        assertEquals(1, viewModel.state.value.stepIndex)

        // Tap B lands within step 1 before the previous write settles.
        viewModel.processIntent(TasbihIntent.Increment)
        testDispatcher.scheduler.advanceTimeBy(20)
        assertEquals(1, viewModel.state.value.count)
        assertEquals(1, viewModel.state.value.stepIndex)

        // Let all pending repository writes settle — the count must not roll back.
        testDispatcher.scheduler.advanceUntilIdle()
        val finalState = viewModel.state.value
        assertEquals(1, finalState.count)
        assertEquals(1, finalState.stepIndex)
        assertEquals("SMART_FLOW_CLASSIC", finalState.currentDhikr.key)
    }
}

open class FakeTasbihRepository : TasbihRepository {
    val _activeCount = kotlinx.coroutines.flow.MutableStateFlow(0)
    override val activeCount: kotlinx.coroutines.flow.Flow<Int> = _activeCount

    val _activeDhikr = kotlinx.coroutines.flow.MutableStateFlow(DhikrCatalog.resolve("SUBHANALLAH"))
    override val activeDhikr: kotlinx.coroutines.flow.Flow<ActiveDhikr> = _activeDhikr

    val _isSmartFlowEnabled = kotlinx.coroutines.flow.MutableStateFlow(true)
    override val isSmartFlowEnabled: kotlinx.coroutines.flow.Flow<Boolean> = _isSmartFlowEnabled

    val _smartFlowVariant = kotlinx.coroutines.flow.MutableStateFlow(SmartFlowVariant.CLASSIC)
    override val smartFlowVariant: kotlinx.coroutines.flow.Flow<SmartFlowVariant> = _smartFlowVariant

    val _isPocketModeActive = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val isPocketModeActive: kotlinx.coroutines.flow.Flow<Boolean> = _isPocketModeActive

    val _streak = kotlinx.coroutines.flow.MutableStateFlow<com.kutubuddin.sabeel.domain.model.Streak?>(null)
    override val streak: kotlinx.coroutines.flow.Flow<com.kutubuddin.sabeel.domain.model.Streak?> = _streak

    var delayMs: Long = 0
    var setDhikrCalls: Int = 0

    override fun getDailyTargetFlow(date: String, dhikrKey: String): kotlinx.coroutines.flow.Flow<com.kutubuddin.sabeel.domain.model.DailyTarget?> =
        kotlinx.coroutines.flow.flowOf(null)

    /** date → dhikrKey → targetCount, one entry per persisted session. */
    val completedTargets = mutableListOf<Triple<String, String, Int>>()

    override suspend fun incrementCount(date: String): Int {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _activeCount.value += 1
        return _activeCount.value
    }

    override suspend fun decrementCount(): Int {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        if (_activeCount.value > 0) _activeCount.value -= 1
        return _activeCount.value
    }

    override suspend fun resetCount() {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _activeCount.value = 0
    }

    override suspend fun setSmartFlowVariant(variant: SmartFlowVariant) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        _smartFlowVariant.value = variant
    }

    override suspend fun setDhikr(key: String) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        setDhikrCalls++
        _activeDhikr.value = DhikrCatalog.resolve(key)
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

    override suspend fun completeDhikrTarget(date: String, dhikrKey: String, targetCount: Int) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        completedTargets.add(Triple(date, dhikrKey, targetCount))
    }
}
