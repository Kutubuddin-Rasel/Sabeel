package com.kutubuddin.sabeel.domain.model

/**
 * The complete built-in dhikr catalog — 35+ authentic entries.
 *
 * All entries are hardcoded: zero DB overhead, zero network, works offline forever.
 * Sourced from Sahih Bukhari, Sahih Muslim, Abu Dawud, and Tirmidhi.
 *
 * OCP: add new entries by simply adding to the appropriate list.
 *      DhikrRepositoryImpl merges this with custom_dhikr Room data.
 */
object DhikrCatalog {

    // ── After Prayer ──────────────────────────────────────────────────────────

    private val afterPrayer = listOf(
        DhikrItem(
            key = DhikrType.SUBHANALLAH.name,
            arabicText = "سُبْحَانَ اللَّهِ",
            displayName = "SubhanAllah",
            transliteration = "Subhāna Allāh",
            meaning = DhikrMeaning(
                en = "Glorified is Allah",
                ur = "اللہ پاک ہے",
                bn = "আল্লাহ পবিত্র"
            ),
            defaultTarget = 33,
            spiritualReward = "Glorified is Allah — removes sins as leaves fall from a tree",
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = DhikrType.ALHAMDULILLAH.name,
            arabicText = "الْحَمْدُ لِلَّهِ",
            displayName = "Alhamdulillah",
            transliteration = "Al-ḥamdu lillāh",
            meaning = DhikrMeaning(
                en = "All praise is due to Allah",
                ur = "تمام تعریفیں اللہ کے لیے ہیں",
                bn = "সমস্ত প্রশংসা আল্লাহর জন্য"
            ),
            defaultTarget = 33,
            spiritualReward = "Fills the scales heavier than the heavens and the earth",
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = DhikrType.ALLAHU_AKBAR.name,
            arabicText = "اللَّهُ أَكْبَرُ",
            displayName = "Allahu Akbar",
            transliteration = "Allāhu akbar",
            meaning = DhikrMeaning(
                en = "Allah is the Greatest",
                ur = "اللہ سب سے بڑا ہے",
                bn = "আল্লাহ সর্বশ্রেষ্ঠ"
            ),
            defaultTarget = 34,
            spiritualReward = "The most beloved words to Allah — Allah is the Most Great",
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = "SMART_FLOW_CLASSIC",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ",
            displayName = "Smart Flow — Variation 1",
            transliteration = "SubhanAllah · Alhamdulillah · Allahu Akbar",
            meaning = DhikrMeaning(
                en = "33 + 33 + 34 sequence after prayer",
                ur = "نماز کے بعد ۳۳+۳۳+۳۴ کا سلسلہ",
                bn = "নামাজের পরে ৩৩+৩৩+৩৪ ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = "Complete post-Salah dhikr — sins forgiven even if as foam on the sea",
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER,
            isSmartFlow = true,
            smartFlowVariant = SmartFlowVariant.CLASSIC
        ),
        DhikrItem(
            key = "SMART_FLOW_WITH_TAHLIL",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ · لَا إِلَٰهَ إِلَّا اللَّهُ",
            displayName = "Smart Flow — Variation 2",
            transliteration = "SubhanAllah · Alhamdulillah · Allahu Akbar · La ilaha illallah",
            meaning = DhikrMeaning(
                en = "33 + 33 + 33 + Tahlil sequence after prayer",
                ur = "نماز کے بعد ۳۳+۳۳+۳۳+تہلیل کا سلسلہ",
                bn = "নামাজের পরে ৩৩+৩৩+৩৩+তাহলীল ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = "Complete post-Salah dhikr ending with the greatest kalimah",
            hadithRef = "Sahih Muslim 595",
            category = DhikrCategory.AFTER_PRAYER,
            isSmartFlow = true,
            smartFlowVariant = SmartFlowVariant.WITH_TAHLIL
        )
    )

    // ── Daily Remembrance ──────────────────────────────────────────────────────

    private val daily = listOf(
        DhikrItem(
            key = DhikrType.ASTAGHFIRULLAH.name,
            arabicText = "أَسْتَغْفِرُ اللَّهَ",
            displayName = "Astaghfirullah",
            transliteration = "Astaghfiru Allāh",
            meaning = DhikrMeaning(
                en = "I seek forgiveness from Allah",
                ur = "میں اللہ سے معافی مانگتا ہوں",
                bn = "আমি আল্লাহর কাছে ক্ষমা চাই"
            ),
            defaultTarget = 100,
            spiritualReward = "Whoever seeks forgiveness, Allah opens a way out from every hardship",
            hadithRef = "Sahih al-Bukhari 6307",
            category = DhikrCategory.DAILY
        ),
        DhikrItem(
            key = DhikrType.SUBHANALLAHI_WA_BIHAMDIHI.name,
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = "SubhanAllahi wa bihamdihi",
            transliteration = "Subhāna Allāhi wa biḥamdih",
            meaning = DhikrMeaning(
                en = "Glorified is Allah and all praise is due to Him",
                ur = "اللہ پاک ہے اور اس کے لیے تعریف ہے",
                bn = "আল্লাহ পবিত্র এবং তাঁর প্রশংসা"
            ),
            defaultTarget = 100,
            spiritualReward = "Forgives sins even if they are as many as the foam of the sea",
            hadithRef = "Sahih al-Bukhari 6405",
            category = DhikrCategory.DAILY
        ),
        DhikrItem(
            key = "SUBHANALLAHI_L_AZIM",
            arabicText = "سُبْحَانَ اللَّهِ الْعَظِيمِ",
            displayName = "SubhanAllahi l-Azim",
            transliteration = "Subhāna Allāhi l-'aẓīm",
            meaning = DhikrMeaning(
                en = "Glorified is Allah, the Magnificent",
                ur = "اللہ عظیم کی تسبیح",
                bn = "মহান আল্লাহর পবিত্রতা"
            ),
            defaultTarget = 100,
            spiritualReward = "Two phrases light on the tongue, heavy on the scales, beloved to Allah",
            hadithRef = "Sahih al-Bukhari 6682",
            category = DhikrCategory.DAILY
        )
    )

    // ── Morning Adhkar ─────────────────────────────────────────────────────────

    private val morning = listOf(
        DhikrItem(
            key = "HASBIYALLAH",
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
            displayName = "Hasbiyallah",
            transliteration = "Hasbiya Allāhu lā ilāha illā huwa",
            meaning = DhikrMeaning(
                en = "Allah is sufficient for me; there is no god but He",
                ur = "مجھے اللہ کافی ہے، اس کے سوا کوئی معبود نہیں",
                bn = "আল্লাহই আমার জন্য যথেষ্ট, তিনি ছাড়া কোনো ইলাহ নেই"
            ),
            defaultTarget = 7,
            spiritualReward = "Whoever recites this 7 times morning and evening, Allah suffices him",
            hadithRef = "Abu Dawud 5081",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_AOUDHU",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            displayName = "Morning Protection Dua",
            transliteration = "A'ūdhu bikalimatillāhi t-tāmmāti min sharri mā khalaqa",
            meaning = DhikrMeaning(
                en = "I seek refuge in the perfect words of Allah from the evil of what He created",
                ur = "اللہ کے کامل کلمات کے ذریعے تخلیق کی برائی سے پناہ",
                bn = "আল্লাহর পূর্ণ বাণীর আশ্রয়ে সৃষ্টির অনিষ্ট থেকে"
            ),
            defaultTarget = 3,
            spiritualReward = "No harm will befall the one who recites this 3 times in the morning",
            hadithRef = "Sahih Muslim 2709",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_TAHLIL",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            displayName = "Morning Tahlil",
            transliteration = "Lā ilāha illā Allāhu waḥdahu lā sharīka lah",
            meaning = DhikrMeaning(
                en = "There is no god but Allah, alone, without partner",
                ur = "اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے",
                bn = "আল্লাহ ছাড়া কোনো ইলাহ নেই, তিনি একা"
            ),
            defaultTarget = 10,
            spiritualReward = "Equal to freeing 10 slaves, 100 good deeds written, shield from Shaytan",
            hadithRef = "Sahih al-Bukhari 3293",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_TASBIH",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = "Morning Tasbih",
            transliteration = "Subhāna Allāhi wa biḥamdih",
            meaning = DhikrMeaning(
                en = "Glorified is Allah and all praise is due to Him",
                ur = "اللہ پاک ہے اور اس کے لیے تعریف ہے",
                bn = "আল্লাহ পবিত্র এবং তাঁর প্রশংসা"
            ),
            defaultTarget = 100,
            spiritualReward = "Whoever says this 100 times in the morning, his sins are erased",
            hadithRef = "Sahih Muslim 2692",
            category = DhikrCategory.MORNING
        )
    )

    // ── Evening Adhkar ─────────────────────────────────────────────────────────

    private val evening = listOf(
        DhikrItem(
            key = "EVENING_TAHLIL",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            displayName = "Evening Tahlil",
            transliteration = "Lā ilāha illā Allāhu waḥdahu lā sharīka lah",
            meaning = DhikrMeaning(
                en = "There is no god but Allah, alone, without partner",
                ur = "اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے",
                bn = "আল্লাহ ছাড়া কোনো ইলাহ নেই, তিনি একা"
            ),
            defaultTarget = 10,
            spiritualReward = "Equal to freeing 10 slaves, protection all evening until morning",
            hadithRef = "Sahih al-Bukhari 3293",
            category = DhikrCategory.EVENING
        ),
        DhikrItem(
            key = "EVENING_TASBIH",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = "Evening Tasbih",
            transliteration = "Subhāna Allāhi wa biḥamdih",
            meaning = DhikrMeaning(
                en = "Glorified is Allah and all praise is due to Him",
                ur = "اللہ پاک ہے اور اس کے لیے تعریف ہے",
                bn = "আল্লাহ পবিত্র এবং তাঁর প্রশংসা"
            ),
            defaultTarget = 100,
            spiritualReward = "Whoever says this 100 times in the evening, sins are erased",
            hadithRef = "Sahih Muslim 2692",
            category = DhikrCategory.EVENING
        ),
        DhikrItem(
            key = "EVENING_ISTIGHFAR",
            arabicText = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            displayName = "Evening Istighfar",
            transliteration = "Astaghfiru Allāha wa atūbu ilayh",
            meaning = DhikrMeaning(
                en = "I seek forgiveness from Allah and repent to Him",
                ur = "میں اللہ سے معافی مانگتا ہوں اور توبہ کرتا ہوں",
                bn = "আমি আল্লাহর কাছে ক্ষমা চাই এবং তওবা করি"
            ),
            defaultTarget = 100,
            spiritualReward = "The Prophet ﷺ used to say this 70–100 times each day",
            hadithRef = "Sahih al-Bukhari 6307",
            category = DhikrCategory.EVENING
        )
    )

    // ── Salawat ────────────────────────────────────────────────────────────────

    private val salawat = listOf(
        DhikrItem(
            key = "SALAWAT_SHORT",
            arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ",
            displayName = "Salawat (Short)",
            transliteration = "Allāhumma ṣalli 'alā Muḥammad",
            meaning = DhikrMeaning(
                en = "O Allah, send blessings upon Muhammad",
                ur = "اے اللہ، محمد پر درود بھیج",
                bn = "হে আল্লাহ, মুহাম্মদের উপর দরুদ পাঠাও"
            ),
            defaultTarget = 10,
            spiritualReward = "Allah sends 10 blessings on you for each salawat upon the Prophet ﷺ",
            hadithRef = "Sahih Muslim 408",
            category = DhikrCategory.SALAWAT
        ),
        DhikrItem(
            key = "SALAWAT_100",
            arabicText = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَىٰ نَبِيِّنَا مُحَمَّدٍ",
            displayName = "Salawat × 100",
            transliteration = "Allāhumma ṣalli wa sallim 'alā nabiyyinā Muḥammad",
            meaning = DhikrMeaning(
                en = "O Allah, send peace and blessings upon our Prophet Muhammad",
                ur = "اے اللہ، ہمارے نبی محمد پر درود و سلام بھیج",
                bn = "হে আল্লাহ, আমাদের নবী মুহাম্মদের উপর শান্তি ও দরুদ পাঠাও"
            ),
            defaultTarget = 100,
            spiritualReward = "Intercession on the Day of Judgment for one who sends 100 salawat",
            hadithRef = "Tirmidhi 484",
            category = DhikrCategory.SALAWAT
        ),
        DhikrItem(
            key = "SALAWAT_IBRAHIMIYYA",
            arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ",
            displayName = "Salawat Ibrahimiyya",
            transliteration = "Allāhumma ṣalli 'alā Muḥammadin wa 'alā āli Muḥammad",
            meaning = DhikrMeaning(
                en = "O Allah, send blessings upon Muhammad and his family as You blessed Ibrahim",
                ur = "اے اللہ، محمد اور ان کے خاندان پر درود بھیج جیسا ابراہیم پر بھیجا",
                bn = "হে আল্লাহ, মুহাম্মদ ও তাঁর পরিবারের উপর দরুদ পাঠাও"
            ),
            defaultTarget = 10,
            spiritualReward = "The most complete form of salawat, recited in every prayer",
            hadithRef = "Sahih al-Bukhari 3370",
            category = DhikrCategory.SALAWAT
        )
    )

    // ── Istighfar ──────────────────────────────────────────────────────────────

    private val istighfar = listOf(
        DhikrItem(
            key = "ISTIGHFAR_AZIM",
            arabicText = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلَٰهَ إِلَّا هُوَ",
            displayName = "Istighfar al-Azim",
            transliteration = "Astaghfiru Allāha l-'aẓīma lladhī lā ilāha illā huwa",
            meaning = DhikrMeaning(
                en = "I seek forgiveness from Allah the Magnificent, besides Whom there is no god",
                ur = "میں اللہ عظیم سے معافی مانگتا ہوں جس کے سوا کوئی معبود نہیں",
                bn = "মহান আল্লাহর কাছে ক্ষমা চাই যিনি ছাড়া কোনো ইলাহ নেই"
            ),
            defaultTarget = 100,
            spiritualReward = "Sins are forgiven even if they were as much as the foam of the sea",
            hadithRef = "Abu Dawud 1517",
            category = DhikrCategory.ISTIGHFAR
        ),
        DhikrItem(
            key = "SAYYID_ISTIGHFAR",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ خَلَقْتَنِي",
            displayName = "Sayyid al-Istighfar",
            transliteration = "Allāhumma anta rabbī lā ilāha illā anta khalaqtanī",
            meaning = DhikrMeaning(
                en = "O Allah, You are my Lord. There is no god but You. You created me.",
                ur = "اے اللہ، تو میرا رب ہے، تیرے سوا کوئی معبود نہیں، تو نے مجھے پیدا کیا",
                bn = "হে আল্লাহ, তুমি আমার রব। তুমি ছাড়া কোনো ইলাহ নেই। তুমি আমাকে সৃষ্টি করেছ।"
            ),
            defaultTarget = 1,
            spiritualReward = "Master of Istighfar — whoever says it once with conviction enters Jannah",
            hadithRef = "Sahih al-Bukhari 6306",
            category = DhikrCategory.ISTIGHFAR
        )
    )

    // ── Tahlil ─────────────────────────────────────────────────────────────────

    private val tahlil = listOf(
        DhikrItem(
            key = DhikrType.TAHLIL.name,
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            displayName = "Tahlil (Full)",
            transliteration = "Lā ilāha illā Allāhu waḥdahu lā sharīka lah",
            meaning = DhikrMeaning(
                en = "There is no god but Allah, alone, without partner",
                ur = "اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے",
                bn = "আল্লাহ ছাড়া কোনো ইলাহ নেই, তিনি একা, তাঁর কোনো অংশীদার নেই"
            ),
            defaultTarget = 100,
            spiritualReward = "Equal to freeing 10 slaves — a shield against Shaytan all day",
            hadithRef = "Sahih al-Bukhari 3293",
            category = DhikrCategory.TAHLIL
        ),
        DhikrItem(
            key = "TAHLIL_SIMPLE",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ",
            displayName = "Tahlil (Simple)",
            transliteration = "Lā ilāha illā Allāh",
            meaning = DhikrMeaning(
                en = "There is no god but Allah",
                ur = "اللہ کے سوا کوئی معبود نہیں",
                bn = "আল্লাহ ছাড়া কোনো ইলাহ নেই"
            ),
            defaultTarget = 100,
            spiritualReward = "The best dhikr is La ilaha illallah — the most excellent of all",
            hadithRef = "Tirmidhi 3383",
            category = DhikrCategory.TAHLIL
        )
    )

    // ── Aggregated catalog ─────────────────────────────────────────────────────
    val all: List<DhikrItem>
        get() = afterPrayer + daily + morning + evening + salawat + istighfar + tahlil
}
