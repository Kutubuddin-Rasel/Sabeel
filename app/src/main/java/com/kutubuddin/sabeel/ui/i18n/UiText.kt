package com.kutubuddin.sabeel.ui.i18n

import com.kutubuddin.sabeel.domain.model.LocalizedText

/**
 * The chrome translation dictionary — single source of truth.
 *
 * One [LocalizedText] per user-facing chrome string. [resolve] builds the typed
 * [UiStrings] bundle for a language; the compiler enforces that every UiStrings
 * field is provided. `UiTextTest.everyStringHasBengali` reflects over the
 * entries here and fails the build if any translation is blank.
 *
 * Interpolated strings use positional placeholders (`%1$s`, `%2$s`) so each
 * language can reorder — note the `$` is escaped as `\$` in Kotlin literals.
 */
object UiText {
    // ── Generic navigation / chrome a11y ──────────────
    val a11yBack = LocalizedText(en = "Back", bn = "ফিরে যান")
    val a11yDismiss = LocalizedText(en = "Dismiss", bn = "বাতিল করুন")
    val a11yDelete = LocalizedText(en = "Delete", bn = "মুছে ফেলুন")
    val a11yExpand = LocalizedText(en = "Expand", bn = "সম্প্রসারণ করুন")
    val a11yCollapse = LocalizedText(en = "Collapse", bn = "সঙ্কুচিত করুন")

    // ── Bottom navigation ─────────────────────────────
    val navHome = LocalizedText(en = "Home", bn = "হোম")
    val navCount = LocalizedText(en = "Count", bn = "গণনা")
    val navDhikr = LocalizedText(en = "Dhikr", bn = "যিকির")
    val navSettings = LocalizedText(en = "Settings", bn = "সেটিংস")

    // ── Home: greetings ───────────────────────────────
    // NOTE: these used to name a specific prayer (e.g. "Maghrib time") based
    // only on the device clock hour. That's a false-precision problem, not a
    // cosmetic one: real prayer times move with date and location (Dhaka's
    // Maghrib alone swings from ~17:05 in December to ~18:50 in July), so the
    // old copy could assert "Maghrib time" while it was actually Isha, or
    // "Asr time" just after Maghrib had started — wrong exactly where a
    // religious app most needs to be trustworthy. Wording below describes the
    // hour bucket itself (morning/midday/evening/night) instead of claiming a
    // specific salah is currently due. True per-location, per-date prayer
    // times would need device location + a calculation engine — a separate
    // feature, not a copy fix. Best-effort ur/bn drafts, not a
    // native-speaker review — worth a check before shipping.
    val greetingFajr = LocalizedText(en = "A blessed early morning", bn = "ভোরের বরকতময় প্রহর")
    val greetingMorning = LocalizedText(en = "Good morning", bn = "শুভ সকাল")
    val greetingDhuhr = LocalizedText(en = "Midday", bn = "দুপুর")
    val greetingAfternoon = LocalizedText(en = "Good afternoon", bn = "শুভ অপরাহ্ন")
    val greetingAsr = LocalizedText(en = "Late afternoon", bn = "বিকেল")
    val greetingMaghrib = LocalizedText(en = "Evening", bn = "সন্ধ্যা")
    val greetingIsha = LocalizedText(en = "Night", bn = "রাত")
    val greetingDefault = LocalizedText(en = "Assalamu alaikum", bn = "আসসালামু আলাইকুম")

    // ── Home: cards, headers, stats ───────────────────
    val homeResume = LocalizedText(en = "Resume", bn = "আবার শুরু")
    val homeTodaysSessions = LocalizedText(en = "Today's Sessions", bn = "আজকের সেশন")
    val homeAllTime = LocalizedText(en = "All Time", bn = "সর্বকাল")
    val homeConsistency = LocalizedText(en = "Consistency", bn = "ধারাবাহিকতা")
    val homeTotalCounted = LocalizedText(en = "Total Counted", bn = "মোট গণনা")
    val homeSessions = LocalizedText(en = "Sessions", bn = "সেশন")
    val homeCompleted = LocalizedText(en = "Completed ✓", bn = "সম্পন্ন ✓")
    val homePartial = LocalizedText(en = "Partial", bn = "আংশিক")
    val homeBeginToday = LocalizedText(en = "Begin today's dhikr", bn = "আজকের যিকির শুরু করুন")
    val homeStartCounting = LocalizedText(en = "Start counting", bn = "গণনা শুরু করুন")
    val homeDayOne = LocalizedText(en = "%1\$s day", bn = "%1\$s দিন")
    val homeDayOther = LocalizedText(en = "%1\$s days", bn = "%1\$s দিন")
    // Reframes a broken/fresh streak: "0 days" reads as a scoreboard you're
    // already behind on. "Start today" is true in both the brand-new-user
    // case and the lapsed-streak case, and doesn't editorialize either one
    // as a failure. Best-effort ur/bn drafts, not a native-speaker review.
    val homeStreakStart = LocalizedText(en = "Start today", bn = "আজ থেকে শুরু করুন")
    // Shown on the Home hero card once at least one session exists today
    // (state.todaysSessions.isNotEmpty()), so a user who already did their
    // first dhikr doesn't see the exact same "Begin today's dhikr" prompt
    // that greeted them at zero progress. Best-effort ur/bn drafts.
    val homeContinueToday = LocalizedText(en = "Continue today's dhikr", bn = "আজকের যিকির চালিয়ে যান")
    
