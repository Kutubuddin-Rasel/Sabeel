package com.kutubuddin.sabeel.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

/**
 * Generates app/src/main/baseline-prof.txt.
 *
 * WHY THIS EXISTS: the app depends on androidx.profileinstaller
 * (see app/build.gradle.kts) but ships no baseline-prof.txt, so every
 * animation path — spring physics, Canvas draws, AnimatedContent /
 * AnimatedVisibility — is JIT-interpreted rather than AOT-compiled for a
 * device's first several runs after install. That's exactly the state a
 * freshly-recorded screen capture is usually in, and it can make correctly
 * -tuned animation code look janky on camera even though the spec is right.
 *
 * HOW TO RUN (Android Studio, physical device or emulator, API 28+ preferred):
 *   1. Open this file and click the green gutter icon next to `generate()`,
 *      then choose "Run 'generate' — Generate Baseline Profile".
 *   2. Studio installs the `benchmark` build type, runs the journey below,
 *      and (Studio 2024.1+) automatically writes the result to
 *      app/src/main/baseline-prof.txt.
 *      If your Studio version doesn't auto-copy it: pull it manually from
 *      benchmark/build/outputs/managed_device_android_test_additional_output/
 *      and save it as app/src/main/baseline-prof.txt yourself.
 *   3. Commit that file. ProfileInstaller (already a dependency) picks it up
 *      automatically at install time on the user's device — no other code
 *      change is needed.
 *
 * This journey deliberately exercises the highest-value paths from the
 * animation audit: cold start → Count tab (TasbihCircle / OdometerCounter
 * springs) → repeated taps (the increment animation loop, the single
 * most-repeated motion in the app) → Dhikr tab (DhikrCard expand/collapse +
 * LazyColumn animateItem() sibling repositioning).
 *
 * The tab lookups use By.desc(...) matching the NavigationBarItem's
 * contentDescription (= its localized label, see SabeelBottomBar.kt). If
 * your device's default locale isn't English these strings won't match —
 * each lookup is wrapped so a miss just skips that step instead of failing
 * the whole profile run, but for the most complete profile, run once with
 * the device locale set to whatever LocalStrings resolves to by default.
 *
 * Extend this journey over time (e.g. the CompletionRest overlay, WirdEdit's
 * reorderable list, Settings' collapsible "Advanced" section) as those paths
 * turn out to matter for real-world first-run smoothness.
 */
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.kutubuddin.sabeel",
        maxIterations = 8,
        includeInStartupProfile = true
    ) {
        // Cold start — captures class-loading/init cost for the Hilt graph,
        // the navigation graph, and the first Home composition.
        pressHome()
        startActivityAndWait()

        // Count tab: exercise TasbihCircle's four coupled animation layers
        // (odometer roll, arc sweep, press-scale, ring pulse) by tapping the
        // center of the screen — the circle occupies a large center region,
        // so this doesn't depend on knowing an exact resource id.
        device.findObject(By.desc("Count"))?.let { countTab ->
            countTab.click()
            device.waitForIdle()
            repeat(5) {
                device.click(device.displayWidth / 2, device.displayHeight / 2)
                device.waitForIdle()
            }
        }

        // Dhikr tab: exercises DhikrCard's coupled expand/collapse springs
        // and the LazyColumn's animateItem() sibling-repositioning fix.
        device.findObject(By.desc("Dhikr"))?.let { dhikrTab ->
            dhikrTab.click()
            device.wait(Until.hasObject(By.scrollable(true)), 3_000)
            device.findObject(By.scrollable(true))?.let { list ->
                // Tap the first row to trigger one expand animation.
                list.children.firstOrNull()?.click()
                device.waitForIdle()
            }
        }

        // Back to Home — exercises the AnimatedContent hero-card crossfade
        // if session state changed as a result of the taps above.
        device.findObject(By.desc("Home"))?.let { homeTab ->
            homeTab.click()
            device.waitForIdle()
        }
    }
}
