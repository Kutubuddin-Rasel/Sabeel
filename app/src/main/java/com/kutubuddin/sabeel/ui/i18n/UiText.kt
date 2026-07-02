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
    )
}
