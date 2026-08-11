package com.kitalonlabs.sabeel.domain.haptic

interface HapticEngine {
    fun playIncrementTick(strength: HapticStrength = HapticStrength.MEDIUM)
    fun playMilestoneClick(strength: HapticStrength = HapticStrength.MEDIUM)
    fun playCompletionThud(strength: HapticStrength = HapticStrength.MEDIUM)
    fun playReset(strength: HapticStrength = HapticStrength.MEDIUM)
}
