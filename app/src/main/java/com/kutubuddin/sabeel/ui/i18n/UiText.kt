package com.kutubuddin.sabeel.ui.i18n

import com.kutubuddin.sabeel.domain.model.LocalizedText

/**
 * The chrome translation dictionary — single source of truth.
 *
 * One [LocalizedText] per user-facing chrome string. [resolve] builds the typed
 * [UiStrings] bundle for a language; the compiler enforces that every UiStrings
 * field is provided. `UiTextTest.everyStringHasUrduAndBengali` reflects over the
 * entries here and fails the build if any translation is blank.
 *
 * Interpolated strings use positional placeholders (`%1$s`, `%2$s`) so each
 * language can reorder — note the `$` is escaped as `\$` in Kotlin literals.
 */
object UiText {
    // ── Bottom navigation ─────────────────────────────
    val navHome = LocalizedText(en = "Home", ur = "ہوم", bn = "হোম")
    val navCount = LocalizedText(en = "Count", ur = "شمار", bn = "গণনা")
    val navDhikr = LocalizedText(en = "Dhikr", ur = "ذکر", bn = "যিকির")
    val navSettings = LocalizedText(en = "Settings", ur = "ترتیبات", bn = "সেটিংস")

    // ── Home: greetings ───────────────────────────────
    val greetingFajr = LocalizedText(en = "Fajr time — a blessed start", ur = "فجر کا وقت — بابرکت آغاز", bn = "ফজরের সময় — বরকতময় সূচনা")
    val greetingMorning = LocalizedText(en = "Good morning", ur = "صبح بخیر", bn = "শুভ সকাল")
    val greetingDhuhr = LocalizedText(en = "Dhuhr time", ur = "ظہر کا وقت", bn = "যোহরের সময়")
    val greetingAfternoon = LocalizedText(en = "Good afternoon", ur = "سہ پہر بخیر", bn = "শুভ অপরাহ্ন")
    val greetingAsr = LocalizedText(en = "Asr time", ur = "عصر کا وقت", bn = "আসরের সময়")
    val greetingMaghrib = LocalizedText(en = "Maghrib time", ur = "مغرب کا وقت", bn = "মাগরিবের সময়")
    val greetingIsha = LocalizedText(en = "Isha time", ur = "عشاء کا وقت", bn = "এশার সময়")
    val greetingDefault = LocalizedText(en = "Assalamu alaikum", ur = "السلام علیکم", bn = "আসসালামু আলাইকুম")

    // ── Home: cards, headers, stats ───────────────────
    val homeResume = LocalizedText(en = "Resume", ur = "دوبارہ شروع کریں", bn = "আবার শুরু")
    val homeTodaysSessions = LocalizedText(en = "Today's Sessions", ur = "آج کے سیشنز", bn = "আজকের সেশন")
    val homeAllTime = LocalizedText(en = "All Time", ur = "اب تک", bn = "সর্বকাল")
    val homeConsistency = LocalizedText(en = "Consistency", ur = "تسلسل", bn = "ধারাবাহিকতা")
    val homeTotalCounted = LocalizedText(en = "Total Counted", ur = "کل شمار", bn = "মোট গণনা")
    val homeSessions = LocalizedText(en = "Sessions", ur = "سیشنز", bn = "সেশন")
    val homeCompleted = LocalizedText(en = "Completed ✓", ur = "مکمل ✓", bn = "সম্পন্ন ✓")
    val homePartial = LocalizedText(en = "Partial", ur = "نامکمل", bn = "আংশিক")
    val homeBeginToday = LocalizedText(en = "Begin today's dhikr", ur = "آج کا ذکر شروع کریں", bn = "আজকের যিকির শুরু করুন")
    val homeStartCounting = LocalizedText(en = "Start counting", ur = "شمار شروع کریں", bn = "গণনা শুরু করুন")
    val homeDayOne = LocalizedText(en = "%1\$s day", ur = "%1\$s دن", bn = "%1\$s দিন")
    val homeDayOther = LocalizedText(en = "%1\$s days", ur = "%1\$s دن", bn = "%1\$s দিন")

