# Customizable Daily Wird — Design Spec

**Date:** 2026-07-03
**Feature (Q3):** Let the user build their own daily dhikr routine — a personal, ordered
list of adhkar, each with its own daily target — that becomes *the* daily goal.
**Status:** Approved design, ready for implementation planning.

---

## 1. Summary

Today the "daily goal" is a single number (`dailyGoal`, default 200): count 200 *total*
across any dhikr. This replaces that coarse target with a **customizable daily wird** — a
checklist of specific adhkar (e.g. Astaghfirullah ×100, Salawat ×100, Tahlil ×100). The
user completes each item by counting it to its target; the wird is done when every item is
done.

### Decisions (locked during brainstorming)

1. **Interaction = checklist of targets.** Count each item independently, in any order,
   across the whole day. Not a forced sequence (guided sequences are already covered by
   the existing Smart Flow engine).
2. **Full replace.** The single-number `dailyGoal` is retired. A sensible default wird is
   seeded so no one starts empty. "Complete your wird" *is* the daily goal.
3. **One daily wird.** A single ordered list — no multiple named routines (YAGNI).
4. **Approach A — progress is derived, never stored.** The wird table stores only the
   *plan*. Actual counts stay in `dhikr_sessions` (the existing single source of truth).
   Today's progress is computed by aggregating today's sessions per dhikr.
5. **Forgiving semantic:** wird progress = *how many times you said this phrase today, from
   anywhere in the app* (library or wird). Counting a wird dhikr from the Dhikr Library
   still advances the wird.

### Out of scope (YAGNI)

- Multiple named wirds / routines, scheduling, per-time-of-day wirds.
- Smart-Flow (multi-step sequence) entries *inside* a wird — a wird item is one phrase.
- Reminders/notifications for the wird.

---

## 2. Architecture

Layered per the existing codebase: Room entity + DAO (data) → domain model + repository →
use case for the derived projection → ViewModels → Compose UI. The wird table owns the
**plan**; `dhikr_sessions` remains the truth for **actuals**; progress is a pure projection.

### 2.1 Persistence (Room v2 → v3)

New entity — the plan only:

```kotlin
@Entity(tableName = "wird_items")
data class WirdItemEntity(
    @PrimaryKey val dhikrKey: String,  // FK into the merged catalog (DhikrType.name / custom UUID)
    val target: Int,                   // per-day target for this dhikr in the wird
    val position: Int                  // checklist order
)
```

`@PrimaryKey dhikrKey` enforces one entry per dhikr at the DB level (no UI dedupe needed).

`WirdDao`:

```kotlin
@Dao
interface WirdDao {
    @Query("SELECT * FROM wird_items ORDER BY position ASC")
    fun observeWird(): Flow<List<WirdItemEntity>>
    @Query("SELECT COUNT(*) FROM wird_items") suspend fun count(): Int
    @Query("SELECT COALESCE(MAX(position), -1) FROM wird_items") suspend fun maxPosition(): Int
    @Upsert suspend fun upsert(item: WirdItemEntity)
    @Query("DELETE FROM wird_items WHERE dhikrKey = :key") suspend fun delete(key: String)
    @Transaction suspend fun reorder(items: List<WirdItemEntity>)  // rewrite positions
}
```

Migration `MIGRATION_2_3` is **purely structural** (mirrors the existing `MIGRATION_1_2`
style), creating `wird_items`. Register the entity + DAO on `SabeelDatabase`, bump `version`
to 3, and add `MIGRATION_2_3` wherever `MIGRATION_1_2` is wired into the builder.

**Default seed** — the canonical post-Salah tasbih, using keys already in `DhikrCatalog`:

| dhikrKey | target |
|---|---|
| `SUBHANALLAH` | 33 |
| `ALHAMDULILLAH` | 33 |
| `ALLAHU_AKBAR` | 34 |

Seeded **app-level when the table is empty** (`seedDefaultIfEmpty()`), covering both fresh
installs and migrated users. Seed content lives in Kotlin (referencing catalog constants),
so the migration never carries content and the default is a one-line change.

