package com.kutubuddin.sabeel.domain.power

interface PowerSaverManager {
    fun startMonitoring()
    fun stopMonitoring()
    fun isWakeLockHeld(): Boolean
}
