package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import com.kutubuddin.sabeel.domain.power.PowerSaverManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerSaverManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PowerSaverManager, SensorEventListener {

    private val sensorManager: SensorManager? by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    }
    
    private val powerManager: PowerManager? by lazy {
        context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private val handler = Handler(Looper.getMainLooper())
    private val inactivityTimeoutMs = 30000L // 30 seconds
    private var isMonitoring = false

    private val releaseRunnable = Runnable {
        Log.d("PowerSaverManager", "Stationary state detected. Releasing WakeLock.")
        releaseWakeLock()
    }

    override fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        val sensorManager = this.sensorManager ?: return
        
        // Register Proximity Sensor
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Register Step Detector (Requires ACTIVITY_RECOGNITION runtime permission)
        val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        stepDetector?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        Log.d("PowerSaverManager", "Sensor monitoring started.")
    }

    override fun stopMonitoring() {
        if (!isMonitoring) return
        isMonitoring = false
        
        sensorManager?.unregisterListener(this)
        handler.removeCallbacks(releaseRunnable)
        releaseWakeLock()
        
        Log.d("PowerSaverManager", "Sensor monitoring stopped.")
    }

    override fun isWakeLockHeld(): Boolean {
        return wakeLock?.isHeld == true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        
        when (event.sensor.type) {
            Sensor.TYPE_PROXIMITY -> {
                val distance = event.values[0]
                val maxRange = event.sensor.maximumRange
                if (distance < maxRange) {
                    // Device is close to something (e.g. in pocket)
                    Log.d("PowerSaverManager", "Proximity detected: Near pocket state.")
                    triggerActiveState()
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                // Step detected - user is active/moving
                Log.d("PowerSaverManager", "Motion detected via Step Detector.")
                triggerActiveState()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun triggerActiveState() {
        acquireWakeLock()
        // Reset the inactivity release runnable
        handler.removeCallbacks(releaseRunnable)
        handler.postDelayed(releaseRunnable, inactivityTimeoutMs)
    }

    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Sabeel:PowerSaverWakeLock"
            )?.apply {
                setReferenceCounted(false)
            }
        }
        
        if (wakeLock?.isHeld == false) {
            Log.d("PowerSaverManager", "Acquiring WakeLock.")
            wakeLock?.acquire(inactivityTimeoutMs * 2) // Safety timeout limits leak window
        }
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            Log.d("PowerSaverManager", "WakeLock released.")
        }
    }
}
