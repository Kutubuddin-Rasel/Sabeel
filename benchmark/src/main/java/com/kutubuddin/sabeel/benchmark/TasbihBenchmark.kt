package com.kutubuddin.sabeel.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasbihBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.kutubuddin.sabeel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun rapidTapZeroJank() = benchmarkRule.measureRepeated(
        packageName = "com.kutubuddin.sabeel",
        metrics = listOf(FrameTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.WARM
    ) {
        pressHome()
        startActivityAndWait()

        // Wait for the UI to settle
        device.wait(Until.hasObject(By.textContains("00")), 5000)

        // Find the central area of the screen to tap
        val center = device.displayHeight / 2
        val widthCenter = device.displayWidth / 2

        // Simulate a rapid tapping user (200 consecutive taps)
        for (i in 1..200) {
            device.click(widthCenter, center)
            // No delay between clicks to simulate extremely rapid successive taps
        }
    }
}
