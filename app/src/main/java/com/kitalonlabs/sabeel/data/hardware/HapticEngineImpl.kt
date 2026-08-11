package com.kitalonlabs.sabeel.data.hardware

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.kitalonlabs.sabeel.domain.haptic.HapticEngine
import com.kitalonlabs.sabeel.domain.haptic.HapticStrength
import com.kitalonlabs.sabeel.domain.haptic.SabeelVibrator
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
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK)
        ) {
            try {
                val effect = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, strength.tick)
                    .compose()
                vibrator.vibrate(effect)
                return
            } catch (_: Throwable) {
                // Fall back if primitive composition fails on custom ROMs
            }
        }

        if (sabeelVibrator.hasAmplitudeControl()) {
            val duration = (15L + (strength.tick * 15L)).toLong()
            sabeelVibrator.vibrate(durationMs = duration, amplitude = strength.tick.toAmplitude())
        } else {
            // PWM timing modulation for hardware without amplitude control
            val pwmDuration = when (strength) {
                HapticStrength.OFF -> 0L
                HapticStrength.LIGHT -> 10L
                HapticStrength.MEDIUM -> 24L
                HapticStrength.STRONG -> 45L
            }
            if (pwmDuration > 0L) {
                sabeelVibrator.vibrate(durationMs = pwmDuration, amplitude = -1)
            }
        }
    }

    override fun playMilestoneClick(strength: HapticStrength) {
        if (strength == HapticStrength.OFF) return
        val vibrator = this.vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_CLICK)
        ) {
            try {
                val effect = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, strength.click)
                    .compose()
                vibrator.vibrate(effect)
                return
            } catch (_: Throwable) {
                // Fall back if primitive composition fails on custom ROMs
            }
        }

        if (sabeelVibrator.hasAmplitudeControl()) {
            val duration = (25L + (strength.click * 20L)).toLong()
            sabeelVibrator.vibrate(durationMs = duration, amplitude = strength.click.toAmplitude())
        } else {
            // PWM timing modulation for hardware without amplitude control
            val pwmDuration = when (strength) {
                HapticStrength.OFF -> 0L
                HapticStrength.LIGHT -> 20L
                HapticStrength.MEDIUM -> 40L
                HapticStrength.STRONG -> 65L
            }
            if (pwmDuration > 0L) {
                sabeelVibrator.vibrate(durationMs = pwmDuration, amplitude = -1)
            }
        }
    }

    override fun playCompletionThud(strength: HapticStrength) {
        if (strength == HapticStrength.OFF) return
        val vibrator = this.vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)
        ) {
            try {
                val effect = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, strength.thud)
                    .compose()
                vibrator.vibrate(effect)
                return
            } catch (_: Throwable) {
                // Fall back if primitive composition fails on custom ROMs
            }
        }

        if (sabeelVibrator.hasAmplitudeControl()) {
            sabeelVibrator.vibratePattern(
                pattern = longArrayOf(0, 80, 50, 80),
                amplitudes = intArrayOf(0, strength.thud.toAmplitude(), 0, strength.thud.toAmplitude())
            )
        } else {
            val pattern = when (strength) {
                HapticStrength.OFF -> longArrayOf(0, 0)
                HapticStrength.LIGHT -> longArrayOf(0, 30, 40, 30)
                HapticStrength.MEDIUM -> longArrayOf(0, 50, 40, 50)
                HapticStrength.STRONG -> longArrayOf(0, 80, 50, 80)
            }
            sabeelVibrator.vibratePattern(pattern = pattern, amplitudes = null)
        }
    }

    override fun playReset(strength: HapticStrength) {
        if (strength == HapticStrength.OFF) return
        val vibrator = this.vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            vibrator.areAllPrimitivesSupported(
                VibrationEffect.Composition.PRIMITIVE_THUD,
                VibrationEffect.Composition.PRIMITIVE_TICK
            )
        ) {
            try {
                val effect = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, strength.thud)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, strength.tick, 90)
                    .compose()
                vibrator.vibrate(effect)
                return
            } catch (_: Throwable) {
                // Fall back if primitive composition fails on custom ROMs
            }
        }

        if (sabeelVibrator.hasAmplitudeControl()) {
            sabeelVibrator.vibratePattern(
                pattern = longArrayOf(0, 70, 60, 40),
                amplitudes = intArrayOf(0, strength.thud.toAmplitude(), 0, strength.tick.toAmplitude())
            )
        } else {
            val pattern = when (strength) {
                HapticStrength.OFF -> longArrayOf(0, 0)
                HapticStrength.LIGHT -> longArrayOf(0, 30, 50, 20)
                HapticStrength.MEDIUM -> longArrayOf(0, 50, 50, 35)
                HapticStrength.STRONG -> longArrayOf(0, 70, 60, 50)
            }
            sabeelVibrator.vibratePattern(pattern = pattern, amplitudes = null)
        }
    }
}
