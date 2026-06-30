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
        composeTestRule.setContent {
            OdometerCounter(count = 5)
        }

        // 5 should be padded to "05", so "0" and "5" must both exist as single unique nodes
        composeTestRule.onNodeWithText("0").assertExists()
        composeTestRule.onNodeWithText("5").assertExists()
    }

    @Test
    fun testLargerDigitsRendered() {
        composeTestRule.setContent {
            OdometerCounter(count = 99)
        }

        // 99 has two identical digits, so onAllNodesWithText("9") should match exactly 2 nodes
        composeTestRule.onAllNodesWithText("9").assertCountEquals(2)
    }
}
