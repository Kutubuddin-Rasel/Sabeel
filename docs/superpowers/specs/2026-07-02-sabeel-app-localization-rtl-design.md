# Sabeel — Full App Localization + RTL (Design Spec)

**Date:** 2026-07-02
**Status:** Approved (design) — ready for implementation plan
**Scope:** Q1 of the three-question UX pass. Make the *entire* app localize to the
user's chosen language (English / Urdu / Bengali), not just dhikr content. Includes
right-to-left layout for Urdu.

---

## 1. Problem & Goal

Today the app has **two tiers** of text:

- **Content** (dhikr meanings, spiritual rewards, hadith refs, numerals) — already
  trilingual via `LocalizedText` / `DhikrMeaning` / `Numerals.kt`.
- **Chrome** (buttons, labels, section headers, settings, greetings, nav) — ~70
  hardcoded English literals with zero localization.

A user who selects اردو or বাংলা still sees "Start counting", "Daily Goal",
"Settings", "Resume", etc. in English. The goal: **one "App Language" setting
switches everything** — chrome + content + numerals + text direction — instantly,
via recomposition, with no Activity recreation.

**Success criteria**

1. Selecting a language in Settings re-renders all visible chrome in that language
   immediately.
2. Urdu renders right-to-left; Bengali and English render left-to-right.
3. All numerals (counts, targets, streaks, goals, totals) render in the language's
   digit set.
4. A build-time test fails if any chrome string is missing an Urdu or Bengali
   translation.
5. No regression to existing screen/gesture/unit tests.

---

## 2. Non-Goals (YAGNI)

- **System-locale following.** Language is driven solely by the DataStore setting,
  consistent with the existing `SettingsRepository.language`. We do not read the
  device locale or plumb `Configuration`/`AppCompat`.
- **Nastaliq typography for Urdu.** Android's bundled Noto renders Urdu in Naskh
  and Bengali correctly. Bundling a Nastaliq font is a noted future enhancement,
  not part of this work.
- **Localizing dhikr proper names.** `DhikrItem.displayName` (e.g. "SubhanAllah")
  and `SmartFlowVariant.displayName` (e.g. "Classic · 33·33·34") stay as their
  current transliterated Latin form for v1. These are transliterations of Arabic
  scripture names, a separate content-localization effort. Dhikr *meanings* and
  *rewards* are already trilingual and continue to resolve by language.
- **Localizing fixed identifiers.** App version ("1.0.0"), the font proper-name
  ("KFGQPC Uthmanic Script Hafs"), the Arabic completion glyph ("تَمَّ"), and the
  سَبِيل brand flourish remain as-is.

---

## 3. Approach (chosen)

**Approach A — in-code `UiStrings` catalog + `CompositionLocal`.** (Confirmed with
user over two alternatives: Android string resources / `values-*` XML, and a
runtime map lookup.)

Rationale:

- **Consistent with existing content localization.** The app already localizes
  content in Kotlin via `LocalizedText(en, ur, bn)` and a DataStore-driven
  `language` string — not via Android resource qualifiers. Approach A reuses that
  exact model for chrome, so there is one localization mechanism, not two.
- **Single source of language.** DataStore `language` already drives content and
  numerals. Chrome joins the same source; no `Configuration`/`AppCompat` plumbing,
  no Activity recreation — language changes are pure recomposition.
- **Type-safe call sites.** Composables read `LocalStrings.current.startCounting`
  (a `String`), not a map key or resource id — refactors and typos are compile
  errors.

Rejected:

- **`values-*` XML resources** would follow the *system* locale by default,
  fighting the existing DataStore-driven model, and would split localization across
  two mechanisms (XML for chrome, Kotlin for content). Instant in-app switching
  without recreation would require `AppCompatDelegate`/`Configuration` overrides —
  more moving parts for no benefit here.
- **Runtime `Map<String, String>` lookup** loses compile-time safety (missing keys
  become runtime blanks) and offers nothing over a typed data class.

---

## 4. Architecture

### 4.1 New files (`ui/i18n/`)

**`UiText.kt` — the translation dictionary (source of truth).**
An `object` holding one `LocalizedText` val per chrome string, grouped by screen
with comments. Reuses the existing `domain.model.LocalizedText`. Interpolated
strings use positional placeholders so each language controls word order:

