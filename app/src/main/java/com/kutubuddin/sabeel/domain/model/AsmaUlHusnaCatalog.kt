package com.kutubuddin.sabeel.domain.model

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
                en = "The supreme name. Reciting it clears the heart and establishes absolute reliance on Him",
                bn = "সর্বোচ্চ নাম। এটি পাঠ করলে অন্তর পরিষ্কার হয় এবং তাঁর ওপর পরম নির্ভরতা তৈরি হয়"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Repeating this brings peace to a restless heart and draws Divine mercy in distress",
                bn = "এটি পাঠ করলে অশান্ত অন্তর প্রশান্তি লাভ করে এবং বিপদে ঐশী করুণা আকৃষ্ট হয়"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Fosters compassion in the heart and ensures safety from unforeseen calamities",
                bn = "অন্তরে কোমলতা সৃষ্টি করে এবং অপ্রত্যাশিত বিপদ থেকে নিরাপত্তা নিশ্চিত করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Grants financial independence, respect among people, and spiritual dignity",
                bn = "আর্থিক স্বাধীনতা, মানুষের মধ্যে সম্মান এবং আধ্যাত্মিক মর্যাদা দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_05_AL_QUDDUS",
            arabicText = "الْقُدُّوسُ",
            displayName = LocalizedText(en = "Al-Quddus", bn = "আল-কুদ্দুস"),
            transliteration = LocalizedText(en = "Al-Quddūs", bn = "আল-কুদ্দুস"),
            meaning = DhikrMeaning(
                en = "The Pure One; completely free from any imperfection or flaw",
                bn = "পরম পবিত্র; যিনি যেকোনো অসম্পূর্ণতা বা ত্রুটি থেকে সম্পূর্ণ মুক্ত"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Purifies the heart from spiritual diseases, anxiety, and wicked thoughts",
                bn = "অন্তরকে আধ্যাত্মিক ব্যাধি, উদ্বেগ এবং মন্দ চিন্তাভাবনা থেকে পবিত্র করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Heals physical illnesses, calms domestic disputes, and provides deep inner peace",
                bn = "শারীরিক অসুস্থতা নিরাময় করে, পারিবারিক বিবাদ মেটায় এবং গভীর মানসিক শান্তি দেয়"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Removes fear, safeguards against oppressors, and strengthens one's conviction",
                bn = "ভয় দূর করে, অত্যাচারীদের হাত থেকে রক্ষা করে এবং বিশ্বাসকে দৃঢ় করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Purifies one's inner state and provides a profound sense of divine protection",
                bn = "মানুষের অভ্যন্তরীণ অবস্থাকে পবিত্র করে এবং ঐশী সুরক্ষার গভীর অনুভূতি দেয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_09_AL_AZIZ",
            arabicText = "الْعَزِيزُ",
            displayName = LocalizedText(en = "Al-Aziz", bn = "আল-আজিজ"),
            transliteration = LocalizedText(en = "Al-'Azīz", bn = "আল-আজিজ"),
            meaning = DhikrMeaning(
                en = "The Almighty and Invincible; the Honorable whose decree cannot be overcome",
                bn = "পরাক্রমশালী ও অপ্রতিরোধ্য; সেই সম্মানিত যাঁর সিদ্ধান্ত খণ্ডন করা যায় না"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Restores lost honor, grants success, and removes dependence on creation",
                bn = "হারানো সম্মান ফিরিয়ে দেয়, সাফল্য দান করে এবং সৃষ্টির প্রতি নির্ভরতা দূর করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Heals emotional trauma, mends broken relationships, and protects against tyranny",
                bn = "মানসিক আঘাত নিরাময় করে, ভেঙে যাওয়া সম্পর্ক জোড়া লাগায় এবং স্বৈরাচার থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Destroys arrogance in the reciter and grants awe and respect in society",
                bn = "পাঠকারীর অহংকার ধ্বংস করে এবং সমাজে ভীতি ও সম্মান দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_12_AL_KHALIQ",
            arabicText = "الْخَالِقُ",
            displayName = LocalizedText(en = "Al-Khaliq", bn = "আল-খালিক"),
            transliteration = LocalizedText(en = "Al-Khāliq", bn = "আল-খালিক"),
            meaning = DhikrMeaning(
                en = "The Creator; the One who brings everything from non-existence into existence",
                bn = "স্রষ্টা; যিনি সবকিছুকে শূন্য থেকে অস্তিত্বে নিয়ে আসেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Illuminates the mind with solutions and protects against unforeseen dangers at night",
                bn = "মনকে সমাধান দিয়ে আলোকিত করে এবং রাতে অপ্রত্যাশিত বিপদ থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Eases the struggles of severe illness and grants a peaceful state of mind",
                bn = "কঠিন অসুস্থতার কষ্ট লাঘব করে এবং মনের শান্ত অবস্থা দান করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Often recited for blessings in pregnancy and for cultivating inner beauty and character",
                bn = "গর্ভাবস্থায় বরকত এবং অভ্যন্তরীণ সৌন্দর্য ও চরিত্র গঠনের জন্য পড়া হয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_15_AL_GHAFFAR",
            arabicText = "الْغَفَّارُ",
            displayName = LocalizedText(en = "Al-Ghaffar", bn = "আল-গাফফার"),
            transliteration = LocalizedText(en = "Al-Ghaffār", bn = "আল-গাফফার"),
            meaning = DhikrMeaning(
                en = "The Perpetual Forgiver; the One who continuously conceals and forgives repeated sins",
                bn = "অধিক ক্ষমাকারী; যিনি বারবার পাপ গোপন করেন এবং ক্ষমা করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Erases the burden of past mistakes and replaces despair with profound hope",
                bn = "অতীতের ভুলের বোঝা মুছে ফেলে এবং হতাশাকে গভীর আশায় রূপান্তরিত করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Conquers worldly desires, breaks bad habits, and provides victory over inner demons",
                bn = "পার্থিব বাসনাকে জয় করে, বদভ্যাস ভাঙে এবং অভ্যন্তরীণ শয়তানের উপর বিজয় দান করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Opens locked doors of sustenance, fulfills impossible desires, and ends poverty",
                bn = "রিজিকের বন্ধ দরজা খুলে দেয়, অসম্ভব আকাঙ্ক্ষা পূরণ করে এবং দারিদ্র্য দূর করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Ensures a steady flow of pure, blessed provision and removes anxiety about the future",
                bn = "পবিত্র এবং বরকতময় রিজিকের অবিরাম প্রবাহ নিশ্চিত করে এবং ভবিষ্যৎ সম্পর্কে দুশ্চিন্তা দূর করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Grants clarity in confusion, solves complex problems, and opens paths to success",
                bn = "বিভ্রান্তিতে স্বচ্ছতা দেয়, জটিল সমস্যার সমাধান করে এবং সাফল্যের পথ খুলে দেয়"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Increases retention, grants profound wisdom, and illuminates the heart with unseen truths",
                bn = "স্মৃতিশক্তি বৃদ্ধি করে, গভীর প্রজ্ঞা দান করে এবং অন্তরকে অদেখা সত্য দিয়ে আলোকিত করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Provides discipline over worldly desires and comfort during times of spiritual constriction",
                bn = "পার্থিব বাসনার উপর নিয়ন্ত্রণ দেয় এবং আধ্যাত্মিক সংকটের সময় সান্ত্বনা প্রদান করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Brings expansion in wealth, joy in the heart, and relief from depression",
                bn = "সম্পদে প্রসারতা আনে, অন্তরে আনন্দ দেয় এবং বিষণ্ণতা থেকে মুক্তি দেয়"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Protects from enemies, subdues the ego, and humbles one's soul before the Divine",
                bn = "শত্রুদের হাত থেকে রক্ষা করে, অহংকার দমন করে এবং আত্মাকে আল্লাহর সামনে বিনীত করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Elevates status in both worlds, bringing spiritual height and societal respect",
                bn = "উভয় জগতে মর্যাদা বৃদ্ধি করে, আধ্যাত্মিক উচ্চতা এবং সামাজিক সম্মান প্রদান করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Instills fearless dignity in the heart and shields against humiliation by others",
                bn = "অন্তরে নির্ভীক মর্যাদা সঞ্চার করে এবং অন্যদের দ্বারা অপমানিত হওয়া থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Grants safety from tyrants, evil plots, and those who seek to degrade you",
                bn = "স্বৈরাচারী, মন্দ ষড়যন্ত্র এবং যারা আপনাকে অবনমিত করতে চায় তাদের থেকে নিরাপত্তা দেয়"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Ensures your deepest, unspoken prayers are answered swiftly",
                bn = "নিশ্চিত করে যে আপনার গভীর, না বলা প্রার্থনাগুলো দ্রুত কবুল হয়"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Grants spiritual insight (Firasah), clarity of vision, and keeps one away from secret sins",
                bn = "আধ্যাত্মিক অন্তর্দৃষ্টি (ফিরাসাহ), দৃষ্টির স্বচ্ছতা দান করে এবং গোপন পাপ থেকে দূরে রাখে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Vindicates the oppressed, reveals the truth, and instills fairness in one’s own heart",
                bn = "মজলুমের পক্ষে রায় দেয়, সত্য প্রকাশ করে এবং নিজের অন্তরে ন্যায়পরায়ণতা সঞ্চার করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Brings absolute balance into one's life and protects against being wronged or wronging others",
                bn = "জীবনে পরম ভারসাম্য নিয়ে আসে এবং অন্যের প্রতি অন্যায় করা বা অন্যায়ের শিকার হওয়া থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_31_AL_LATIF",
            arabicText = "اللَّطِيفُ",
            displayName = LocalizedText(en = "Al-Latif", bn = "আল-লতীফ"),
            transliteration = LocalizedText(en = "Al-Laṭīf", bn = "আল-লতীফ"),
            meaning = DhikrMeaning(
                en = "The Subtle One; the One who knows the intricate details and is profoundly gentle",
                bn = "সূক্ষ্মদর্শী ও দয়ালু; যিনি ক্ষুদ্রতম বিবরণ জানেন এবং গভীরভাবে কোমল"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Relieves invisible anxieties, smooths out difficulties, and brings provisions from unexpected places",
                bn = "অদৃশ্য উদ্বেগ দূর করে, অসুবিধাগুলো সহজ করে এবং অপ্রত্যাশিত জায়গা থেকে রিজিক নিয়ে আসে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Cures ignorance, protects from hidden deception, and grants understanding of deep realities",
                bn = "অজ্ঞতা নিরাময় করে, গোপন প্রতারণা থেকে রক্ষা করে এবং গভীর বাস্তবতা বোঝার ক্ষমতা দান করে"
            ),
            hadithRef = "Asma ul Husna",
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
                en = "Cools anger, develops immense personal patience, and grants a gentle disposition",
                bn = "রাগ প্রশমিত করে, অপরিসীম ব্যক্তিগত ধৈর্য বিকাশ করে এবং একটি কোমল স্বভাব দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        )
    )
}