### 2.2 Domain

```kotlin
// Plan
data class WirdItem(val dhikrKey: String, val target: Int, val position: Int)

// Projection (resolved + today's progress)
data class WirdProgressItem(
    val dhikrKey: String,
    val displayName: String,
    val arabicText: String,
    val transliteration: String?,
    val target: Int,
    val countToday: Int,
    val position: Int,
) {
    val isComplete: Boolean get() = countToday >= target
}

data class WirdProgress(val items: List<WirdProgressItem>) {
    val completed: Int get() = items.count { it.isComplete }
    val total: Int get() = items.size
    val allComplete: Boolean get() = items.isNotEmpty() && items.all { it.isComplete }
    // Sum-based fill for the progress bar:
    val countedSum: Int get() = items.sumOf { minOf(it.countToday, it.target) }
    val targetSum: Int get() = items.sumOf { it.target }
}
```

`WirdRepository` (interface in `domain/repository`, impl in `data/repository`):

```kotlin
interface WirdRepository {
    fun observeWird(): Flow<List<WirdItem>>
    suspend fun addToWird(dhikrKey: String, target: Int)
    suspend fun updateTarget(dhikrKey: String, target: Int)
    suspend fun removeFromWird(dhikrKey: String)
    suspend fun reorder(orderedKeys: List<String>)
    suspend fun seedDefaultIfEmpty()
}
```

`removeFromWird` is also invoked when a custom dhikr is deleted, so the wird never keeps a
dangling custom key.

### 2.3 Progress projection — `ObserveWirdProgress` use case

A small, focused use case (domain) combining four flows → `Flow<WirdProgress>`:

- `wirdDao.observeWird()` — the plan
- `dhikrSessionDao.getCountsByKeyForDate(today)` — saved totals per key (new query below)
- `DhikrRepository` merged catalog — resolves each key to display name / Arabic / translit
- `tasbihRepository.activeDhikr` + `activeCount` — folds the **live, unsaved** round in

New aggregation on the existing `DhikrSessionDao`:

```kotlin
@Query("SELECT dhikrKey, COALESCE(SUM(count),0) AS total FROM dhikr_sessions WHERE dateKey = :dateKey GROUP BY dhikrKey")
fun getCountsByKeyForDate(dateKey: String): Flow<List<KeyCountRow>>   // KeyCountRow(dhikrKey, total)
```

Per item: `countToday = savedForKey + liveContribution`, where `liveContribution` reuses the
guard already proven in `HomeViewModel.displayedToday`:

```
liveContribution = if (activeDhikr.key == item.dhikrKey && activeCount in 1 until activeDhikr.target) activeCount else 0
```

