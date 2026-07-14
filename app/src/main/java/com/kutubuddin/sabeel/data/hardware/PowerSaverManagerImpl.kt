package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import android.util.Log
import com.kutubuddin.sabeel.di.ApplicationScope
import com.kutubuddin.sabeel.domain.power.PowerSaverManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages proximity/step sensors and a PARTIAL_WAKE_LOCK for Pocket Mode.
 *
 * Threading:
 * - [startMonitoring] / [stopMonitoring] are called from the Main thread.
 * - [onSensorChanged] fires on the sensor hardware thread.
 * - [isMonitoring] is an [AtomicBoolean] so both paths see consistent state
 *   without locking — eliminates the double-registration race (LEAK-01).
 *
 * WakeLock management:
 * - [triggerActiveState] acquires the lock once and resets a coroutine timer.
 *   Re-calls while held do NOT re-acquire (LEAK-02 / BATT-02 fix).
 * - [inactivityJob] is cancelled and nulled on [stopMonitoring] so the
 *   coroutine does not keep the scope alive after Pocket Mode ends.
 *
 * Handler removed:
 * - The old [android.os.Handler] + [Runnable] held a reference chain
 *   (Main Looper → releaseRunnable → this → context + wakeLock) for the
 *   full 60-second window even after [stopMonitoring]. Replaced with a
 *   coroutine [delay] inside [applicationScope] which the caller already
 *   owns and controls. (LEAK-02 fix)
 */
@Singleton
class PowerSaverManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope  // LEAK-02: replaces Handler
) : PowerSaverManager, SensorEventListener {

    private val sensorManager: SensorManager? by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    }

    private val powerManager: PowerManager? by lazy {
        context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    }

    private var wakeLock: PowerManager.WakeLock? = null

    // LEAK-02 / BATT-02: coroutine job replaces Handler + Runnable
    private var inactivityJob: Job? = null

    private val inactivityTimeoutMs = 30_000L // 30 seconds

    // LEAK-01: AtomicBoolean — compareAndSet prevents double sensor registration
    // when startMonitoring is called from multiple threads (e.g. Main + sensor callbacks).
    private val isMonitoring = AtomicBoolean(false)

    override fun startMonitoring() {
        // compareAndSet(false, true) returns true only for the first caller.
        // Any concurrent or duplicate call sees false and returns immediately.
        if (!isMonitoring.compareAndSet(false, true)) return

        val sm = sensorManager ?: return

        // Register Proximity Sensor
        // BATT-01: maxReportLatencyUs=500_000 (500ms) — sensor HAL batches events and
        // delivers them in bursts, allowing the CPU to sleep between deliveries.
        // Pocket detection with < 500ms latency is imperceptible to the user.
        val proximitySensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        proximitySensor?.let {
            sm.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL, 500_000)
        }

        // Register Step Detector (requires ACTIVITY_RECOGNITION runtime permission)
        // BATT-01: maxReportLatencyUs=2_000_000 (2s) — step events for dhikr counting
        // don't need sub-second precision; user is walking, not tapping a UI.
        // 2-second batch window reduces CPU wakeups from ~18,000/hr to ~1,800/hr.
        val stepDetector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        stepDetector?.let {
            sm.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL, 2_000_000)
        }

        Log.d(TAG, "Sensor monitoring started.")
    }

    override fun stopMonitoring() {
        // compareAndSet(true, false) — only the first caller proceeds.
        if (!isMonitoring.compareAndSet(true, false)) return

        sensorManager?.unregisterListener(this)

        // Cancel the inactivity timer so it doesn't fire after monitoring stops.
        inactivityJob?.cancel()
        inactivityJob = null

        releaseWakeLock()
        wakeLock = null  // LEAK-01: null after release so GC can collect the PM binder ref

        Log.d(TAG, "Sensor monitoring stopped.")
    }

    override fun isWakeLockHeld(): Boolean = wakeLock?.isHeld == true

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_PROXIMITY -> {
                val distance = event.values[0]
                val maxRange = event.sensor.maximumRange
                if (distance < maxRange) {
                    Log.d(TAG, "Proximity detected: Near pocket state.")
                    triggerActiveState()
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                Log.d(TAG, "Motion detected via Step Detector.")
                triggerActiveState()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun triggerActiveState() {
        // BATT-02 fix: acquire only if not already held —
        // step events (1-2 Hz while walking) no longer reset the hard timeout.
        acquireWakeLock()

        // Reset the release timer. Cancelling and relaunching is safe and cheap.
        inactivityJob?.cancel()
        inactivityJob = scope.launch {
            delay(inactivityTimeoutMs)
            Log.d(TAG, "Stationary state detected. Releasing WakeLock.")
            releaseWakeLock()
        }
    }

    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Sabeel:PowerSaverWakeLock"
            )?.apply { setReferenceCounted(false) }
        }
        // BATT-02: only acquire if not already held; re-acquiring a non-reference-counted
        // lock resets its internal timeout, nullifying the 30-second inactivity guard.
        if (wakeLock?.isHeld != true) {
            Log.d(TAG, "Acquiring WakeLock.")
            wakeLock?.acquire(inactivityTimeoutMs * 2) // Hard safety cap: 60 s
        }
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            Log.d(TAG, "WakeLock released.")
        }
    }

    private companion object {
        const val TAG = "PowerSaverManager"
    }
}
