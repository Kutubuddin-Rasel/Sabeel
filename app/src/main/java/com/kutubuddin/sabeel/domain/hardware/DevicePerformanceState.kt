package com.kutubuddin.sabeel.domain.hardware

data class DevicePerformanceState(
    val isPowerSaveMode: Boolean = false,
    val isLowTierDevice: Boolean = false
) {
    /**
     * True if the device is struggling or actively trying to save power.
     * Use this flag to degrade animations gracefully (e.g., stopping infinite loops).
     */
    val reduceAnimations: Boolean
        get() = isPowerSaveMode || isLowTierDevice
}
