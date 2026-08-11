package com.kitalonlabs.sabeel.domain.model

object AsmaUlHusnaCatalog {

    val list = listOf(
        DhikrItem(
            key = "ASMA_01_ALLAH",
            arabicText = "اللَّهُ",
            displayName = LocalizedText(en = "Allah", bn = "আল্লাহ"),
            transliteration = LocalizedText(en = "Allāh", bn = "আল্লাহ"),
            meaning = DhikrMeaning(
                en = "The One True God, possessing all attributes of perfection",
                bn = "একমাত্র সত্য উপাস্য, যিনি সমস্ত পূর্ণতার গুণের অধিকারী"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "The supreme name. Many find that reciting it settles the heart and deepens reliance on Him.",
                bn = "সর্বোচ্চ নাম। অনেকে দেখেন এটি পাঠ করলে অন্তর স্থির হয় এবং তাঁর ওপর নির্ভরতা গভীর হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_02_AR_RAHMAN",
            arabicText = "الرَّحْمَنُ",
            displayName = LocalizedText(en = "Ar-Rahman", bn = "আর-রহমান"),
            transliteration = LocalizedText(en = "Ar-Raḥmān", bn = "আর-রহমান"),
            meaning = DhikrMeaning(
                en = "The Most Gracious; His mercy is all-inclusive, embracing all creation",
                bn = "পরম করুণাময়; যাঁর দয়া সমস্ত সৃষ্টিকে পরিবেষ্টন করে আছে"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Many recite this seeking peace for a restless heart and Divine mercy in times of distress.",
                bn = "অনেকে অশান্ত অন্তরের প্রশান্তি এবং কষ্টের সময় ঐশী করুণার আশায় এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_03_AR_RAHIM",
            arabicText = "الرَّحِيمُ",
            displayName = LocalizedText(en = "Ar-Rahim", bn = "আর-রহীম"),
            transliteration = LocalizedText(en = "Ar-Raḥīm", bn = "আর-রহীম"),
            meaning = DhikrMeaning(
                en = "The Most Merciful; granting specific, continuous mercy to the believers",
                bn = "অসীম দয়ালু; যিনি মুমিনদের প্রতি বিশেষ ও অবিরাম দয়া প্রদর্শন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Traditionally recited to foster compassion in the heart, and in hope of protection from unforeseen calamities.",
                bn = "প্রথাগতভাবে অন্তরে কোমলতা গড়ে তুলতে এবং অপ্রত্যাশিত বিপদ থেকে সুরক্ষার আশায় পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_04_AL_MALIK",
            arabicText = "الْمَلِكُ",
            displayName = LocalizedText(en = "Al-Malik", bn = "আল-মালিক"),
            transliteration = LocalizedText(en = "Al-Malik", bn = "আল-মালিক"),
            meaning = DhikrMeaning(
                en = "The Absolute Sovereign and King; He owns the universe and everything within it",
                bn = "পরম অধিপতি ও বাদশা; তিনি মহাবিশ্ব এবং এর ভেতরের সবকিছুর মালিক"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Often recited in hope of provision, standing among people, and spiritual dignity.",
                bn = "রিজিক, মানুষের মধ্যে মর্যাদা এবং আধ্যাত্মিক সম্মানের আশায় প্রায়ই পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_05_AL_QUDDUS",
            arabicText = "الْقُدُّوسُ",
            displayName = LocalizedText(en = "Al-Quddus", bn = "আল-কুদ্দুস"),
            transliteration = LocalizedText(en = "Al-Quddūs", bn = "আল-কুদ্দু:স"),
            meaning = DhikrMeaning(
                en = "The Pure One; completely free from any imperfection or flaw",
                bn = "পরম পবিত্র; যিনি যেকোনো অসম্পূর্ণতা বা ত্রুটি থেকে সম্পূর্ণ মুক্ত"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Many recite this seeking purification of the heart from spiritual unease, anxiety, and troubling thoughts.",
                bn = "অনেকে আধ্যাত্মিক অস্থিরতা, উদ্বেগ এবং কষ্টদায়ক চিন্তা থেকে অন্তরের পবিত্রতার আশায় এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_06_AS_SALAM",
            arabicText = "السَّلَامُ",
            displayName = LocalizedText(en = "As-Salam", bn = "আস-সালাম"),
            transliteration = LocalizedText(en = "As-Salām", bn = "আস-সালাম"),
            meaning = DhikrMeaning(
                en = "The Source of Peace; the One who is flawless and brings tranquility to creation",
                bn = "শান্তির উৎস; যিনি ত্রুটিমুক্ত এবং সৃষ্টির জন্য প্রশান্তি নিয়ে আসেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Traditionally recited for comfort during illness, to calm household discord, and for deep inner peace. Not a substitute for medical care.",
                bn = "প্রথাগতভাবে অসুস্থতার সময় সান্ত্বনার জন্য, পারিবারিক অশান্তি প্রশমনে এবং গভীর মানসিক শান্তির জন্য পাঠ করা হয়। এটি চিকিৎসার বিকল্প নয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_07_AL_MU_MIN",
            arabicText = "الْمُؤْمِنُ",
            displayName = LocalizedText(en = "Al-Mu'min", bn = "আল-মুমিন"),
            transliteration = LocalizedText(en = "Al-Mu'min", bn = "আল-মুমিন"),
            meaning = DhikrMeaning(
                en = "The Inspirer of Faith; the One who grants security and validates His messengers",
                bn = "ঈমান দানকারী; যিনি নিরাপত্তা প্রদান করেন এবং তাঁর রাসূলদের সত্যতা প্রমাণ করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Often recited to ease fear, in hope of protection from oppressors, and to strengthen one's conviction.",
                bn = "ভয় লাঘব করতে, অত্যাচারী থেকে সুরক্ষার আশায় এবং বিশ্বাস দৃঢ় করতে প্রায়ই পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_08_AL_MUHAYMIN",
            arabicText = "الْمُهَيْمِنُ",
            displayName = LocalizedText(en = "Al-Muhaymin", bn = "আল-মুহাইমিন"),
            transliteration = LocalizedText(en = "Al-Muhaymin", bn = "আল-মুহাইমিন"),
            meaning = DhikrMeaning(
                en = "The Guardian and Overseer; the One who witnesses, protects, and determines all affairs",
                bn = "রক্ষক ও তত্ত্বাবধায়ক; যিনি সবকিছু দেখেন, রক্ষা করেন এবং সমস্ত বিষয় নির্ধারণ করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited by many seeking inner purification and a sense of divine protection.",
                bn = "অনেকে অভ্যন্তরীণ পবিত্রতা এবং ঐশী সুরক্ষার অনুভূতির আশায় এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_09_AL_AZIZ",
            arabicText = "الْعَزِيزُ",
            displayName = LocalizedText(en = "Al-Aziz", bn = "আল-আজিজ"),
            transliteration = LocalizedText(en = "Al-'Azīz", bn = "আল-'আজ়ী:জ"),
            meaning = DhikrMeaning(
                en = "The Almighty and Invincible; the Honorable whose decree cannot be overcome",
                bn = "পরাক্রমশালী ও অপ্রতিরোধ্য; সেই সম্মানিত যাঁর সিদ্ধান্ত খণ্ডন করা যায় না"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Traditionally recited in hope of restored honor, success, and freedom from dependence on others.",
                bn = "প্রথাগতভাবে হারানো সম্মান ফিরে পাওয়া, সাফল্য এবং অন্যের ওপর নির্ভরতা থেকে মুক্তির আশায় পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_10_AL_JABBAR",
            arabicText = "الْجَبَّارُ",
            displayName = LocalizedText(en = "Al-Jabbar", bn = "আল-জাব্বার"),
            transliteration = LocalizedText(en = "Al-Jabbār", bn = "আল-জাব্বার"),
            meaning = DhikrMeaning(
                en = "The Compeller and Restorer; He mends the brokenhearted and enforces His will",
                bn = "প্রবল পরাক্রান্ত ও ক্ষতিপূরণকারী; তিনি ভগ্ন হৃদয় জোড়া লাগান এবং তাঁর ইচ্ছা বাস্তবায়ন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Many recite this seeking comfort after emotional pain, reconciliation in broken relationships, and protection against tyranny.",
                bn = "অনেকে মানসিক কষ্টের পর সান্ত্বনা, ভাঙা সম্পর্কে পুনর্মিলন এবং স্বৈরাচার থেকে সুরক্ষার আশায় এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_11_AL_MUTAKABBIR",
            arabicText = "الْمُتَكَبِّرُ",
            displayName = LocalizedText(en = "Al-Mutakabbir", bn = "আল-মুতাকাব্বির"),
            transliteration = LocalizedText(en = "Al-Mutakabbir", bn = "আল-মুতাকাব্বির"),
            meaning = DhikrMeaning(
                en = "The Supremely Great; the One who rightfully possesses all majesty",
                bn = "সর্বশ্রেষ্ঠ ও অহংকারী; যিনি ন্যায়ত সমস্ত মহিমার অধিকারী"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon to guard against arrogance in oneself, and recited in hope of standing and respect in society.",
                bn = "নিজের মধ্যে অহংকার থেকে সতর্ক থাকতে এবং সমাজে মর্যাদা ও সম্মানের আশায় এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_12_AL_KHALIQ",
            arabicText = "الْخَالِقُ",
            displayName = LocalizedText(en = "Al-Khaliq", bn = "আল-খালিক"),
            transliteration = LocalizedText(en = "Al-Khāliq", bn = "আল-খা:লিক্ব"),
            meaning = DhikrMeaning(
                en = "The Creator; the One who brings everything from non-existence into existence",
                bn = "স্রষ্টা; যিনি সবকিছুকে শূন্য থেকে অস্তিত্বে নিয়ে আসেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Many recite this seeking clarity of mind and protection against unforeseen dangers at night.",
                bn = "অনেকে মনের স্বচ্ছতা এবং রাতে অপ্রত্যাশিত বিপদ থেকে সুরক্ষার আশায় এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_13_AL_BARI",
            arabicText = "الْبَارِئُ",
            displayName = LocalizedText(en = "Al-Bari", bn = "আল-বারি"),
            transliteration = LocalizedText(en = "Al-Bāri'", bn = "আল-বারি"),
            meaning = DhikrMeaning(
                en = "The Maker and Originator; the One who executes creation flawlessly",
                bn = "নির্মাণকারী ও রূপদাতা; যিনি নিখুঁতভাবে সৃষ্টি সম্পাদন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited by some for solace during serious illness, alongside proper medical care, and in hope of a peaceful state of mind.",
                bn = "কিছু মানুষ গুরুতর অসুস্থতার সময় সান্ত্বনার জন্য, উপযুক্ত চিকিৎসার পাশাপাশি, এবং মনের প্রশান্তির আশায় এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_14_AL_MUSAWWIR",
            arabicText = "الْمُصَوِّرُ",
            displayName = LocalizedText(en = "Al-Musawwir", bn = "আল-মুছাওউইর"),
            transliteration = LocalizedText(en = "Al-Muṣawwir", bn = "আল-মুছাওউইর"),
            meaning = DhikrMeaning(
                en = "The Fashioner of Forms; the One who gives everything its unique shape and nature",
                bn = "রূপদানকারী; যিনি প্রত্যেক বস্তুকে তার অনন্য রূপ ও প্রকৃতি দান করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Traditionally recited seeking blessings during pregnancy, and for cultivating inner beauty and good character.",
                bn = "প্রথাগতভাবে গর্ভাবস্থায় বরকতের আশায় এবং অভ্যন্তরীণ সৌন্দর্য ও চরিত্র গঠনের জন্য পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_15_AL_GHAFFAR",
            arabicText = "الْغَفَّارُ",
            displayName = LocalizedText(en = "Al-Ghaffar", bn = "আল-গাফফার"),
            transliteration = LocalizedText(en = "Al-Ghaffār", bn = "আল-গাফ্ফা:র"),
            meaning = DhikrMeaning(
                en = "The Perpetual Forgiver; the One who continuously conceals and forgives repeated sins",
                bn = "অধিক ক্ষমাকারী; যিনি বারবার পাপ গোপন করেন এবং ক্ষমা করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Many recite this seeking relief from the burden of past mistakes, and hope in place of despair.",
                bn = "অনেকে অতীতের ভুলের বোঝা থেকে মুক্তি এবং হতাশার বদলে আশার জন্য এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_16_AL_QAHHAR",
            arabicText = "الْقَهَّارُ",
            displayName = LocalizedText(en = "Al-Qahhar", bn = "আল-কাহহার"),
            transliteration = LocalizedText(en = "Al-Qahhār", bn = "আল-কাহহার"),
            meaning = DhikrMeaning(
                en = "The Subduer; the One who prevails over all creation and vanquishes opposition",
                bn = "সর্বনিয়ন্তা; যিনি সমগ্র সৃষ্টির উপর প্রবল এবং বিরোধিতাকে পরাস্ত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited by some in the struggle against worldly desires, harmful habits, and inner temptation.",
                bn = "কিছু মানুষ পার্থিব বাসনা, ক্ষতিকর অভ্যাস এবং অভ্যন্তরীণ প্রলোভনের বিরুদ্ধে সংগ্রামে এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_17_AL_WAHHAB",
            arabicText = "الْوَهَّابُ",
            displayName = LocalizedText(en = "Al-Wahhab", bn = "আল-ওয়াহহাব"),
            transliteration = LocalizedText(en = "Al-Wahhāb", bn = "আল-ওয়াহহাব"),
            meaning = DhikrMeaning(
                en = "The Supreme Bestower; the One who gives abundantly without expecting anything in return",
                bn = "মহা দাতা; যিনি কোন কিছুর প্রত্যাশা ছাড়াই প্রচুর পরিমাণে দান করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Traditionally recited in hope of open doors to sustenance and relief from poverty.",
                bn = "প্রথাগতভাবে রিজিকের দরজা খোলার এবং দারিদ্র্য থেকে মুক্তির আশায় পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_18_AR_RAZZAQ",
            arabicText = "الرَّزَّاقُ",
            displayName = LocalizedText(en = "Ar-Razzaq", bn = "আর-রাজ্জাক"),
            transliteration = LocalizedText(en = "Ar-Razzāq", bn = "আর-রাজ্জাক"),
            meaning = DhikrMeaning(
                en = "The Provider; the One who creates and sustains all forms of nourishment",
                bn = "রিজিকদাতা; যিনি সকল প্রকার জীবিকা সৃষ্টি করেন এবং সরবরাহ করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Often recited seeking steady, blessed provision and relief from anxiety about the future.",
                bn = "স্থির ও বরকতময় রিজিক এবং ভবিষ্যৎ নিয়ে উদ্বেগ থেকে মুক্তির আশায় প্রায়ই পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_19_AL_FATTAH",
            arabicText = "الْفَتَّاحُ",
            displayName = LocalizedText(en = "Al-Fattah", bn = "আল-ফাত্তাহ"),
            transliteration = LocalizedText(en = "Al-Fattāḥ", bn = "আল-ফাত্তাহ"),
            meaning = DhikrMeaning(
                en = "The Supreme Opener; the One who clears blocks and judges with ultimate fairness",
                bn = "মহা উন্মোচক; যিনি বাধা দূর করেন এবং চূড়ান্ত ন্যায্যতার সাথে বিচার করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited by many seeking clarity amid confusion and a way forward through difficult problems.",
                bn = "অনেকে বিভ্রান্তির মধ্যে স্বচ্ছতা এবং জটিল সমস্যার মধ্য দিয়ে এগিয়ে যাওয়ার পথের আশায় এটি পাঠ করেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_20_AL_ALIM",
            arabicText = "الْعَلِيمُ",
            displayName = LocalizedText(en = "Al-Alim", bn = "আল-আলীম"),
            transliteration = LocalizedText(en = "Al-'Alīm", bn = "আল-আলীম"),
            meaning = DhikrMeaning(
                en = "The All-Knowing; the One whose knowledge encompasses the past, present, and future",
                bn = "সর্বজ্ঞানী; যাঁর জ্ঞান অতীত, বর্তমান এবং ভবিষ্যৎকে পরিবেষ্টন করে"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Many recite this while studying or seeking wisdom; reflecting on it is said to bring the heart closer to unseen truths.",
                bn = "অনেকে পড়াশোনার সময় বা প্রজ্ঞার আশায় এটি পাঠ করেন; এ নিয়ে চিন্তা করলে অন্তর অদেখা সত্যের কাছাকাছি আসে বলে বিশ্বাস করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_21_AL_QAABID",
            arabicText = "الْقَابِضُ",
            displayName = LocalizedText(en = "Al-Qaabid", bn = "আল-ক্বাবিদ"),
            transliteration = LocalizedText(en = "Al-Qābiḍ", bn = "আল-ক্বাবিদ"),
            meaning = DhikrMeaning(
                en = "The Restricter; the One who withholds sustenance or souls by His wisdom",
                bn = "সংকোচনকারী; যিনি তাঁর প্রজ্ঞা দ্বারা রিজিক বা আত্মাকে সংকুচিত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon for discipline over worldly desire, and comfort during times of spiritual hardship.",
                bn = "পার্থিব বাসনার ওপর নিয়ন্ত্রণ এবং আধ্যাত্মিক কষ্টের সময় সান্ত্বনার জন্য এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_22_AL_BAASIT",
            arabicText = "الْبَاسِطُ",
            displayName = LocalizedText(en = "Al-Baasit", bn = "আল-বাসিত"),
            transliteration = LocalizedText(en = "Al-Bāsiṭ", bn = "আল-বাসিত"),
            meaning = DhikrMeaning(
                en = "The Expander; the One who freely extends His provision and mercy",
                bn = "সম্প্রসারণকারী; যিনি তাঁর রিজিক ও দয়া অবাধে প্রসারিত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Traditionally recited in hope of expanded provision, joy in the heart, and lightness after a heavy heart.",
                bn = "প্রথাগতভাবে প্রশস্ত রিজিক, অন্তরে আনন্দ এবং ভারী মনের পর হালকা অনুভূতির আশায় পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_23_AL_KHAAFID",
            arabicText = "الْخَافِضُ",
            displayName = LocalizedText(en = "Al-Khaafid", bn = "আল-খাফিদ"),
            transliteration = LocalizedText(en = "Al-Khāfiḍ", bn = "আল-খাফিদ"),
            meaning = DhikrMeaning(
                en = "The Abaser; the One who brings down the arrogant and lowers falsehood",
                bn = "অবনমনকারী; যিনি অহংকারীদের নিচে নামান এবং মিথ্যাকে পদদলিত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon for protection from harm, humility of the ego, and submission before the Divine.",
                bn = "ক্ষতি থেকে সুরক্ষা, অহংকার দমন এবং আল্লাহর সামনে বিনয়ের জন্য এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_24_AR_RAAFI",
            arabicText = "الرَّافِعُ",
            displayName = LocalizedText(en = "Ar-Raafi", bn = "আর-রাফি"),
            transliteration = LocalizedText(en = "Ar-Rāfi'", bn = "আর-রাফি"),
            meaning = DhikrMeaning(
                en = "The Exalter; the One who raises the ranks of the righteous and elevates truth",
                bn = "উন্নীতকারী; যিনি পুণ্যবানদের মর্যাদা বৃদ্ধি করেন এবং সত্যকে উচ্চে স্থাপন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited in hope of elevated standing in this life and the next.",
                bn = "ইহকাল ও পরকালে উন্নত মর্যাদার আশায় এটি পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_25_AL_MU_IZZ",
            arabicText = "الْمُعِزُّ",
            displayName = LocalizedText(en = "Al-Mu'izz", bn = "আল-মুইজ"),
            transliteration = LocalizedText(en = "Al-Mu'izz", bn = "আল-মুইজ"),
            meaning = DhikrMeaning(
                en = "The Bestower of Honor; the One who grants dignity and power to whomever He wills",
                bn = "সম্মানদাতা; যিনি যাকে ইচ্ছা মর্যাদা ও ক্ষমতা দান করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon for dignity and steadiness in the heart, and in hope of protection from humiliation.",
                bn = "অন্তরে মর্যাদা ও স্থিরতা এবং অপমান থেকে সুরক্ষার আশায় এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_26_AL_MUDHILL",
            arabicText = "الْمُذِلُّ",
            displayName = LocalizedText(en = "Al-Mudhill", bn = "আল-মুজিল"),
            transliteration = LocalizedText(en = "Al-Mudhill", bn = "আল-মুজিল"),
            meaning = DhikrMeaning(
                en = "The Dishonorer; the One who humbles the oppressors and strips them of power",
                bn = "অপমানকারী; যিনি অত্যাচারীদের বিনীত করেন এবং তাদের ক্ষমতা কেড়ে নেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited in hope of safety from tyranny, ill intentions, and those who seek to bring you low.",
                bn = "স্বৈরাচার, মন্দ উদ্দেশ্য এবং যারা অবনমিত করতে চায় তাদের থেকে নিরাপত্তার আশায় এটি পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_27_AS_SAMI",
            arabicText = "السَّمِيعُ",
            displayName = LocalizedText(en = "As-Sami", bn = "আস-সামী"),
            transliteration = LocalizedText(en = "As-Samī'", bn = "আস-সামী"),
            meaning = DhikrMeaning(
                en = "The All-Hearing; the One who hears every sound, spoken or silent",
                bn = "সর্বশ্রোতা; যিনি প্রতিটি শব্দ শোনেন, তা উচ্চারিত হোক বা নীরব"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited with the hope that even unspoken prayers are heard by Him.",
                bn = "এই আশায় পাঠ করা হয় যে, না-বলা প্রার্থনাও তিনি শোনেন।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_28_AL_BASIR",
            arabicText = "الْبَصِيرُ",
            displayName = LocalizedText(en = "Al-Basir", bn = "আল-বাসীর"),
            transliteration = LocalizedText(en = "Al-Baṣīr", bn = "আল-বাসীর"),
            meaning = DhikrMeaning(
                en = "The All-Seeing; the One whose sight penetrates the hidden and the visible",
                bn = "সর্বদ্রষ্টা; যাঁর দৃষ্টি দৃশ্যমান এবং অদৃশ্য সবকিছু ভেদ করে"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon for spiritual insight (firasah), clarity, and mindfulness away from private sin.",
                bn = "আধ্যাত্মিক অন্তর্দৃষ্টি (ফিরাসাহ), স্বচ্ছতা এবং গোপন পাপ থেকে সচেতন থাকার জন্য এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_29_AL_HAKAM",
            arabicText = "الْحَكَمُ",
            displayName = LocalizedText(en = "Al-Hakam", bn = "আল-হাকাম"),
            transliteration = LocalizedText(en = "Al-Ḥakam", bn = "আল-হাকাম"),
            meaning = DhikrMeaning(
                en = "The Impartial Judge; the One who delivers perfect justice and settles all disputes",
                bn = "ন্যায়বিচারক; যিনি নিখুঁত বিচার করেন এবং সমস্ত বিবাদ নিষ্পত্তি করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Recited by the wronged in hope of justice, and reflected upon to instill fairness in one's own heart.",
                bn = "মজলুম ব্যক্তি ন্যায়বিচারের আশায় এটি পাঠ করেন, এবং নিজের অন্তরে ন্যায়পরায়ণতা গড়ে তুলতে এ নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_30_AL_ADL",
            arabicText = "الْعَدْلُ",
            displayName = LocalizedText(en = "Al-'Adl", bn = "আল-আদল"),
            transliteration = LocalizedText(en = "Al-'Adl", bn = "আল-আদল"),
            meaning = DhikrMeaning(
                en = "The Utterly Just; the One who is the source of all fairness and rectitude",
                bn = "পরম ন্যায়পরায়ণ; যিনি সমস্ত ন্যায্যতা ও সততার উৎস"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon for balance in one's life, and mindfulness against wronging or being wronged.",
                bn = "জীবনে ভারসাম্য এবং অন্যায় করা বা অন্যায়ের শিকার হওয়া সম্পর্কে সচেতনতার জন্য এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_31_AL_LATIF",
            arabicText = "اللَّطِيفُ",
            displayName = LocalizedText(en = "Al-Latif", bn = "আল-লতীফ"),
            transliteration = LocalizedText(en = "Al-Laṭīf", bn = "আল-লাতী:ফ"),
            meaning = DhikrMeaning(
                en = "The Subtle One; the One who knows the intricate details and is profoundly gentle",
                bn = "সূক্ষ্মদর্শী ও দয়ালু; যিনি ক্ষুদ্রতম বিবরণ জানেন এবং গভীরভাবে কোমল"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Often recited in hope of relief from hidden anxieties, ease in difficulty, and provision from unexpected places.",
                bn = "অদৃশ্য উদ্বেগ থেকে মুক্তি, কষ্টে স্বস্তি এবং অপ্রত্যাশিত জায়গা থেকে রিজিকের আশায় প্রায়ই পাঠ করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_32_AL_KHABIR",
            arabicText = "الْخَبِيرُ",
            displayName = LocalizedText(en = "Al-Khabir", bn = "আল-খাবীর"),
            transliteration = LocalizedText(en = "Al-Khabīr", bn = "আল-খাবীর"),
            meaning = DhikrMeaning(
                en = "The All-Aware; the One who knows the inner reality and secrets of all things",
                bn = "সর্বজ্ঞাত; যিনি সমস্ত কিছুর অভ্যন্তরীণ বাস্তবতা এবং গোপনীয়তা জানেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon for understanding in place of ignorance, awareness of hidden deception, and insight into deeper realities.",
                bn = "অজ্ঞতার বদলে বোধগম্যতা, গোপন প্রতারণা সম্পর্কে সচেতনতা এবং গভীর বাস্তবতার অন্তর্দৃষ্টির জন্য এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_33_AL_HALIM",
            arabicText = "الْحَلِيمُ",
            displayName = LocalizedText(en = "Al-Halim", bn = "আল-হালীম"),
            transliteration = LocalizedText(en = "Al-Ḥalīm", bn = "আল-হালীম"),
            meaning = DhikrMeaning(
                en = "The Forbearing; the One who is exceedingly patient and delays punishment",
                bn = "সহনশীল; যিনি অত্যন্ত ধৈর্যশীল এবং শাস্তি প্রদানে বিলম্ব করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Reflected upon when working to cool anger, build patience, and cultivate a gentle disposition.",
                bn = "রাগ প্রশমন, ধৈর্য গড়ে তোলা এবং কোমল স্বভাব চর্চার সময় এটি নিয়ে চিন্তা-ভাবনা করা হয়।"
            ),
            hadithRef = "Popular tradition, not a hadith",
            category = DhikrCategory.ASMA_UL_HUSNA
        )
    )
}
