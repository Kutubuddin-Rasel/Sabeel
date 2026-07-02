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

    fun resolve(lang: String) = UiStrings(
        navHome = navHome.get(lang),
        navCount = navCount.get(lang),
        navDhikr = navDhikr.get(lang),
        navSettings = navSettings.get(lang),
    )
}
