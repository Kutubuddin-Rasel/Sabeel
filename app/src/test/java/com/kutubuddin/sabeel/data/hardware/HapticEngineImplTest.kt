package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.test.core.app.ApplicationProvider
import com.kutubuddin.sabeel.domain.haptic.SabeelVibrator
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
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
    fun testPlayIncrementTick_legacySDK_vibratesOneShot() {
        hapticEngine.playIncrementTick()
        io.mockk.verify(exactly = 1) { sabeelVibrator.vibrate(15L) }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayMilestoneClick_legacySDK_vibratesOneShot() {
        hapticEngine.playMilestoneClick()
        io.mockk.verify(exactly = 1) { sabeelVibrator.vibrate(45L) }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun testPlayCompletionThud_legacySDK_vibratesWaveform() {
        hapticEngine.playCompletionThud()
        io.mockk.verify(exactly = 1) { sabeelVibrator.vibratePattern(longArrayOf(0, 80, 50, 80)) }
    }
}
