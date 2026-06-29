package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class HapticEngineImplTest {

    private lateinit var context: Context
    private lateinit var hapticEngine: HapticEngineImpl
    private lateinit var vibrator: Vibrator

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        hapticEngine = HapticEngineImpl(context)
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayIncrementTick_legacySDK_vibratesOneShot() {
        val shadowVibrator = shadowOf(vibrator)
        hapticEngine.playIncrementTick()
        assertTrue(shadowVibrator.isVibrating)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayMilestoneClick_legacySDK_vibratesOneShot() {
        val shadowVibrator = shadowOf(vibrator)
        hapticEngine.playMilestoneClick()
        assertTrue(shadowVibrator.isVibrating)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayCompletionThud_legacySDK_vibratesWaveform() {
        val shadowVibrator = shadowOf(vibrator)
        hapticEngine.playCompletionThud()
        assertTrue(shadowVibrator.isVibrating)
    }
}
