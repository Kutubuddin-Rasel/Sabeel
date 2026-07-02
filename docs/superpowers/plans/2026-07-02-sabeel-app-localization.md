# Sabeel App Localization + RTL — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the entire app (chrome + numerals + text direction) localize to the user's chosen language (English / Urdu / Bengali) instantly, driven by the existing DataStore `language` setting.

**Architecture:** An in-code translation dictionary (`UiText`, one `LocalizedText` per string) resolves to a typed per-language bundle (`UiStrings`) provided through a `CompositionLocal` (`LocalStrings`) at the `MainActivity` theme wrapper, alongside `LocalLayoutDirection` for RTL. Composables read `LocalStrings.current.*`; numerals flow through the existing `Numerals.kt` helpers. Domain stays language-agnostic (category labels resolve in the UI layer).

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Hilt, DataStore, JUnit4 + Robolectric + Compose UI test.

**Spec:** `docs/superpowers/specs/2026-07-02-sabeel-app-localization-rtl-design.md`

## Global Constraints

- Language source is DataStore `SettingsRepository.language` (`"en"`/`"ur"`/`"bn"`) — NOT the system locale. No AppCompat/Configuration.
- Reuse the existing `com.kutubuddin.sabeel.domain.model.LocalizedText(en, ur="", bn="")` and its `.get(lang)` (en-fallback on blank).
- Numerals go through existing `com.kutubuddin.sabeel.ui.i18n` helpers: `Int.toLocalizedNumerals(lang)`, `String.localizeDigits(lang)`, `localizeHadithRef(ref, lang)`.
- RTL only for `"ur"`; `"en"`/`"bn"` are LTR.
- Kotlin string escaping: printf placeholders authored as `"%1\$s"` (escape `$`).
- Per-task gate: `./gradlew :app:compileDebugKotlin` compiles AND `./gradlew :app:testDebugUnitTest` passes (excepting the known-pre-existing `PocketModeServiceTest.testVolumeKeysInterception_triggersHaptics` failure and the `@Ignore`d long-press test).
- Do NOT deliberately stage `DhikrViewModel.kt` (unrelated) or `Screenshots/`.
- Leave fixed identifiers untranslated: app version, font proper-name, "تَمَّ", the سَبِيل brand flourish, native language names (English / اردو / বাংলা), and dhikr proper-names (`DhikrItem.displayName`, `SmartFlowVariant.displayName`).
- Commit trailer: `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.

---

## File Structure

- Create `app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiStrings.kt` — resolved per-language bundle (`data class UiStrings`), `LocalStrings` CompositionLocal, `categoryLabel()`.
- Create `app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiText.kt` — translation dictionary (`object UiText` of `LocalizedText` vals) + `resolve(lang): UiStrings`.
- Create `app/src/main/java/com/kutubuddin/sabeel/ui/home/GreetingType.kt` — time-of-day enum.
- Modify `MainActivity.kt` — collect `language`, provide `LocalStrings` + `LocalLayoutDirection`.
- Modify screens/components to read `LocalStrings.current.*` + localize numerals: `SabeelTab.kt`/`SabeelBottomBar.kt`, `HomeScreen.kt`, `HomeViewModel.kt`, `HomeState.kt`, `TasbihScreen.kt`, `TasbihCircle.kt`, `SequenceTracker.kt`, `CompletionRest.kt`, `SpiritualRewardCard.kt`, `OdometerCounter.kt`, `SettingsScreen.kt`, `DhikrLibraryScreen.kt`.
- Modify `Numerals.kt` — add grouped-numeral helper.
- Tests under `app/src/test/java/com/kutubuddin/sabeel/ui/i18n/` and `ui/home/`.

---

## Task 1: Core i18n types (UiStrings, UiText, LocalStrings) + resolution/fallback/completeness tests

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiStrings.kt`
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiText.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/ui/i18n/UiTextTest.kt`

**Interfaces:**
- Produces: `data class UiStrings(...)` (String fields; grows as screens migrate); `object UiText { val <name>: LocalizedText; fun resolve(lang: String): UiStrings }`; `val LocalStrings: ProvidableCompositionLocal<UiStrings>`; `fun UiStrings.categoryLabel(cat: DhikrCategory): String`.
- Consumes: `domain.model.LocalizedText`, `domain.model.DhikrCategory`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.kutubuddin.sabeel.ui.i18n

import com.kutubuddin.sabeel.domain.model.LocalizedText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.reflect.full.declaredMemberProperties

class UiTextTest {
    @Test fun resolvesRequestedLanguage() {
        assertEquals("ہوم", UiText.resolve("ur").navHome)
        assertEquals("হোম", UiText.resolve("bn").navHome)
        assertEquals("Home", UiText.resolve("en").navHome)
    }

    @Test fun blankTranslationFallsBackToEnglish() {
        val t = LocalizedText(en = "Only English")
        assertEquals("Only English", t.get("ur"))
        assertEquals("Only English", t.get("bn"))
    }

    // The build-time guard: every dictionary entry must have ur AND bn.
    @Test fun everyStringHasUrduAndBengali() {
        val missing = UiText::class.declaredMemberProperties
            .mapNotNull { prop ->
                (prop.getter.call(UiText) as? LocalizedText)?.let { lt -> prop.name to lt }
            }
            .filter { (_, lt) -> lt.ur.isBlank() || lt.bn.isBlank() }
            .map { it.first }
        assertTrue("Untranslated UiText entries (missing ur/bn): $missing", missing.isEmpty())
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.kutubuddin.sabeel.ui.i18n.UiTextTest"`
Expected: FAIL — `UiText`/`UiStrings` unresolved references (won't compile).

- [ ] **Step 3: Write minimal implementation**

`UiStrings.kt`:
```kotlin
package com.kutubuddin.sabeel.ui.i18n

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.kutubuddin.sabeel.domain.model.DhikrCategory

/** Resolved, per-language chrome strings. Grows one field per string as
 *  screens migrate. Composables read these via [LocalStrings]. */
data class UiStrings(
    // Bottom navigation
    val navHome: String,
    val navCount: String,
    val navDhikr: String,
    val navSettings: String,
) {
    fun categoryLabel(cat: DhikrCategory): String = when (cat) {
        // Filled in Task 6 once category fields exist; placeholder keeps the
        // domain-pure mapping in the UI layer.
        else -> cat.displayName
    }
}

/** Default EN so previews / un-wrapped test trees render instead of crashing. */
val LocalStrings: ProvidableCompositionLocal<UiStrings> =
    staticCompositionLocalOf { UiText.resolve("en") }
```

`UiText.kt`:
```kotlin
package com.kutubuddin.sabeel.ui.i18n

import com.kutubuddin.sabeel.domain.model.LocalizedText

/** Translation dictionary — the single source of truth. One [LocalizedText]
 *  per chrome string. `resolve` builds the typed [UiStrings] bundle; the
 *  compiler enforces every UiStrings field is provided. */
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
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.kutubuddin.sabeel.ui.i18n.UiTextTest"`
Expected: PASS (3 tests).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiStrings.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiText.kt \
        app/src/test/java/com/kutubuddin/sabeel/ui/i18n/UiTextTest.kt
git commit -m "feat(i18n): UiText dictionary + UiStrings bundle + completeness test

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 2: Wire MainActivity + RTL direction; migrate bottom nav to LocalStrings

**Files:**
- Modify: `app/src/main/java/com/kutubuddin/sabeel/MainActivity.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/navigation/SabeelTab.kt` and/or `SabeelBottomBar.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/ui/i18n/LayoutDirectionTest.kt`

**Interfaces:**
- Consumes: `LocalStrings`, `UiText.resolve`, `SettingsRepository.language`.
- Produces: app tree wrapped in `CompositionLocalProvider(LocalStrings provides …, LocalLayoutDirection provides …)`.

- [ ] **Step 1: Write the failing test** (pure direction-selection logic, kept testable)

Add a tiny pure helper to `UiStrings.kt` and test it:
```kotlin
// in LayoutDirectionTest.kt
package com.kutubuddin.sabeel.ui.i18n
import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertEquals
import org.junit.Test
class LayoutDirectionTest {
    @Test fun urduIsRtlOthersLtr() {
        assertEquals(LayoutDirection.Rtl, layoutDirectionFor("ur"))
        assertEquals(LayoutDirection.Ltr, layoutDirectionFor("en"))
        assertEquals(LayoutDirection.Ltr, layoutDirectionFor("bn"))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.kutubuddin.sabeel.ui.i18n.LayoutDirectionTest"`
Expected: FAIL — `layoutDirectionFor` unresolved.

- [ ] **Step 3: Write minimal implementation**

Add to `UiStrings.kt`:
```kotlin
import androidx.compose.ui.unit.LayoutDirection
fun layoutDirectionFor(lang: String): LayoutDirection =
    if (lang == "ur") LayoutDirection.Rtl else LayoutDirection.Ltr
```

Wire `MainActivity.kt` (inside `setContent`, alongside the existing `theme` collection):
```kotlin
val theme by settingsRepository.theme.collectAsState(initial = "dark")
val language by settingsRepository.language.collectAsState(initial = "en")
val strings = remember(language) { UiText.resolve(language) }
CompositionLocalProvider(
    LocalStrings provides strings,
    LocalLayoutDirection provides layoutDirectionFor(language),
) {
    SabeelTheme(darkTheme = theme != "light") {
        SabeelNavHost(/* existing args */)
    }
}
```
(Add imports: `androidx.compose.runtime.*`, `androidx.compose.ui.platform.LocalLayoutDirection`, `com.kutubuddin.sabeel.ui.i18n.*`.)

Migrate bottom-nav labels: replace the hardcoded `label` render in `SabeelBottomBar.kt` with `LocalStrings.current.navHome/navCount/navDhikr/navSettings` keyed by tab (leave `SabeelTab.route` as-is; only the display label is localized).

- [ ] **Step 4: Run tests**

Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest --tests "com.kutubuddin.sabeel.ui.i18n.*"`
Expected: compile OK; PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/kutubuddin/sabeel/MainActivity.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/navigation/ \
        app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiStrings.kt \
        app/src/test/java/com/kutubuddin/sabeel/ui/i18n/LayoutDirectionTest.kt
git commit -m "feat(i18n): provide LocalStrings + LayoutDirection; localize bottom nav

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 3: Home screen — GreetingType refactor + chrome + numerals

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/home/GreetingType.kt`
- Modify: `HomeViewModel.kt` (`resolveGreeting()` → `GreetingType`), `HomeState.kt` (`greeting: GreetingType`), `HomeScreen.kt` (resolve via `LocalStrings`; localize numerals), `UiStrings.kt` + `UiText.kt` (add Home fields).
- Test: `app/src/test/java/com/kutubuddin/sabeel/ui/home/GreetingTypeTest.kt`

**Interfaces:**
- Produces: `enum class GreetingType { FAJR, MORNING, DHUHR, AFTERNOON, ASR, MAGHRIB, ISHA, DEFAULT }`; `fun greetingTypeForHour(hour: Int): GreetingType`; `UiStrings` gains `greetingFajr, greetingMorning, greetingDhuhr, greetingAfternoon, greetingAsr, greetingMaghrib, greetingIsha, greetingDefault, homeTodaysSessions, homeAllTime, homeResume, homeConsistency, homeDailyGoal, homeTotalCounted, homeSessions, homeCompleted, homePartial, homeBeginToday, homeStartCounting`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.kutubuddin.sabeel.ui.home
import org.junit.Assert.assertEquals
import org.junit.Test
class GreetingTypeTest {
    @Test fun mapsHourBoundaries() {
        assertEquals(GreetingType.FAJR, greetingTypeForHour(5))
        assertEquals(GreetingType.MORNING, greetingTypeForHour(9))
        assertEquals(GreetingType.DHUHR, greetingTypeForHour(12))
        assertEquals(GreetingType.AFTERNOON, greetingTypeForHour(14))
        assertEquals(GreetingType.ASR, greetingTypeForHour(16))
        assertEquals(GreetingType.MAGHRIB, greetingTypeForHour(18))
        assertEquals(GreetingType.ISHA, greetingTypeForHour(20))
        assertEquals(GreetingType.DEFAULT, greetingTypeForHour(2))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.kutubuddin.sabeel.ui.home.GreetingTypeTest"`
Expected: FAIL — `GreetingType`/`greetingTypeForHour` unresolved.

- [ ] **Step 3: Write minimal implementation**

`GreetingType.kt`:
```kotlin
package com.kutubuddin.sabeel.ui.home

enum class GreetingType { FAJR, MORNING, DHUHR, AFTERNOON, ASR, MAGHRIB, ISHA, DEFAULT }

fun greetingTypeForHour(hour: Int): GreetingType = when (hour) {
    in 4..6   -> GreetingType.FAJR
    in 7..11  -> GreetingType.MORNING
    in 12..13 -> GreetingType.DHUHR
    in 14..15 -> GreetingType.AFTERNOON
    in 16..17 -> GreetingType.ASR
    in 18..19 -> GreetingType.MAGHRIB
    in 20..21 -> GreetingType.ISHA
    else      -> GreetingType.DEFAULT
}
```
- `HomeViewModel.resolveGreeting()` → returns `greetingTypeForHour(LocalTime.now().hour)`; `HomeState.greeting: GreetingType = GreetingType.DEFAULT`.
- `HomeScreen`: `val s = LocalStrings.current`; map `state.greeting` → `when` over `s.greeting*`; replace section headers, "Resume", "Consistency", "Daily Goal", "Total Counted", "Sessions", `"Completed ✓"`/`"Partial"`, "Begin today's dhikr", "Start counting" with `s.*`. Thread `language` (from settings) and localize every numeric (`count`, `target`, `totalToday`, `dailyGoal`, `session.count`, `totalSessionCount`) via `.toLocalizedNumerals(language)`.

- [ ] **Step 4: Run tests**

Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest --tests "com.kutubuddin.sabeel.ui.home.*" --tests "com.kutubuddin.sabeel.ui.i18n.*"`
Expected: compile OK; PASS (completeness test still green — new fields seeded with ur/bn).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/kutubuddin/sabeel/ui/home/ \
        app/src/main/java/com/kutubuddin/sabeel/ui/i18n/ \
        app/src/test/java/com/kutubuddin/sabeel/ui/home/GreetingTypeTest.kt
git commit -m "feat(i18n): localize Home chrome + numerals; greeting via GreetingType

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 4: Counting screen chrome + numerals

**Files:**
- Modify: `TasbihScreen.kt` ("Tasbīḥ after Salah", streak "Xd" template, "−1", "Tap the circle to count"), `TasbihCircle.kt` ("of X" already numeral-aware — localize label word), `SequenceTracker.kt` ("Step X of Y" template), `CompletionRest.kt` ("X complete", "Finish", "Continue"), `SpiritualRewardCard.kt` ("SPIRITUAL REWARD").
- Modify: `UiStrings.kt` + `UiText.kt` (add counting fields incl. templates `stepXofY`, `xComplete`, `streakDaysShort`).
- Test: extend `UiTextTest` completeness (auto-covers new entries).

**Interfaces:**
- Produces: `UiStrings` gains `countTasbihAfterSalah, countTapHint, countDecrement, countOfLabel, spiritualReward, stepXofY, xComplete, streakDaysShort, restFinish, restContinue`.

- [ ] **Step 1: Write the failing test** — add to `UiTextTest`:
```kotlin
@Test fun countingTemplatesUsePositionalPlaceholders() {
    assertTrue(UiText.stepXofY.en.contains("%1\$s") && UiText.stepXofY.en.contains("%2\$s"))
}
```
- [ ] **Step 2: Run to verify fail** — `./gradlew :app:testDebugUnitTest --tests "...UiTextTest"` → FAIL (`stepXofY` unresolved).
- [ ] **Step 3: Implement** — add fields to `UiText`/`UiStrings`; migrate the five components to `LocalStrings.current.*`. For templates: `String.format(s.stepXofY, (stepIndex+1).toLocalizedNumerals(language), stepCount.toLocalizedNumerals(language))`. Keep `OdometerCounter` numerals as-is (already language-aware). Keep "تَمَّ".
- [ ] **Step 4: Run** — `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → compile OK; existing `TasbihScreenGestureTest` still green on EN default; completeness green.
- [ ] **Step 5: Commit** — `git commit -m "feat(i18n): localize counting screen chrome + numerals\n\nCo-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"`

---

## Task 5: Settings screen chrome (headers, labels, descriptions, options) + rename Translation→Language

**Files:**
- Modify: `SettingsScreen.kt` (all `SettingsHeader`, `SettingsSegmentRow`/`SettingsToggleRow` labels+descriptions+options, `DailyGoalRow` "Daily target" + goal numeral, `SettingsInfoRow` labels). Rename the language row label to `s.settingsLanguage`; keep option display values native (English/اردو/বাংলা) and keep info-row *values* (font, version) verbatim.
- Modify: `UiStrings.kt` + `UiText.kt` (add ~25 settings fields).

**Interfaces:**
- Produces: `UiStrings` gains `settingsAppearance, settingsTheme, themeDark, themeLight, settingsLanguageSection, settingsLanguage, settingsShowTranslit, settingsShowTranslitDesc, settingsCountingBehaviour, settingsHaptic, hapticOff, hapticLight, hapticMedium, hapticStrong, settingsSoundMilestone, settingsSoundMilestoneDesc, settingsAutoReset, settingsAutoResetDesc, settingsShowStreaks, settingsShowStreaksDesc, settingsDailyGoalSection, settingsDailyTarget, settingsAbout, settingsFont, settingsVersion`.

- [ ] **Step 1–2: Failing test** — completeness test auto-covers the new entries; add `@Test fun settingsPresent() { assertEquals("زبان", UiText.resolve("ur").settingsLanguage) }` (adjust once translation authored). Run → FAIL (unresolved). *(If exact translation not yet chosen, assert non-blank instead: `assertTrue(UiText.resolve("ur").settingsLanguage.isNotBlank())`.)*
- [ ] **Step 3: Implement** — add fields; migrate all `SettingsScreen` literals to `LocalStrings.current.*`; localize the daily-goal number via `goal.toLocalizedNumerals(language)` (thread `state.language`).
- [ ] **Step 4: Run** — `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → PASS.
- [ ] **Step 5: Commit** — `feat(i18n): localize Settings; rename Translation→Language`.

---

## Task 6: Dhikr Library chrome + category labels

**Files:**
- Modify: `DhikrLibraryScreen.kt` (search placeholder, "Ref:" prefix, "Count Now →", empty-state lines, `CategoryHeader` via `s.categoryLabel(category)`).
- Modify: `UiStrings.kt` (`categoryLabel` real mapping + 8 category fields + `libSearchHint, libRefPrefix, libCountNow, libEmptyTitle, libEmptyHint`), `UiText.kt`.

**Interfaces:**
- Produces: `UiStrings` gains `catAfterPrayer, catDaily, catMorning, catEvening, catSalawat, catIstighfar, catTahlil, catCustom` and the library fields; `categoryLabel(cat)` switches over them.

- [ ] **Step 1–2: Failing test** — add to `UiTextTest`:
```kotlin
@Test fun categoryLabelResolves() {
    val ur = UiText.resolve("ur")
    assertTrue(ur.categoryLabel(com.kutubuddin.sabeel.domain.model.DhikrCategory.AFTER_PRAYER).isNotBlank())
}
```
Run → FAIL.
- [ ] **Step 3: Implement** — fill `categoryLabel` `when` over the 8 fields; migrate library literals (`"Ref: ${localizeHadithRef(...)}"` keeps the hadith helper; wrap prefix in `s.libRefPrefix` via `String.format`). Empty-state `"No dhikr found for \"$query\""` → `String.format(s.libEmptyTitle, query)`.
- [ ] **Step 4: Run** — compile + tests PASS.
- [ ] **Step 5: Commit** — `feat(i18n): localize Dhikr Library + category labels`.

---

## Task 7: RTL fixes — OdometerCounter LTR + SpiritualRewardCard direction-aware accent

**Files:**
- Modify: `OdometerCounter.kt` (wrap digit `Row` in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr)`).
- Modify: `SpiritualRewardCard.kt` (`drawBehind`: read `layoutDirection`, draw accent at start edge — `x = if (layoutDirection == Ltr) strokeWidth/2 else size.width - strokeWidth/2`).
- Test: `app/src/test/java/com/kutubuddin/sabeel/ui/tasbih/OdometerRtlTest.kt` — render `OdometerCounter(count=12)` under an RTL parent, assert digits appear left-to-right ("1" node is left of "2" node) OR assert the composable places digits in ascending index x-order.

**Interfaces:** none new.

- [ ] **Step 1: Write failing test** (Compose UI test under RTL parent asserting digit order preserved).
- [ ] **Step 2: Run → FAIL** (digits reversed under RTL before fix).
- [ ] **Step 3: Implement** both fixes.
- [ ] **Step 4: Run → PASS**; `./gradlew :app:compileDebugKotlin` OK.
- [ ] **Step 5: Commit** — `fix(i18n): keep numbers LTR + start-edge reward accent under RTL`.

---

## Task 8: Grouped-numeral helper for large totals

**Files:**
- Modify: `Numerals.kt` (add `fun Int.toGroupedLocalizedNumerals(lang): String` — group with thousands separators then `localizeDigits(lang)`).
- Modify: `HomeScreen.kt` line ~278 — replace `NumberFormat.getInstance().format(state.totalAllTime)` with `state.totalAllTime.toGroupedLocalizedNumerals(language)`.
- Test: `NumeralsTest.kt` — extend.

- [ ] **Step 1: Failing test** — `assertEquals("۱٬۲۳۴" or expected, 1234.toGroupedLocalizedNumerals("ur"))` (choose grouping consistent with existing `localizeDigits`; assert digits localized + grouping present).
- [ ] **Step 2: Run → FAIL** (unresolved).
- [ ] **Step 3: Implement** helper (`java.text.NumberFormat.getIntegerInstance(java.util.Locale.US).format(this).localizeDigits(lang)`).
- [ ] **Step 4: Run → PASS**.
- [ ] **Step 5: Commit** — `feat(i18n): grouped localized numerals for all-time total`.

---

## Task 9: Author + review all ur/bn translations; finalize completeness

**Files:**
- Modify: `UiText.kt` (fill every `LocalizedText` with reviewed ur/bn), any test asserting exact translation text.

- [ ] **Step 1:** Ensure `UiTextTest.everyStringHasUrduAndBengali` covers the full catalog (it auto-discovers via reflection — no edit needed).
- [ ] **Step 2:** Run it — it FAILS listing any entry still missing ur/bn.
- [ ] **Step 3:** Fill all translations (drafted by Claude).
- [ ] **Step 4:** Run — PASS. Full suite: `./gradlew :app:testDebugUnitTest`.
- [ ] **Step 5:** Present the ur/bn table to the user for review (per spec §10). Commit after approval — `feat(i18n): complete Urdu + Bengali translations`.

---

## Self-Review

**Spec coverage:** §4 infra → Task 1–2; §5 inventory → Tasks 2–6; §6 refactors → Task 3 (greeting) + Task 6 (categoryLabel); §7 RTL → Task 7 (+ direction provided in Task 2); §8 numerals → Tasks 3–6 + Task 8; §9 testing → resolution/fallback/completeness (Task 1), RTL (Task 7), numerals (Task 8), greeting (Task 3); §10 translation review → Task 9. All covered.

**Placeholder scan:** Task 4/5/6 use compact step notation (not full 5-step code blocks) to avoid duplicating the 70-string catalog; each still names exact fields, files, commands, and gates — acceptable given the dictionary pattern is fully shown in Task 1.

**Type consistency:** `UiStrings` field names referenced in later tasks match the `Produces` blocks; `layoutDirectionFor`, `greetingTypeForHour`, `categoryLabel`, `toGroupedLocalizedNumerals` are consistent across tasks.
