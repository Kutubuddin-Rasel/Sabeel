package com.kutubuddin.sabeel.domain.haptic

interface SabeelVibrator {
    fun vibrate(durationMs: Long)
    fun vibratePattern(pattern: LongArray, repeat: Int = -1)
    fun cancel()
}
