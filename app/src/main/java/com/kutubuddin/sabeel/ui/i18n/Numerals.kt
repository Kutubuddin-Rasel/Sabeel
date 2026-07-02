package com.kutubuddin.sabeel.ui.i18n

/**
 * Numeral and citation localization.
 *
 * Digits render in the script the user reads: Western for English, Extended
 * Arabic-Indic for Urdu, Bengali for Bengali. Hadith references translate only
 * the *book name* — the number is universal, so we localize its digits but
 * never "translate" it.
 *
 * SRP: pure String/Int transforms, no Compose or Android dependency, so they
 * are trivially unit-testable and reusable from any layer.
 */

private val URDU_DIGITS    = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
private val BENGALI_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

/** Rewrites ASCII digits `0-9` into the locale's script; other characters pass through. */
fun String.localizeDigits(lang: String): String {
    val table = when (lang) {
        "ur" -> URDU_DIGITS
        "bn" -> BENGALI_DIGITS
        else -> return this
    }
    return buildString {
        for (c in this@localizeDigits) append(if (c in '0'..'9') table[c - '0'] else c)
    }
}

fun Int.toLocalizedNumerals(lang: String): String = this.toString().localizeDigits(lang)

/**
 * Book-name lookup. Longest English prefix first so "Sahih al-Bukhari" is
 * matched before the shorter "Sahih …" would be. OCP: add a book by adding a row.
 */
private val HADITH_BOOKS: List<Triple<String, String, String>> = listOf(
    // english-prefix          urdu            bengali
    Triple("Sahih al-Bukhari", "صحیح بخاری", "সহীহ বুখারী"),
    Triple("Sahih Muslim",     "صحیح مسلم",  "সহীহ মুসলিম"),
    Triple("Abu Dawud",        "ابو داؤد",   "আবু দাউদ"),
    Triple("Tirmidhi",         "ترمذی",      "তিরমিযী")
)

/** Localizes a citation like "Sahih Muslim 596" → "صحیح مسلم ۵۹۶" (ur) / "সহীহ মুসলিম ৫৯৬" (bn). */
fun localizeHadithRef(ref: String, lang: String): String {
    if (lang == "en") return ref
    for ((en, ur, bn) in HADITH_BOOKS) {
        if (ref.startsWith(en)) {
            val name = if (lang == "ur") ur else bn
            val rest = ref.removePrefix(en).localizeDigits(lang)
            return name + rest
        }
    }
    return ref.localizeDigits(lang)
}
