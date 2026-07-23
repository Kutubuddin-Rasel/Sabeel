package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.test.core.app.ApplicationProvider
import com.kutubuddin.sabeel.domain.haptic.HapticStrength
import com.kutubuddin.sabeel.domain.haptic.SabeelVibrator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class HapticEngineImplTest {

    private lateinit var context: Context
    private lateinit var hapticEngine: HapticEngineImpl
    private lateinit var vibrator: Vibrator
    private lateinit var sabeelVibrator: SabeelVibrator

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        sabeelVibrator = mockk(relaxed = true)
        hapticEngine = HapticEngineImpl(context, sabeelVibrator)
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayIncrementTick_offStrength_doesNotVibrate() {
        hapticEngine.playIncrementTick(HapticStrength.OFF)
        verify(exactly = 0) { sabeelVibrator.vibrate(any(), any()) }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayIncrementTick_withAmplitudeControl_vibratesWithScaledAmplitude() {
        every { sabeelVibrator.hasAmplitudeControl() } returns true
        hapticEngine.playIncrementTick(HapticStrength.MEDIUM)
        verify(exactly = 1) { sabeelVibrator.vibrate(durationMs = 24L, amplitude = 165) }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayIncrementTick_withoutAmplitudeControl_vibratesPwmDuration() {
        every { sabeelVibrator.hasAmplitudeControl() } returns false
        hapticEngine.playIncrementTick(HapticStrength.LIGHT)
        verify(exactly = 1) { sabeelVibrator.vibrate(durationMs = 10L, amplitude = -1) }

        hapticEngine.playIncrementTick(HapticStrength.STRONG)
        verify(exactly = 1) { sabeelVibrator.vibrate(durationMs = 45L, amplitude = -1) }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayMilestoneClick_withoutAmplitudeControl_vibratesPwmDuration() {
        every { sabeelVibrator.hasAmplitudeControl() } returns false
        hapticEngine.playMilestoneClick(HapticStrength.MEDIUM)
        verify(exactly = 1) { sabeelVibrator.vibrate(durationMs = 40L, amplitude = -1) }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayCompletionThud_withoutAmplitudeControl_vibratesPattern() {
        every { sabeelVibrator.hasAmplitudeControl() } returns false
        hapticEngine.playCompletionThud(HapticStrength.MEDIUM)
        verify(exactly = 1) { sabeelVibrator.vibratePattern(pattern = longArrayOf(0, 50, 40, 50), amplitudes = null) }
    }
}
