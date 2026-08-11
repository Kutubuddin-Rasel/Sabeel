package com.kitalonlabs.sabeel.ui.i18n

import org.junit.Assert.assertEquals
import org.junit.Test

class NumeralsTest {
    @Test fun urdu_digits() = assertEquals("۳۳", 33.toLocalizedNumerals("ur"))
    @Test fun bengali_digits() = assertEquals("৩৩", 33.toLocalizedNumerals("bn"))
    @Test fun english_digits() = assertEquals("33", 33.toLocalizedNumerals("en"))
    @Test fun hadith_urdu() = assertEquals("صحیح مسلم ۵۹۶", localizeHadithRef("Sahih Muslim 596", "ur"))
    @Test fun hadith_bengali() = assertEquals("সহীহ মুসলিম ৫৯৬", localizeHadithRef("Sahih Muslim 596", "bn"))
    @Test fun hadith_english_unchanged() = assertEquals("Sahih Muslim 596", localizeHadithRef("Sahih Muslim 596", "en"))
    @Test fun hadith_bukhari_urdu() = assertEquals("صحیح بخاری ۶۳۰۷", localizeHadithRef("Sahih al-Bukhari 6307", "ur"))

    // Grouped totals: Western thousands separators (universal), localized digits.
    @Test fun grouped_english() = assertEquals("1,234,567", 1234567.toGroupedLocalizedNumerals("en"))
    @Test fun grouped_urdu() = assertEquals("۱,۲۳۴,۵۶۷", 1234567.toGroupedLocalizedNumerals("ur"))
    @Test fun grouped_bengali() = assertEquals("১,২৩৪,৫৬৭", 1234567.toGroupedLocalizedNumerals("bn"))
}
