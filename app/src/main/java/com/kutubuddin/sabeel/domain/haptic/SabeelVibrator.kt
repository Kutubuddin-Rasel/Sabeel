package com.kutubuddin.sabeel.domain.haptic

interface SabeelVibrator {
    fun hasAmplitudeControl(): Boolean
    fun vibrate(durationMs: Long, amplitude: Int = -1)
    fun vibratePattern(pattern: LongArray, amplitudes: IntArray? = null, repeat: Int = -1)
    fun cancel()
}
