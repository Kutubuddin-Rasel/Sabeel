package com.kitalonlabs.sabeel.domain.power

interface PowerSaverManager {
    fun startMonitoring()
    fun stopMonitoring()
    fun isWakeLockHeld(): Boolean
}