    // ── Daily Wird ──────────────────────────────────
    val wirdTitle = LocalizedText(en = "Today's Wird", ur = "آج کا وِرد", bn = "আজকের ওয়ির্দ")
    val wirdSetup = LocalizedText(en = "Set up", ur = "ترتیب دیں", bn = "সেট আপ করুন")
    val wirdDoneOf = LocalizedText(en = "%1\$s of %2\$s done", ur = "%2\$s میں سے %1\$s مکمل", bn = "%2\$s এর মধ্যে %1\$s সম্পন্ন")
    val wirdEditTitle = LocalizedText(en = "Edit Wird", ur = "وِرد میں ترمیم کریں", bn = "ওয়ির্দ সম্পাদনা করুন")
    val wirdEmpty = LocalizedText(en = "Your wird is empty", ur = "آپ کا وِرد خالی ہے", bn = "আপনার ওয়ির্দ খালি")
    val wirdEmptyHint = LocalizedText(en = "Add adhkar to build your daily routine", ur = "اپنا روزانہ کا معمول بنانے کے لیے اذکار شامل کریں", bn = "দৈনিক রুটিন গড়তে আযকার যোগ করুন")
    val wirdAddDhikr = LocalizedText(en = "Add dhikr", ur = "ذکر شامل کریں", bn = "যিকর যোগ করুন")
    val wirdReorder = LocalizedText(en = "Reorder", ur = "ترتیب بدلیں", bn = "ক্রম বদলান")
    val wirdRemove = LocalizedText(en = "Remove from wird", ur = "وِرد سے ہٹائیں", bn = "ওয়ির্দ থেকে সরান")
    val wirdTargetA11y = LocalizedText(en = "Target", ur = "ہدف", bn = "লক্ষ্য")

    // ── Counting screen ───────────────────────────────
    val countSmartFlow = LocalizedText(en = "Tasbīḥ after Salah", ur = "نماز کے بعد تسبیح", bn = "নামাযের পর তাসবিহ")
    val countConsistencyA11y = LocalizedText(en = "Consistency: %1\$s days", ur = "تسلسل: %1\$s دن", bn = "ধারাবাহিকতা: %1\$s দিন")
    val countStreakShort = LocalizedText(en = "%1\$sd", ur = "%1\$s دن", bn = "%1\$s দিন")
    val countUndo = LocalizedText(en = "Undo last count", ur = "آخری شمار واپس لیں", bn = "শেষ গণনা ফিরিয়ে নিন")
    val countDecrementAction = LocalizedText(en = "Decrement", ur = "کم کریں", bn = "কমান")
    val countTapHint = LocalizedText(en = "Tap the circle to count", ur = "شمار کرنے کے لیے دائرے کو چھوئیں", bn = "গণনা করতে বৃত্তে ট্যাপ করুন")
    val countComplete = LocalizedText(en = "%1\$s complete", ur = "%1\$s مکمل", bn = "%1\$s সম্পন্ন")
    val countFinish = LocalizedText(en = "Finish", ur = "ختم", bn = "শেষ")
    val countContinue = LocalizedText(en = "Continue", ur = "جاری رکھیں", bn = "চালিয়ে যান")
    val countStepOf = LocalizedText(en = "Step %1\$s of %2\$s", ur = "مرحلہ %1\$s از %2\$s", bn = "ধাপ %1\$s / %2\$s")
    val countReward = LocalizedText(en = "SPIRITUAL REWARD", ur = "روحانی اجر", bn = "আধ্যাত্মিক সওয়াব")
    val countCircleA11y = LocalizedText(
        en = "Count %1\$s of %2\$s. Tap to count. Long press to reset.",
        ur = "شمار %1\$s از %2\$s۔ شمار کرنے کے لیے چھوئیں۔ ری سیٹ کرنے کے لیے دبائے رکھیں۔",
        bn = "গণনা %1\$s / %2\$s। গণনা করতে ট্যাপ করুন। রিসেট করতে চেপে ধরে রাখুন।"
    )
    val countOf = LocalizedText(en = "of %1\$s", ur = "از %1\$s", bn = "%1\$s এর মধ্যে")

