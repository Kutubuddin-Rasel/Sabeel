package com.kutubuddin.sabeel.domain.haptic

/**
 * Per-level scale factors for the three tasbih haptic primitives.
 *
 * The values map the user's Settings choice ("light"|"medium"|"strong"|"off")
 * onto the `scale` argument of `VibrationEffect.Composition.addPrimitive(id, scale)`,
 * which is what actually varies the felt strength on API ≥ R devices. Without this
 * the Haptic Feedback setting was a placebo — every level fired at 1.0.
 */
enum class HapticStrength(val tick: Float, val click: Float, val thud: Float) {
    OFF   (0f,    0f,   0f),
    LIGHT (0.35f, 0.5f, 0.6f),
    MEDIUM(0.65f, 0.8f, 0.85f),
    STRONG(1.0f,  1.0f, 1.0f);

    companion object {
        fun fromSetting(value: String): HapticStrength = when (value.lowercase()) {
            "off"                   -> OFF
            "light", "low"          -> LIGHT
            "strong", "high", "max" -> STRONG
            else                    -> MEDIUM   // "medium" + any unknown
        }
    }
}
