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
    val homeResume = LocalizedText(en = "Resume", ur = "جاری رکھیں", bn = "আবার শুরু")
    val homeTodaysSessions = LocalizedText(en = "Today's Sessions", ur = "آج کے سیشنز", bn = "আজকের সেশন")
    val homeAllTime = LocalizedText(en = "All Time", ur = "اب تک", bn = "সর্বকাল")
    val homeConsistency = LocalizedText(en = "Consistency", ur = "تسلسل", bn = "ধারাবাহিকতা")
    val homeDailyGoal = LocalizedText(en = "Daily Goal", ur = "روزانہ ہدف", bn = "দৈনিক লক্ষ্য")
    val homeTotalCounted = LocalizedText(en = "Total Counted", ur = "کل شمار", bn = "মোট গণনা")
    val homeSessions = LocalizedText(en = "Sessions", ur = "سیشنز", bn = "সেশন")
    val homeCompleted = LocalizedText(en = "Completed ✓", ur = "مکمل ✓", bn = "সম্পন্ন ✓")
    val homePartial = LocalizedText(en = "Partial", ur = "نامکمل", bn = "আংশিক")
    val homeBeginToday = LocalizedText(en = "Begin today's dhikr", ur = "آج کا ذکر شروع کریں", bn = "আজকের যিকির শুরু করুন")
    val homeStartCounting = LocalizedText(en = "Start counting", ur = "شمار شروع کریں", bn = "গণনা শুরু করুন")
    val homeDayOne = LocalizedText(en = "%1\$s day", ur = "%1\$s دن", bn = "%1\$s দিন")
    val homeDayOther = LocalizedText(en = "%1\$s days", ur = "%1\$s دن", bn = "%1\$s দিন")

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
        homeDailyGoal = homeDailyGoal.get(lang),
        homeTotalCounted = homeTotalCounted.get(lang),
        homeSessions = homeSessions.get(lang),
        homeCompleted = homeCompleted.get(lang),
        homePartial = homePartial.get(lang),
        homeBeginToday = homeBeginToday.get(lang),
        homeStartCounting = homeStartCounting.get(lang),
        homeDayOne = homeDayOne.get(lang),
        homeDayOther = homeDayOther.get(lang),
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
    )
}