    // ── Settings screen ───────────────────────────────
    val settingsAppearance = LocalizedText(en = "Appearance", ur = "ظاہری شکل", bn = "অ্যাপিয়ারেন্স")
    val settingsLanguageText = LocalizedText(en = "Language & Text", ur = "زبان اور متن", bn = "ভাষা ও টেক্সট")
    val settingsCountingBehaviour = LocalizedText(en = "Counting Behaviour", ur = "شمار کا انداز", bn = "গণনা পদ্ধতি")
    val settingsAbout = LocalizedText(en = "About", ur = "تعارف", bn = "অ্যাপ সম্পর্কে")
    val settingsTheme = LocalizedText(en = "Theme", ur = "تھیم", bn = "থিম")
    val settingsThemeDark = LocalizedText(en = "Dark", ur = "گہرا", bn = "গাঢ়")
    val settingsThemeLight = LocalizedText(en = "Light", ur = "ہلکا", bn = "হালকা")
    val settingsLanguage = LocalizedText(en = "Language", ur = "زبان", bn = "ভাষা")
    val settingsHaptics = LocalizedText(en = "Haptic Feedback", ur = "ہپٹک فیڈبیک", bn = "হ্যাপটিক ফিডব্যাক")
    val settingsHapticOff = LocalizedText(en = "Off", ur = "بند", bn = "বন্ধ")
    val settingsHapticLight = LocalizedText(en = "Light", ur = "ہلکا", bn = "হালকা")
    val settingsHapticMedium = LocalizedText(en = "Medium", ur = "درمیانہ", bn = "মাঝারি")
    val settingsHapticStrong = LocalizedText(en = "Strong", ur = "مضبوط", bn = "জোরালো")
    val settingsTranslit = LocalizedText(en = "Show Transliteration", ur = "نقل حرفی دکھائیں", bn = "উচ্চারণ দেখান")
    val settingsTranslitDesc = LocalizedText(en = "Romanized pronunciation under Arabic", ur = "عربی کے نیچے رومن تلفظ", bn = "আরবির নিচে ইংরেজি উচ্চারণ")
    val settingsSound = LocalizedText(en = "Sound on Milestone", ur = "سنگ میل پر آواز", bn = "মাইলস্টোনে শব্দ")
    val settingsSoundDesc = LocalizedText(en = "Subtle chime at 33, 100 etc.", ur = "۳۳، ۱۰۰ وغیرہ پر ہلکی آواز", bn = "৩৩, ১০০ ইত্যাদিতে মৃদু ধ্বনি")
    val settingsAutoReset = LocalizedText(en = "Auto-reset on Completion", ur = "تکمیل پر خودکار ری سیٹ", bn = "লক্ষ্য শেষে অটো-রিসেট")
    val settingsAutoResetDesc = LocalizedText(en = "Counter resets when target is hit", ur = "ہدف پر پہنچنے پر شمار خودکار ری سیٹ ہو جاتا ہے", bn = "লক্ষ্যে পৌঁছালে গণনা রিসেট হয়")
    val settingsShowStreaks = LocalizedText(en = "Show Streaks", ur = "تسلسل دکھائیں", bn = "ধারাবাহিকতা দেখান")
    val settingsShowStreaksDesc = LocalizedText(en = "Hide consistency counts for pure ibadah", ur = "خالص عبادت کے لیے تسلسل چھپائیں", bn = "খাঁটি ইবাদতের জন্য ধারাবাহিকতা লুকান")
    val settingsAutoProgressWird = LocalizedText(en = "Wird Auto-Progression", ur = "وِرد خودکار ترقی", bn = "ওয়ির্দ স্বয়ংক্রিয় অগ্রগতি")
    val settingsAutoProgressWirdDesc = LocalizedText(en = "Automatically transition to the next Dhikr in your daily Wird", ur = "اپنے روزمرہ کے وِرد میں خود بخود اگلے ذکر پر جائیں", bn = "স্বয়ংক্রিয়ভাবে আপনার দৈনন্দিন ওয়ির্দের পরবর্তী যিকিরে যান")
    val settingsFont = LocalizedText(en = "Font", ur = "فونٹ", bn = "ফন্ট")
    val settingsVersion = LocalizedText(en = "Version", ur = "ورژن", bn = "সংস্করণ")

