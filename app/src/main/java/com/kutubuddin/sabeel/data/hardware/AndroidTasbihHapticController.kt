package com.kutubuddin.sabeel.data.hardware

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.kutubuddin.sabeel.domain.haptic.TasbihHapticController
import javax.inject.Inject

class AndroidTasbihHapticController @Inject constructor(
    private val vibrator: Vibrator
) : TasbihHapticController {

    override fun playIncrementTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && 
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK)) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK)
                .compose()
            vibrator.vibrate(effect)
        } else {
            vibrateLegacy(10)
        }
    }

    override fun playMilestoneClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && 
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_CLICK)) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                .compose()
            vibrator.vibrate(effect)
        } else {
            vibrateLegacy(30)
        }
    }

    override fun playCompletionThud() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && 
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD)
                .compose()
            vibrator.vibrate(effect)
        } else {
            vibrateLegacy(100)
        }
    }

    private fun vibrateLegacy(durationMs: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    durationMs,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
