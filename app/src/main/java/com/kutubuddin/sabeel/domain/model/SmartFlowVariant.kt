package com.kutubuddin.sabeel.domain.model

/**
 * Defines the two authentic variations of the Post-Salah Dhikr sequence,
 * both narrated in Sahih Muslim (595–596) and Sahih al-Bukhari (843).
 *
 * The Smart Flow engine reads this variant to determine transition thresholds
 * and the final dhikr in the sequence. Adding new variants is OCP-compliant —
 * no modification to the counting engine is required, only extension here.
 */
enum class SmartFlowVariant(
    val displayName: String,
    val hadithRef: String,
    val description: String
) {
    /**
     * Variation 1 (33-33-34):
     * SubhanAllah × 33 → Alhamdulillah × 33 → Allahu Akbar × 34 = 100
     * Reference: Sahih Muslim 596, Sahih al-Bukhari 843
     */
    CLASSIC(
        displayName = "Classic · 33·33·34",
        hadithRef = "Sahih Muslim 596",
        description = "SubhanAllah 33 · Alhamdulillah 33 · Allahu Akbar 34"
    ),

    /**
     * Variation 2 (33-33-33 + Tahlil):
     * SubhanAllah × 33 → Alhamdulillah × 33 → Allahu Akbar × 33 →
     * La ilaha illallahu wahdahu la sharika lahu... × 1 = 100
     * Reference: Sahih Muslim 596, Sahih al-Bukhari 843
     */
    WITH_TAHLIL(
        displayName = "With Tahlīl · 33·33·33·1",
        hadithRef = "Sahih Muslim 595",
        description = "SubhanAllah 33 · Alhamdulillah 33 · Allahu Akbar 33 · Tahlil 1"
    )
}
