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
            displayName = LocalizedText(en = "SubhanAllah", ur = "سبحان اللہ", bn = "সুবহানাল্লাহ"),
            transliteration = LocalizedText(en = "Subhāna Allāh", ur = "سبحان اللہ", bn = "সুবহানাল্লাহ"),
            meaning = DhikrMeaning(
                en = "Allah is free from all imperfections",
                ur = "اللہ ہر نقص اور عیب سے پاک ہے",
                bn = "আল্লাহ সকল ত্রুটি ও অসম্পূর্ণতা থেকে মুক্ত"
            ),
            defaultTarget = 33,
            spiritualReward = LocalizedText(
                en = "Removes your sins even if they are like the foam of the sea",
                ur = "گناہ مٹا دیتا ہے چاہے وہ سمندر کی جھاگ کے برابر ہی کیوں نہ ہوں",
                bn = "আপনার গুনাহ মুছে দেয়, এমনকি তা সমুদ্রের ফেনার মত হলেও"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = DhikrType.ALHAMDULILLAH.name,
            arabicText = "الْحَمْدُ لِلَّهِ",
            displayName = LocalizedText(en = "Alhamdulillah", ur = "الحمد للہ", bn = "আলহামদুলিল্লাহ"),
            transliteration = LocalizedText(en = "Al-ḥamdu lillāh", ur = "الحمد للہ", bn = "আলহামদুলিল্লাহ"),
            meaning = DhikrMeaning(
                en = "All absolute praise and gratitude belong solely to Allah",
                ur = "تمام کامل تعریفیں اور شکر صرف اللہ کے لیے ہیں",
                bn = "সকল নিখুঁত প্রশংসা এবং কৃতজ্ঞতা কেবল আল্লাহর জন্য"
            ),
            defaultTarget = 33,
            spiritualReward = LocalizedText(
                en = "Fills the Scale on the Day of Judgment to overflowing",
                ur = "قیامت کے دن میزان کو نیکیوں سے بھر دیتا ہے",
                bn = "কিয়ামতের দিন আমলের পাল্লা কানায় কানায় পূর্ণ করে"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = DhikrType.ALLAHU_AKBAR.name,
            arabicText = "اللَّهُ أَكْبَرُ",
            displayName = LocalizedText(en = "Allahu Akbar", ur = "اللہ اکبر", bn = "আল্লাহু আকবার"),
            transliteration = LocalizedText(en = "Allāhu akbar", ur = "اللہ اکبر", bn = "আল্লাহু আকবার"),
            meaning = DhikrMeaning(
                en = "Allah is infinitely greater than anything else we can comprehend",
                ur = "اللہ ہماری سوچ سے بھی کہیں زیادہ بڑا اور عظیم ہے",
                bn = "আল্লাহ আমাদের উপলব্ধির চেয়েও অসীম মহান"
            ),
            defaultTarget = 34,
            spiritualReward = LocalizedText(
                en = "The most beloved words; a declaration of His ultimate majesty",
                ur = "اللہ کو سب سے محبوب کلمات؛ اس کی চূড়ান্ত عظمت کا اقرار",
                bn = "সবচেয়ে প্রিয় বাক্য; তাঁর চূড়ান্ত মহিমার ঘোষণা"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER
        ),
        DhikrItem(
            key = "SMART_FLOW_CLASSIC",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ",
            displayName = LocalizedText(en = "Tasbih after Salah · Classic", ur = "نماز کے بعد تسبیح · کلاسک", bn = "নামাজের পরে তাসবিহ · ক্লাসিক"),
            transliteration = LocalizedText(en = "SubhanAllah · Alhamdulillah · Allahu Akbar", ur = "نماز کے بعد تسبیح · کلاسک", bn = "নামাজের পরে তাসবিহ · ক্লাসিক"),
            meaning = DhikrMeaning(
                en = "The foundational 33 + 33 + 34 sequence after obligatory prayer",
                ur = "فرض نماز کے بعد کا بنیادی ۳۳+۳۳+۳۴ کا سلسلہ",
                bn = "ফরজ নামাজের পরে মৌলিক ৩৩+৩৩+৩৪ ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "A shield against Hellfire and a guarantee of forgiveness",
                ur = "جہنم کی آگ سے ڈھال اور مغفرت کی ضمانت",
                bn = "জাহান্নামের আগুন থেকে ঢাল এবং ক্ষমার গ্যারান্টি"
            ),
            hadithRef = "Sahih Muslim 596",
            category = DhikrCategory.AFTER_PRAYER,
            isSmartFlow = true,
            smartFlowVariant = SmartFlowVariant.CLASSIC
        ),
        DhikrItem(
            key = "SMART_FLOW_WITH_TAHLIL",
            arabicText = "سُبْحَانَ اللَّهِ · الْحَمْدُ لِلَّهِ · اللَّهُ أَكْبَرُ · لَا إِلَٰهَ إِلَّا اللَّهُ",
            displayName = LocalizedText(en = "Tasbih after Salah · With Tahlil", ur = "نماز کے بعد تسبیح · تہلیل کے ساتھ", bn = "নামাজের পরে তাসবিহ · তাহলীল সহ"),
            transliteration = LocalizedText(en = "SubhanAllah · Alhamdulillah · Allahu Akbar · La ilaha illallah", ur = "نماز کے بعد تسبیح · تہلیل کے ساتھ", bn = "নামাজের পরে তাসবিহ · তাহলীল সহ"),
            meaning = DhikrMeaning(
                en = "33 + 33 + 33 sequence capped with the statement of pure monotheism",
                ur = "توحید کے اقرار پر ختم ہونے والا ۳۳+۳۳+۳۳ کا سلسلہ",
                bn = "খাঁটি একত্ববাদের ঘোষণা দিয়ে সমাপ্ত ৩৩+৩৩+৩৩ ক্রম"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Sins are forgiven entirely, bringing the heart profound tranquility",
                ur = "گناہ مکمل طور پر معاف کر دیے جاتے ہیں، دل کو گہرا سکون ملتا ہے",
                bn = "গুনাহ সম্পূর্ণভাবে ক্ষমা করা হয়, অন্তর গভীর প্রশান্তি লাভ করে"
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
            displayName = LocalizedText(en = "Astaghfirullah", ur = "استغفر اللہ", bn = "আস্তাগফিরুল্লাহ"),
            transliteration = LocalizedText(en = "Astaghfiru Allāh", ur = "استغفر اللہ", bn = "আস্তাগফিরুল্লাহ"),
            meaning = DhikrMeaning(
                en = "I seek His forgiveness, acknowledging my weakness and His absolute mercy",
                ur = "میں اپنی کمزوری کا اعتراف کرتے ہوئے اس کی بخشش طلب کرتا ہوں",
                bn = "আমি আমার দুর্বলতা স্বীকার করে তাঁর অসীম ক্ষমার আশ্রয় চাই"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "A constant shield: Allah provides a way out of every anxiety and hardship",
                ur = "ایک دائمی ڈھال: اللہ ہر پریشانی اور مشکل سے نکلنے کا راستہ دیتا ہے",
                bn = "একটি ধ্রুবক ঢাল: আল্লাহ প্রতিটি উদ্বেগ ও কষ্ট থেকে উত্তরণের পথ দেন"
            ),
            hadithRef = "Sahih al-Bukhari 6307",
            category = DhikrCategory.DAILY
        ),
        DhikrItem(
            key = DhikrType.SUBHANALLAHI_WA_BIHAMDIHI.name,
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = LocalizedText(en = "SubhanAllahi wa bihamdihi", ur = "سبحان اللہ وبحمدہ", bn = "সুবহানাল্লাহি ওয়া বিহামদিহি"),
            transliteration = LocalizedText(en = "Subhāna Allāhi wa biḥamdih", ur = "سبحان اللہ وبحمدہ", bn = "সুবহানাল্লাহি ওয়া বিহামদিহি"),
            meaning = DhikrMeaning(
                en = "Flawless is Allah, and wrapped in gratitude is His praise",
                ur = "اللہ ہر عیب سے پاک ہے اور اسی کے لیے تمام تعریفیں ہیں",
                bn = "আল্লাহ নিখুঁত এবং তাঁর প্রশংসাই কৃতজ্ঞতায় মোড়ানো"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Reciting this 100 times wipes away minor sins completely",
                ur = "اسے ۱۰۰ بار پڑھنے سے چھوٹے گناہ مکمل طور پر مٹ جاتے ہیں",
                bn = "এটি ১০০ বার পড়লে সগিরা গুনাহ সম্পূর্ণ মুছে যায়"
            ),
            hadithRef = "Sahih al-Bukhari 6405",
            category = DhikrCategory.DAILY
        ),
        DhikrItem(
            key = "SUBHANALLAHI_L_AZIM",
            arabicText = "سُبْحَانَ اللَّهِ الْعَظِيمِ",
            displayName = LocalizedText(en = "SubhanAllahi l-Azim", ur = "سبحان اللہ العظیم", bn = "সুবহানাল্লাহিল আজিম"),
            transliteration = LocalizedText(en = "Subhāna Allāhi l-'aẓīm", ur = "سبحان اللہ العظیم", bn = "সুবহানাল্লাহিল আজিম"),
            meaning = DhikrMeaning(
                en = "Exalted is Allah, the Most Magnificent and Supreme",
                ur = "عظیم اللہ کی ذات پاک ہے، جو سب سے برتر ہے",
                bn = "মহান আল্লাহ পবিত্র, যিনি সর্বোচ্চ মর্যাদাবান"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Incredibly light on the tongue, unimaginably heavy on the Scale of deeds",
                ur = "زبان پر بے حد ہلکے، نامہ اعمال کے ترازو میں ناقابل تصور بھاری",
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
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
            displayName = LocalizedText(en = "Hasbiyallah", ur = "حسبی اللہ", bn = "হাসবিআল্লাহ"),
            transliteration = LocalizedText(en = "Hasbiya Allāhu lā ilāha illā huwa", ur = "حسبی اللہ", bn = "হাসবিআল্লাহ"),
            meaning = DhikrMeaning(
                en = "Allah is entirely sufficient for me; there is no deity worthy of worship but Him",
                ur = "مجھے اللہ کافی ہے، اس کے سوا کوئی عبادت کے لائق نہیں",
                bn = "আল্লাহই আমার জন্য যথেষ্ট; তিনি ছাড়া ইবাদতের যোগ্য কেউ নেই"
            ),
            defaultTarget = 7,
            spiritualReward = LocalizedText(
                en = "A promise of divine care: Allah will suffice you in whatever matters you",
                ur = "الٰہی کفالت کا وعدہ: آپ کے ہر معاملے میں اللہ آپ کو کافی ہو جائے گا",
                bn = "ঐশী যত্নের প্রতিশ্রুতি: আপনার সকল বিষয়ে আল্লাহই যথেষ্ট হবেন"
            ),
            hadithRef = "Abu Dawud 5081",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_AOUDHU",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            displayName = LocalizedText(en = "Morning Protection Dua", ur = "صبح کی حفاظتی دعا", bn = "সকালের সুরক্ষার দোয়া"),
            transliteration = LocalizedText(en = "A'ūdhu bikalimatillāhi t-tāmmāti min sharri mā khalaqa", ur = "صبح کی حفاظتی دعا", bn = "সকালের সুরক্ষার দোয়া"),
            meaning = DhikrMeaning(
                en = "I take absolute refuge in the perfect words of Allah from the evil of His creation",
                ur = "میں اللہ کے کامل کلمات کے ذریعے اس کی مخلوق کے شر سے پناہ مانگتا ہوں",
                bn = "আমি আল্লাহর পূর্ণ বাণীর আশ্রয়ে তাঁর সৃষ্টির অনিষ্ট থেকে সুরক্ষা চাই"
            ),
            defaultTarget = 3,
            spiritualReward = LocalizedText(
                en = "Forms an impenetrable shield of protection around you until evening",
                ur = "شام تک آپ کے گرد حفاظت کی ایک ناقابل تسخیر ڈھال بنا دیتا ہے",
                bn = "সন্ধ্যা পর্যন্ত আপনার চারপাশে একটি দুর্ভেদ্য সুরক্ষার ঢাল তৈরি করে"
            ),
            hadithRef = "Sahih Muslim 2709",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_TAHLIL",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيکَ لَهُ",
            displayName = LocalizedText(en = "Morning Tahlil", ur = "صبح کی تہلیل", bn = "সকালের তাহলীল"),
            transliteration = LocalizedText(en = "Lā ilāha illā Allāhu waḥdahu lā sharīka lah", ur = "صبح کی تہلیل", bn = "সকালের তাহলীল"),
            meaning = DhikrMeaning(
                en = "None has the right to be worshipped but Allah alone, Who has no partner",
                ur = "اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے، اس کا کوئی شریک نہیں",
                bn = "আল্লাহ ছাড়া ইবাদতের যোগ্য কেউ নেই, তিনি এক, তাঁর কোন শরীক নেই"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "Rewards you as if you freed 10 slaves, grants 100 good deeds, and shields from Shaytan",
                ur = "دس غلام آزاد کرنے کا ثواب، سو نیکیاں، اور شیطان سے ڈھال",
                bn = "১০ জন দাস মুক্ত করার সওয়াব, ১০০ নেকি, এবং শয়তান থেকে ঢাল"
            ),
            hadithRef = "Sahih al-Bukhari 3293",
            category = DhikrCategory.MORNING
        ),
        DhikrItem(
            key = "MORNING_TASBIH",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = LocalizedText(en = "Morning Tasbih", ur = "صبح کی تسبیح", bn = "সকালের তাসবিহ"),
            transliteration = LocalizedText(en = "Subhāna Allāhi wa biḥamdih", ur = "صبح کی تسبیح", bn = "সকালের তাসবিহ"),
            meaning = DhikrMeaning(
                en = "How perfect is Allah and I praise Him for His perfection",
                ur = "اللہ کی ذات پاک ہے اور میں اس کے کمال کی تعریف کرتا ہوں",
                bn = "আল্লাহ কতই না নিখুঁত এবং আমি তাঁর পূর্ণতার প্রশংসা করি"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Ensures no one comes on the Day of Resurrection with better deeds than you",
                ur = "اس بات کی ضمانت کہ قیامت کے دن کوئی آپ سے بہتر اعمال نہیں لائے گا",
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
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيکَ لَهُ",
            displayName = LocalizedText(en = "Evening Tahlil", ur = "شام کی تہلیل", bn = "সন্ধ্যার তাহলীল"),
            transliteration = LocalizedText(en = "Lā ilāha illā Allāhu waḥdahu lā sharīka lah", ur = "شام کی تہلیل", bn = "সন্ধ্যার তাহলীল"),
            meaning = DhikrMeaning(
                en = "None has the right to be worshipped but Allah alone, Who has no partner",
                ur = "اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے، اس کا کوئی شریک نہیں",
                bn = "আল্লাহ ছাড়া ইবাদতের যোগ্য কেউ নেই, তিনি এক, তাঁর কোন শরীক নেই"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "Secures the soul through the night and provides angelic protection until dawn",
                ur = "رات بھر روح کی حفاظت اور صبح تک فرشتوں کا پہرہ",
                bn = "সারারাত আত্মাকে সুরক্ষিত রাখে এবং ভোর পর্যন্ত ফেরেশতাদের নিরাপত্তা দেয়"
            ),
            hadithRef = "Sahih al-Bukhari 3293",
            category = DhikrCategory.EVENING
        ),
        DhikrItem(
            key = "EVENING_TASBIH",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            displayName = LocalizedText(en = "Evening Tasbih", ur = "شام کی تسبیح", bn = "সন্ধ্যার তাসবিহ"),
            transliteration = LocalizedText(en = "Subhāna Allāhi wa biḥamdih", ur = "شام کی تسبیح", bn = "সন্ধ্যার তাসবিহ"),
            meaning = DhikrMeaning(
                en = "How perfect is Allah and I praise Him for His perfection",
                ur = "اللہ کی ذات پاک ہے اور میں اس کے کمال کی تعریف کرتا ہوں",
                bn = "আল্লাহ কতই না নিখুঁত এবং আমি তাঁর পূর্ণতার প্রশংসা করি"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Washes away the transgressions accumulated during the day",
                ur = "دن بھر میں ہونے والی کوتاہیوں اور خطاؤں کو دھو ڈالتا ہے",
                bn = "সারাদিনের জমে থাকা ভুলত্রুটি ধুয়ে মুছে পরিষ্কার করে"
            ),
            hadithRef = "Sahih Muslim 2692",
            category = DhikrCategory.EVENING
        ),
        DhikrItem(
            key = "EVENING_ISTIGHFAR",
            arabicText = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            displayName = LocalizedText(en = "Evening Istighfar", ur = "شام کا استغفار", bn = "সন্ধ্যার ইস্তিগফার"),
            transliteration = LocalizedText(en = "Astaghfiru Allāha wa atūbu ilayh", ur = "شام کا استغفار", bn = "সন্ধ্যার ইস্তিগফার"),
            meaning = DhikrMeaning(
                en = "I seek Allah's vast forgiveness and turn to Him in sincere repentance",
                ur = "میں اللہ کی وسیع مغفرت طلب کرتا ہوں اور سچے دل سے توبہ کرتا ہوں",
                bn = "আমি আল্লাহর বিশাল ক্ষমা চাই এবং তাঁর কাছে আন্তরিক তওবা করি"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Revives the heart, following the constant practice of the Prophet ﷺ",
                ur = "نبی ﷺ کی سنت کی پیروی کرتے ہوئے دل کو زندہ اور روشن کرتا ہے",
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
            displayName = LocalizedText(en = "Salawat (Short)", ur = "درود (مختصر)", bn = "দরূদ (সংক্ষিপ্ত)"),
            transliteration = LocalizedText(en = "Allāhumma ṣalli 'alā Muḥammad", ur = "درود (مختصر)", bn = "দরূদ (সংক্ষিপ্ত)"),
            meaning = DhikrMeaning(
                en = "O Allah, exalt the mention of Muhammad and bestow peace upon him",
                ur = "اے اللہ، محمد کا ذکر بلند فرما اور ان پر سلامتی نازل کر",
                bn = "হে আল্লাহ, মুহাম্মদের সম্মান বৃদ্ধি করুন এবং তাঁর উপর শান্তি বর্ষণ করুন"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "A divine multiplier: Allah sends ten blessings upon you for every one you send",
                ur = "اللہ آپ کے ایک درود کے بدلے آپ پر دس رحمتیں نازل فرماتا ہے",
                bn = "একটি ঐশ্বরিক গুণক: আপনার প্রতিটি দরুদের বিনিময়ে আল্লাহ দশটি রহমত পাঠান"
            ),
            hadithRef = "Sahih Muslim 408",
            category = DhikrCategory.SALAWAT
        ),
        DhikrItem(
            key = "SALAWAT_100",
            arabicText = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَىٰ نَبِيِّنَا مُحَمَّدٍ",
            displayName = LocalizedText(en = "Salawat × 100", ur = "درود × ۱۰۰", bn = "দরূদ × ১০০"),
            transliteration = LocalizedText(en = "Allāhumma ṣalli wa sallim 'alā nabiyyinā Muḥammad", ur = "درود × ۱۰۰", bn = "দরূদ × ১০০"),
            meaning = DhikrMeaning(
                en = "O Allah, send perfect peace and blessings upon our noble Prophet",
                ur = "اے اللہ، ہمارے معزز نبی پر کامل درود و سلام نازل فرما",
                bn = "হে আল্লাহ, আমাদের সম্মানিত নবীর উপর পূর্ণ শান্তি ও দরুদ বর্ষণ করুন"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Guarantees the Prophet's ﷺ intercession for you on the Day of Resurrection",
                ur = "روز قیامت آپ کے لیے نبی ﷺ کی شفاعت کو واجب کر دیتا ہے",
                bn = "কিয়ামতের দিন আপনার জন্য নবী ﷺ এর সুপারিশ নিশ্চিত করে"
            ),
            hadithRef = "Tirmidhi 484",
            category = DhikrCategory.SALAWAT
        ),
        DhikrItem(
            key = "SALAWAT_IBRAHIMIYYA",
            arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ",
            displayName = LocalizedText(en = "Salawat Ibrahimiyya", ur = "درود ابراہیمی", bn = "দরূদে ইব্রাহিম"),
            transliteration = LocalizedText(en = "Allāhumma ṣalli 'alā Muḥammadin wa 'alā āli Muḥammad", ur = "درود ابراہیمی", bn = "দরূদে ইব্রাহিম"),
            meaning = DhikrMeaning(
                en = "O Allah, honor Muhammad and his family, just as You honored Ibrahim and his family",
                ur = "اے اللہ، محمد اور ان کی آل کو عزت بخش جیسے تو نے ابراہیم اور ان کی آل کو بخشی",
                bn = "হে আল্লাহ, মুহাম্মদ ও তাঁর পরিবারকে সম্মানিত করুন, যেমন ইব্রাহিম ও তাঁর পরিবারকে করেছিলেন"
            ),
            defaultTarget = 10,
            spiritualReward = LocalizedText(
                en = "The pinnacle of salawat; the exact phrasing taught by the Prophet ﷺ himself",
                ur = "درود کا اعلیٰ ترین درجہ؛ وہ الفاظ جو خود نبی ﷺ نے سکھائے",
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
            displayName = LocalizedText(en = "Istighfar al-Azim", ur = "استغفار عظیم", bn = "ইস্তিগফারে আজিম"),
            transliteration = LocalizedText(en = "Astaghfiru Allāha l-'aẓīma lladhī lā ilāha illā huwa", ur = "استغفار عظیم", bn = "ইস্তিগফারে আজিম"),
            meaning = DhikrMeaning(
                en = "I seek forgiveness from Allah the Magnificent, the solely worthy of worship",
                ur = "میں اس عظیم اللہ سے معافی مانگتا ہوں جس کے سوا کوئی عبادت کے لائق نہیں",
                bn = "আমি মহান আল্লাহর কাছে ক্ষমা চাই, যিনি ছাড়া ইবাদতের যোগ্য কেউ নেই"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Pardons massive sins, even if one fled from the battlefield",
                ur = "بڑے گناہوں کو معاف کر دیتا ہے، یہاں تک کہ میدان جنگ سے بھاگنے جیسا گناہ بھی",
                bn = "বড় বড় গুনাহ মাফ করে, এমনকি যুদ্ধের ময়দান থেকে পালানোর মত গুনাহও"
            ),
            hadithRef = "Abu Dawud 1517",
            category = DhikrCategory.ISTIGHFAR
        ),
        DhikrItem(
            key = "SAYYID_ISTIGHFAR",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ خَلَقْتَنِي",
            displayName = LocalizedText(en = "Sayyid al-Istighfar", ur = "سید الاستغفار", bn = "সাইয়িদুল ইস্তিগফার"),
            transliteration = LocalizedText(en = "Allāhumma anta rabbī lā ilāha illā anta khalaqtanī", ur = "سید الاستغفار", bn = "সাইয়িদুল ইস্তিগফার"),
            meaning = DhikrMeaning(
                en = "O Allah, You are my Lord. I am Your servant, resting upon Your covenant",
                ur = "اے اللہ، تو میرا رب ہے۔ میں تیرا بندہ ہوں اور تیرے عہد پر قائم ہوں",
                bn = "হে আল্লাহ, তুমি আমার রব। আমি তোমার বান্দা এবং তোমার প্রতিশ্রুতির উপর আছি"
            ),
            defaultTarget = 1,
            spiritualReward = LocalizedText(
                en = "The Master of Forgiveness — whoever says it with certainty and dies that day enters Paradise",
                ur = "استغفار کا سردار — جو یقین سے پڑھے اور اس دن مر جائے تو جنت میں جائے گا",
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
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيکَ لَهُ",
            displayName = LocalizedText(en = "Tahlil (Full)", ur = "تہلیل (مکمل)", bn = "তাহলীল (পূর্ণ)"),
            transliteration = LocalizedText(en = "Lā ilāha illā Allāhu waḥdahu lā sharīka lah", ur = "تہلیل (مکمل)", bn = "তাহলীল (পূর্ণ)"),
            meaning = DhikrMeaning(
                en = "There is no true deity but Allah alone, Who has no partner; His is the dominion",
                ur = "اللہ کے سوا کوئی سچا معبود نہیں، وہ اکیلا ہے، اسی کی بادشاہت ہے",
                bn = "আল্লাহ ছাড়া কোন সত্য ইলাহ নেই, তিনি এক; রাজত্ব কেবল তাঁরই"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "Eradicates 100 bad deeds, elevates you 100 degrees, and forms an iron clad protection",
                ur = "سو برائیاں مٹا دیتا ہے، سو درجات بلند کرتا ہے، اور لوہے جیسی حفاظت دیتا ہے",
                bn = "১০০ টি পাপ মুছে দেয়, ১০০ ডিগ্রি মর্যাদা বৃদ্ধি করে এবং একটি লৌহবর্মের সুরক্ষা দেয়"
            ),
            hadithRef = "Sahih al-Bukhari 3293",
            category = DhikrCategory.TAHLIL
        ),
        DhikrItem(
            key = "TAHLIL_SIMPLE",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ",
            displayName = LocalizedText(en = "Tahlil (Simple)", ur = "تہلیل (مختصر)", bn = "তাহলীল (সরল)"),
            transliteration = LocalizedText(en = "Lā ilāha illā Allāh", ur = "تہلیل (مختصر)", bn = "তাহলীল (সরল)"),
            meaning = DhikrMeaning(
                en = "There is nothing worthy of worship except Allah",
                ur = "اللہ کے سوا کوئی عبادت کے لائق نہیں",
                bn = "আল্লাহ ছাড়া ইবাদতের যোগ্য কেউ নেই"
            ),
            defaultTarget = 100,
            spiritualReward = LocalizedText(
                en = "The ultimate declaration of faith and the very best of all remembrance",
                ur = "ایمان کا سب سے بڑا اقرار اور تمام اذکار میں سب سے افضل ذکر",
                bn = "ঈমানের চূড়ান্ত ঘোষণা এবং সকল জিকিরের মধ্যে সর্বোত্তম"
            ),
            hadithRef = "Tirmidhi 3383",
            category = DhikrCategory.TAHLIL
        )
    )

    val asmaAll = listOf(
        DhikrItem(
            key = "ASMA_ALL_99",
            arabicText = "أَسْمَاءُ اللَّهِ الْحُسْنَىٰ",
            displayName = LocalizedText(en = "The 99 Names (Sequence)", ur = "اسماء الحسنىٰ (ترتیب سے)", bn = "৯৯টি নাম (ধারাবাহিক)"),
            transliteration = LocalizedText(en = "Asmā’ Allāh Al-Ḥusnā", ur = "اسماء الحسنىٰ (ترتیب سے)", bn = "৯৯টি নাম (ধারাবাহিক)"),
            meaning = DhikrMeaning(
                en = "Recite all 99 names of Allah sequentially",
                ur = "اللہ کے 99 نام ترتیب سے پڑھیں",
                bn = "আল্লাহর ৯৯টি নাম ধারাবাহিকভাবে পাঠ করুন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Whoever memorizes them will enter Paradise (Sahih al-Bukhari 2736)",
                ur = "جو ان کو یاد کرے گا وہ جنت میں جائے گا (صحیح بخاری 2736)",
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

    private val stepSubhan = DhikrStep("سُبْحَانَ اللَّهِ", LocalizedText(en = "SubhanAllah", ur = "سبحان اللہ", bn = "সুবহানাল্লাহ"), LocalizedText(en = "Subhāna Allāh", ur = "سبحان اللہ", bn = "সুবহানাল্লাহ"), DhikrMeaning("Allah is free from all imperfections", "اللہ ہر نقص اور عیب سے پاک ہے", "আল্লাহ সকল ত্রুটি ও অসম্পূর্ণতা থেকে মুক্ত"), 33)
    private val stepHamd = DhikrStep("الْحَمْدُ لِلَّهِ", LocalizedText(en = "Alhamdulillah", ur = "الحمد للہ", bn = "আলহামদুলিল্লাহ"), LocalizedText(en = "Al-ḥamdu lillāh", ur = "الحمد للہ", bn = "আলহামদুলিল্লাহ"), DhikrMeaning("All absolute praise and gratitude belong solely to Allah", "تمام کامل تعریفیں اور شکر صرف اللہ کے لیے ہیں", "সমস্ত নিরঙ্কুশ প্রশংসা এবং কৃতজ্ঞতা একমাত্র আল্লাহর"), 33)
    private val stepAkbar34 = DhikrStep("اللَّهُ أَكْبَرُ", LocalizedText(en = "Allahu Akbar", ur = "اللہ اکبر", bn = "আল্লাহু আকবার"), LocalizedText(en = "Allāhu akbar", ur = "اللہ اکبر", bn = "আল্লাহু আকবার"), DhikrMeaning("Allah is infinitely greater than anything else we can comprehend", "اللہ ہماری سوچ سے بھی کہیں زیادہ بڑا اور عظیم ہے", "আল্লাহ আমাদের উপলব্ধির চেয়েও অসীম মহান"), 34)
    private val stepAkbar33 = stepAkbar34.copy(target = 33)
    private val stepTahlil = DhikrStep("لَا إِلَٰهَ إِلَّا اللَّهُ", LocalizedText(en = "La ilaha illallah", ur = "لا الہ الا اللہ", bn = "লা ইলাহা ইল্লাল্লাহ"), LocalizedText(en = "Lā ilāha illā Allāh", ur = "لا الہ الا اللہ", bn = "লা ইলাহা ইল্লাল্লাহ"), DhikrMeaning("There is no deity worthy of worship except Allah", "اللہ کے سوا کوئی عبادت کے لائق نہیں", "আল্লাহ ছাড়া ইবাদতের যোগ্য কোনো উপাস্য নেই"), 1)

    /**
     * Human-readable name for a stored key, or the raw key if it isn't a built-in
     * entry (e.g. a custom dhikr). Used for labelling saved sessions.
     */
    fun displayNameFor(key: String): LocalizedText = byKey[key]?.displayName ?: LocalizedText(en = key, ur = key, bn = key)

    /** Returns the multi-step sequence for a post-Salah entry key, or null. */
    fun sequenceFor(key: String): DhikrSequence? = when (key) {
        "SMART_FLOW_CLASSIC" -> DhikrSequence(
            key,
            LocalizedText(
                en = "Tasbīḥ after Salah · Classic",
                ur = "نماز کے بعد تسبیح · کلاسک",
                bn = "নামাজের পরে তাসবিহ · ক্লাসিক"
            ),
            listOf(stepSubhan, stepHamd, stepAkbar34)
        )
        "SMART_FLOW_WITH_TAHLIL" -> DhikrSequence(
            key,
            LocalizedText(
                en = "Tasbīḥ after Salah · With Tahlīl",
                ur = "نماز کے بعد تسبیح · تہلیل کے ساتھ",
                bn = "নামাজের পরে তাসবিহ · তাহলীল সহ"
            ),
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
            transliteration = item.transliteration,
            meaning = item.meaning,
            target = item.defaultTarget,
            spiritualReward = item.spiritualReward,
            hadithRef = item.hadithRef,
            sequenceKey = if (item.isSmartFlow) item.key else null
        )
    }
}
