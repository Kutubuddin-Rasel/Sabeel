package com.kutubuddin.sabeel.ui.i18n

/**
 * Numeral and citation localization.
 *
 * Digits render in the script the user reads: Western for English, Extended
 * Arabic-Indic for Bengali. Hadith references translate only
 * the *book name* — the number is universal, so we localize its digits but
 * never "translate" it.
 *
 * SRP: pure String/Int transforms, no Compose or Android dependency, so they
 * are trivially unit-testable and reusable from any layer.
 */

private val BENGALI_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

/** Rewrites ASCII digits `0-9` into the locale's script; other characters pass through. */
fun String.localizeDigits(lang: String): String {
    val table = when (lang) {
        "bn" -> BENGALI_DIGITS
        else -> return this
    }
    return buildString {
        for (c in this@localizeDigits) append(if (c in '0'..'9') table[c - '0'] else c)
    }
}

fun Int.toLocalizedNumerals(lang: String): String = this.toString().localizeDigits(lang)

/**
 * Groups a number with thousands separators, then localizes the digits — for
 * large chrome totals (e.g. the all-time count). Grouping is Western
 * (`1,234,567`) for universal clarity; only the digits follow the language.
 */
fun Int.toGroupedLocalizedNumerals(lang: String): String =
    java.text.NumberFormat.getIntegerInstance(java.util.Locale.US)
        .format(this.toLong())
        .localizeDigits(lang)

/**
 * Book-name lookup. Longest English prefix first so "Sahih al-Bukhari" is
 * matched before the shorter "Sahih …" would be. OCP: add a book by adding a row.
 */
private val HADITH_BOOKS: List<Pair<String, String>> = listOf(
    // english-prefix          bengali
    Pair("Sahih al-Bukhari", "সহীহ বুখারী"),
    Pair("Sahih Muslim",     "সহীহ মুসলিম"),
    Pair("Abu Dawud",        "আবু দাউদ"),
    Pair("Tirmidhi",         "তিরমিযী")
)

/** Localizes a citation like "Sahih Muslim 596" → "সহীহ মুসলিম ৫৯৬" (bn). */
fun localizeHadithRef(ref: String, lang: String): String {
    if (lang == "en") return ref
    for ((en, bn) in HADITH_BOOKS) {
        if (ref.startsWith(en)) {
            val rest = ref.removePrefix(en).localizeDigits(lang)
            return bn + rest
        }
    }
    return ref.localizeDigits(lang)
}
