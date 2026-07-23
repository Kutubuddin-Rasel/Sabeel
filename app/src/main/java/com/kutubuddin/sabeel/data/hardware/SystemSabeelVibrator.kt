package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.kutubuddin.sabeel.domain.haptic.SabeelVibrator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemSabeelVibrator @Inject constructor(
    @ApplicationContext private val context: Context
) : SabeelVibrator {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun hasAmplitudeControl(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.hasAmplitudeControl() == true
            } else {
                false
            }
        } catch (_: Throwable) {
            false
        }
    }

    override fun vibrate(durationMs: Long, amplitude: Int) {
        val vibrator = this.vibrator ?: return
        if (!vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val safeAmplitude = if (hasAmplitudeControl() && amplitude != -1) amplitude else VibrationEffect.DEFAULT_AMPLITUDE
                vibrator.vibrate(
                    VibrationEffect.createOneShot(durationMs, safeAmplitude)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Throwable) {
            // Silently swallow OEM driver framework bugs
        }
    }

    override fun vibratePattern(pattern: LongArray, amplitudes: IntArray?, repeat: Int) {
        val vibrator = this.vibrator ?: return
        if (!vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (amplitudes != null && amplitudes.size == pattern.size && hasAmplitudeControl()) {
                    VibrationEffect.createWaveform(pattern, amplitudes, repeat)
                } else {
                    VibrationEffect.createWaveform(pattern, repeat)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        } catch (_: Throwable) {
            // Silently swallow OEM driver framework bugs
        }
    }

    override fun cancel() {
        try {
            vibrator?.cancel()
        } catch (_: Throwable) {}
    }
}