    val homeSmartPlayNext = LocalizedText(en = "Next: %1\$s", bn = "পরবর্তী: %1\$s")
    val homeGoalCompleteTitle = LocalizedText(en = "Alhamdulillah, Daily Goal Complete \uD83C\uDF89", bn = "আলহামদুলিল্লাহ, দৈনিক লক্ষ্য সম্পন্ন \uD83C\uDF89")
    val homeGoalCompleteAction = LocalizedText(en = "Keep Going", bn = "চালিয়ে যান")

    // Replaces the "0 Total Counted / 0 Sessions" stat row for a user with
    // zero all-time sessions — two prominent zeroes on first launch reads as
    // "behind," not "about to start." Best-effort ur/bn drafts.
    val homeFirstTimeEncouragement = LocalizedText(
        en = "Every tasbih is written — even the first one.",
        bn = "প্রতিটি তাসবিহ লেখা হয় — এমনকি প্রথমটিও।"
    )

    // ── Daily Wird ──────────────────────────────────
    val wirdTitle = LocalizedText(en = "Today's Wird", bn = "আজকের ওয়ির্দ")
    // NOTE: replaces the old `wirdSetup` ("Set up") now that Home's empty
    // state is a full self-explanatory card instead of a bare label next to
    // a separate Edit chip. ur/bn below are my best-effort drafts, not a
    // native-speaker review — worth a check before shipping, same as any
    // new string here.
    val wirdSetupTitle = LocalizedText(
        en = "Set your daily wird",
        bn = "আপনার দৈনিক ওয়ির্দ নির্ধারণ করুন"
    )
    val wirdSetupSubtitle = LocalizedText(
        en = "Choose the dhikr you want to complete each day.",
        bn = "প্রতিদিন সম্পন্ন করতে চান এমন যিকির নির্বাচন করুন।"
    )
    val wirdDoneOf = LocalizedText(en = "%1\$s of %2\$s done", bn = "%2\$s এর মধ্যে %1\$s সম্পন্ন")
    val wirdEditTitle = LocalizedText(en = "Edit Wird", bn = "ওয়ির্দ সম্পাদনা করুন")
    val wirdEmpty = LocalizedText(en = "Your wird is empty", bn = "আপনার ওয়ির্দ খালি")
    val wirdEmptyHint = LocalizedText(en = "Add adhkar to build your daily routine", bn = "দৈনিক রুটিন গড়তে আযকার যোগ করুন")
    val wirdAddDhikr = LocalizedText(en = "Add dhikr", bn = "যিকর যোগ করুন")
    val wirdReorder = LocalizedText(en = "Reorder", bn = "ক্রম বদলান")
    val wirdRemove = LocalizedText(en = "Remove from wird", bn = "ওয়ির্দ থেকে সরান")
    val wirdTargetA11y = LocalizedText(en = "Target", bn = "লক্ষ্য")
    val wirdDone = LocalizedText(en = "Done", bn = "সম্পন্ন")
    val wirdEditCta = LocalizedText(en = "Edit", bn = "সম্পাদনা")
    val wirdGoalHintTitle = LocalizedText(
        en = "Set your daily goal",
        bn = "আপনার দৈনিক লক্ষ্য নির্ধারণ করুন"
    )
    val wirdGoalHintBody = LocalizedText(
        en = "Tap Edit next to Today's Wird to add or change dhikr targets.",
        bn = "যিকিরের লক্ষ্য যোগ বা পরিবর্তন করতে আজকের ওয়ির্দের পাশে সম্পাদনা-এ ট্যাপ করুন।"
    )