    // ── Dhikr Library ─────────────────────────────────
    val dhikrSearchPlaceholder = LocalizedText(en = "Search dhikr…", ur = "ذکر تلاش کریں…", bn = "যিকির খুঁজুন…")
    val dhikrRef = LocalizedText(en = "Ref: %1\$s", ur = "حوالہ: %1\$s", bn = "সূত্র: %1\$s")
    val dhikrCountNow = LocalizedText(en = "Count Now", ur = "ابھی شمار کریں", bn = "এখন গণনা করুন")
    val dhikrNoResults = LocalizedText(en = "No dhikr found for \"%1\$s\"", ur = "\"%1\$s\" کے لیے کوئی ذکر نہیں ملا", bn = "\"%1\$s\" এর জন্য কোনো যিকির পাওয়া যায়নি")
    val dhikrSearchHint = LocalizedText(en = "Try searching in Arabic or English", ur = "عربی یا انگریزی میں تلاش کریں", bn = "আরবি বা ইংরেজিতে খুঁজে দেখুন")

    // ── Dhikr categories ──────────────────────────────
    val catAfterPrayer = LocalizedText(en = "After Prayer", ur = "نماز کے بعد", bn = "নামাযের পর")
    val catDaily = LocalizedText(en = "Daily Remembrance", ur = "روزانہ ذکر", bn = "দৈনিক যিকির")
    val catMorning = LocalizedText(en = "Morning Adhkar", ur = "صبح کے اذکار", bn = "সকালের যিকির")
    val catEvening = LocalizedText(en = "Evening Adhkar", ur = "شام کے اذکار", bn = "সন্ধ্যার যিকির")
    val catSalawat = LocalizedText(en = "Salawat", ur = "درود", bn = "দরূদ")
    val catIstighfar = LocalizedText(en = "Istighfar", ur = "استغفار", bn = "ইস্তিগফার")
    val catTahlil = LocalizedText(en = "Tahlil", ur = "تہلیل", bn = "তাহলিল")
    val catCustom = LocalizedText(en = "My Dhikr", ur = "میرا ذکر", bn = "আমার যিকির")

