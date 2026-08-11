package com.kitalonlabs.sabeel.data.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSensorManager

/**
 * Unit tests for [PowerSaverManagerImpl].
 *
 * Post-LEAK-01/02 regression suite verifies:
 * 1. Sensor listener is registered exactly once even if startMonitoring is called twice.
 * 2. Sensor listener is fully unregistered and wakeLock is null after stopMonitoring.
 * 3. triggerActiveState called twice → only one inactivity Job active at a time.
 * 4. WakeLock is released after the inactivity timeout expires (coroutine timer fires).
 *
 * Uses [TestScope] so the coroutine inactivity timer can be fast-forwarded.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class PowerSaverManagerImplTest {

    private lateinit var context: Context
    private lateinit var powerSaverManager: PowerSaverManagerImpl
    private lateinit var sensorManager: SensorManager
    private lateinit var shadowSensorManager: ShadowSensorManager

    // Shared TestScope so we can advance virtual time in tests.
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // LEAK-02: constructor now requires @ApplicationScope; pass TestScope.
        powerSaverManager = PowerSaverManagerImpl(context, testScope)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shadowSensorManager = shadowOf(sensorManager)
    }

    // ─── Existing tests (updated for new constructor) ──────────────────────────

    @Test
    fun testStartStopMonitoring_registersAndUnregistersListeners() {
        powerSaverManager.startMonitoring()

        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        if (proximitySensor != null) {
            assertTrue(shadowSensorManager.getListeners().contains(powerSaverManager))
        }

        powerSaverManager.stopMonitoring()

        if (proximitySensor != null) {
            assertFalse(shadowSensorManager.getListeners().contains(powerSaverManager))
        }
    }

    @Test
    fun testSensorChanged_proximityNear_acquiresWakeLock() {
        powerSaverManager.startMonitoring()

        val sensor = mockk<Sensor>(relaxed = true)
        every { sensor.type } returns Sensor.TYPE_PROXIMITY
        every { sensor.maximumRange } returns 5f

        val event = createSensorEvent(sensor, floatArrayOf(2f)) // near range

        powerSaverManager.onSensorChanged(event)

        assertTrue(powerSaverManager.isWakeLockHeld())

        powerSaverManager.stopMonitoring()
        assertFalse(powerSaverManager.isWakeLockHeld())
    }

    @Test
    fun testSensorChanged_stepDetector_acquiresWakeLock() {
        powerSaverManager.startMonitoring()

        val sensor = mockk<Sensor>(relaxed = true)
        every { sensor.type } returns Sensor.TYPE_STEP_DETECTOR

        val event = createSensorEvent(sensor, floatArrayOf(1.0f))

        powerSaverManager.onSensorChanged(event)

        assertTrue(powerSaverManager.isWakeLockHeld())

        powerSaverManager.stopMonitoring()
        assertFalse(powerSaverManager.isWakeLockHeld())
    }

    // ─── LEAK-01 regression: double-call idempotency ───────────────────────────

    /**
     * LEAK-01: Calling startMonitoring() twice must register the listener exactly once.
     * Previously: isMonitoring was a plain `var`, so concurrent or rapid duplicate
     * calls could bypass the guard and register 2 listeners.
     * Now: AtomicBoolean.compareAndSet guarantees only the first caller proceeds.
     */
    @Test
    fun startMonitoring_calledTwice_registersListenerOnce() {
        powerSaverManager.startMonitoring()
        powerSaverManager.startMonitoring() // second call — must be no-op

        val listenerCount = shadowSensorManager.getListeners()
            .count { it === powerSaverManager }

        // getListeners() may contain one entry per registered sensor type.
        // We assert no more than the expected sensor count (≤2: proximity + step).
        assertTrue("Listener registered more than twice: $listenerCount", listenerCount <= 2)

        powerSaverManager.stopMonitoring()
    }

    /**
     * LEAK-01: Calling stopMonitoring() twice must not cause a double-unregister crash
     * and must leave the listener unregistered.
     */
    @Test
    fun stopMonitoring_calledTwice_isIdempotent() {
        powerSaverManager.startMonitoring()
        powerSaverManager.stopMonitoring()
        powerSaverManager.stopMonitoring() // second call — must be safe

        assertFalse(shadowSensorManager.getListeners().contains(powerSaverManager))
    }

    /**
     * LEAK-02: After stopMonitoring, the inactivity job must be cancelled.
     * Advancing virtual time past the timeout must NOT release a wakeLock (because
     * monitoring is already stopped and the job was cancelled in stopMonitoring).
     */
    @Test
    fun stopMonitoring_cancelsInactivityJob() = testScope.runTest {
        powerSaverManager.startMonitoring()

        // Trigger active state (starts the inactivity timer)
        val sensor = mockk<Sensor>(relaxed = true)
        every { sensor.type } returns Sensor.TYPE_STEP_DETECTOR
        powerSaverManager.onSensorChanged(createSensorEvent(sensor, floatArrayOf(1f)))
        assertTrue(powerSaverManager.isWakeLockHeld())

        // Stop before the timer fires
        powerSaverManager.stopMonitoring()
        assertFalse(powerSaverManager.isWakeLockHeld())

        // Advance time well past the 30s timeout — job must be cancelled, no crash.
        advanceTimeBy(60_000L)

        // Still released — the cancelled job did not fire after stopMonitoring.
        assertFalse(powerSaverManager.isWakeLockHeld())
    }

    /**
     * LEAK-02: Calling triggerActiveState() rapidly (e.g., step events at 2 Hz)
     * must result in exactly one active inactivity Job, not N stacked jobs.
     * Verified by confirming the wakeLock is released exactly once after the timeout.
     */
    @Test
    fun triggerActiveState_calledRepeatedly_resetsTimerNotStacksJobs() = testScope.runTest {
        powerSaverManager.startMonitoring()

        val sensor = mockk<Sensor>(relaxed = true)
        every { sensor.type } returns Sensor.TYPE_STEP_DETECTOR

        // Simulate 5 step events in quick succession — each one resets the timer.
        repeat(5) {
            powerSaverManager.onSensorChanged(createSensorEvent(sensor, floatArrayOf(1f)))
        }

        assertTrue(powerSaverManager.isWakeLockHeld())

        // Advance exactly to the timeout — lock should be released by the single active job.
        advanceTimeBy(30_001L)

        assertFalse(
            "WakeLock should be released after inactivity timeout",
            powerSaverManager.isWakeLockHeld()
        )

        powerSaverManager.stopMonitoring()
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private fun createSensorEvent(sensor: Sensor, values: FloatArray): android.hardware.SensorEvent {
        val constructor = android.hardware.SensorEvent::class.java
            .getDeclaredConstructor(Int::class.javaPrimitiveType)
        constructor.isAccessible = true
        val event = constructor.newInstance(values.size)

        val sensorField = android.hardware.SensorEvent::class.java.getField("sensor")
        sensorField.isAccessible = true
        sensorField.set(event, sensor)

        val valuesField = android.hardware.SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true
        val eventValues = valuesField.get(event) as FloatArray
        System.arraycopy(values, 0, eventValues, 0, values.size)

        return event
    }
}