```kotlin
object UiText {
    // ── Bottom navigation ─────────────────────────────
    val navHome  = LocalizedText(en = "Home",  ur = "ہوم",   bn = "হোম")
    val navCount = LocalizedText(en = "Count", ur = "شمار",  bn = "গণনা")
    // … ~70 entries …

    // Templates: %1$s / %2$s let translations reorder.
    val stepXofY = LocalizedText(
        en = "Step %1$s of %2$s",
        ur = "%2$s میں سے %1$s قدم",
        bn = "%2$s-এর ধাপ %1$s"
    )

    fun resolve(lang: String) = UiStrings(
        navHome  = navHome.get(lang),
        navCount = navCount.get(lang),
        // … every UiStrings field, compiler-enforced …
    )
}
```

**`UiStrings.kt` — the resolved per-language bundle + the CompositionLocal.**
A `data class` whose fields are already-resolved `String`s (and template strings),
plus the local that provides it:

```kotlin
data class UiStrings(
    val navHome: String,
    val navCount: String,
    // … one field per chrome string …
    val stepXofY: String,       // template, formatted at call site
) {
    // Domain enum → label mapping lives in the UI layer, keeping the
    // domain model free of presentation/language concerns (DIP).
    fun categoryLabel(cat: DhikrCategory): String = when (cat) {
        DhikrCategory.AFTER_PRAYER -> catAfterPrayer
        DhikrCategory.DAILY        -> catDaily
        // …
    }
}

val LocalStrings = staticCompositionLocalOf { UiText.resolve("en") }
```

- `staticCompositionLocalOf` (not `compositionLocalOf`): the value changes rarely
  (only on language switch), so we want the cheaper static local that recomposes
  the whole subtree rather than tracking fine-grained reads.
- Default = `UiText.resolve("en")` so Compose previews and any un-wrapped test tree
  render in English instead of crashing.

### 4.2 Wiring point — `MainActivity`

`MainActivity` already collects `theme` and wraps `SabeelNavHost` in `SabeelTheme`.
Add a `language` collection alongside it and provide both the strings bundle and the
layout direction above the theme:

```kotlin
val theme by settingsRepository.theme.collectAsState(initial = "dark")
val language by settingsRepository.language.collectAsState(initial = "en")
val strings = remember(language) { UiText.resolve(language) }
val direction = if (language == "ur") LayoutDirection.Rtl else LayoutDirection.Ltr

CompositionLocalProvider(
    LocalStrings provides strings,
    LocalLayoutDirection provides direction,
) {
    SabeelTheme(darkTheme = theme != "light") {
        SabeelNavHost(/* … */)
    }
}
```

This is the *single* wiring point. Every descendant reads `LocalStrings.current`
and inherits `LayoutDirection`.

### 4.3 Data flow

```
DataStore(language) ──▶ MainActivity.collectAsState
                          │
              remember(language){ UiText.resolve(language) }   language=="ur"?
                          │                                         │
                 CompositionLocalProvider(LocalStrings, LocalLayoutDirection)
                          │
        ┌─────────────────┼───────────────────────────┐
        ▼                 ▼                             ▼
  chrome strings     numerals (existing            RTL mirroring
  LocalStrings.current   toLocalizedNumerals(lang))   (automatic +
                                                       two manual fixes)
```

Numerals continue to flow through the existing `language` param that screens/
components already pass to `toLocalizedNumerals` / `localizeDigits`. Screens that
render raw numbers today (Home) will thread `language` in the same way the counting
screen already does.

---

## 5. String Inventory (chrome to localize)

Grouped by surface. ~70 strings total. (Verified by grep sweep 2026-07-02.)

**Bottom nav (`SabeelTab.kt`)** — Home, Count, Dhikr, Settings.

**Home (`HomeScreen.kt` / `HomeViewModel.kt`)**
- 8 greeting variants → resolved via `GreetingType` (see §6.1): Fajr, Good morning,
  Dhuhr, Good afternoon, Asr, Maghrib, Isha, Assalamu alaikum.
- Section headers: "Today's Sessions", "All Time".
- "Resume", "Consistency", "Daily Goal", "Total Counted", "Sessions".
- "Completed ✓" / "Partial".
- "Begin today's dhikr", "Start counting".
- Numerals: `lastCount / target`, `totalToday / dailyGoal`, `count`,
  `totalAllTime` (grouped), `totalSessionCount`.

