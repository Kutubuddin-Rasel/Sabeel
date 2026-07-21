package com.kutubuddin.sabeel.domain.model

/**
 * All supported dhikr types with their display metadata and theological context.
 *
 * spiritualReward: Sahih-sourced benefit text displayed on the counting screen.
 * This is static, offline data — no network dependency.
 *
 * LEGACY NOTE: per ActiveDhikr's doc comment, the counting screen no longer
 * hard-wires to this enum — it resolves ActiveDhikr from DhikrCatalog by
 * String key instead, and DhikrCatalog.kt only reads `DhikrType.X.name` from
 * here as a stable key constant. DhikrCatalog.kt is the source of truth for
 * displayName/arabicText/defaultTarget/spiritualReward; the values below are
 * kept in sync with it so nothing contradicts the catalog if this enum's
 * other fields are ever read again.
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
        spiritualReward = "Part of the after-prayer dhikr the Prophet ﷺ promised will never leave the one who recites it disappointed"
    ),
    ALHAMDULILLAH(
        displayName = "Alhamdulillah",
        arabicText = "الْحَمْدُ لِلَّهِ",
        defaultTarget = 33,
        spiritualReward = "Fills the Scale on the Day of Judgment to overflowing"
    ),
    ALLAHU_AKBAR(
        displayName = "Allahu Akbar",
        arabicText = "اللَّهُ أَكْبَرُ",
        defaultTarget = 34,
        spiritualReward = "Among the four words most beloved to Allah"
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
