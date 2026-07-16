package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.domain.haptic.HapticStrength
import com.kutubuddin.sabeel.domain.haptic.SabeelVibrator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sabeelVibrator: SabeelVibrator
) : HapticEngine {

    private fun Float.toAmplitude(): Int = (this * 255).toInt().coerceIn(1, 255)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun playIncrementTick(strength: HapticStrength) {
        if (strength == HapticStrength.OFF) return
        val vibrator = this.vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK)
        ) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, strength.tick)
                .compose()
            vibrator.vibrate(effect)
        } else {
            // ERM fallback: Scale duration up for stronger feedback, as ERMs need time to spin up.
            // A flat 15ms at low amplitude is imperceptible on many Samsung/older devices.
            val duration = (15L + (strength.tick * 20L)).toLong()
            sabeelVibrator.vibrate(durationMs = duration, amplitude = strength.tick.toAmplitude())
        }
    }

    override fun playMilestoneClick(strength: HapticStrength) {
        if (strength == HapticStrength.OFF) return
        val vibrator = this.vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_CLICK)
        ) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, strength.click)
                .compose()
            vibrator.vibrate(effect)
        } else {
            val duration = (35L + (strength.click * 25L)).toLong()
            sabeelVibrator.vibrate(durationMs = duration, amplitude = strength.click.toAmplitude())
        }
    }

    override fun playCompletionThud(strength: HapticStrength) {
        if (strength == HapticStrength.OFF) return
        val vibrator = this.vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)
        ) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, strength.thud)
                .compose()
            vibrator.vibrate(effect)
        } else {
            // Double-pulse vibration for heavy feedback fallback
            sabeelVibrator.vibratePattern(
                pattern = longArrayOf(0, 80, 50, 80),
                amplitudes = intArrayOf(0, strength.thud.toAmplitude(), 0, strength.thud.toAmplitude())
            )
        }
    }

    /**
     * A firm double-thud (THUD + a delayed TICK tail) so a manual reset feels
     * distinct from a completion — recognisable eyes-free without looking.
     */
    override fun playReset(strength: HapticStrength) {
        if (strength == HapticStrength.OFF) return
        val vibrator = this.vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            vibrator.areAllPrimitivesSupported(
                VibrationEffect.Composition.PRIMITIVE_THUD,
                VibrationEffect.Composition.PRIMITIVE_TICK
            )
        ) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, strength.thud)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, strength.tick, 90) // delayMs tail
                .compose()
            vibrator.vibrate(effect)
        } else {
            sabeelVibrator.vibratePattern(
                pattern = longArrayOf(0, 70, 60, 40),
                amplitudes = intArrayOf(0, strength.thud.toAmplitude(), 0, strength.tick.toAmplitude())
            )
        }
    }
}