**Counting (`TasbihScreen.kt` + components)**
- "Tasbīḥ after Salah" indicator.
- Streak chip "%1$sd" (template — digit + localized day-abbrev).
- "−1" (symbol kept; localized only if a language needs it — kept as-is).
- "Tap the circle to count".
- Circle "of %1$s" sub-label (`TasbihCircle`).
- Odometer numerals (`OdometerCounter`, already language-aware).
- "SPIRITUAL REWARD" header (`SpiritualRewardCard`).
- "Step %1$s of %2$s" (`SequenceTracker`, already language-aware).
- CompletionRest: "%1$s complete", "Finish", "Continue". ("تَمَّ" kept.)

**Settings (`SettingsScreen.kt`)**
- Section headers: Appearance, Language & Text, Counting Behaviour, Daily Goal,
  About.
- "Theme" + options Dark / Light.
- **Rename "Translation" → "Language"** + options English / اردو / বাংলা (native
  names kept, not translated).
- "Show Transliteration" + desc "Romanized pronunciation under Arabic".
- "Haptic Feedback" + options Off / Light / Medium / Strong.
- "Sound on Milestone" + desc "Subtle chime at 33, 100 etc." (numerals localized).
- "Auto-reset on Completion" + desc "Counter resets when target is hit".
- "Show Streaks" + desc "Hide consistency counts for pure ibadah".
- "Daily target" + goal numeral. ("−"/"+" symbols kept.)
- "Font" / value kept; "Version" / value kept.

**Dhikr Library (`DhikrLibraryScreen.kt`)**
- Search placeholder "Search dhikr…".
- "Ref: %1$s" prefix.
- "Count Now  →".
- Empty state: "No dhikr found for \"%1$s\"", "Try searching in Arabic or English".
- 8 category headers (`DhikrCategory`): After Prayer, Daily Remembrance, Morning
  Adhkar, Evening Adhkar, Salawat, Istighfar, Tahlil, My Dhikr — via
  `UiStrings.categoryLabel(cat)`.

---

## 6. Decoupling Refactors (SOLID)

These are targeted improvements to code we're touching — not unrelated refactoring.

### 6.1 `HomeViewModel` greeting → `GreetingType`

