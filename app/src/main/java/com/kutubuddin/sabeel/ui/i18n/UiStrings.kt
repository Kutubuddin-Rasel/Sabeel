package com.kutubuddin.sabeel.ui.i18n

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import com.kutubuddin.sabeel.domain.model.DhikrCategory

/**
 * Resolved, per-language chrome strings — the type-safe bundle composables read
 * via [LocalStrings]. Grows one field per string as screens migrate.
 *
 * The compiler keeps this in sync with [UiText.resolve]: adding a field here
 * without filling it there is a compile error, and vice-versa.
 */
data class UiStrings(
    // ── Bottom navigation ─────────────────────────────
    val navHome: String,
    val navCount: String,
    val navDhikr: String,
    val navSettings: String,
    // ── Home: greetings (resolved from GreetingType at the call site) ──
    val greetingFajr: String,
    val greetingMorning: String,
    val greetingDhuhr: String,
    val greetingAfternoon: String,
    val greetingAsr: String,
    val greetingMaghrib: String,
    val greetingIsha: String,
    val greetingDefault: String,
    // ── Home: cards, headers, stats ──
    val homeResume: String,
    val homeTodaysSessions: String,
    val homeAllTime: String,
    val homeConsistency: String,
    val homeDailyGoal: String,
    val homeTotalCounted: String,
    val homeSessions: String,
    val homeCompleted: String,
    val homePartial: String,
    val homeBeginToday: String,
    val homeStartCounting: String,
    val homeDayOne: String,   // template: "%1$s day"
    val homeDayOther: String, // template: "%1$s days"
    // ── Daily Wird ──
    val wirdTitle: String,
    val wirdSetup: String,
    val wirdDoneOf: String,   // template: "%1$s of %2$s"
    // ── Counting screen ──
    val countSmartFlow: String,
    val countConsistencyA11y: String, // template: "Consistency: %1$s days"
    val countStreakShort: String,     // template: "%1$sd"
    val countUndo: String,            // a11y: undo-last-count description
    val countDecrementAction: String, // a11y: onClick action label
    val countTapHint: String,
    val countComplete: String,        // template: "%1$s complete"
    val countFinish: String,
    val countContinue: String,
    val countStepOf: String,          // template: "Step %1$s of %2$s"
    val countReward: String,
    val countCircleA11y: String,      // template: "Count %1$s of %2$s. Tap to count. Long press to reset."
    val countOf: String,              // template: "of %1$s"
    // ── Settings screen ──
    val settingsAppearance: String,
    val settingsLanguageText: String,
    val settingsCountingBehaviour: String,
    val settingsDailyGoalHeader: String,
    val settingsAbout: String,
    val settingsTheme: String,
    val settingsThemeDark: String,
    val settingsThemeLight: String,
    val settingsLanguage: String,
    val settingsHaptics: String,
    val settingsHapticOff: String,
    val settingsHapticLight: String,
    val settingsHapticMedium: String,
    val settingsHapticStrong: String,
    val settingsTranslit: String,
    val settingsTranslitDesc: String,
    val settingsSound: String,
    val settingsSoundDesc: String,
    val settingsAutoReset: String,
    val settingsAutoResetDesc: String,
    val settingsShowStreaks: String,
    val settingsShowStreaksDesc: String,
    val settingsDailyTarget: String,
    val settingsFont: String,
    val settingsVersion: String,
    // ── Dhikr Library ──
    val dhikrSearchPlaceholder: String,
    val dhikrRef: String,          // template: "Ref: %1$s"
    val dhikrCountNow: String,
    val dhikrNoResults: String,    // template: "No dhikr found for \"%1$s\""
    val dhikrSearchHint: String,
    // ── Dhikr categories (resolved via categoryLabel) ──
    val catAfterPrayer: String,
    val catDaily: String,
    val catMorning: String,
    val catEvening: String,
    val catSalawat: String,
    val catIstighfar: String,
    val catTahlil: String,
    val catCustom: String,
) {
    /**
     * Category labels resolve in the UI layer (not the domain enum) so the
     * domain model stays free of presentation/language concerns. The exhaustive
     * `when` makes the compiler flag any [DhikrCategory] we forget to localize.
     */
    fun categoryLabel(cat: DhikrCategory): String = when (cat) {
        DhikrCategory.AFTER_PRAYER -> catAfterPrayer
        DhikrCategory.DAILY -> catDaily
        DhikrCategory.MORNING -> catMorning
        DhikrCategory.EVENING -> catEvening
        DhikrCategory.SALAWAT -> catSalawat
        DhikrCategory.ISTIGHFAR -> catIstighfar
        DhikrCategory.TAHLIL -> catTahlil
        DhikrCategory.CUSTOM -> catCustom
    }
}

/**
 * The current language's chrome strings. Static (not observable-per-field)
 * because the value changes only on language switch — the whole subtree
 * recomposes, which is what we want. Defaults to English so Compose previews
 * and un-wrapped test trees render instead of crashing.
 */
val LocalStrings: ProvidableCompositionLocal<UiStrings> =
    staticCompositionLocalOf { UiText.resolve("en") }

/** Urdu is right-to-left; English and Bengali are left-to-right. */
fun layoutDirectionFor(lang: String): LayoutDirection =
    if (lang == "ur") LayoutDirection.Rtl else LayoutDirection.Ltr