    // Best-effort ur/bn drafts, not a native-speaker review, same caveat as
    // wirdSetupTitle above.
    // FIX 8: intention framing, not "tasks". Best-effort ur/bn drafts.
    val wirdBeginToday = LocalizedText(en = "Your wird awaits", bn = "আপনার ওয়ির্দ অপেক্ষা করছে")
    val wirdComplete = LocalizedText(en = "Today's wird, complete", bn = "আজকের ওয়ির্দ সম্পূর্ণ")
    val wirdInProgress = LocalizedText(en = "%1\$s of %2\$s remembered", bn = "%2\$s-এর মধ্যে %1\$s সম্পন্ন")
    val wirdRingCaptionTarget = LocalizedText(en = "total reps today", bn = "আজকের মোট লক্ষ্য")
    val wirdOverflowRounds = LocalizedText(en = "completed %1\$s times over", bn = "%1\$s বার সম্পন্ন")
    val wirdExpandRowA11y = LocalizedText(en = "Show options", bn = "অপশন দেখান")


    // ── Counting screen ───────────────────────────────
    val countSmartFlow = LocalizedText(en = "Tasbih after Salah", bn = "নামাযের পর তাসবিহ")
    val countConsistencyA11y = LocalizedText(en = "Consistency: %1\$s days", bn = "ধারাবাহিকতা: %1\$s দিন")
    val countStreakShort = LocalizedText(en = "%1\$sd", bn = "%1\$s দিন")
    val countUndo = LocalizedText(en = "Undo last count", bn = "শেষ গণনা ফিরিয়ে নিন")
    val countDecrementAction = LocalizedText(en = "Decrement", bn = "কমান")
    val countIncrementA11y = LocalizedText(en = "Count", bn = "গণনা করুন")
    val countTapHint = LocalizedText(en = "Tap anywhere to count", bn = "গণনা করতে যেকোনো জায়গায় ট্যাপ করুন")
    val countComplete = LocalizedText(en = "%1\$s complete", bn = "%1\$s সম্পন্ন")
    val countFinish = LocalizedText(en = "Finish", bn = "শেষ")
    val countContinue = LocalizedText(en = "Continue", bn = "চালিয়ে যান")
    val countAgain = LocalizedText(en = "Count Again", bn = "আবার গণনা করুন")
    val countDailyGoalCompleted = LocalizedText(en = "Daily Goal Completed", bn = "দৈনিক লক্ষ্য সম্পন্ন")
    val countAlhamdulillah = LocalizedText(en = "Alhamdulillah", bn = "আলহামদুলিল্লাহ")
    val countStepOf = LocalizedText(en = "Step %1\$s of %2\$s", bn = "ধাপ %1\$s / %2\$s")
    val countReward = LocalizedText(en = "Spiritual Reward", bn = "আধ্যাত্মিক সওয়াব")
    val countCircleA11y = LocalizedText(
        en = "Count %1\$s of %2\$s. Tap to count. Long press to reset.",
        bn = "গণনা %1\$s / %2\$s। গণনা করতে ট্যাপ করুন। রিসেট করতে চেপে ধরে রাখুন।"
    )
    val countOf = LocalizedText(en = "of %1\$s", bn = "%1\$s এর মধ্যে")

    // ── Settings screen ───────────────────────────────
    val settingsAppearance = LocalizedText(en = "Appearance", bn = "অ্যাপিয়ারেন্স")
    val settingsLanguageText = LocalizedText(en = "Language & Text", bn = "ভাষা ও টেক্সট")
    val settingsCountingBehaviour = LocalizedText(en = "Counting Behaviour", bn = "গণনা পদ্ধতি")
    val settingsAbout = LocalizedText(en = "About", bn = "অ্যাপ সম্পর্কে")
    val settingsTheme = LocalizedText(en = "Theme", bn = "থিম")
    val settingsThemeDark = LocalizedText(en = "Dark", bn = "গাঢ়")
    val settingsThemeLight = LocalizedText(en = "Light", bn = "হালকা")
    val settingsLanguage = LocalizedText(en = "Language", bn = "ভাষা")
    val settingsHaptics = LocalizedText(en = "Haptic feedback", bn = "হ্যাপটিক ফিডব্যাক")
    val settingsHapticOff = LocalizedText(en = "Off", bn = "বন্ধ")
    val settingsHapticLight = LocalizedText(en = "Light", bn = "হালকা")
    val settingsHapticMedium = LocalizedText(en = "Medium", bn = "মাঝারি")
    val settingsHapticStrong = LocalizedText(en = "Strong", bn = "জোরালো")
    val settingsTranslit = LocalizedText(en = "Show Transliteration", bn = "উচ্চারণ দেখান")
    val settingsTranslitDesc = LocalizedText(en = "Romanized pronunciation under Arabic", bn = "আরবির নিচে ইংরেজি উচ্চারণ")

