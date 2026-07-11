package com.kutubuddin.sabeel.domain.model

object AsmaUlHusnaCatalog {

    val list = listOf(
        DhikrItem(
            key = "ASMA_01_ALLAH",
            arabicText = "اللَّهُ",
            displayName = LocalizedText(en = "Allah", ur = "اللہ", bn = "আল্লাহ"),
            transliteration = "Allāh",
            meaning = DhikrMeaning(
                en = "The One True God, possessing all attributes of perfection",
                ur = "وہ یکتا معبود، جو تمام کمالات کی صفات کا مالک ہے",
                bn = "একমাত্র সত্য উপাস্য, যিনি সমস্ত পূর্ণতার গুণের অধিকারী"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "The supreme name. Reciting it clears the heart and establishes absolute reliance on Him",
                ur = "سب سے عظیم نام۔ اس کا ذکر دل کو پاک کرتا ہے اور اس پر کامل توکل پیدا کرتا ہے",
                bn = "সর্বোচ্চ নাম। এটি পাঠ করলে অন্তর পরিষ্কার হয় এবং তাঁর ওপর পরম নির্ভরতা তৈরি হয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_02_AR_RAHMAN",
            arabicText = "الرَّحْمَنُ",
            displayName = LocalizedText(en = "Ar-Rahman", ur = "الرحمٰن", bn = "আর-রহমান"),
            transliteration = "Ar-Raḥmān",
            meaning = DhikrMeaning(
                en = "The Most Gracious; His mercy is all-inclusive, embracing all creation",
                ur = "بے حد رحم کرنے والا؛ جس کی رحمت تمام مخلوقات کو گھیرے ہوئے ہے",
                bn = "পরম করুণাময়; যাঁর দয়া সমস্ত সৃষ্টিকে পরিবেষ্টন করে আছে"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Repeating this brings peace to a restless heart and draws Divine mercy in distress",
                ur = "اس کا ذکر بے چین دل کو سکون بخشتا ہے اور مشکل میں الٰہی رحمت کو متوجہ کرتا ہے",
                bn = "এটি পাঠ করলে অশান্ত অন্তর প্রশান্তি লাভ করে এবং বিপদে ঐশী করুণা আকৃষ্ট হয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_03_AR_RAHIM",
            arabicText = "الرَّحِيمُ",
            displayName = LocalizedText(en = "Ar-Rahim", ur = "الرحیم", bn = "আর-রহীম"),
            transliteration = "Ar-Raḥīm",
            meaning = DhikrMeaning(
                en = "The Most Merciful; granting specific, continuous mercy to the believers",
                ur = "نہایت مہربان؛ جو مومنوں پر خاص اور مسلسل رحم فرماتا ہے",
                bn = "অসীম দয়ালু; যিনি মুমিনদের প্রতি বিশেষ ও অবিরাম দয়া প্রদর্শন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Fosters compassion in the heart and ensures safety from unforeseen calamities",
                ur = "دل میں نرمی پیدا کرتا ہے اور ناگہانی آفات سے حفاظت کی ضمانت دیتا ہے",
                bn = "অন্তরে কোমলতা সৃষ্টি করে এবং অপ্রত্যাশিত বিপদ থেকে নিরাপত্তা নিশ্চিত করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_04_AL_MALIK",
            arabicText = "الْمَلِكُ",
            displayName = LocalizedText(en = "Al-Malik", ur = "الملک", bn = "আল-মালিক"),
            transliteration = "Al-Malik",
            meaning = DhikrMeaning(
                en = "The Absolute Sovereign and King; He owns the universe and everything within it",
                ur = "حقیقی بادشاہ؛ وہ کائنات اور اس کی ہر چیز کا مالک ہے",
                bn = "পরম অধিপতি ও বাদশা; তিনি মহাবিশ্ব এবং এর ভেতরের সবকিছুর মালিক"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Grants financial independence, respect among people, and spiritual dignity",
                ur = "مالی خودمختاری، لوگوں میں عزت اور روحانی وقار عطا کرتا ہے",
                bn = "আর্থিক স্বাধীনতা, মানুষের মধ্যে সম্মান এবং আধ্যাত্মিক মর্যাদা দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_05_AL_QUDDUS",
            arabicText = "الْقُدُّوسُ",
            displayName = LocalizedText(en = "Al-Quddus", ur = "القدوس", bn = "আল-কুদ্দুস"),
            transliteration = "Al-Quddūs",
            meaning = DhikrMeaning(
                en = "The Pure One; completely free from any imperfection or flaw",
                ur = "انتہائی پاک ذات؛ جو ہر نقص یا خامی سے مکمل طور پر پاک ہے",
                bn = "পরম পবিত্র; যিনি যেকোনো অসম্পূর্ণতা বা ত্রুটি থেকে সম্পূর্ণ মুক্ত"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Purifies the heart from spiritual diseases, anxiety, and wicked thoughts",
                ur = "دل کو روحانی بیماریوں، بے چینی اور برے خیالات سے پاک کرتا ہے",
                bn = "অন্তরকে আধ্যাত্মিক ব্যাধি, উদ্বেগ এবং মন্দ চিন্তাভাবনা থেকে পবিত্র করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_06_AS_SALAM",
            arabicText = "السَّلَامُ",
            displayName = LocalizedText(en = "As-Salam", ur = "السلام", bn = "আস-সালাম"),
            transliteration = "As-Salām",
            meaning = DhikrMeaning(
                en = "The Source of Peace; the One who is flawless and brings tranquility to creation",
                ur = "سلامتی کا سرچشمہ؛ جو بے عیب ہے اور مخلوق کو سکون بخشتا ہے",
                bn = "শান্তির উৎস; যিনি ত্রুটিমুক্ত এবং সৃষ্টির জন্য প্রশান্তি নিয়ে আসেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Heals physical illnesses, calms domestic disputes, and provides deep inner peace",
                ur = "جسمانی بیماریوں کو شفا دیتا ہے، گھریلو جھگڑے ختم کرتا ہے اور گہرا دلی سکون دیتا ہے",
                bn = "শারীরিক অসুস্থতা নিরাময় করে, পারিবারিক বিবাদ মেটায় এবং গভীর মানসিক শান্তি দেয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_07_AL_MU_MIN",
            arabicText = "الْمُؤْمِنُ",
            displayName = LocalizedText(en = "Al-Mu'min", ur = "المؤمن", bn = "আল-মুমিন"),
            transliteration = "Al-Mu'min",
            meaning = DhikrMeaning(
                en = "The Inspirer of Faith; the One who grants security and validates His messengers",
                ur = "ایمان عطا کرنے والا؛ جو امن بخشتا ہے اور اپنے رسولوں کی تصدیق کرتا ہے",
                bn = "ঈমান দানকারী; যিনি নিরাপত্তা প্রদান করেন এবং তাঁর রাসূলদের সত্যতা প্রমাণ করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Removes fear, safeguards against oppressors, and strengthens one's conviction",
                ur = "خوف دور کرتا ہے، ظالموں سے بچاتا ہے اور یقین کو مضبوط کرتا ہے",
                bn = "ভয় দূর করে, অত্যাচারীদের হাত থেকে রক্ষা করে এবং বিশ্বাসকে দৃঢ় করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_08_AL_MUHAYMIN",
            arabicText = "الْمُهَيْمِنُ",
            displayName = LocalizedText(en = "Al-Muhaymin", ur = "المھیمن", bn = "আল-মুহাইমিন"),
            transliteration = "Al-Muhaymin",
            meaning = DhikrMeaning(
                en = "The Guardian and Overseer; the One who witnesses, protects, and determines all affairs",
                ur = "نگہبان اور محافظ؛ جو سب دیکھتا ہے، بچاتا ہے اور تمام معاملات طے کرتا ہے",
                bn = "রক্ষক ও তত্ত্বাবধায়ক; যিনি সবকিছু দেখেন, রক্ষা করেন এবং সমস্ত বিষয় নির্ধারণ করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Purifies one's inner state and provides a profound sense of divine protection",
                ur = "انسان کے باطن کو پاک کرتا ہے اور الٰہی حفاظت کا گہرا احساس دیتا ہے",
                bn = "মানুষের অভ্যন্তরীণ অবস্থাকে পবিত্র করে এবং ঐশী সুরক্ষার গভীর অনুভূতি দেয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_09_AL_AZIZ",
            arabicText = "الْعَزِيزُ",
            displayName = LocalizedText(en = "Al-Aziz", ur = "العزیز", bn = "আল-আজিজ"),
            transliteration = "Al-'Azīz",
            meaning = DhikrMeaning(
                en = "The Almighty and Invincible; the Honorable whose decree cannot be overcome",
                ur = "غالب اور زبردست؛ وہ عزت والا جس کے فیصلے کو ٹالا نہیں جا سکتا",
                bn = "পরাক্রমশালী ও অপ্রতিরোধ্য; সেই সম্মানিত যাঁর সিদ্ধান্ত খণ্ডন করা যায় না"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Restores lost honor, grants success, and removes dependence on creation",
                ur = "کھوئی ہوئی عزت واپس دلاتا ہے، کامیابی بخشتا ہے اور مخلوق کی محتاجی ختم کرتا ہے",
                bn = "হারানো সম্মান ফিরিয়ে দেয়, সাফল্য দান করে এবং সৃষ্টির প্রতি নির্ভরতা দূর করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_10_AL_JABBAR",
            arabicText = "الْجَبَّارُ",
            displayName = LocalizedText(en = "Al-Jabbar", ur = "الجبار", bn = "আল-জাব্বার"),
            transliteration = "Al-Jabbār",
            meaning = DhikrMeaning(
                en = "The Compeller and Restorer; He mends the brokenhearted and enforces His will",
                ur = "زبردست اور نقصان پورا کرنے والا؛ وہ ٹوٹے دلوں کو جوڑتا ہے اور اپنا حکم نافذ کرتا ہے",
                bn = "প্রবল পরাক্রান্ত ও ক্ষতিপূরণকারী; তিনি ভগ্ন হৃদয় জোড়া লাগান এবং তাঁর ইচ্ছা বাস্তবায়ন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Heals emotional trauma, mends broken relationships, and protects against tyranny",
                ur = "جذباتی صدمات کو شفا دیتا ہے، ٹوٹے رشتے جوڑتا ہے اور ظلم سے بچاتا ہے",
                bn = "মানসিক আঘাত নিরাময় করে, ভেঙে যাওয়া সম্পর্ক জোড়া লাগায় এবং স্বৈরাচার থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_11_AL_MUTAKABBIR",
            arabicText = "الْمُتَكَبِّرُ",
            displayName = LocalizedText(en = "Al-Mutakabbir", ur = "المتکبر", bn = "আল-মুতাকাব্বির"),
            transliteration = "Al-Mutakabbir",
            meaning = DhikrMeaning(
                en = "The Supremely Great; the One who rightfully possesses all majesty",
                ur = "سب سے بڑا اور عظمت والا؛ جو حقیقی طور پر تمام بڑائی کا حقدار ہے",
                bn = "সর্বশ্রেষ্ঠ ও অহংকারী; যিনি ন্যায়ত সমস্ত মহিমার অধিকারী"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Destroys arrogance in the reciter and grants awe and respect in society",
                ur = "پڑھنے والے کا تکبر ختم کرتا ہے اور معاشرے میں رعب و عزت عطا کرتا ہے",
                bn = "পাঠকারীর অহংকার ধ্বংস করে এবং সমাজে ভীতি ও সম্মান দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_12_AL_KHALIQ",
            arabicText = "الْخَالِقُ",
            displayName = LocalizedText(en = "Al-Khaliq", ur = "الخالق", bn = "আল-খালিক"),
            transliteration = "Al-Khāliq",
            meaning = DhikrMeaning(
                en = "The Creator; the One who brings everything from non-existence into existence",
                ur = "پیدا کرنے والا؛ جو ہر چیز کو عدم سے وجود میں لاتا ہے",
                bn = "স্রষ্টা; যিনি সবকিছুকে শূন্য থেকে অস্তিত্বে নিয়ে আসেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Illuminates the mind with solutions and protects against unforeseen dangers at night",
                ur = "ذہن کو نئے حل سوجھاتا ہے اور رات کے وقت ناگہانی خطرات سے بچاتا ہے",
                bn = "মনকে সমাধান দিয়ে আলোকিত করে এবং রাতে অপ্রত্যাশিত বিপদ থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_13_AL_BARI",
            arabicText = "الْبَارِئُ",
            displayName = LocalizedText(en = "Al-Bari", ur = "البارئ", bn = "আল-বারি"),
            transliteration = "Al-Bāri'",
            meaning = DhikrMeaning(
                en = "The Maker and Originator; the One who executes creation flawlessly",
                ur = "جان ڈالنے والا اور بنانے والا؛ جو تخلیق کو بے عیب انجام دیتا ہے",
                bn = "নির্মাণকারী ও রূপদাতা; যিনি নিখুঁতভাবে সৃষ্টি সম্পাদন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Eases the struggles of severe illness and grants a peaceful state of mind",
                ur = "سخت بیماری کی تکلیف کو آسان کرتا ہے اور ذہنی سکون عطا کرتا ہے",
                bn = "কঠিন অসুস্থতার কষ্ট লাঘব করে এবং মনের শান্ত অবস্থা দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_14_AL_MUSAWWIR",
            arabicText = "الْمُصَوِّرُ",
            displayName = LocalizedText(en = "Al-Musawwir", ur = "المصور", bn = "আল-মুছাওউইর"),
            transliteration = "Al-Muṣawwir",
            meaning = DhikrMeaning(
                en = "The Fashioner of Forms; the One who gives everything its unique shape and nature",
                ur = "صورت بنانے والا؛ جو ہر چیز کو اس کی منفرد شکل اور فطرت دیتا ہے",
                bn = "রূপদানকারী; যিনি প্রত্যেক বস্তুকে তার অনন্য রূপ ও প্রকৃতি দান করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Often recited for blessings in pregnancy and for cultivating inner beauty and character",
                ur = "حمل میں برکت اور باطنی خوبصورتی اور کردار کو سنوارنے کے لیے پڑھا جاتا ہے",
                bn = "গর্ভাবস্থায় বরকত এবং অভ্যন্তরীণ সৌন্দর্য ও চরিত্র গঠনের জন্য পড়া হয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_15_AL_GHAFFAR",
            arabicText = "الْغَفَّارُ",
            displayName = LocalizedText(en = "Al-Ghaffar", ur = "الغفار", bn = "আল-গাফফার"),
            transliteration = "Al-Ghaffār",
            meaning = DhikrMeaning(
                en = "The Perpetual Forgiver; the One who continuously conceals and forgives repeated sins",
                ur = "بہت زیادہ بخشنے والا؛ جو بار بار ہونے والے گناہوں کو چھپاتا اور معاف کرتا ہے",
                bn = "অধিক ক্ষমাকারী; যিনি বারবার পাপ গোপন করেন এবং ক্ষমা করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Erases the burden of past mistakes and replaces despair with profound hope",
                ur = "ماضی کی غلطیوں کا بوجھ مٹا دیتا ہے اور مایوسی کو گہری امید میں بدل دیتا ہے",
                bn = "অতীতের ভুলের বোঝা মুছে ফেলে এবং হতাশাকে গভীর আশায় রূপান্তরিত করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_16_AL_QAHHAR",
            arabicText = "الْقَهَّارُ",
            displayName = LocalizedText(en = "Al-Qahhar", ur = "القهار", bn = "আল-কাহহার"),
            transliteration = "Al-Qahhār",
            meaning = DhikrMeaning(
                en = "The Subduer; the One who prevails over all creation and vanquishes opposition",
                ur = "سب پر غالب؛ جو تمام مخلوق پر حاوی ہے اور مخالفت کو مٹا دیتا ہے",
                bn = "সর্বনিয়ন্তা; যিনি সমগ্র সৃষ্টির উপর প্রবল এবং বিরোধিতাকে পরাস্ত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Conquers worldly desires, breaks bad habits, and provides victory over inner demons",
                ur = "دنیاوی خواہشات پر غلبہ دیتا ہے، بری عادتیں چھڑاتا ہے اور نفس پر فتح بخشتا ہے",
                bn = "পার্থিব বাসনাকে জয় করে, বদভ্যাস ভাঙে এবং অভ্যন্তরীণ শয়তানের উপর বিজয় দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_17_AL_WAHHAB",
            arabicText = "الْوَهَّابُ",
            displayName = LocalizedText(en = "Al-Wahhab", ur = "الوھاب", bn = "আল-ওয়াহহাব"),
            transliteration = "Al-Wahhāb",
            meaning = DhikrMeaning(
                en = "The Supreme Bestower; the One who gives abundantly without expecting anything in return",
                ur = "سب کچھ عطا کرنے والا؛ جو بغیر کسی غرض کے کثرت سے دیتا ہے",
                bn = "মহা দাতা; যিনি কোন কিছুর প্রত্যাশা ছাড়াই প্রচুর পরিমাণে দান করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Opens locked doors of sustenance, fulfills impossible desires, and ends poverty",
                ur = "رزق کے بند دروازے کھولتا ہے، ناممکن خواہشات پوری کرتا ہے اور غربت ختم کرتا ہے",
                bn = "রিজিকের বন্ধ দরজা খুলে দেয়, অসম্ভব আকাঙ্ক্ষা পূরণ করে এবং দারিদ্র্য দূর করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_18_AR_RAZZAQ",
            arabicText = "الرَّزَّاقُ",
            displayName = LocalizedText(en = "Ar-Razzaq", ur = "الرزاق", bn = "আর-রাজ্জাক"),
            transliteration = "Ar-Razzāq",
            meaning = DhikrMeaning(
                en = "The Provider; the One who creates and sustains all forms of nourishment",
                ur = "رزق دینے والا؛ جو ہر قسم کا رزق پیدا کرتا اور مہیا کرتا ہے",
                bn = "রিজিকদাতা; যিনি সকল প্রকার জীবিকা সৃষ্টি করেন এবং সরবরাহ করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Ensures a steady flow of pure, blessed provision and removes anxiety about the future",
                ur = "پاکیزہ اور بابرکت رزق کی فراوانی کو یقینی بناتا ہے اور مستقبل کی فکر دور کرتا ہے",
                bn = "পবিত্র এবং বরকতময় রিজিকের অবিরাম প্রবাহ নিশ্চিত করে এবং ভবিষ্যৎ সম্পর্কে দুশ্চিন্তা দূর করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_19_AL_FATTAH",
            arabicText = "الْفَتَّاحُ",
            displayName = LocalizedText(en = "Al-Fattah", ur = "الفتاح", bn = "আল-ফাত্তাহ"),
            transliteration = "Al-Fattāḥ",
            meaning = DhikrMeaning(
                en = "The Supreme Opener; the One who clears blocks and judges with ultimate fairness",
                ur = "کھولنے والا؛ جو رکاوٹیں دور کرتا ہے اور مکمل انصاف کے ساتھ فیصلہ کرتا ہے",
                bn = "মহা উন্মোচক; যিনি বাধা দূর করেন এবং চূড়ান্ত ন্যায্যতার সাথে বিচার করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Grants clarity in confusion, solves complex problems, and opens paths to success",
                ur = "الجھن میں رہنمائی دیتا ہے، پیچیدہ مسائل حل کرتا ہے اور کامیابی کے راستے کھولتا ہے",
                bn = "বিভ্রান্তিতে স্বচ্ছতা দেয়, জটিল সমস্যার সমাধান করে এবং সাফল্যের পথ খুলে দেয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_20_AL_ALIM",
            arabicText = "الْعَلِيمُ",
            displayName = LocalizedText(en = "Al-Alim", ur = "العلیم", bn = "আল-আলীম"),
            transliteration = "Al-'Alīm",
            meaning = DhikrMeaning(
                en = "The All-Knowing; the One whose knowledge encompasses the past, present, and future",
                ur = "سب کچھ جاننے والا؛ جس کا علم ماضی، حال اور مستقبل کو محیط ہے",
                bn = "সর্বজ্ঞানী; যাঁর জ্ঞান অতীত, বর্তমান এবং ভবিষ্যৎকে পরিবেষ্টন করে"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Increases retention, grants profound wisdom, and illuminates the heart with unseen truths",
                ur = "یادداشت بڑھاتا ہے، گہری حکمت عطا کرتا ہے اور دل کو غیبی حقائق سے منور کرتا ہے",
                bn = "স্মৃতিশক্তি বৃদ্ধি করে, গভীর প্রজ্ঞা দান করে এবং অন্তরকে অদেখা সত্য দিয়ে আলোকিত করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_21_AL_QAABID",
            arabicText = "الْقَابِضُ",
            displayName = LocalizedText(en = "Al-Qaabid", ur = "القابض", bn = "আল-ক্বাবিদ"),
            transliteration = "Al-Qābiḍ",
            meaning = DhikrMeaning(
                en = "The Restricter; the One who withholds sustenance or souls by His wisdom",
                ur = "تنگ کرنے والا؛ جو اپنی حکمت سے رزق یا روحوں کو روک لیتا ہے",
                bn = "সংকোচনকারী; যিনি তাঁর প্রজ্ঞা দ্বারা রিজিক বা আত্মাকে সংকুচিত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Provides discipline over worldly desires and comfort during times of spiritual constriction",
                ur = "دنیاوی خواہشات پر قابو پانے کی طاقت دیتا ہے اور روحانی تنگی کے وقت سکون بخشتا ہے",
                bn = "পার্থিব বাসনার উপর নিয়ন্ত্রণ দেয় এবং আধ্যাত্মিক সংকটের সময় সান্ত্বনা প্রদান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_22_AL_BAASIT",
            arabicText = "الْبَاسِطُ",
            displayName = LocalizedText(en = "Al-Baasit", ur = "الباسط", bn = "আল-বাসিত"),
            transliteration = "Al-Bāsiṭ",
            meaning = DhikrMeaning(
                en = "The Expander; the One who freely extends His provision and mercy",
                ur = "کشادہ کرنے والا؛ جو اپنے رزق اور رحمت کو فراوانی سے پھیلاتا ہے",
                bn = "সম্প্রসারণকারী; যিনি তাঁর রিজিক ও দয়া অবাধে প্রসারিত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Brings expansion in wealth, joy in the heart, and relief from depression",
                ur = "مال میں کشادگی لاتا ہے، دل کو خوشی دیتا ہے اور مایوسی سے نجات دلاتا ہے",
                bn = "সম্পদে প্রসারতা আনে, অন্তরে আনন্দ দেয় এবং বিষণ্ণতা থেকে মুক্তি দেয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_23_AL_KHAAFID",
            arabicText = "الْخَافِضُ",
            displayName = LocalizedText(en = "Al-Khaafid", ur = "الخافض", bn = "আল-খাফিদ"),
            transliteration = "Al-Khāfiḍ",
            meaning = DhikrMeaning(
                en = "The Abaser; the One who brings down the arrogant and lowers falsehood",
                ur = "پست کرنے والا؛ جو متکبرین کو نیچا دکھاتا ہے اور باطل کو گراتا ہے",
                bn = "অবনমনকারী; যিনি অহংকারীদের নিচে নামান এবং মিথ্যাকে পদদলিত করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Protects from enemies, subdues the ego, and humbles one's soul before the Divine",
                ur = "دشمنوں سے بچاتا ہے، نفس کو مغلوب کرتا ہے اور اللہ کے سامنے روح کو عاجز بناتا ہے",
                bn = "শত্রুদের হাত থেকে রক্ষা করে, অহংকার দমন করে এবং আত্মাকে আল্লাহর সামনে বিনীত করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_24_AR_RAAFI",
            arabicText = "الرَّافِعُ",
            displayName = LocalizedText(en = "Ar-Raafi", ur = "الرافع", bn = "আর-রাফি"),
            transliteration = "Ar-Rāfi'",
            meaning = DhikrMeaning(
                en = "The Exalter; the One who raises the ranks of the righteous and elevates truth",
                ur = "بلند کرنے والا؛ جو نیک لوگوں کے درجات بلند کرتا ہے اور حق کو سر بلند کرتا ہے",
                bn = "উন্নীতকারী; যিনি পুণ্যবানদের মর্যাদা বৃদ্ধি করেন এবং সত্যকে উচ্চে স্থাপন করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Elevates status in both worlds, bringing spiritual height and societal respect",
                ur = "دونوں جہانوں میں درجہ بلند کرتا ہے، روحانی بلندی اور معاشرتی عزت بخشتا ہے",
                bn = "উভয় জগতে মর্যাদা বৃদ্ধি করে, আধ্যাত্মিক উচ্চতা এবং সামাজিক সম্মান প্রদান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_25_AL_MU_IZZ",
            arabicText = "الْمُعِزُّ",
            displayName = LocalizedText(en = "Al-Mu'izz", ur = "المعز", bn = "আল-মুইজ"),
            transliteration = "Al-Mu'izz",
            meaning = DhikrMeaning(
                en = "The Bestower of Honor; the One who grants dignity and power to whomever He wills",
                ur = "عزت دینے والا؛ جو جسے چاہے وقار اور طاقت عطا کرتا ہے",
                bn = "সম্মানদাতা; যিনি যাকে ইচ্ছা মর্যাদা ও ক্ষমতা দান করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Instills fearless dignity in the heart and shields against humiliation by others",
                ur = "دل میں بے خوف وقار پیدا کرتا ہے اور دوسروں کی طرف سے رسوائی سے بچاتا ہے",
                bn = "অন্তরে নির্ভীক মর্যাদা সঞ্চার করে এবং অন্যদের দ্বারা অপমানিত হওয়া থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_26_AL_MUDHILL",
            arabicText = "الْمُذِلُّ",
            displayName = LocalizedText(en = "Al-Mudhill", ur = "المذل", bn = "আল-মুজিল"),
            transliteration = "Al-Mudhill",
            meaning = DhikrMeaning(
                en = "The Dishonorer; the One who humbles the oppressors and strips them of power",
                ur = "ذلت دینے والا؛ جو ظالموں کو عاجز کرتا ہے اور ان سے طاقت چھین لیتا ہے",
                bn = "অপমানকারী; যিনি অত্যাচারীদের বিনীত করেন এবং তাদের ক্ষমতা কেড়ে নেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Grants safety from tyrants, evil plots, and those who seek to degrade you",
                ur = "ظالموں، بری سازشوں اور آپ کو ذلیل کرنے والوں سے محفوظ رکھتا ہے",
                bn = "স্বৈরাচারী, মন্দ ষড়যন্ত্র এবং যারা আপনাকে অবনমিত করতে চায় তাদের থেকে নিরাপত্তা দেয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_27_AS_SAMI",
            arabicText = "السَّمِيعُ",
            displayName = LocalizedText(en = "As-Sami", ur = "السمیع", bn = "আস-সামী"),
            transliteration = "As-Samī'",
            meaning = DhikrMeaning(
                en = "The All-Hearing; the One who hears every sound, spoken or silent",
                ur = "سب کچھ سننے والا؛ جو ہر آواز، خواہ ظاہری ہو یا پوشیدہ، سنتا ہے",
                bn = "সর্বশ্রোতা; যিনি প্রতিটি শব্দ শোনেন, তা উচ্চারিত হোক বা নীরব"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Ensures your deepest, unspoken prayers are answered swiftly",
                ur = "اس بات کو یقینی بناتا ہے کہ آپ کی گہری اور ان کہی دعائیں جلد قبول ہوں",
                bn = "নিশ্চিত করে যে আপনার গভীর, না বলা প্রার্থনাগুলো দ্রুত কবুল হয়"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_28_AL_BASIR",
            arabicText = "الْبَصِيرُ",
            displayName = LocalizedText(en = "Al-Basir", ur = "البصیر", bn = "আল-বাসীর"),
            transliteration = "Al-Baṣīr",
            meaning = DhikrMeaning(
                en = "The All-Seeing; the One whose sight penetrates the hidden and the visible",
                ur = "سب کچھ دیکھنے والا؛ جس کی نگاہ ظاہر اور پوشیدہ سب کو دیکھتی ہے",
                bn = "সর্বদ্রষ্টা; যাঁর দৃষ্টি দৃশ্যমান এবং অদৃশ্য সবকিছু ভেদ করে"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Grants spiritual insight (Firasah), clarity of vision, and keeps one away from secret sins",
                ur = "روحانی بصیرت (فراست) اور نگاہ کی پاکیزگی عطا کرتا ہے، اور خفیہ گناہوں سے بچاتا ہے",
                bn = "আধ্যাত্মিক অন্তর্দৃষ্টি (ফিরাসাহ), দৃষ্টির স্বচ্ছতা দান করে এবং গোপন পাপ থেকে দূরে রাখে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_29_AL_HAKAM",
            arabicText = "الْحَكَمُ",
            displayName = LocalizedText(en = "Al-Hakam", ur = "الحکم", bn = "আল-হাকাম"),
            transliteration = "Al-Ḥakam",
            meaning = DhikrMeaning(
                en = "The Impartial Judge; the One who delivers perfect justice and settles all disputes",
                ur = "انصاف کرنے والا؛ جو کامل انصاف کرتا ہے اور تمام تنازعات کا فیصلہ کرتا ہے",
                bn = "ন্যায়বিচারক; যিনি নিখুঁত বিচার করেন এবং সমস্ত বিবাদ নিষ্পত্তি করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Vindicates the oppressed, reveals the truth, and instills fairness in one’s own heart",
                ur = "مظلوم کو انصاف دلاتا ہے، سچائی ظاہر کرتا ہے اور دل میں انصاف پسندی پیدا کرتا ہے",
                bn = "মজলুমের পক্ষে রায় দেয়, সত্য প্রকাশ করে এবং নিজের অন্তরে ন্যায়পরায়ণতা সঞ্চার করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_30_AL_ADL",
            arabicText = "الْعَدْلُ",
            displayName = LocalizedText(en = "Al-'Adl", ur = "العدل", bn = "আল-আদল"),
            transliteration = "Al-'Adl",
            meaning = DhikrMeaning(
                en = "The Utterly Just; the One who is the source of all fairness and rectitude",
                ur = "سراپا عدل؛ جو ہر انصاف اور درستگی کا سرچشمہ ہے",
                bn = "পরম ন্যায়পরায়ণ; যিনি সমস্ত ন্যায্যতা ও সততার উৎস"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Brings absolute balance into one's life and protects against being wronged or wronging others",
                ur = "زندگی میں کامل توازن لاتا ہے اور ظلم کرنے یا ظلم سہنے سے بچاتا ہے",
                bn = "জীবনে পরম ভারসাম্য নিয়ে আসে এবং অন্যের প্রতি অন্যায় করা বা অন্যায়ের শিকার হওয়া থেকে রক্ষা করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_31_AL_LATIF",
            arabicText = "اللَّطِيفُ",
            displayName = LocalizedText(en = "Al-Latif", ur = "اللطیف", bn = "আল-লতীফ"),
            transliteration = "Al-Laṭīf",
            meaning = DhikrMeaning(
                en = "The Subtle One; the One who knows the intricate details and is profoundly gentle",
                ur = "باریک بین اور لطف کرنے والا؛ جو پوشیدہ باتوں کو جانتا ہے اور بے حد مہربان ہے",
                bn = "সূক্ষ্মদর্শী ও দয়ালু; যিনি ক্ষুদ্রতম বিবরণ জানেন এবং গভীরভাবে কোমল"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Relieves invisible anxieties, smooths out difficulties, and brings provisions from unexpected places",
                ur = "پوشیدہ پریشانیوں کو دور کرتا ہے، مشکلیں آسان کرتا ہے اور نامعلوم ذرائع سے رزق دیتا ہے",
                bn = "অদৃশ্য উদ্বেগ দূর করে, অসুবিধাগুলো সহজ করে এবং অপ্রত্যাশিত জায়গা থেকে রিজিক নিয়ে আসে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_32_AL_KHABIR",
            arabicText = "الْخَبِيرُ",
            displayName = LocalizedText(en = "Al-Khabir", ur = "الخبیر", bn = "আল-খাবীর"),
            transliteration = "Al-Khabīr",
            meaning = DhikrMeaning(
                en = "The All-Aware; the One who knows the inner reality and secrets of all things",
                ur = "باخبر؛ جو ہر چیز کی اندرونی حقیقت اور رازوں کو جانتا ہے",
                bn = "সর্বজ্ঞাত; যিনি সমস্ত কিছুর অভ্যন্তরীণ বাস্তবতা এবং গোপনীয়তা জানেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Cures ignorance, protects from hidden deception, and grants understanding of deep realities",
                ur = "جہالت کو دور کرتا ہے، پوشیدہ فریب سے بچاتا ہے اور گہری حقیقتوں کی سمجھ عطا کرتا ہے",
                bn = "অজ্ঞতা নিরাময় করে, গোপন প্রতারণা থেকে রক্ষা করে এবং গভীর বাস্তবতা বোঝার ক্ষমতা দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        ),
        DhikrItem(
            key = "ASMA_33_AL_HALIM",
            arabicText = "الْحَلِيمُ",
            displayName = LocalizedText(en = "Al-Halim", ur = "الحلیم", bn = "আল-হালীম"),
            transliteration = "Al-Ḥalīm",
            meaning = DhikrMeaning(
                en = "The Forbearing; the One who is exceedingly patient and delays punishment",
                ur = "بردبار؛ جو بے حد صبر کرنے والا ہے اور سزا دینے میں جلدی نہیں کرتا",
                bn = "সহনশীল; যিনি অত্যন্ত ধৈর্যশীল এবং শাস্তি প্রদানে বিলম্ব করেন"
            ),
            defaultTarget = 99,
            spiritualReward = LocalizedText(
                en = "Cools anger, develops immense personal patience, and grants a gentle disposition",
                ur = "غصہ ٹھنڈا کرتا ہے، بے پناہ ذاتی صبر پیدا کرتا ہے اور نرم مزاجی عطا کرتا ہے",
                bn = "রাগ প্রশমিত করে, অপরিসীম ব্যক্তিগত ধৈর্য বিকাশ করে এবং একটি কোমল স্বভাব দান করে"
            ),
            hadithRef = "Asma ul Husna",
            category = DhikrCategory.ASMA_UL_HUSNA
        )
    )
}