This guard is extracted into a **pure, unit-tested helper** shared by Home and the wird
projection (one definition of "fold the in-progress round in without double-counting a
just-completed one"). Items whose key does not resolve against the merged catalog are
**filtered out** (defensive against deleted custom dhikr) — never silently swapped.

### 2.4 Counting integration — tap-to-count with target override

A wird item's target may differ from the dhikr's `defaultTarget`, so counting a wird item
must count to *its* target. Today target flows from a single point:
`activeDhikr = activeDhikrKeyFlow.map { DhikrCatalog.resolve(it) }` (target = `defaultTarget`),
and `incrementCount` re-resolves the same way for completion detection.

Introduce an optional per-active override:

- **Intent:** `SetDhikr(val key: String)` → `SetDhikr(val key: String, val target: Int? = null)`.
  The Dhikr Library "Count Now" passes no target (unchanged behaviour). A wird item passes
  its target.
- **Persistence:** `CounterDataStore` gains `active_target_override` (`intPreferencesKey`),
  `0`/absent meaning "no override". `setDhikrWithTarget(key, target)` sets both key and
  override; the existing `setDhikrKey(key)` clears the override (library launches use the
  default target).
- **Resolution:** `activeDhikr` and `incrementCount` apply `override ?: defaultTarget`.
  Because `completeDhikrTarget` is handed the target by the ViewModel (read from
  `activeDhikr.target`), one override point cascades to display, completion, and the saved
  session's `target`.

Tapping a wird item: `SetDhikr(key, item.target)` → navigate to the Count screen. On
completion a `DhikrSessionEntity` is saved (as today), and the wird projection reflects it.

---

## 3. UI

Four tabs stay (Home / Count / Dhikr / Settings). The wird is surfaced through Home as the
daily goal, with two pushed screens.

### 3.1 Home — wird summary (replaces the goal row in `StreakGoalCard`)

The consistency (streak) row is untouched. The "Daily Goal N / 200" row + bar becomes a
**tappable "Today's Wird" summary**: label, `completed / total done`, and a progress bar.
Bar fill is **sum-based** (`countedSum / targetSum`) for a smooth fill; the text is
**discrete** (`completed / total` items). Tapping navigates to the Wird screen. If the wird
is empty, the summary reads "Set up your wird".

### 3.2 Wird screen (view + count)

```
Today's Wird              2 / 3 done
▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░  (sum-based)
─────────────────────────────────
 ✓  Astaghfirullah        100 / 100
    أَسْتَغْفِرُ ٱللَّه
 ◯  Salawat                40 / 100    ← tap → Count (target 100)
    ٱللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّد
 ◯  Tahlil                  0 / 100
─────────────────────────────────
                        ✎ Edit wird
```

Tapping an incomplete item → `SetDhikr(key, target)` + navigate to Count. Completed items
show ✓ + sage-green treatment (existing `SabeelColors`). All numerals localized; RTL honored
(consistent with the just-completed i18n work).

### 3.3 Edit Wird screen

```
← Edit Wird
─────────────────────────────────
 ⠿  Astaghfirullah   [− 100 +]   🗑
 ⠿  Salawat          [− 100 +]   🗑
 ⠿  Tahlil           [− 100 +]   🗑
─────────────────────────────────
           +  Add dhikr
```

- **Add dhikr** opens a picker that **reuses the Dhikr Library** catalog + search (via
  `DhikrViewModel`), filtering out Smart-Flow entries and already-added keys. Selecting adds
  at the end (`position = maxPosition + 1`) with the dhikr's `defaultTarget` pre-filled.
- **Target stepper** `[− n +]` → `updateTarget`; **🗑** → `removeFromWird`; **drag** ⠿ →
  `reorder`. Target has a sane min (1) and step (e.g. +/−1, long-press for larger).

### 3.4 Empty state

The wird is empty only if the user removes every item. Then: Home card reads "Set up your
wird"; the Wird screen shows an empty message + hint and a prominent **Add dhikr**.

### 3.5 Navigation

Two new routes (`wird`, `wird/edit`) added to the existing NavHost; Home gains an
`onOpenWird` callback. The "Add dhikr" picker is a mode of the library UI (browse vs. pick),
reached from the Edit screen.

---

## 4. Settings migration (retire the single-number goal)

- Remove `dailyGoal` / `setDailyGoal` from `SettingsRepository` and its DataStore-backed
  impl; remove the **Daily Goal** section + `DailyGoalRow` from `SettingsScreen` /
  `SettingsState` / `SettingsViewModel`.
- `HomeViewModel` stops combining `settingsRepository.dailyGoal`; it consumes
  `ObserveWirdProgress` instead and maps the result to a lean summary. `HomeState` drops
  `dailyGoal` and carries a small **`WirdSummary(completed, total, countedSum, targetSum,
  isEmpty)`** — Home needs only the numbers, not the full resolved item list (that lives on
  the Wird screen's own ViewModel). The Wird / Edit screens consume `WirdProgress` /
  `WirdItem` directly.
- The orphaned DataStore `counter`/goal key is harmless and left in place (DataStore needs
  no migration); we simply stop reading it.
- `DailyTargetEntity` (used by legacy streak logic in `TasbihRepositoryImpl.incrementCount`)
  is a *different* concept (per-dhikr daily target for streaks) and is **left untouched**.

---

## 5. Localization

Follow the established i18n pattern (`UiStrings` field + `UiText` `LocalizedText` with
en/ur/bn + `resolve()` wiring; the reflection completeness test guards non-blank ur/bn).

**Add** (chrome): `wirdTitle` ("Today's Wird"), `wirdDoneOf` (template `"%1$s / %2$s done"`),
`wirdEditTitle` ("Edit Wird"), `wirdAddDhikr` ("Add dhikr"), `wirdEmpty`
("Your wird is empty"), `wirdEmptyHint` ("Add adhkar to build your daily routine"),
`wirdSetup` ("Set up your wird"), plus a11y labels `wirdRemove`, `wirdReorder`,
`wirdTargetA11y`. Urdu/Bengali drafted during implementation and reviewed as a final pass
(as with the Q1 Task 9 review).

**Remove** (now dead): `homeDailyGoal`, `settingsDailyGoalHeader`, `settingsDailyTarget`
(compiler-enforced via `resolve()` — removing the `UiStrings` field forces removing the
dictionary entry and vice-versa).

---

## 6. Error handling & edge cases

- **Dangling custom key** — projection filters items whose key is not in the merged catalog;
  deleting a custom dhikr also calls `removeFromWird`. No silent fallback.
- **Empty wird** — explicit empty states on Home and the Wird screen; `allComplete` is
  `false` for an empty list (never "done" with nothing to do).
- **Target ≥ 1** — stepper clamps at 1; `updateTarget` coerces.
- **Duplicate add** — impossible: `@PrimaryKey dhikrKey` + picker filters already-added keys.
- **Smart-Flow exclusion** — picker filters `isSmartFlow`; a wird item is always a single
  phrase, keeping the checklist model unambiguous.
- **Live round double-count** — the shared `activeCount in 1 until target` guard prevents
  double-counting a just-completed round (same guard Home already relies on).

---

## 7. Testing plan

Unit tests (existing baseline: 1 pre-existing unrelated `PocketModeServiceTest` failure):

- **Projection helper** (pure): fold-in of live round (in-progress adds, just-completed does
  not), `isComplete` boundary, sum-based fill, empty list, dangling-key filtered out.
- **`WirdDao`** (in-memory Room): ordered observe, upsert dedupe by key, delete, `reorder`
  rewrites positions, `maxPosition`, `seedDefaultIfEmpty` seeds 3 and is idempotent.
- **Migration 2→3** (`MigrationTestHelper`): `wird_items` created; existing data preserved.
- **`getCountsByKeyForDate`**: groups/sums per key for the date only.
- **Target override**: `activeDhikr` applies override; `setDhikr` from library clears it;
  saved session carries the wird target.
- **i18n completeness test** continues to guard the new wird strings.

---

## 8. Build sequence (bisect-safe; each phase compiles + tests green)

1. **Persistence** — `WirdItemEntity` + `WirdDao` + `MIGRATION_2_3` + DB registration + seed
   (+ DAO/migration tests).
2. **Domain** — `WirdItem`, `WirdProgress*` models, `WirdRepository` + impl + DI (+ tests).
3. **Projection** — `getCountsByKeyForDate` + shared live-count helper + `ObserveWirdProgress`
   (+ tests).
4. **Counting override** — `SetDhikr(key, target?)`, `CounterDataStore` override, resolution
   (+ tests).
5. **Goal migration** — retire `dailyGoal`; `HomeViewModel`/`HomeState` consume `WirdProgress`;
   remove Settings goal row (+ tests).
6. **UI** — Wird screen + Edit screen + library picker mode + nav routes + Home summary card.
7. **Localization** — add wird strings, remove dead goal strings; completeness test green;
   final ur/bn review.

Each phase is one (or a few) buildable commits gated by
`./gradlew :app:compileDebugKotlin` + `:app:testDebugUnitTest`, with the
`Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>` trailer.