    fun resolve(lang: String) = UiStrings(
        navHome = navHome.get(lang),
        navCount = navCount.get(lang),
        navDhikr = navDhikr.get(lang),
        navSettings = navSettings.get(lang),
        greetingFajr = greetingFajr.get(lang),
        greetingMorning = greetingMorning.get(lang),
        greetingDhuhr = greetingDhuhr.get(lang),
        greetingAfternoon = greetingAfternoon.get(lang),
        greetingAsr = greetingAsr.get(lang),
        greetingMaghrib = greetingMaghrib.get(lang),
        greetingIsha = greetingIsha.get(lang),
        greetingDefault = greetingDefault.get(lang),
        homeResume = homeResume.get(lang),
        homeTodaysSessions = homeTodaysSessions.get(lang),
        homeAllTime = homeAllTime.get(lang),
        homeConsistency = homeConsistency.get(lang),
        homeTotalCounted = homeTotalCounted.get(lang),
        homeSessions = homeSessions.get(lang),
        homeCompleted = homeCompleted.get(lang),
        homePartial = homePartial.get(lang),
        homeBeginToday = homeBeginToday.get(lang),
        homeStartCounting = homeStartCounting.get(lang),
        homeDayOne = homeDayOne.get(lang),
        homeDayOther = homeDayOther.get(lang),
        wirdTitle = wirdTitle.get(lang),
        wirdSetup = wirdSetup.get(lang),
        wirdDoneOf = wirdDoneOf.get(lang),
        wirdEditTitle = wirdEditTitle.get(lang),
        wirdEmpty = wirdEmpty.get(lang),
        wirdEmptyHint = wirdEmptyHint.get(lang),
        wirdAddDhikr = wirdAddDhikr.get(lang),
        wirdReorder = wirdReorder.get(lang),
        wirdRemove = wirdRemove.get(lang),
        wirdTargetA11y = wirdTargetA11y.get(lang),
        countSmartFlow = countSmartFlow.get(lang),
        countConsistencyA11y = countConsistencyA11y.get(lang),
        countStreakShort = countStreakShort.get(lang),
        countUndo = countUndo.get(lang),
        countDecrementAction = countDecrementAction.get(lang),
        countTapHint = countTapHint.get(lang),
        countComplete = countComplete.get(lang),
        countFinish = countFinish.get(lang),
        countContinue = countContinue.get(lang),
        countStepOf = countStepOf.get(lang),
        countReward = countReward.get(lang),
        countCircleA11y = countCircleA11y.get(lang),
        countOf = countOf.get(lang),
        settingsAppearance = settingsAppearance.get(lang),
        settingsLanguageText = settingsLanguageText.get(lang),
        settingsCountingBehaviour = settingsCountingBehaviour.get(lang),
        settingsAbout = settingsAbout.get(lang),
        settingsTheme = settingsTheme.get(lang),
        settingsThemeDark = settingsThemeDark.get(lang),
        settingsThemeLight = settingsThemeLight.get(lang),
        settingsLanguage = settingsLanguage.get(lang),
        settingsHaptics = settingsHaptics.get(lang),
        settingsHapticOff = settingsHapticOff.get(lang),
        settingsHapticLight = settingsHapticLight.get(lang),
        settingsHapticMedium = settingsHapticMedium.get(lang),
        settingsHapticStrong = settingsHapticStrong.get(lang),
        settingsTranslit = settingsTranslit.get(lang),
        settingsTranslitDesc = settingsTranslitDesc.get(lang),
        settingsSound = settingsSound.get(lang),
        settingsSoundDesc = settingsSoundDesc.get(lang),
        settingsAutoReset = settingsAutoReset.get(lang),
        settingsAutoResetDesc = settingsAutoResetDesc.get(lang),
        settingsShowStreaks = settingsShowStreaks.get(lang),
        settingsShowStreaksDesc = settingsShowStreaksDesc.get(lang),
        settingsAutoProgressWird = settingsAutoProgressWird.get(lang),
        settingsAutoProgressWirdDesc = settingsAutoProgressWirdDesc.get(lang),
        settingsFont = settingsFont.get(lang),
        settingsVersion = settingsVersion.get(lang),
        dhikrSearchPlaceholder = dhikrSearchPlaceholder.get(lang),
        dhikrRef = dhikrRef.get(lang),
        dhikrCountNow = dhikrCountNow.get(lang),
        dhikrNoResults = dhikrNoResults.get(lang),
        dhikrSearchHint = dhikrSearchHint.get(lang),
        catAfterPrayer = catAfterPrayer.get(lang),
        catDaily = catDaily.get(lang),
        catMorning = catMorning.get(lang),
        catEvening = catEvening.get(lang),
        catSalawat = catSalawat.get(lang),
        catIstighfar = catIstighfar.get(lang),
        catTahlil = catTahlil.get(lang),
        catCustom = catCustom.get(lang),
    )
}
