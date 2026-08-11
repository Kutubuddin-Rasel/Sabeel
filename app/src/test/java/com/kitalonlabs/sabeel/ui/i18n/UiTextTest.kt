package com.kitalonlabs.sabeel.ui.i18n

import com.kitalonlabs.sabeel.domain.model.LocalizedText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.reflect.full.declaredMemberProperties

/**
 * Guards the chrome-localization dictionary.
 *
 * The completeness test uses reflection to auto-discover every LocalizedText
 * entry in UiText, so no manual list must be maintained — adding a string with
 * a blank ur/bn translation turns the build red.
 */
class UiTextTest {

    @Test
    fun resolvesRequestedLanguage() {
        assertEquals("ہوم", UiText.resolve("ur").navHome)
        assertEquals("হোম", UiText.resolve("bn").navHome)
        assertEquals("Home", UiText.resolve("en").navHome)
    }

    @Test
    fun blankTranslationFallsBackToEnglish() {
        val t = LocalizedText(en = "Only English")
        assertEquals("Only English", t.get("ur"))
        assertEquals("Only English", t.get("bn"))
    }

    @Test
    fun everyStringHasUrduAndBengali() {
        val missing = UiText::class.declaredMemberProperties
            .mapNotNull { prop ->
                (prop.getter.call(UiText) as? LocalizedText)?.let { lt -> prop.name to lt }
            }
            .filter { (_, lt) -> lt.ur.isBlank() || lt.bn.isBlank() }
            .map { it.first }
        assertTrue("Untranslated UiText entries (missing ur/bn): $missing", missing.isEmpty())
    }
}