    val settingsAutoProgressWird = LocalizedText(en = "Wird Auto-Progression", bn = "ওয়ির্দ স্বয়ংক্রিয় অগ্রগতি")
    val settingsAutoProgressWirdDesc = LocalizedText(en = "Automatically transition to the next Dhikr in your daily Wird", bn = "স্বয়ংক্রিয়ভাবে আপনার দৈনন্দিন ওয়ির্দের পরবর্তী যিকিরে যান")
    val settingsSmartFlow = LocalizedText(en = "Smart Flow Sequence", bn = "স্মার্ট ফ্লো সিকোয়েন্স")
    val settingsSmartFlowDesc = LocalizedText(en = "Automatically transition through multi-step Dhikrs (e.g., Tasbih after Salah)", bn = "স্বয়ংক্রিয়ভাবে বহু-ধাপ যিকিরে (যেমন, নামাজের পর তাসবিহ) অগ্রসর হন")
    val settingsAdvanced = LocalizedText(en = "Advanced", bn = "উন্নত সেটিংস")
    val settingsFont = LocalizedText(en = "Font", bn = "ফন্ট")
    val settingsVersion = LocalizedText(en = "Version", bn = "সংস্করণ")
    
    // ── Notifications ───────────────────────────────
    val settingsDailyReminders = LocalizedText(en = "Daily Reminders", bn = "দৈনিক রিমাইন্ডার")
    val settingsDailyRemindersDesc = LocalizedText(en = "Smart nudges to protect your streak & daily goal", bn = "আপনার লক্ষ্য পূরণের জন্য স্মার্ট রিমাইন্ডার")
    val settingsReminderTime = LocalizedText(en = "Reminder Time", bn = "রিমাইন্ডারের সময়")

    // ── Dhikr Library ─────────────────────────────────
    val dhikrSearchPlaceholder = LocalizedText(en = "Search dhikr…", bn = "যিকির খুঁজুন…")
    val dhikrTargetLabel = LocalizedText(en = "Target", bn = "লক্ষ্য")
    val dhikrRef = LocalizedText(en = "Ref: %1\$s", bn = "সূত্র: %1\$s")
    val dhikrCountNow = LocalizedText(en = "Count Now", bn = "এখন গণনা করুন")
    val dhikrNoResults = LocalizedText(en = "No dhikr found for \"%1\$s\"", bn = "\"%1\$s\" এর জন্য কোনো যিকির পাওয়া যায়নি")
    val dhikrSearchHint = LocalizedText(en = "Try searching in Arabic or English", bn = "আরবি বা ইংরেজিতে খুঁজে দেখুন")
    val dhikrSequenceLabel = LocalizedText(en = "%1\$s-step sequence", bn = "%1\$s-ধাপের ক্রম")

    // ── Wird Collection ─────────────────────────────────
    val catAfterPrayer = LocalizedText(en = "After Prayer", bn = "নামাযের পর")
    val catDaily = LocalizedText(en = "Daily Remembrance", bn = "দৈনিক যিকির")
    val catMorning = LocalizedText(en = "Morning Adhkar", bn = "সকালের যিকির")
    val catEvening = LocalizedText(en = "Evening Adhkar", bn = "সন্ধ্যার যিকির")
    val catSalawat = LocalizedText(en = "Salawat", bn = "দরূদ")
    val catIstighfar = LocalizedText(en = "Istighfar", bn = "ইস্তিগফার")
    val catTahlil = LocalizedText(en = "Tahlil", bn = "তাহলিল")
    val catAsmaUlHusna = LocalizedText(en = "Asma ul Husna", bn = "আসমাউল হুসনা")
    val catCustom = LocalizedText(en = "My Dhikr", bn = "আমার যিকির")

    // ── Dhikr Library: Asma ul Husna gallery card ──────
    val exploreGalleryTitle = LocalizedText(
        en = "Explore Individual Names",
        bn = "পৃথকভাবে নাম অন্বেষণ করুন"
    )
    val exploreGallerySubtitle = LocalizedText(
        en = "Recite any of the 99 Names individually",
        bn = "আল্লাহর ৯৯টি নামের যেকোনো একটি পৃথকভাবে পড়ুন"
    )

