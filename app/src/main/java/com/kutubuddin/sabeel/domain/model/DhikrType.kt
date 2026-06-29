package com.kutubuddin.sabeel.domain.model

enum class DhikrType(
    val displayName: String,
    val arabicText: String,
    val defaultTarget: Int
) {
    SUBHANALLAH("SubhanAllah", "سُبْحَانَ اللَّهِ", 33),
    ALHAMDULILLAH("Alhamdulillah", "الْحَمْدُ لِلَّهِ", 33),
    ALLAHU_AKBAR("Allahu Akbar", "اللَّهُ أَكْبَرُ", 34),
    ASTAGHFIRULLAH("Astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", 100),
    SUBHANALLAHI_WA_BIHAMDIHI("SubhanAllahi wa bihamdihi", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", 100),
    TAHLIL("La ilaha illallah", "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", 100)
}
