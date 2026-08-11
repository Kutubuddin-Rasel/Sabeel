package com.kitalonlabs.sabeel.domain.model

import kotlinx.collections.immutable.persistentListOf

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
            displayName = LocalizedText(en = "SubhanAllah", bn = "সুবহানাল্লাহ"),
            transliteration = LocalizedText(en = "Subhāna Allāh", bn = "সুবহ'া:নাল্লা:হ"),
            meaning = DhikrMeaning(
                en = "Allah is free from all imperfections",
                bn = "আল্লাহ সকল ত্রুটি ও অসম্পূর্ণতা থেকে মুক্ত"
            ),
            defaultTarget = 33,
            spiritualReward = LocalizedText(
                en = "Part of the after-prayer dhikr the Prophet ﷺ promised will never leave the one who recites it disappointed",
                bn = "নামাযের পরের এই যিকিরের অংশ, যা সম্পর্কে নবী ﷺ প্রতিশ্রুতি দিয়েছেন যে পাঠকারী কখনো নিরাশ হবে না"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = DhikrType.ALHAMDULILLAH.name,
            arabicText = "الْحَمْدُ لِلَّهِ",
            displayName = LocalizedText(en = "Alhamdulillah", bn = "আলহামদুলিল্লাহ"),
            transliteration = LocalizedText(en = "Al-ḥamdu lillāh", bn = "আলহ'ামদুলিল্লা:হ"),
            meaning = DhikrMeaning(
                en = "All absolute praise and gratitude belong solely to Allah",
                bn = "সকল নিখুঁত প্রশংসা এবং কৃতজ্ঞতা কেবল আল্লাহর জন্য"
            ),
            defaultTarget = 33,
            spiritualReward = LocalizedText(
                en = "Fills the Scale on the Day of Judgment to overflowing",
                bn = "কিয়ামতের দিন আমলের পাল্লা কানায় কানায় পূর্ণ করে"
            ),
            hadithRef = "Sahih Muslim 223",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = DhikrType.ALLAHU_AKBAR.name,
            arabicText = "اللَّهُ أَكْبَرُ",
            displayName = LocalizedText(en = "Allahu Akbar", bn = "আল্লাহু আকবার"),
            transliteration = LocalizedText(en = "Allāhu akbar", bn = "আল্লা:হু আকবার"),
            meaning = DhikrMeaning(
                en = "Allah is infinitely greater than anything else we can comprehend",
                bn = "আল্লাহ আমাদের উপলব্ধির চেয়েও অসীম মহান"
            ),
            defaultTarget = 34,
            spiritualReward = LocalizedText(
                en = "Among the four words most beloved to Allah",
                bn = "আল্লাহর নিকট সর্বাধিক প্রিয় চারটি বাক্যের অন্যতম"
            ),
            hadithRef = "Sahih Muslim 2137",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = "SMART_FLOW_CLASSIC",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ",
            displayName = LocalizedText(en = "Tasbih after Salah", bn = "নামাজের পরে তাসবিহ"),
            transliteration = LocalizedText(en = "SubhanAllah · Alhamdulillah · Allahu Akbar", bn = "সুবহ'া:নাল্লা:হ · আলহ'ামদুলিল্লা:হ · আল্লা:হু আকবার"),
            meaning = DhikrMeaning(
                en = "The foundational 33 + 33 + 34 sequence after obligatory prayer",
                bn = "ফরজ নামাজের পরে মৌলিক ৩৩+৩৩+৩৪ ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "The Prophet ﷺ promised this dhikr will never leave the one who recites it after every prayer disappointed",
                bn = "নবী ﷺ প্রতিশ্রুতি দিয়েছেন, প্রতি নামাযের পর এই যিকির পাঠকারী কখনো নিরাশ হবে না"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER,
            isSmartFlow = true,
            smartFlowVariant = SmartFlowVariant.CLASSIC
        ),
        DhikrItem(
            key = "SMART_FLOW_WITH_TAHLIL",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ · لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            displayName = LocalizedText(en = "Tasbih after Salah", bn = "নামাজের পরে তাসবিহ"),
            transliteration = LocalizedText(en = "SubhanAllah · Alhamdulillah · Allahu Akbar · Lā ilāha illallāhu waḥdahu lā sharīka lahu, lahul-mulku wa lahul-ḥamdu, wa huwa 'alā kulli shay'in qadīr", bn = "সুবহ'া:নাল্লা:হ · আলহ'ামদুলিল্লা:হ · আল্লা:হু আকবার · লা: ইলা:হা ইল্লাল্লা:হু ওয়াহ'দাহু লা: শারীকা লাহু, লাহুল মুলকু ওয়া লাহুল হ'ামদু, ওয়া হুয়া 'আলা: কুল্লি শাইয়িন ক্বাদীর"),
            meaning = DhikrMeaning(
                en = "33 + 33 + 33 sequence capped with the statement of pure monotheism",
                bn = "খাঁটি একত্ববাদের ঘোষণা দিয়ে সমাপ্ত ৩৩+৩৩+৩৩ ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Sins are forgiven, even if they are as much as the foam of the sea",
                bn = "গুনাহ ক্ষমা করা হয়, এমনকি তা সমুদ্রের ফেনার মত হলেও"
            ),
            hadithRef = "Sahih Muslim 597",
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
            displayName = LocalizedText(en = "Astaghfirullah", bn = "আস্তাগফিরুল্লাহ"),
            transliteration = LocalizedText(en = "Astaghfiru Allāh", bn = "আস্তাগফিরুল্লা:হ"),
            meaning = DhikrMeaning(
                en = "I seek His forgiveness, acknowledging my weakness and His absolute mercy",
                bn = "আমি আমার দুর্বলতা স্বীকার করে তাঁর অসীম ক্ষমার আশ্রয় চাই"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "A constant shield: Allah provides a way out of every anxiety and hardship",
                bn = "একটি ধ্রুবক ঢাল: আল্লাহ প্রতিটি উদ্বেগ ও কষ্ট থেকে উত্তরণের পথ দেন"
            ),
            hadithRef = "Sunan Abi Dawud 1518",
            category = DhikrCategory.DAILY
        ),
        DhikrItem(
            key = DhikrType.SUBHANALLAHI_WA_BIHAMDIHI.name,
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = LocalizedText(en = "SubhanAllahi wa bihamdihi", bn = "সুবহানাল্লাহি ওয়া বিহামদিহি"),
            transliteration = LocalizedText(en = "Subhāna Allāhi wa biḥamdih", bn = "সুবহ'া:নাল্লা:হি ওয়া বিহ'ামদিহি"),
            meaning = DhikrMeaning(
                en = "Flawless is Allah, and wrapped in gratitude is His praise",
                bn = "আল্লাহ নিখুঁত এবং তাঁর প্রশংসাই কৃতজ্ঞতায় মোড়ানো"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Reciting this 100 times wipes away minor sins completely",
                bn = "এটি ১০০ বার পড়লে সগিরা গুনাহ সম্পূর্ণ মুছে যায়"
            ),
            hadithRef = "Sahih al-Bukhari 6405",
            category = DhikrCategory.DAILY
        ),
        DhikrItem(
            key = "SUBHANALLAHI_L_AZIM",
            arabicText = "سُبْحَانَ اللَّهِ الْعَظِيمِ",
            displayName = LocalizedText(en = "SubhanAllahi l-Azim", bn = "সুবহানাল্লাহিল আজিম"),
            transliteration = LocalizedText(en = "Subhāna Allāhi l-'aẓīm", bn = "সুবহ'া:নাল্লা:হিল 'আজ'ীম"),
            meaning = DhikrMeaning(
                en = "Exalted is Allah, the Most Magnificent and Supreme",
                bn = "মহান আল্লাহ পবিত্র, যিনি সর্বোচ্চ মর্যাদাবান"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Incredibly light on the tongue, unimaginably heavy on the Scale of deeds",
                bn = "জিহ্বায় অবিশ্বাস্যভাবে হালকা, আমলের পাল্লায় অকল্পনীয় ভারী"
            ),
            hadithRef = "Sahih al-Bukhari 6682",
            category = DhikrCategory.DAILY
        )
    )

    // ── Morning Adhkar ─────────────────────────────────────────────────────────

    private val morning = listOf(
        DhikrItem(
            key = "HASBIYALLAH",
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            displayName = LocalizedText(en = "Hasbiyallah", bn = "হাসবিআল্লাহ"),
            transliteration = LocalizedText(en = "Ḥasbiya Allāhu lā ilāha illā huwa 'alayhi tawakkaltu wa huwa rabbul 'arshil 'aẓīm", bn = "হ'াসবিয়াল্লা:হু লা: ইলা:হা ইল্লা হুয়া 'আলাইহি তাওয়াক্কালতু ওয়া হুয়া রাব্বুল 'আরশিল 'আজ'ীম"),
            meaning = DhikrMeaning(
                en = "Allah is sufficient for me; there is no deity except Him. On Him I have relied, and He is the Lord of the Great Throne",
                bn = "আল্লাহই আমার জন্য যথেষ্ট; তিনি ছাড়া কোনো উপাস্য নেই। আমি তাঁরই ওপর ভরসা করেছি, এবং তিনি মহান আরশের অধিপতি"
            ),
            defaultTarget = 7,
            spiritualReward = LocalizedText(
                en = "A promise of divine care: Allah will suffice you in whatever matters you",
                bn = "ঐশী যত্নের প্রতিশ্রুতি: আপনার সকল বিষয়ে আল্লাহই যথেষ্ট হবেন"
            ),
            hadithRef = "Abu Dawud 5081",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_AOUDHU",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            displayName = LocalizedText(en = "Morning Protection Dua", bn = "সকালের সুরক্ষার দোয়া"),
            transliteration = LocalizedText(en = "A'ūdhu bikalimatillāhi t-tāmmāti min sharri mā khalaqa", bn = "আ'ঊয়'ু বিকালিমা:তিল্লা:হিত তাম্মা:তি মিন শাররি মা: খালাক"),
            meaning = DhikrMeaning(
                en = "I take absolute refuge in the perfect words of Allah from the evil of His creation",
                bn = "আমি আল্লাহর পূর্ণ বাণীর আশ্রয়ে তাঁর সৃষ্টির অনিষ্ট থেকে সুরক্ষা চাই"
            ),
            defaultTarget = 3,
            spiritualReward = LocalizedText(
                en = "Forms an impenetrable shield of protection around you until evening",
                bn = "সন্ধ্যা পর্যন্ত আপনার চারপাশে একটি দুর্ভেদ্য সুরক্ষার ঢাল তৈরি করে"
            ),
            hadithRef = "Sahih Muslim 2709",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_TAHLIL",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            displayName = LocalizedText(en = "Morning Tahlil", bn = "সকালের তাহলীল"),
            transliteration = LocalizedText(en = "Lā ilāha illallāhu waḥdahu lā sharīka lahu, lahul-mulku wa lahul-ḥamdu, wa huwa 'alā kulli shay'in qadīr", bn = "লা: ইলা:হা ইল্লাল্লা:হু ওয়াহ'দাহু লা: শারীকা লাহু, লাহুল মুলকু ওয়া লাহুল হ'ামদু, ওয়া হুয়া 'আলা: কুল্লি শাইয়িন ক্বাদীর"),
            meaning = DhikrMeaning(
                en = "There is no true deity but Allah alone, Who has no partner; His is the dominion and His is the praise, and He is Able to do all things",
                bn = "আল্লাহ ছাড়া কোনো উপাস্য নেই, তিনি এক, তাঁর কোনো শরীক নেই; রাজত্ব তাঁরই, এবং সমস্ত প্রশংসাও তাঁর; এবং তিনি সবকিছুর ওপর ক্ষমতাবান"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "Equivalent to freeing four souls from the descendants of Isma'il",
                bn = "ইসমাইলের (আঃ) বংশধরদের মধ্য থেকে চারজন দাস মুক্ত করার সমতুল্য সওয়াব"
            ),
            hadithRef = "Sunan Abi Dawud 5077",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_TASBIH",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = LocalizedText(en = "Morning Tasbih", bn = "সকালের তাসবিহ"),
            transliteration = LocalizedText(en = "Subhāna Allāhi wa biḥamdih", bn = "সুবহানাল্লাহি ওয়া বিহামদিহি"),
            meaning = DhikrMeaning(
                en = "How perfect is Allah and I praise Him for His perfection",
                bn = "আল্লাহ কতই না নিখুঁত এবং আমি তাঁর পূর্ণতার প্রশংসা করি"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Ensures no one comes on the Day of Resurrection with better deeds than you",
                bn = "কিয়ামতের দিন আপনার চেয়ে ভালো আমল নিয়ে কেউ আসবে না, তা নিশ্চিত করে"
            ),
            hadithRef = "Sahih Muslim 2692",
            category = DhikrCategory.MORNING
        )
    )

    // ── Evening Adhkar ─────────────────────────────────────────────────────────

    private val evening = listOf(
        DhikrItem(
            key = "EVENING_TAHLIL",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            displayName = LocalizedText(en = "Evening Tahlil", bn = "সন্ধ্যার তাহলীল"),
            transliteration = LocalizedText(en = "Lā ilāha illallāhu waḥdahu lā sharīka lahu, lahul-mulku wa lahul-ḥamdu, wa huwa 'alā kulli shay'in qadīr", bn = "লা: ইলা:হা ইল্লাল্লা:হু ওয়াহ'দাহু লা: শারীকা লাহু, লাহুল মুলকু ওয়া লাহুল হ'ামদু, ওয়া হুয়া 'আলা: কুল্লি শাইয়িন ক্বাদীর"),
            meaning = DhikrMeaning(
                en = "There is no true deity but Allah alone, Who has no partner; His is the dominion and His is the praise, and He is Able to do all things",
                bn = "আল্লাহ ছাড়া কোনো উপাস্য নেই, তিনি এক, তাঁর কোনো শরীক নেই; রাজত্ব তাঁরই, এবং সমস্ত প্রশংসাও তাঁর; এবং তিনি সবকিছুর ওপর ক্ষমতাবান"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "Equivalent to freeing four souls from the descendants of Isma'il",
                bn = "ইসমাইলের (আঃ) বংশধরদের মধ্য থেকে চারজন দাস মুক্ত করার সমতুল্য সওয়াব"
            ),
            hadithRef = "Sunan Abi Dawud 5077",
            category = DhikrCategory.EVENING
        ),
        DhikrItem(
            key = "EVENING_TASBIH",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = LocalizedText(en = "Evening Tasbih", bn = "সন্ধ্যার তাসবিহ"),
            transliteration = LocalizedText(en = "Subhāna Allāhi wa biḥamdih", bn = "সুবহানাল্লাহি ওয়া বিহামদিহি"),
            meaning = DhikrMeaning(
                en = "How perfect is Allah and I praise Him for His perfection",
                bn = "আল্লাহ কতই না নিখুঁত এবং আমি তাঁর পূর্ণতার প্রশংসা করি"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Washes away the transgressions accumulated during the day",
                bn = "সারাদিনের জমে থাকা ভুলত্রুটি ধুয়ে মুছে পরিষ্কার করে"
            ),
            hadithRef = "Sahih Muslim 2692",
            category = DhikrCategory.EVENING
        ),
        DhikrItem(
            key = "EVENING_ISTIGHFAR",
            arabicText = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            displayName = LocalizedText(en = "Evening Istighfar", bn = "সন্ধ্যার ইস্তিগফার"),
            transliteration = LocalizedText(en = "Astaghfiru Allāha wa atūbu ilayh", bn = "আস্তাগফিরুল্লা:হ ওয়া আতূবু ইলাইহ"),
            meaning = DhikrMeaning(
                en = "I seek Allah's vast forgiveness and turn to Him in sincere repentance",
                bn = "আমি আল্লাহর বিশাল ক্ষমা চাই এবং তাঁর কাছে আন্তরিক তওবা করি"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Revives the heart, following the constant practice of the Prophet ﷺ",
                bn = "নবী ﷺ এর নিয়মিত অভ্যাসের অনুসরণে অন্তরকে পুনরুজ্জীবিত করে"
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
            displayName = LocalizedText(en = "Salawat", bn = "দরূদ"),
            transliteration = LocalizedText(en = "Allāhumma ṣalli 'alā Muḥammad", bn = "আল্লা:হুম্মা স'াল্লি 'আলা: মুহ'াম্মাদ"),
            meaning = DhikrMeaning(
                en = "O Allah, exalt the mention of Muhammad and bestow peace upon him",
                bn = "হে আল্লাহ, মুহাম্মদের সম্মান বৃদ্ধি করুন এবং তাঁর উপর শান্তি বর্ষণ করুন"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "A divine multiplier: Allah sends ten blessings upon you for every one you send",
                bn = "একটি ঐশ্বরিক গুণক: আপনার প্রতিটি দরুদের বিনিময়ে আল্লাহ দশটি রহমত পাঠান"
            ),
            hadithRef = "Sahih Muslim 408",
            category = DhikrCategory.SALAWAT
        ),
        DhikrItem(
            key = "SALAWAT_100",
            arabicText = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَىٰ نَبِيِّنَا مُحَمَّدٍ",
            displayName = LocalizedText(en = "Salawat", bn = "দরূদ"),
            transliteration = LocalizedText(en = "Allāhumma ṣalli wa sallim 'alā nabiyyinā Muḥammad", bn = "আল্লা:হুম্মা স'াল্লি ওয়া সাল্লিম 'আলা: নাবিয়্যিনা: মুহ'াম্মাদ"),
            meaning = DhikrMeaning(
                en = "O Allah, send perfect peace and blessings upon our noble Prophet",
                bn = "হে আল্লাহ, আমাদের সম্মানিত নবীর উপর পূর্ণ শান্তি ও দরুদ বর্ষণ করুন"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Among the closest to the Prophet ﷺ on the Day of Resurrection — most entitled to his intercession",
                bn = "কিয়ামতের দিন নবী ﷺ এর সবচেয়ে কাছের ব্যক্তিদের একজন, যিনি তাঁর সুপারিশের সর্বাধিক উপযুক্ত"
            ),
            hadithRef = "Tirmidhi 484",
            category = DhikrCategory.SALAWAT
        ),
        DhikrItem(
            key = "SALAWAT_IBRAHIMIYYA",
            arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            displayName = LocalizedText(en = "Salawat Ibrahimiyya", bn = "দরূদে ইব্রাহিম"),
            transliteration = LocalizedText(en = "Allāhumma ṣalli 'alā Muḥammadin wa 'alā āli Muḥammad, kamā ṣallayta 'alā Ibrāhīma wa 'alā āli Ibrāhīm, innaka ḥamīdun majīd. Allāhumma bārik 'alā Muḥammadin wa 'alā āli Muḥammad, kamā bārakta 'alā Ibrāhīma wa 'alā āli Ibrāhīm, innaka ḥamīdun majīd.", bn = "আল্লা:হুম্মা স'াল্লি 'আলা: মুহ'াম্মাদিন ওয়া 'আলা: আ:লি মুহ'াম্মাদ, কামা: স'াল্লাইতা 'আলা: ইবরা:হীমা ওয়া 'আলা: আ:লি ইবরা:হীম, ইন্নাকা হ'ামীদুন মাজীদ। আল্লা:হুম্মা বা:রিক 'আলা: মুহ'াম্মাদিন ওয়া 'আলা: আ:লি মুহ'াম্মাদ, কামা: বা:রাকতা 'আলা: ইবরা:হীমা ওয়া 'আলা: আ:লি ইবরা:হীম, ইন্নাকা হ'ামীদুন মাজীদ।"),
            meaning = DhikrMeaning(
                en = "O Allah, exalt the mention of Muhammad and the family of Muhammad, as You exalted Ibrahim and the family of Ibrahim. You are Praised and Glorious. O Allah, bless Muhammad and the family of Muhammad, as You blessed Ibrahim and the family of Ibrahim. You are Praised and Glorious.",
                bn = "হে আল্লাহ, মুহাম্মদ এবং মুহাম্মদের পরিবারের ওপর রহমত বর্ষণ করুন, যেমন আপনি ইব্রাহিম এবং ইব্রাহিমের পরিবারের ওপর রহমত বর্ষণ করেছিলেন। নিশ্চয়ই আপনি প্রশংসিত ও মহিমান্বিত। হে আল্লাহ, মুহাম্মদ এবং মুহাম্মদের পরিবারের ওপর বরকত দান করুন, যেমন আপনি ইব্রাহিম এবং ইব্রাহিমের পরিবারের ওপর বরকত দান করেছিলেন। নিশ্চয়ই আপনি প্রশংসিত ও মহিমান্বিত।"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "The pinnacle of salawat; the exact phrasing taught by the Prophet ﷺ himself",
                bn = "দরুদের সর্বোচ্চ রূপ; নবী ﷺ নিজেই এই বাক্য শিখিয়েছেন"
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
            displayName = LocalizedText(en = "Istighfar al-Azim", bn = "ইস্তিগফারে আজিম"),
            transliteration = LocalizedText(en = "Astaghfiru Allāha l-'aẓīma lladhī lā ilāha illā huwa", bn = "আস্তাগফিরুল্লা:হাল 'আজ'ীমাল্লায়'ী লা: ইলা:হা ইল্লা হুওয়া"),
            meaning = DhikrMeaning(
                en = "I seek forgiveness from Allah the Magnificent, the solely worthy of worship",
                bn = "আমি মহান আল্লাহর কাছে ক্ষমা চাই, যিনি ছাড়া ইবাদতের যোগ্য কেউ নেই"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Pardons massive sins, even if one fled from the battlefield",
                bn = "বড় বড় গুনাহ মাফ করে, এমনকি যুদ্ধের ময়দান থেকে পালানোর মত গুনাহও"
            ),
            hadithRef = "Abu Dawud 1517",
            category = DhikrCategory.ISTIGHFAR
        ),
        DhikrItem(
            key = "SAYYID_ISTIGHFAR",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            displayName = LocalizedText(en = "Sayyid al-Istighfar", bn = "সাইয়িদুল ইস্তিগফার"),
            transliteration = LocalizedText(en = "Allāhumma anta rabbī lā ilāha illā anta, khalaqtanī wa anā 'abduka, wa anā 'alā 'ahdika wa wa'dika māstaṭa'tu, a'ūdhu bika min sharri mā ṣana'tu, abū'u laka bini'matika 'alayya, wa abū'u laka bidhanbī faghfir lī fa'innahu lā yaghfirudh-dhunūba illā anta.", bn = "আল্লা:হুম্মা আন্তা রাব্বী লা: ইলা:হা ইল্লা আন্তা, খালাকতানী ওয়া আনা 'আবদুকা, ওয়া আনা 'আলা: 'আহ'দিকা ওয়া ওয়া'দিকা মা:স্তাত্বা'তু, 'আঊয়'ু বিকা মিন শাররি মা: স'ানা'তু, আবূ'উ লাকা বিনি'মাতিকা 'আলাইয়্যা, ওয়া আবূ'উ লাকা বিয়'াম্বী ফাগফির লী ফা'ইন্নাহু লা ইয়াগফিরুয়' য়ুনূবা ইল্লা আন্তা।"),
            meaning = DhikrMeaning(
                en = "O Allah, You are my Lord; there is no deity except You. You created me and I am Your servant, and I am upon Your covenant and promise as much as I am able. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me, and I acknowledge my sin, so forgive me, for none forgives sins except You.",
                bn = "হে আল্লাহ, আপনি আমার রব, আপনি ছাড়া কোনো উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা, আমি আমার সাধ্যমতো আপনার প্রতিশ্রুতি ও অঙ্গীকারের ওপর আছি। আমি যা করেছি তার অনিষ্ট থেকে আপনার আশ্রয় চাই। আমার ওপর আপনার অনুগ্রহ আমি স্বীকার করছি এবং আমার গুনাহও স্বীকার করছি, সুতরাং আমাকে ক্ষমা করুন, কেননা আপনি ছাড়া আর কেউ গুনাহ ক্ষমা করতে পারে না।"
            ),
            defaultTarget = 1,
            spiritualReward = LocalizedText(
                en = "The Master of Forgiveness — whoever says it with certainty and dies that day enters Paradise",
                bn = "ক্ষমা চাওয়ার শ্রেষ্ঠ দোয়া — যে দৃঢ় বিশ্বাসে পড়ে এবং সে দিন মারা যায়, সে জান্নাতে যাবে"
            ),
            hadithRef = "Sahih al-Bukhari 6306",
            category = DhikrCategory.ISTIGHFAR
        )
    )

    // ── Tahlil ─────────────────────────────────────────────────────────────────

    private val tahlil = listOf(
        DhikrItem(
            key = DhikrType.TAHLIL.name,
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            displayName = LocalizedText(en = "Tahlil", bn = "তাহলীল"),
            transliteration = LocalizedText(en = "Lā ilāha illallāhu waḥdahu lā sharīka lahu, lahul-mulku wa lahul-ḥamdu, wa huwa 'alā kulli shay'in qadīr", bn = "লা: ইলা:হা ইল্লাল্লা:হু ওয়াহ'দাহু লা: শারীকা লাহু, লাহুল মুলকু ওয়া লাহুল হ'ামদু, ওয়া হুয়া 'আলা: কুল্লি শাইয়িন ক্বাদীর"),
            meaning = DhikrMeaning(
                en = "There is no true deity but Allah alone, Who has no partner; His is the dominion and His is the praise, and He is Able to do all things",
                bn = "আল্লাহ ছাড়া কোনো উপাস্য নেই, তিনি এক, তাঁর কোনো শরীক নেই; রাজত্ব তাঁরই, এবং সমস্ত প্রশংসাও তাঁর; এবং তিনি সবকিছুর ওপর ক্ষমতাবান"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Erases 100 sins, records 100 good deeds, and shields you from Shaytan until evening",
                bn = "১০০ টি গুনাহ মুছে দেয়, ১০০ টি নেকি লেখা হয়, এবং সন্ধ্যা পর্যন্ত শয়তান থেকে সুরক্ষা দেয়"
            ),
            hadithRef = "Sahih al-Bukhari 3293",
            category = DhikrCategory.TAHLIL
        ),
        DhikrItem(
            key = "TAHLIL_SIMPLE",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ",
            displayName = LocalizedText(en = "Tahlil", bn = "তাহলীল"),
            transliteration = LocalizedText(en = "Lā ilāha illā Allāh", bn = "লা: ইলা:হা ইল্লাল্লা:হ"),
            meaning = DhikrMeaning(
                en = "There is nothing worthy of worship except Allah",
                bn = "আল্লাহ ছাড়া ইবাদতের যোগ্য কেউ নেই"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "The ultimate declaration of faith and the very best of all remembrance",
                bn = "ঈমানের চূড়ান্ত ঘোষণা এবং সকল যিকিরের মধ্যে সর্বোত্তম"
            ),
            hadithRef = "Tirmidhi 3383",
            category = DhikrCategory.TAHLIL
        )
    )

    val asmaAll = listOf(
        DhikrItem(
            key = "ASMA_ALL_99",
            arabicText = "أَسْمَاءُ اللَّهِ الْحُسْنَىٰ",
            displayName = LocalizedText(en = "Asma-ul-Husna (Full Sequence)", bn = "আসমাউল হুসনা (সম্পূর্ণ)"),
            transliteration = LocalizedText(en = "Asmā’ Allāh Al-Ḥusnā", bn = "আসমাউল হু'সনা:"),
            meaning = DhikrMeaning(
                en = "Recite all 99 names of Allah sequentially",
                bn = "আল্লাহর ৯৯টি নাম ধারাবাহিকভাবে পড়ুন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Whoever memorizes them will enter Paradise (Sahih al-Bukhari 2736)",
                bn = "যে এগুলো মুখস্থ করবে সে জান্নাতে প্রবেশ করবে (সহীহ বুখারী ২৭৩৬)"
            ),
            hadithRef = "Bukhari 2736",
            category = DhikrCategory.ASMA_UL_HUSNA
        )
    )

    val asmaUlHusnaList: List<DhikrItem>
        get() = AsmaUlHusnaCatalog.list + AsmaUlHusnaCatalog2.list + AsmaUlHusnaCatalog3.list

    // ── Aggregated catalog ─────────────────────────────────────────────────────
    val all: List<DhikrItem>
        get() = asmaAll + afterPrayer + daily + morning + evening + salawat + istighfar + tahlil + asmaUlHusnaList

    /** Fast key → item lookup, built once. */
    private val byKey: Map<String, DhikrItem> by lazy { all.associateBy { it.key } }

    // ── Post-Salah sequences ───────────────────────────────────────────────────
    // Built from the same phrases used elsewhere in the catalog so there is one
    // source of truth for each Arabic string.

    private val stepSubhan = DhikrStep("سُبْحَانَ اللَّهِ", LocalizedText(en = "SubhanAllah", bn = "সুবহানাল্লাহ"), LocalizedText(en = "Subhāna Allāh", bn = "সুবহ'া:নাল্লা:হ"), DhikrMeaning("Allah is free from all imperfections", "আল্লাহ সকল ত্রুটি ও অসম্পূর্ণতা থেকে মুক্ত"), 33)
    private val stepHamd = DhikrStep("الْحَمْدُ لِلَّهِ", LocalizedText(en = "Alhamdulillah", bn = "আলহামদুলিল্লাহ"), LocalizedText(en = "Al-ḥamdu lillāh", bn = "আলহ'ামদুলিল্লা:হ"), DhikrMeaning("All absolute praise and gratitude belong solely to Allah", "সমস্ত নিরঙ্কুশ প্রশংসা এবং কৃতজ্ঞতা একমাত্র আল্লাহর"), 33)
    private val stepAkbar34 = DhikrStep("اللَّهُ أَكْبَرُ", LocalizedText(en = "Allahu Akbar", bn = "আল্লাহু আকবার"), LocalizedText(en = "Allāhu akbar", bn = "আল্লা:হু আকবার"), DhikrMeaning("Allah is infinitely greater than anything else we can comprehend", "আল্লাহ আমাদের উপলব্ধির চেয়েও অসীম মহান"), 34)
    private val stepAkbar33 = stepAkbar34.copy(target = 33)
    private val stepTahlil = DhikrStep("لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", LocalizedText(en = "Tahlil", bn = "তাহলীল"), LocalizedText(en = "Lā ilāha illallāhu waḥdahu lā sharīka lahu...", bn = "লা: ইলা:হা ইল্লাল্লা:হু ওয়াহ'দাহু লা: শারীকা লাহু..."), DhikrMeaning("There is no true deity but Allah alone, Who has no partner; His is the dominion and His is the praise, and He is Able to do all things", "আল্লাহ ছাড়া কোনো উপাস্য নেই, তিনি এক, তাঁর কোনো শরীক নেই; রাজত্ব তাঁরই, এবং সমস্ত প্রশংসাও তাঁর; এবং তিনি সবকিছুর ওপর ক্ষমতাবান"), 1)

    /**
     * Human-readable name for a stored key, or the raw key if it isn't a built-in
     * entry (e.g. a custom dhikr). Used for labelling saved sessions.
     */
    fun displayNameFor(key: String): LocalizedText = byKey[key]?.displayName ?: LocalizedText(en = key, bn = key)

    /** Returns the multi-step sequence for a post-Salah entry key, or null. */
    fun sequenceFor(key: String): DhikrSequence? = when (key) {
        "SMART_FLOW_CLASSIC" -> DhikrSequence(
            key,
            LocalizedText(
                en = "Tasbīḥ after Salah · Classic",
                bn = "নামাজের পরে তাসবিহ · ক্লাসিক"
            ),
            persistentListOf(stepSubhan, stepHamd, stepAkbar34)
        )
        "SMART_FLOW_WITH_TAHLIL" -> DhikrSequence(
            key,
            LocalizedText(
                en = "Tasbīḥ after Salah · With Tahlīl",
                bn = "নামাজের পরে তাসবিহ · তাহলীল সহ"
            ),
            persistentListOf(stepSubhan, stepHamd, stepAkbar33, stepTahlil)
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
            transliteration = item.transliteration,
            meaning = item.meaning,
            target = item.defaultTarget,
            spiritualReward = item.spiritualReward,
            hadithRef = item.hadithRef,
            sequenceKey = if (item.isSmartFlow) item.key else null
        )
    }
}