Today `resolveGreeting()` returns an English `String` stored in
`HomeState.greeting`, baking presentation language into the ViewModel (SRP/DIP
smell — the VM shouldn't know display language).

Change: introduce `enum class GreetingType { FAJR, MORNING, DHUHR, AFTERNOON, ASR,
MAGHRIB, ISHA, DEFAULT }`. `resolveGreeting()` returns a `GreetingType` (pure
time→type logic, trivially unit-testable). `HomeState.greeting: GreetingType`.
`HomeScreen` maps type → localized string via `LocalStrings.current`.

### 6.2 Category labels in the UI layer

`DhikrCategory.displayName: String` currently hardcodes English in the domain enum.
Rather than inject a language into the domain (a presentation concern leaking down),
add the 8 category labels to `UiText`/`UiStrings` and resolve them via
`UiStrings.categoryLabel(cat)` at the library call site. The domain enum's
`displayName` may remain for its existing non-UI uses (e.g. `DhikrItem.matches()`
search), which are English-keyed and out of scope here.

---

## 7. RTL (Urdu)

The codebase is already RTL-clean: layout uses `start`/`end` padding and
`Arrangement`, so providing `LayoutDirection.Rtl` mirrors the tree automatically.
Two components need a manual fix because they bypass the layout system:

### 7.1 `OdometerCounter` — force LTR locally

The digit `Row` would lay children right-to-left under RTL, reversing digit order
(e.g. "12" → "21"). Numbers are always LTR regardless of script. Wrap the `Row` in
`CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr)`.

### 7.2 `SpiritualRewardCard` — direction-aware accent

The 3dp accent is hand-drawn via `drawBehind` at `x = strokeWidth/2` (physical
left). Under RTL it should sit on the **leading (start)** edge. Fix: read
`layoutDirection` inside the `DrawScope` and draw at `x = size.width - strokeWidth/2`
when `Rtl`. (The card's `padding(start=…, end=…)` already flips correctly.)

No other component draws or positions by physical side.

---

## 8. Numerals & Formatting

- All chrome numbers route through the existing `Int.toLocalizedNumerals(lang)` /
  `String.localizeDigits(lang)` in `Numerals.kt`.
- `HomeScreen` line 278 uses `NumberFormat.getInstance()` (system-locale grouping)
  for `totalAllTime`. Replace with a helper that applies grouping then
  `localizeDigits(lang)`, so grouping and digits both follow the app language, not
  the device locale. Add the helper to `Numerals.kt` (e.g.
  `Int.toGroupedLocalizedNumerals(lang)`).
- Dates: no user-facing calendar date is rendered in chrome today (the greeting is
  time-of-day, resolved via `GreetingType`, not a formatted date). No date
  formatting work is required. (If a date surface is added later, it routes through
  the same numeral helpers.)

---

## 9. Testing

Following TDD (red → green) per task.

1. **`UiTextResolutionTest`** — `UiText.resolve("ur").navHome == "ہوم"`;
   `resolve("bn")`, `resolve("en")` return the right language.
2. **`UiTextFallbackTest`** — a `LocalizedText` with a blank translation falls back
   to `en` via the existing `.get(lang)` contract.
3. **`UiTextCompletenessTest`** (the key guard) — reflect over `UiText`'s
   `LocalizedText` properties (`UiText::class.memberProperties` filtered to
   `LocalizedText`); assert every entry's `ur` and `bn` are non-blank. Fails the
   build if any chrome string ships untranslated.
4. **`GreetingTypeTest`** — hour → `GreetingType` boundaries (4, 7, 12, 14, 16, 18,
   20, else).
5. **RTL** — a Compose/semantics test asserting the provider yields
   `LayoutDirection.Rtl` when `language == "ur"` and `Ltr` otherwise; an
   `OdometerCounter` test asserting digit order is preserved under an RTL parent.
6. **Numerals** — extend `Numerals` tests for the new grouped helper.
7. **Regression** — all existing screen/gesture/unit tests continue to pass on the
   default `en` bundle (they render through `LocalStrings`'s EN default).

---

## 10. Risks & Mitigations

| Risk | Mitigation |
|------|-----------|
| A hardcoded literal is missed during migration | The §5 grep inventory is the checklist; the completeness test guards only strings that reached `UiText`, so the migration sweep is the real safeguard — do it screen-by-screen and re-grep for `text = "`/label literals after each. |
| RTL flips numbers | Force-LTR on `OdometerCounter`; numeral helpers never reorder digits. |
| Word order wrong in interpolated strings | Positional `%1$s`/`%2$s` placeholders let each translation reorder; formatted at call sites via `String.format`. |
| Urdu renders in Naskh not Nastaliq | Accepted for v1 (non-goal); Android Noto is legible. |
| `UiStrings` and `UiText.resolve` drift | Compiler-enforced: `resolve()` must fill every `UiStrings` field or it won't compile. Blank translations caught by the completeness test. |
| Translation quality (ur/bn) | Drafted by Claude, reviewed by the user before merge (agreed workflow). |

---

## 11. Build Sequence (bisect-safe; each step compiles + passes tests)

1. **Infra.** Add `UiText.kt` + `UiStrings.kt` + `LocalStrings`; wire
   `CompositionLocalProvider(LocalStrings, LocalLayoutDirection)` in `MainActivity`;
   rename Settings "Translation" → "Language". Tests: resolution, fallback,
   completeness (seeded with the first strings). *App still renders English.*
2. **Migrate chrome, screen by screen** — Bottom nav → Home → Counting → Settings →
   Library. For each: replace literals with `LocalStrings.current.*`, localize
   numerals, add its strings to `UiText`. Existing screen tests stay green on EN.
3. **RTL pass.** Provide direction (already wired in step 1); fix
   `OdometerCounter` (force LTR) and `SpiritualRewardCard` (direction-aware accent).
   Add RTL tests.
4. **Decoupling refactors.** `GreetingType` enum + `categoryLabel`.
5. **Translations + completeness.** Fill all ur/bn entries; completeness test now
   covers the full catalog. User reviews translations before merge.

---

## 12. Open Questions

None blocking. Translation wording will be drafted in step 5 and reviewed by the
user before merge.
