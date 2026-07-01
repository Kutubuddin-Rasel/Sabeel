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
            spiritualReward = LocalizedText(
                en = "Removes sins as leaves fall from a tree",
                ur = "درخت سے پتے گرنے کی طرح گناہ جھڑ جاتے ہیں",
                bn = "গাছ থেকে পাতা ঝরার মতো গুনাহ ঝরে পড়ে"
            ),
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
            spiritualReward = LocalizedText(
                en = "Fills the scales heavier than the heavens and the earth",
                ur = "ترازو کو آسمانوں اور زمین سے زیادہ بھر دیتا ہے",
                bn = "আসমান-জমিনের চেয়ে ভারী করে দাঁড়িপাল্লা পূর্ণ করে"
            ),
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
            spiritualReward = LocalizedText(
                en = "The most beloved words to Allah — Allah is the Most Great",
                ur = "اللہ کو سب سے محبوب کلمات — اللہ سب سے بڑا ہے",
                bn = "আল্লাহর কাছে সর্বাধিক প্রিয় বাক্য — আল্লাহ সর্বশ্রেষ্ঠ"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = "SMART_FLOW_CLASSIC",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ",
            displayName = "Tasbīḥ after Salah · Classic",
            transliteration = "SubhanAllah · Alhamdulillah · Allahu Akbar",
            meaning = DhikrMeaning(
                en = "33 + 33 + 34 sequence after prayer",
                ur = "نماز کے بعد ۳۳+۳۳+۳۴ کا سلسلہ",
                bn = "নামাজের পরে ৩৩+৩৩+৩৪ ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Complete post-Salah dhikr — sins forgiven even if as foam on the sea",
                ur = "مکمل بعد از نماز ذکر — گناہ سمندر کی جھاگ کے برابر بھی ہوں تو معاف",
                bn = "পূর্ণ নামাজ-পরবর্তী জিকির — সমুদ্রের ফেনা সমান গুনাহও ক্ষমা"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER,
            isSmartFlow = true,
            smartFlowVariant = SmartFlowVariant.CLASSIC
        ),
        DhikrItem(
            key = "SMART_FLOW_WITH_TAHLIL",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ · لَا إِلَٰهَ إِلَّا اللَّهُ",
            displayName = "Tasbīḥ after Salah · With Tahlīl",
            transliteration = "SubhanAllah · Alhamdulillah · Allahu Akbar · La ilaha illallah",
            meaning = DhikrMeaning(
                en = "33 + 33 + 33 + Tahlil sequence after prayer",
                ur = "نماز کے بعد ۳۳+۳۳+۳۳+تہلیل کا سلسلہ",
                bn = "নামাজের পরে ৩৩+৩৩+৩৩+তাহলীল ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Complete post-Salah dhikr ending with the greatest kalimah",
                ur = "سب سے بڑے کلمے پر ختم ہونے والا مکمل بعد از نماز ذکر",
                bn = "সর্বশ্রেষ্ঠ কালিমা দিয়ে সমাপ্ত পূর্ণ নামাজ-পরবর্তী জিকির"
            ),
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
            spiritualReward = LocalizedText(
                en = "Whoever seeks forgiveness, Allah opens a way out from every hardship",
                ur = "جو استغفار کرے، اللہ ہر مشکل سے نکلنے کا راستہ بنا دیتا ہے",
                bn = "যে ক্ষমা চায়, আল্লাহ তার প্রতিটি কষ্ট থেকে উত্তরণের পথ খুলে দেন"
            ),
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
            spiritualReward = LocalizedText(
                en = "Forgives sins even if they are as many as the foam of the sea",
                ur = "گناہ سمندر کی جھاگ کے برابر بھی ہوں تو معاف ہو جاتے ہیں",
                bn = "সমুদ্রের ফেনা সমান গুনাহও ক্ষমা হয়ে যায়"
            ),
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
            spiritualReward = LocalizedText(
                en = "Two phrases light on the tongue, heavy on the scales, beloved to Allah",
                ur = "زبان پر ہلکے، ترازو میں بھاری، اللہ کو محبوب دو کلمے",
                bn = "জিভে হালকা, পাল্লায় ভারী, আল্লাহর প্রিয় দুই বাক্য"
            ),
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
            spiritualReward = LocalizedText(
                en = "Whoever recites this 7 times morning and evening, Allah suffices him",
                ur = "جو صبح و شام سات بار پڑھے، اللہ اسے کافی ہو جاتا ہے",
                bn = "যে সকাল-সন্ধ্যায় সাতবার পড়ে, আল্লাহ তার জন্য যথেষ্ট"
            ),
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
            spiritualReward = LocalizedText(
                en = "No harm will befall the one who recites this 3 times in the morning",
                ur = "جو صبح تین بار پڑھے اسے کوئی نقصان نہیں پہنچے گا",
                bn = "যে সকালে তিনবার পড়ে তার কোনো ক্ষতি হবে না"
            ),
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
            spiritualReward = LocalizedText(
                en = "Equal to freeing 10 slaves, 100 good deeds written, shield from Shaytan",
                ur = "دس غلام آزاد کرنے کے برابر، سو نیکیاں، شیطان سے حفاظت",
                bn = "দশ দাস মুক্তির সমান, একশ নেকি, শয়তান থেকে সুরক্ষা"
            ),
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
            spiritualReward = LocalizedText(
                en = "Whoever says this 100 times in the morning, his sins are erased",
                ur = "جو صبح سو بار کہے، اس کے گناہ مٹا دیے جاتے ہیں",
                bn = "যে সকালে একশবার বলে, তার গুনাহ মুছে দেওয়া হয়"
            ),
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
            spiritualReward = LocalizedText(
                en = "Equal to freeing 10 slaves, protection all evening until morning",
                ur = "دس غلام آزاد کرنے کے برابر، صبح تک شام بھر حفاظت",
                bn = "দশ দাস মুক্তির সমান, সকাল পর্যন্ত সারা সন্ধ্যা সুরক্ষা"
            ),
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
            spiritualReward = LocalizedText(
                en = "Whoever says this 100 times in the evening, sins are erased",
                ur = "جو شام سو بار کہے، گناہ مٹا دیے جاتے ہیں",
                bn = "যে সন্ধ্যায় একশবার বলে, গুনাহ মুছে দেওয়া হয়"
            ),
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
            spiritualReward = LocalizedText(
                en = "The Prophet ﷺ used to say this 70–100 times each day",
                ur = "نبی ﷺ ہر دن ستر سے سو بار یہ کہا کرتے تھے",
                bn = "নবী ﷺ প্রতিদিন সত্তর থেকে একশবার এটি বলতেন"
            ),
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
            spiritualReward = LocalizedText(
                en = "Allah sends 10 blessings on you for each salawat upon the Prophet ﷺ",
                ur = "نبی ﷺ پر ہر درود پر اللہ آپ پر دس رحمتیں بھیجتا ہے",
                bn = "নবী ﷺ-এর উপর প্রতিটি দরুদে আল্লাহ আপনার উপর দশটি রহমত পাঠান"
            ),
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
            spiritualReward = LocalizedText(
                en = "Intercession on the Day of Judgment for one who sends 100 salawat",
                ur = "جو سو درود بھیجے اس کے لیے قیامت کے دن شفاعت",
                bn = "যে একশ দরুদ পাঠায় তার জন্য কিয়ামতের দিন সুপারিশ"
            ),
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
            spiritualReward = LocalizedText(
                en = "The most complete form of salawat, recited in every prayer",
                ur = "درود کی سب سے مکمل صورت، ہر نماز میں پڑھی جاتی ہے",
                bn = "দরুদের সর্বাধিক পূর্ণ রূপ, প্রতি নামাজে পঠিত"
            ),
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
            spiritualReward = LocalizedText(
                en = "Sins are forgiven even if they were as much as the foam of the sea",
                ur = "گناہ سمندر کی جھاگ کے برابر بھی ہوں تو معاف",
                bn = "সমুদ্রের ফেনা সমান গুনাহও ক্ষমা"
            ),
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
            spiritualReward = LocalizedText(
                en = "Master of Istighfar — whoever says it once with conviction enters Jannah",
                ur = "سید الاستغفار — جو یقین کے ساتھ ایک بار کہے جنت میں داخل",
                bn = "সাইয়িদুল ইস্তিগফার — যে দৃঢ় বিশ্বাসে একবার বলে জান্নাতে প্রবেশ করে"
            ),
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
            spiritualReward = LocalizedText(
                en = "Equal to freeing 10 slaves — a shield against Shaytan all day",
                ur = "دس غلام آزاد کرنے کے برابر — دن بھر شیطان سے ڈھال",
                bn = "দশ দাস মুক্তির সমান — সারাদিন শয়তান থেকে ঢাল"
            ),
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
            spiritualReward = LocalizedText(
                en = "The best dhikr is La ilaha illallah — the most excellent of all",
                ur = "بہترین ذکر لا الہ الا اللہ ہے — سب سے افضل",
                bn = "সর্বোত্তম জিকির লা ইলাহা ইল্লাল্লাহ — সবচেয়ে শ্রেষ্ঠ"
            ),
            hadithRef = "Tirmidhi 3383",
            category = DhikrCategory.TAHLIL
        )
    )

    // ── Aggregated catalog ─────────────────────────────────────────────────────
    val all: List<DhikrItem>
        get() = afterPrayer + daily + morning + evening + salawat + istighfar + tahlil

    /** Fast key → item lookup, built once. */
    private val byKey: Map<String, DhikrItem> by lazy { all.associateBy { it.key } }

    // ── Post-Salah sequences ───────────────────────────────────────────────────
    // Built from the same phrases used elsewhere in the catalog so there is one
    // source of truth for each Arabic string.

    private val stepSubhan = DhikrStep("سُبْحَانَ اللَّهِ", "SubhanAllah", "Subhāna Allāh", 33)
    private val stepHamd = DhikrStep("الْحَمْدُ لِلَّهِ", "Alhamdulillah", "Al-ḥamdu lillāh", 33)
    private val stepAkbar34 = DhikrStep("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allāhu akbar", 34)
    private val stepAkbar33 = stepAkbar34.copy(target = 33)
    private val stepTahlil = DhikrStep("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "Lā ilāha illā Allāh", 1)

    /**
     * Human-readable name for a stored key, or the raw key if it isn't a built-in
     * entry (e.g. a custom dhikr). Used for labelling saved sessions.
     */
    fun displayNameFor(key: String): String = byKey[key]?.displayName ?: key

    /** Returns the multi-step sequence for a post-Salah entry key, or null. */
    fun sequenceFor(key: String): DhikrSequence? = when (key) {
        "SMART_FLOW_CLASSIC" -> DhikrSequence(
            key, "Tasbīḥ after Salah · Classic",
            listOf(stepSubhan, stepHamd, stepAkbar34)
        )
        "SMART_FLOW_WITH_TAHLIL" -> DhikrSequence(
            key, "Tasbīḥ after Salah · With Tahlīl",
            listOf(stepSubhan, stepHamd, stepAkbar33, stepTahlil)
        )
        else -> null
    }

    /**
     * Resolves a stored String key into a countable [ActiveDhikr].
     *
     * Falls back to SubhanAllah when the key is unknown (e.g. a custom dhikr the
     * built-in catalog can't see, or legacy data) so the Count screen always has
     * a valid dhikr to show.
     */
    fun resolve(key: String): ActiveDhikr {
        val item = byKey[key] ?: byKey.getValue(DhikrType.SUBHANALLAH.name)
        return ActiveDhikr(
            key = item.key,
            arabicText = item.arabicText,
            displayName = item.displayName,
            target = item.defaultTarget,
            spiritualReward = item.spiritualReward,
            hadithRef = item.hadithRef,
            sequenceKey = if (item.isSmartFlow) item.key else null
        )
    }
}