    fun resolve(lang: String) = UiStrings(
        a11yBack = a11yBack.get(lang),
        a11yDismiss = a11yDismiss.get(lang),
        a11yDelete = a11yDelete.get(lang),
        a11yExpand = a11yExpand.get(lang),
        a11yCollapse = a11yCollapse.get(lang),
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
        homeStreakStart = homeStreakStart.get(lang),
        homeContinueToday = homeContinueToday.get(lang),
        homeSmartPlayNext = homeSmartPlayNext.get(lang),
        homeGoalCompleteTitle = homeGoalCompleteTitle.get(lang),
        homeGoalCompleteAction = homeGoalCompleteAction.get(lang),
        homeFirstTimeEncouragement = homeFirstTimeEncouragement.get(lang),
        wirdTitle = wirdTitle.get(lang),
        wirdSetupTitle = wirdSetupTitle.get(lang),
        wirdSetupSubtitle = wirdSetupSubtitle.get(lang),
        wirdDoneOf = wirdDoneOf.get(lang),
        wirdEditTitle = wirdEditTitle.get(lang),
        wirdEmpty = wirdEmpty.get(lang),
        wirdEmptyHint = wirdEmptyHint.get(lang),
        wirdAddDhikr = wirdAddDhikr.get(lang),
        wirdReorder = wirdReorder.get(lang),
        wirdRemove = wirdRemove.get(lang),
        wirdTargetA11y = wirdTargetA11y.get(lang),
        wirdDone = wirdDone.get(lang),
        wirdEditCta = wirdEditCta.get(lang),
        wirdGoalHintTitle = wirdGoalHintTitle.get(lang),
        wirdGoalHintBody = wirdGoalHintBody.get(lang),
        wirdBeginToday = wirdBeginToday.get(lang),
        wirdComplete = wirdComplete.get(lang),
        wirdInProgress = wirdInProgress.get(lang),
        wirdRingCaptionTarget = wirdRingCaptionTarget.get(lang),
        wirdOverflowRounds = wirdOverflowRounds.get(lang),
        wirdExpandRowA11y = wirdExpandRowA11y.get(lang),
        countSmartFlow = countSmartFlow.get(lang),
        countConsistencyA11y = countConsistencyA11y.get(lang),
        countStreakShort = countStreakShort.get(lang),
        countUndo = countUndo.get(lang),
        countDecrementAction = countDecrementAction.get(lang),
        countIncrementA11y = countIncrementA11y.get(lang),
        countTapHint = countTapHint.get(lang),
        countComplete = countComplete.get(lang),
        countFinish = countFinish.get(lang),
        countContinue = countContinue.get(lang),
        countAgain = countAgain.get(lang),
        countDailyGoalCompleted = countDailyGoalCompleted.get(lang),
        countAlhamdulillah = countAlhamdulillah.get(lang),
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

        settingsAutoProgressWird = settingsAutoProgressWird.get(lang),
        settingsAutoProgressWirdDesc = settingsAutoProgressWirdDesc.get(lang),
        settingsSmartFlow = settingsSmartFlow.get(lang),
        settingsSmartFlowDesc = settingsSmartFlowDesc.get(lang),
        settingsAdvanced = settingsAdvanced.get(lang),
        settingsFont = settingsFont.get(lang),
        settingsVersion = settingsVersion.get(lang),
        settingsDailyReminders = settingsDailyReminders.get(lang),
        settingsDailyRemindersDesc = settingsDailyRemindersDesc.get(lang),
        settingsReminderTime = settingsReminderTime.get(lang),
        dhikrSearchPlaceholder = dhikrSearchPlaceholder.get(lang),
        dhikrTargetLabel = dhikrTargetLabel.get(lang),
        dhikrRef = dhikrRef.get(lang),
        dhikrCountNow = dhikrCountNow.get(lang),
        dhikrNoResults = dhikrNoResults.get(lang),
        dhikrSearchHint = dhikrSearchHint.get(lang),
        dhikrSequenceLabel = dhikrSequenceLabel.get(lang),
        catAfterPrayer = catAfterPrayer.get(lang),
        catDaily = catDaily.get(lang),
        catMorning = catMorning.get(lang),
        catEvening = catEvening.get(lang),
        catSalawat = catSalawat.get(lang),
        catIstighfar = catIstighfar.get(lang),
        catTahlil = catTahlil.get(lang),
        catAsmaUlHusna = catAsmaUlHusna.get(lang),
        catCustom = catCustom.get(lang),
        exploreGalleryTitle = exploreGalleryTitle.get(lang),
        exploreGallerySubtitle = exploreGallerySubtitle.get(lang),
    )
}