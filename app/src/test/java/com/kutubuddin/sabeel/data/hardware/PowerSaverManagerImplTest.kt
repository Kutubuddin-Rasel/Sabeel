package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.os.Build
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowSensorManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class PowerSaverManagerImplTest {

    private lateinit var context: Context
    private lateinit var powerSaverManager: PowerSaverManagerImpl
    private lateinit var sensorManager: SensorManager
    private lateinit var shadowSensorManager: ShadowSensorManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        powerSaverManager = PowerSaverManagerImpl(context)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shadowSensorManager = shadowOf(sensorManager)
    }

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

    private fun createSensorEvent(sensor: Sensor, values: FloatArray): SensorEvent {
        val constructor = SensorEvent::class.java.getDeclaredConstructor(Int::class.javaPrimitiveType)
        constructor.isAccessible = true
        val event = constructor.newInstance(values.size)
        
        val sensorField = SensorEvent::class.java.getField("sensor")
        sensorField.isAccessible = true
        sensorField.set(event, sensor)
        
        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true
        val eventValues = valuesField.get(event) as FloatArray
        System.arraycopy(values, 0, eventValues, 0, values.size)
        
        return event
    }
}
