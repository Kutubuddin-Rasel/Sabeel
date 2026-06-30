package com.kutubuddin.sabeel.domain.model

/**
 * All supported dhikr types with their display metadata and theological context.
 *
 * spiritualReward: Sahih-sourced benefit text displayed on the counting screen.
 * This is static, offline data — no network dependency.
 */
enum class DhikrType(
    val displayName: String,
    val arabicText: String,
    val defaultTarget: Int,
    val spiritualReward: String
) {
    SUBHANALLAH(
        displayName = "SubhanAllah",
        arabicText = "سُبْحَانَ اللَّهِ",
        defaultTarget = 33,
        spiritualReward = "Glorified is Allah — removes sins as leaves fall from a tree"
    ),
    ALHAMDULILLAH(
        displayName = "Alhamdulillah",
        arabicText = "الْحَمْدُ لِلَّهِ",
        defaultTarget = 33,
        spiritualReward = "Fills the scales heavier than the heavens and the earth"
    ),
    ALLAHU_AKBAR(
        displayName = "Allahu Akbar",
        arabicText = "اللَّهُ أَكْبَرُ",
        defaultTarget = 34,
        spiritualReward = "The most beloved words to Allah — Allah is the Most Great"
    ),
    ASTAGHFIRULLAH(
        displayName = "Astaghfirullah",
        arabicText = "أَسْتَغْفِرُ اللَّهَ",
        defaultTarget = 100,
        spiritualReward = "Whoever seeks forgiveness, Allah opens a way out from every hardship"
    ),
    SUBHANALLAHI_WA_BIHAMDIHI(
        displayName = "SubhanAllahi wa bihamdihi",
        arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        defaultTarget = 100,
        spiritualReward = "Forgives sins even if they are as many as the foam of the sea"
    ),
    TAHLIL(
        displayName = "La ilaha illallah",
        arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
        defaultTarget = 100,
        spiritualReward = "Equal to freeing ten slaves — a shield against Shaytan all day"
    )
}
