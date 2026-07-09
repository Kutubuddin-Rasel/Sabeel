package com.kutubuddin.sabeel.ui.tasbih.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class OdometerCounterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPaddedDigitsRendered() {
        // target = 33 is 2 digits, so 5 should still pad to "05" here.
        composeTestRule.setContent {
            OdometerCounter(count = 5, target = 33)
        }

        composeTestRule.onNodeWithText("0").assertExists()
        composeTestRule.onNodeWithText("5").assertExists()
    }

    @Test
    fun testLargerDigitsRendered() {
        composeTestRule.setContent {
            OdometerCounter(count = 99, target = 100)
        }

        // 99 has two identical digits, so onAllNodesWithText("9") should match exactly 2 nodes
        composeTestRule.onAllNodesWithText("9").assertCountEquals(2)
    }

    // IX-05 regression: a small target (e.g. 3) used to still force a
    // hardcoded 2-digit pad, so a fresh count of 0 rendered "00" — read as a
    // possible glitch rather than "zero of three". Pad width now tracks the
    // target's own digit count, so a 1-digit target renders a single "0".
    @Test
    fun testSmallTargetDoesNotOverPad() {
        composeTestRule.setContent {
            OdometerCounter(count = 0, target = 3)
        }

        // Exactly one "0" node — not the old two-digit "00".
        composeTestRule.onAllNodesWithText("0").assertCountEquals(1)
    }

    @Test
    fun testThreeDigitTargetPadsToThreeDigits() {
        composeTestRule.setContent {
            OdometerCounter(count = 7, target = 100)
        }

        // target = 100 is 3 digits, so 7 should pad to "007".
        composeTestRule.onAllNodesWithText("0").assertCountEquals(2)
        composeTestRule.onNodeWithText("7").assertExists()
    }
}
