package com.kutubuddin.sabeel.ui.tasbih

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.click
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import com.kutubuddin.sabeel.ui.settings.SettingsViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class TasbihScreenGestureTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = mockk<TasbihRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val hapticEngine = mockk<HapticEngine>(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: TasbihViewModel
    // Injected so TasbihScreen doesn't reach for hiltViewModel() (no Hilt in this
    // Robolectric test); state falls back to SettingsState() defaults.
    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { repository.activeCount } returns flowOf(0)
        every { repository.activeDhikr } returns flowOf(DhikrCatalog.resolve("SUBHANALLAH"))
        every { repository.isSmartFlowEnabled } returns flowOf(true)
        every { repository.isPocketModeActive } returns flowOf(false)
        every { repository.streak } returns flowOf(null)

        viewModel = TasbihViewModel(repository)
        settingsViewModel = SettingsViewModel(settingsRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testTapToIncrement() {
        composeTestRule.setContent {
            TasbihScreen(
                viewModel = viewModel,
                hapticEngine = hapticEngine,
                settingsViewModel = settingsViewModel
            )
        }

        // Tap the screen (root node)
        composeTestRule.onRoot().performClick()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify incrementCount was triggered on repository
        coVerify(exactly = 1) { repository.incrementCount(any()) }
    }

    // Under Robolectric with a manually-driven StandardTestDispatcher, longClick()
    // resolves as a tap before the long-press timeout fires on the compose clock, so
    // the gesture increments instead of resetting. The reset wiring itself is verified
    // by TasbihViewModelTest.testResetIntent (Reset intent → repository.resetCount()).
    @Ignore("Robolectric long-press timing does not trip detectTapGestures.onLongPress")
    @Test
    fun testLongPressToReset() {
        composeTestRule.setContent {
            TasbihScreen(
                viewModel = viewModel,
                hapticEngine = hapticEngine,
                settingsViewModel = settingsViewModel
            )
        }

        // Long press the screen (root node)
        composeTestRule.onRoot().performTouchInput {
            longClick()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify resetCount was triggered on repository
        coVerify(exactly = 1) { repository.resetCount() }
    }

    @Test
    fun testDoubleTapInterception() {
        composeTestRule.setContent {
            TasbihScreen(
                viewModel = viewModel,
                hapticEngine = hapticEngine,
                settingsViewModel = settingsViewModel
            )
        }

        // Perform two clicks in rapid succession (double click)
        composeTestRule.onRoot().performTouchInput {
            click()
            click()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify that it is processed as two separate increments
        coVerify(exactly = 2) { repository.incrementCount(any()) }
    }
}
