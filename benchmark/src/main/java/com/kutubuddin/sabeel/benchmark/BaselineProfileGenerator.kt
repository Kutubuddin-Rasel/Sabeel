package com.kutubuddin.sabeel.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.kutubuddin.sabeel",
        profileBlock = {
            // Start the app and wait for it to settle
            startActivityAndWait()
            device.wait(Until.hasObject(By.textContains("00")), 5000)

            // Find the central area of the screen to tap
            val center = device.displayHeight / 2
            val widthCenter = device.displayWidth / 2

            // Perform a few taps to pre-compile the AnimatedContent and Morph paths
            for (i in 1..10) {
                device.click(widthCenter, center)
                Thread.sleep(100) // Small delay to let animations start
            }
            
            // Trigger a reset to compile long-press paths
            device.swipe(widthCenter, center, widthCenter, center, 100)
        }
    )
}
