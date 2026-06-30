package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest

@RunWith(AndroidJUnit4::class)
class PowerSaverManagerInstrumentationTest {

    private lateinit var context: Context
    private lateinit var powerSaverManager: PowerSaverManagerImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        powerSaverManager = PowerSaverManagerImpl(context)
    }

    @Test
    fun testWakeLockAcquiredAndReleasedGracefully() = runTest {
        // Start monitoring (requires WakeLock permission in AndroidManifest)
        powerSaverManager.startMonitoring()
        
        // Simulating the sensor event directly in the instrumentation context
        // to bypass the physical hardware requirement.
        val mockSensor = mockSensor(Sensor.TYPE_STEP_DETECTOR)
        val mockEvent = createMockSensorEvent(mockSensor, floatArrayOf(1.0f))
        
        powerSaverManager.onSensorChanged(mockEvent)
        
        // Verify the PARTIAL_WAKE_LOCK was acquired
        assertTrue("WakeLock should be held when motion is detected", powerSaverManager.isWakeLockHeld())
        
        // Stop monitoring simulates a stationary transition timeout
        powerSaverManager.stopMonitoring()
        
        // Verify the PARTIAL_WAKE_LOCK was gracefully released
        assertFalse("WakeLock should be released when stationary", powerSaverManager.isWakeLockHeld())
    }
    
    private fun mockSensor(type: Int): Sensor {
        val constructor = Sensor::class.java.getDeclaredConstructor()
        constructor.isAccessible = true
        val sensor = constructor.newInstance()
        val typeField = Sensor::class.java.getDeclaredField("mType")
        typeField.isAccessible = true
        typeField.set(sensor, type)
        return sensor
    }

    private fun createMockSensorEvent(sensor: Sensor, values: FloatArray): SensorEvent {
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
