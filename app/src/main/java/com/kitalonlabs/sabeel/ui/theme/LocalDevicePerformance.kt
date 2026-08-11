package com.kitalonlabs.sabeel.ui.theme

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.kitalonlabs.sabeel.domain.hardware.DevicePerformanceState

val LocalDevicePerformance = staticCompositionLocalOf { DevicePerformanceState() }

/**
 * Creates and remembers a [DevicePerformanceState] by actively listening to the Android OS
 * for Power Save Mode changes, and querying the [ActivityManager] for Low-RAM status.
 */
@Composable
fun rememberDevicePerformanceState(): DevicePerformanceState {
    val context = LocalContext.current
    
    val powerManager = remember(context) { 
        context.getSystemService(Context.POWER_SERVICE) as PowerManager 
    }
    val activityManager = remember(context) { 
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager 
    }

    var isPowerSaveMode by remember { mutableStateOf(powerManager.isPowerSaveMode) }
    val isLowTierDevice = remember(activityManager) { activityManager.isLowRamDevice }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                    isPowerSaveMode = powerManager.isPowerSaveMode
                }
            }
        }
        context.registerReceiver(
            receiver,
            IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        )
        
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    return remember(isPowerSaveMode, isLowTierDevice) {
        DevicePerformanceState(
            isPowerSaveMode = isPowerSaveMode,
            isLowTierDevice = isLowTierDevice
        )
    }
}
