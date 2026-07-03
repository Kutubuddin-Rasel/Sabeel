# Customizable Daily Wird — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the single-number daily goal with a customizable daily wird — one ordered checklist of adhkar, each with its own target — whose progress is derived from existing session records.

**Architecture:** Approach A. A new `wird_items` Room table stores only the *plan*; actual counts stay in `dhikr_sessions` (single source of truth). Today's progress is a pure projection (`ObserveWirdProgress`) combining the plan, per-key session sums, the merged catalog, and the live in-progress round. Tapping a wird item counts it to its own target via a target override threaded through the existing counting engine.

**Tech Stack:** Kotlin, Jetpack Compose + Material 3, Hilt, Room (v2→v3), DataStore, StateFlow/`combine`, MVI. Tests: JUnit + Robolectric + MockK + `kotlinx-coroutines-test`.

**Spec:** `docs/superpowers/specs/2026-07-03-customizable-daily-wird-design.md`

## Global Constraints

- **Bisect-safe commits:** every task ends GREEN under `./gradlew :app:compileDebugKotlin` **and** `./gradlew :app:testDebugUnitTest`. Test baseline has exactly **1 pre-existing unrelated failure** (`PocketModeServiceTest > testVolumeKeysInterception_triggersHaptics`) + **1 skipped**; a task is GREEN if it adds no *new* failures.
- **Commit trailer (every commit):** `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`
- **Stage only the task's explicit files by path.** NEVER `git add -A`/`.`. NEVER stage `Screenshots/` (untracked) or any unrelated modification. `docs/` is gitignored — plan/spec are already committed; do not re-add.
- **Branch:** `sabeel-daily-wird` (already checked out).
- **i18n:** every new user-facing chrome string goes through `UiStrings` + `UiText` (en/ur/bn) + `resolve()`; the reflection completeness test must stay green. Numerals via `Int.toLocalizedNumerals(lang)`; RTL respected.
- **Threading:** repository suspend fns wrap Room/DataStore in `withContext(ioDispatcher)` using the `@IoDispatcher` qualifier (`com.kutubuddin.sabeel.di.IoDispatcher`).
- **SOLID/DIP:** domain models carry no presentation/language; use cases depend on repositories, not DAOs.

---

## File Structure

**New files:**
- `data/local/db/entity/WirdItemEntity.kt` — Room row (the plan).
- `data/local/db/dao/WirdDao.kt` — wird CRUD + reorder.
- `data/local/db/dao/KeyCountRow.kt` — projection POJO for per-key session sums.
- `domain/model/WirdItem.kt` — domain plan item.
- `domain/model/WirdProgress.kt` — `WirdProgressItem`, `WirdProgress`, `liveContribution` helper.
- `domain/repository/WirdRepository.kt` — plan CRUD interface.
- `data/repository/WirdRepositoryImpl.kt` — impl + default seed.
- `domain/usecase/ObserveWirdProgress.kt` — derived-progress projection.
- `ui/wird/WirdViewModel.kt`, `ui/wird/WirdScreen.kt` — checklist view + count.
- `ui/wird/WirdEditViewModel.kt`, `ui/wird/WirdEditScreen.kt`, `ui/wird/WirdEditState.kt` — build/edit.

**Modified files:**
- `data/local/db/SabeelDatabase.kt` — entity list, `version = 3`, `wirdDao()`, `MIGRATION_2_3`.
- `di/DataModule.kt` — `provideWirdDao`, `.addMigrations(..., MIGRATION_2_3)`.
- `di/RepositoryModule.kt` — bind `WirdRepository`.
- `data/local/db/dao/DhikrSessionDao.kt` — `getCountsByKeyForDate`.
- `domain/repository/SessionRepository.kt` + `data/repository/SessionRepositoryImpl.kt` — `getCountsByKeyForDate`.
- `data/local/datastore/CounterDataStore.kt` — active-target override.
- `domain/repository/TasbihRepository.kt` + `data/repository/TasbihRepositoryImpl.kt` — `setDhikr(key, targetOverride)`, override in `activeDhikr`/`incrementCount`.
- `ui/tasbih/TasbihContract.kt` + `ui/tasbih/TasbihViewModel.kt` — `SetDhikr(key, target?)`.
- `domain/repository/SettingsRepository.kt` + impl, `ui/settings/*` — retire `dailyGoal`.
- `ui/home/HomeState.kt` + `HomeViewModel.kt` + `HomeScreen.kt` — wird summary card.
- `ui/navigation/SabeelNavHost.kt` — `wird` + `wird/edit` routes.
- `MainActivity.kt` — seed default wird on launch.
- `ui/i18n/UiStrings.kt` + `UiText.kt` — add wird strings, remove dead goal strings.

---

## Task 1: Wird persistence — entity, DAO, migration

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/data/local/db/entity/WirdItemEntity.kt`
- Create: `app/src/main/java/com/kutubuddin/sabeel/data/local/db/dao/WirdDao.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/data/local/db/SabeelDatabase.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/di/DataModule.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/data/local/db/WirdDaoTest.kt`

**Interfaces — Produces:**
- `WirdItemEntity(dhikrKey: String, target: Int, position: Int)` (PK = `dhikrKey`)
- `WirdDao`: `observeWird(): Flow<List<WirdItemEntity>>`, `getAllOnce(): List<WirdItemEntity>`, `getByKey(key: String): WirdItemEntity?`, `count(): Int`, `maxPosition(): Int`, `upsert(item: WirdItemEntity)`, `delete(key: String)`, `reorder(items: List<WirdItemEntity>)`
- `SabeelDatabase.wirdDao(): WirdDao`

- [ ] **Step 1: Write the failing DAO test**

Create `app/src/test/java/com/kutubuddin/sabeel/data/local/db/WirdDaoTest.kt`:

```kotlin
package com.kutubuddin.sabeel.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kutubuddin.sabeel.data.local.db.dao.WirdDao
import com.kutubuddin.sabeel.data.local.db.entity.WirdItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WirdDaoTest {

    private lateinit var db: SabeelDatabase
    private lateinit var dao: WirdDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SabeelDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.wirdDao()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun observeWird_returnsItemsOrderedByPosition() = runTest {
        dao.upsert(WirdItemEntity("B", 33, 1))
        dao.upsert(WirdItemEntity("A", 33, 0))
        val items = dao.observeWird().first()
        assertEquals(listOf("A", "B"), items.map { it.dhikrKey })
    }

    @Test
    fun upsert_isUniqueByKey() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.upsert(WirdItemEntity("A", 100, 0)) // same key overwrites, not duplicates
        val items = dao.observeWird().first()
        assertEquals(1, items.size)
        assertEquals(100, items.first().target)
    }

    @Test
    fun delete_removesByKey() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.delete("A")
        assertEquals(0, dao.count())
    }

    @Test
    fun maxPosition_isMinusOneWhenEmpty_thenHighest() = runTest {
        assertEquals(-1, dao.maxPosition())
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.upsert(WirdItemEntity("B", 33, 5))
        assertEquals(5, dao.maxPosition())
    }

    @Test
    fun getByKey_returnsRowOrNull() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        assertEquals(33, dao.getByKey("A")?.target)
        assertNull(dao.getByKey("Z"))
    }

    @Test
    fun reorder_rewritesPositions() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.upsert(WirdItemEntity("B", 33, 1))
        dao.reorder(listOf(WirdItemEntity("B", 33, 0), WirdItemEntity("A", 33, 1)))
        assertEquals(listOf("B", "A"), dao.observeWird().first().map { it.dhikrKey })
    }
}
```

- [ ] **Step 2: Run test — verify it fails to compile**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdDaoTest*"`
Expected: FAIL — `WirdItemEntity`, `WirdDao`, `db.wirdDao()` unresolved.

- [ ] **Step 3: Create the entity**

`WirdItemEntity.kt`:

```kotlin
package com.kutubuddin.sabeel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One entry in the user's daily wird — the PLAN only (which dhikr, what target,
 * what order). Actual counts live in dhikr_sessions; progress is derived.
 * @PrimaryKey dhikrKey enforces one entry per dhikr at the DB level.
 */
@Entity(tableName = "wird_items")
data class WirdItemEntity(
    @PrimaryKey val dhikrKey: String,
    val target: Int,
    val position: Int
)
```

- [ ] **Step 4: Create the DAO**

`WirdDao.kt`:

```kotlin
package com.kutubuddin.sabeel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kutubuddin.sabeel.data.local.db.entity.WirdItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WirdDao {

    @Query("SELECT * FROM wird_items ORDER BY position ASC")
    fun observeWird(): Flow<List<WirdItemEntity>>

    @Query("SELECT * FROM wird_items ORDER BY position ASC")
    suspend fun getAllOnce(): List<WirdItemEntity>

    @Query("SELECT * FROM wird_items WHERE dhikrKey = :key")
    suspend fun getByKey(key: String): WirdItemEntity?

    @Query("SELECT COUNT(*) FROM wird_items")
    suspend fun count(): Int

    @Query("SELECT COALESCE(MAX(position), -1) FROM wird_items")
    suspend fun maxPosition(): Int

    @Upsert
    suspend fun upsert(item: WirdItemEntity)

    @Query("DELETE FROM wird_items WHERE dhikrKey = :key")
    suspend fun delete(key: String)

    /** Rewrite positions in one transaction (drag-to-reorder). */
    @Transaction
    suspend fun reorder(items: List<WirdItemEntity>) {
        items.forEach { upsert(it) }
    }
}
```

- [ ] **Step 5: Register on the database + add migration**

In `SabeelDatabase.kt`: add the import `import com.kutubuddin.sabeel.data.local.db.dao.WirdDao` and `import com.kutubuddin.sabeel.data.local.db.entity.WirdItemEntity`; add `WirdItemEntity::class` to the `entities` array; change `version = 2` to `version = 3`; add `abstract fun wirdDao(): WirdDao`; and add this migration inside the `companion object` (after `MIGRATION_1_2`):

```kotlin
        /** v2 → v3: adds the wird_items table (the daily-wird plan). Purely structural. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS wird_items (
                        dhikrKey TEXT PRIMARY KEY NOT NULL,
                        target INTEGER NOT NULL,
                        position INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
```

- [ ] **Step 6: Provide the DAO + wire the migration in DI**

In `DataModule.kt`: add `import com.kutubuddin.sabeel.data.local.db.dao.WirdDao`; change the builder line to `.addMigrations(SabeelDatabase.MIGRATION_1_2, SabeelDatabase.MIGRATION_2_3)`; and add:

```kotlin
    @Provides
    @Singleton
    fun provideWirdDao(database: SabeelDatabase): WirdDao = database.wirdDao()
```

- [ ] **Step 7: Run test — verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdDaoTest*"`
Expected: PASS (6 tests).

- [ ] **Step 8: Full gate + commit**

Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest`
Expected: only the 1 pre-existing `PocketModeServiceTest` failure + 1 skipped.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/data/local/db/entity/WirdItemEntity.kt \
        app/src/main/java/com/kutubuddin/sabeel/data/local/db/dao/WirdDao.kt \
        app/src/main/java/com/kutubuddin/sabeel/data/local/db/SabeelDatabase.kt \
        app/src/main/java/com/kutubuddin/sabeel/di/DataModule.kt \
        app/src/test/java/com/kutubuddin/sabeel/data/local/db/WirdDaoTest.kt
git commit -m "$(cat <<'EOF'
feat(wird): add wird_items table, DAO, and v2→v3 migration

The daily-wird plan (dhikrKey, target, position) as a Room table; progress
is derived elsewhere. PK on dhikrKey enforces one entry per dhikr.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 2: Wird domain model + repository (+ default seed)

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/domain/model/WirdItem.kt`
- Create: `app/src/main/java/com/kutubuddin/sabeel/domain/repository/WirdRepository.kt`
- Create: `app/src/main/java/com/kutubuddin/sabeel/data/repository/WirdRepositoryImpl.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/di/RepositoryModule.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/data/repository/WirdRepositoryImplTest.kt`

**Interfaces:**
- Consumes (Task 1): `WirdDao`, `WirdItemEntity`.
- Produces:
  - `WirdItem(dhikrKey: String, target: Int, position: Int)`
  - `WirdRepository`: `observeWird(): Flow<List<WirdItem>>`, `addToWird(dhikrKey: String, target: Int)`, `updateTarget(dhikrKey: String, target: Int)`, `removeFromWird(dhikrKey: String)`, `reorder(orderedKeys: List<String>)`, `seedDefaultIfEmpty()`

- [ ] **Step 1: Write the failing repository test**

`WirdRepositoryImplTest.kt`:

```kotlin
package com.kutubuddin.sabeel.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kutubuddin.sabeel.data.local.db.SabeelDatabase
import com.kutubuddin.sabeel.domain.model.DhikrType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WirdRepositoryImplTest {

    private lateinit var db: SabeelDatabase
    private lateinit var repo: WirdRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SabeelDatabase::class.java)
            .allowMainThreadQueries().build()
        repo = WirdRepositoryImpl(db.wirdDao(), Dispatchers.Unconfined)
    }

    @After
    fun teardown() = db.close()

    @Test
    fun addToWird_appendsAtEnd_andClampsTargetToOne() = runTest {
        repo.addToWird("A", 33)
        repo.addToWird("B", 0) // clamps to 1
        val items = repo.observeWird().first()
        assertEquals(listOf("A", "B"), items.map { it.dhikrKey })
        assertEquals(listOf(0, 1), items.map { it.position })
        assertEquals(1, items.first { it.dhikrKey == "B" }.target)
    }

    @Test
    fun updateTarget_changesTarget_keepsPosition() = runTest {
        repo.addToWird("A", 33)
        repo.updateTarget("A", 100)
        val item = repo.observeWird().first().first()
        assertEquals(100, item.target)
        assertEquals(0, item.position)
    }

    @Test
    fun removeFromWird_deletes() = runTest {
        repo.addToWird("A", 33)
        repo.removeFromWird("A")
        assertEquals(emptyList<String>(), repo.observeWird().first().map { it.dhikrKey })
    }

    @Test
    fun reorder_appliesNewOrder() = runTest {
        repo.addToWird("A", 33)
        repo.addToWird("B", 33)
        repo.reorder(listOf("B", "A"))
        assertEquals(listOf("B", "A"), repo.observeWird().first().map { it.dhikrKey })
    }

    @Test
    fun seedDefaultIfEmpty_seedsPostSalahTasbih_andIsIdempotent() = runTest {
        repo.seedDefaultIfEmpty()
        repo.seedDefaultIfEmpty() // second call must not duplicate
        val items = repo.observeWird().first()
        assertEquals(
            listOf(DhikrType.SUBHANALLAH.name, DhikrType.ALHAMDULILLAH.name, DhikrType.ALLAHU_AKBAR.name),
            items.map { it.dhikrKey }
        )
        assertEquals(listOf(33, 33, 34), items.map { it.target })
    }
}
```

- [ ] **Step 2: Run — verify fail**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdRepositoryImplTest*"`
Expected: FAIL — `WirdItem`, `WirdRepository`, `WirdRepositoryImpl` unresolved.

- [ ] **Step 3: Create the domain model**

`WirdItem.kt`:

```kotlin
package com.kutubuddin.sabeel.domain.model

/** One item in the daily wird plan: which dhikr, its per-day target, its order. */
data class WirdItem(
    val dhikrKey: String,
    val target: Int,
    val position: Int
)
```

- [ ] **Step 4: Create the repository interface**

`WirdRepository.kt`:

```kotlin
package com.kutubuddin.sabeel.domain.repository

import com.kutubuddin.sabeel.domain.model.WirdItem
import kotlinx.coroutines.flow.Flow

/** CRUD over the daily-wird plan. Progress is derived elsewhere (ObserveWirdProgress). */
interface WirdRepository {
    fun observeWird(): Flow<List<WirdItem>>
    suspend fun addToWird(dhikrKey: String, target: Int)
    suspend fun updateTarget(dhikrKey: String, target: Int)
    suspend fun removeFromWird(dhikrKey: String)
    suspend fun reorder(orderedKeys: List<String>)
    suspend fun seedDefaultIfEmpty()
}
```

- [ ] **Step 5: Create the impl**

`WirdRepositoryImpl.kt`:

```kotlin
package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.db.dao.WirdDao
import com.kutubuddin.sabeel.data.local.db.entity.WirdItemEntity
import com.kutubuddin.sabeel.di.IoDispatcher
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.WirdItem
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WirdRepositoryImpl @Inject constructor(
    private val wirdDao: WirdDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : WirdRepository {

    override fun observeWird(): Flow<List<WirdItem>> =
        wirdDao.observeWird().map { rows ->
            rows.map { WirdItem(it.dhikrKey, it.target, it.position) }
        }

    override suspend fun addToWird(dhikrKey: String, target: Int) = withContext(io) {
        val position = wirdDao.maxPosition() + 1
        wirdDao.upsert(WirdItemEntity(dhikrKey, target.coerceAtLeast(1), position))
    }

    override suspend fun updateTarget(dhikrKey: String, target: Int) = withContext(io) {
        val existing = wirdDao.getByKey(dhikrKey) ?: return@withContext
        wirdDao.upsert(existing.copy(target = target.coerceAtLeast(1)))
    }

    override suspend fun removeFromWird(dhikrKey: String) = withContext(io) {
        wirdDao.delete(dhikrKey)
    }

    override suspend fun reorder(orderedKeys: List<String>) = withContext(io) {
        val byKey = wirdDao.getAllOnce().associateBy { it.dhikrKey }
        val reordered = orderedKeys.mapIndexedNotNull { index, key ->
            byKey[key]?.copy(position = index)
        }
        wirdDao.reorder(reordered)
    }

    override suspend fun seedDefaultIfEmpty() = withContext(io) {
        if (wirdDao.count() == 0) {
            DEFAULT_WIRD.forEachIndexed { index, (key, target) ->
                wirdDao.upsert(WirdItemEntity(key, target, index))
            }
        }
    }

    companion object {
        /** Canonical post-Salah tasbih — 33 / 33 / 34. Keys exist in DhikrCatalog. */
        private val DEFAULT_WIRD = listOf(
            DhikrType.SUBHANALLAH.name to 33,
            DhikrType.ALHAMDULILLAH.name to 33,
            DhikrType.ALLAHU_AKBAR.name to 34
        )
    }
}
```

- [ ] **Step 6: Bind in Hilt**

In `RepositoryModule.kt`: add imports `import com.kutubuddin.sabeel.data.repository.WirdRepositoryImpl` and `import com.kutubuddin.sabeel.domain.repository.WirdRepository`; add:

```kotlin
    @Binds @Singleton
    abstract fun bindWirdRepository(impl: WirdRepositoryImpl): WirdRepository
```

- [ ] **Step 7: Run — verify pass, then gate + commit**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdRepositoryImplTest*"` → PASS (5 tests).
Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/domain/model/WirdItem.kt \
        app/src/main/java/com/kutubuddin/sabeel/domain/repository/WirdRepository.kt \
        app/src/main/java/com/kutubuddin/sabeel/data/repository/WirdRepositoryImpl.kt \
        app/src/main/java/com/kutubuddin/sabeel/di/RepositoryModule.kt \
        app/src/test/java/com/kutubuddin/sabeel/data/repository/WirdRepositoryImplTest.kt
git commit -m "$(cat <<'EOF'
feat(wird): WirdItem model + WirdRepository with default post-Salah seed

Plan CRUD (add appends at maxPosition+1, target clamped >=1, reorder rewrites
positions) and an idempotent seedDefaultIfEmpty seeding SubhanAllah/Alhamdulillah/
Allahu Akbar 33/33/34.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 3: Seed the default wird on app launch

**Files:**
- Modify: `app/src/main/java/com/kutubuddin/sabeel/MainActivity.kt`

**Interfaces — Consumes (Task 2):** `WirdRepository.seedDefaultIfEmpty()`.

This is Android glue (no unit test); the compile gate covers it and Task 10 verifies the seed appears in the UI.

- [ ] **Step 1: Inject + call seed in onCreate**

In `MainActivity.kt`: add `import com.kutubuddin.sabeel.domain.repository.WirdRepository`. Add the injected field next to the others:

```kotlin
    @Inject
    lateinit var wirdRepository: WirdRepository
```

Then, in `onCreate` immediately after `observeServiceSideEffects()`, add:

```kotlin
        // Seed the default daily wird on first launch (idempotent — no-op if non-empty).
        lifecycleScope.launch { wirdRepository.seedDefaultIfEmpty() }
```

(`lifecycleScope` and `launch` are already imported.)

- [ ] **Step 2: Gate + commit**

Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/MainActivity.kt
git commit -m "$(cat <<'EOF'
feat(wird): seed the default wird on app launch

Idempotent seedDefaultIfEmpty() call in MainActivity.onCreate so fresh installs
and migrated users always start with the post-Salah tasbih wird.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 4: Per-key session aggregation (DAO + repository)

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/data/local/db/dao/KeyCountRow.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/data/local/db/dao/DhikrSessionDao.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/domain/repository/SessionRepository.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/data/repository/SessionRepositoryImpl.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/data/local/db/DhikrSessionKeyCountTest.kt`

**Interfaces — Produces:**
- `KeyCountRow(dhikrKey: String, total: Int)`
- `DhikrSessionDao.getCountsByKeyForDate(dateKey: String): Flow<List<KeyCountRow>>`
- `SessionRepository.getCountsByKeyForDate(dateKey: String): Flow<Map<String, Int>>`

- [ ] **Step 1: Write the failing DAO test**

`DhikrSessionKeyCountTest.kt`:

```kotlin
package com.kutubuddin.sabeel.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kutubuddin.sabeel.data.local.db.dao.DhikrSessionDao
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DhikrSessionKeyCountTest {

    private lateinit var db: SabeelDatabase
    private lateinit var dao: DhikrSessionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SabeelDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = db.dhikrSessionDao()
    }

    @After
    fun teardown() = db.close()

    private fun session(key: String, count: Int, date: String) =
        DhikrSessionEntity(dhikrKey = key, count = count, target = 33,
            isComplete = true, dateKey = date, endedAt = 0L)

    @Test
    fun getCountsByKeyForDate_sumsPerKey_forThatDateOnly() = runTest {
        dao.insertSession(session("A", 33, "2026-07-03"))
        dao.insertSession(session("A", 33, "2026-07-03"))
        dao.insertSession(session("B", 10, "2026-07-03"))
        dao.insertSession(session("A", 99, "2026-07-02")) // other day — excluded

        val map = dao.getCountsByKeyForDate("2026-07-03").first()
            .associate { it.dhikrKey to it.total }

        assertEquals(66, map["A"])
        assertEquals(10, map["B"])
        assertEquals(null, map["Z"])
    }
}
```

- [ ] **Step 2: Run — verify fail**

Run: `./gradlew :app:testDebugUnitTest --tests "*DhikrSessionKeyCountTest*"`
Expected: FAIL — `getCountsByKeyForDate` / `KeyCountRow` unresolved.

- [ ] **Step 3: Create `KeyCountRow`**

`KeyCountRow.kt`:

```kotlin
package com.kutubuddin.sabeel.data.local.db.dao

/** Projection row for "SUM(count) per dhikrKey" aggregation. */
data class KeyCountRow(
    val dhikrKey: String,
    val total: Int
)
```

- [ ] **Step 4: Add the aggregation query**

In `DhikrSessionDao.kt`: add `import com.kutubuddin.sabeel.data.local.db.dao.KeyCountRow` is unnecessary (same package). Add:

```kotlin
    /** Sum of counts per dhikr for a date — powers derived wird progress. */
    @Query("SELECT dhikrKey AS dhikrKey, COALESCE(SUM(count), 0) AS total FROM dhikr_sessions WHERE dateKey = :dateKey GROUP BY dhikrKey")
    fun getCountsByKeyForDate(dateKey: String): Flow<List<KeyCountRow>>
```

- [ ] **Step 5: Expose on the repository**

In `SessionRepository.kt`: add:

```kotlin
    /** Map of dhikrKey → total counted for the given date. */
    fun getCountsByKeyForDate(dateKey: String): Flow<Map<String, Int>>
```

In `SessionRepositoryImpl.kt`: add `import kotlinx.coroutines.flow.map` and:

```kotlin
    override fun getCountsByKeyForDate(dateKey: String): Flow<Map<String, Int>> =
        dao.getCountsByKeyForDate(dateKey).map { rows -> rows.associate { it.dhikrKey to it.total } }
```

- [ ] **Step 6: Run — verify pass, gate, commit**

Run: `./gradlew :app:testDebugUnitTest --tests "*DhikrSessionKeyCountTest*"` → PASS.
Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/data/local/db/dao/KeyCountRow.kt \
        app/src/main/java/com/kutubuddin/sabeel/data/local/db/dao/DhikrSessionDao.kt \
        app/src/main/java/com/kutubuddin/sabeel/domain/repository/SessionRepository.kt \
        app/src/main/java/com/kutubuddin/sabeel/data/repository/SessionRepositoryImpl.kt \
        app/src/test/java/com/kutubuddin/sabeel/data/local/db/DhikrSessionKeyCountTest.kt
git commit -m "$(cat <<'EOF'
feat(wird): per-key session-sum aggregation for a date

DhikrSessionDao.getCountsByKeyForDate + SessionRepository map projection —
the raw material for derived wird progress.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 5: Progress models + live-count helper

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/domain/model/WirdProgress.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/domain/model/WirdProgressTest.kt`

**Interfaces — Produces:**
- `WirdProgressItem(dhikrKey, displayName, arabicText, transliteration, target, countToday, position)` with `val isComplete: Boolean`
- `WirdProgress(items: List<WirdProgressItem>)` with `completed`, `total`, `allComplete`, `countedSum`, `targetSum`
- `fun liveContribution(itemKey: String, activeKey: String, activeCount: Int, activeTarget: Int): Int`

- [ ] **Step 1: Write the failing test**

`WirdProgressTest.kt`:

```kotlin
package com.kutubuddin.sabeel.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WirdProgressTest {

    private fun item(key: String, target: Int, count: Int) =
        WirdProgressItem(key, key, "ar", null, target, count, 0)

    @Test
    fun isComplete_atOrAboveTarget() {
        assertFalse(item("A", 33, 32).isComplete)
        assertTrue(item("A", 33, 33).isComplete)
        assertTrue(item("A", 33, 40).isComplete)
    }

    @Test
    fun aggregate_countsAndSums() {
        val p = WirdProgress(listOf(item("A", 33, 33), item("B", 100, 40)))
        assertEquals(1, p.completed)
        assertEquals(2, p.total)
        assertFalse(p.allComplete)
        assertEquals(73, p.countedSum)   // 33 + 40
        assertEquals(133, p.targetSum)   // 33 + 100
    }

    @Test
    fun countedSum_clampsPerItemAtTarget() {
        val p = WirdProgress(listOf(item("A", 33, 99))) // over-count doesn't inflate the bar
        assertEquals(33, p.countedSum)
    }

    @Test
    fun allComplete_isFalseForEmpty() {
        assertFalse(WirdProgress(emptyList()).allComplete)
    }

    @Test
    fun liveContribution_addsOnlyForMatchingInProgressRound() {
        // matches, in progress (1..target-1) -> contributes
        assertEquals(20, liveContribution("A", "A", 20, 33))
        // matches but round finished (>= target) -> excluded (session already saved)
        assertEquals(0, liveContribution("A", "A", 33, 33))
        // different active dhikr -> excluded
        assertEquals(0, liveContribution("A", "B", 20, 33))
        // zero count -> excluded
        assertEquals(0, liveContribution("A", "A", 0, 33))
    }
}
```

- [ ] **Step 2: Run — verify fail**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdProgressTest*"`
Expected: FAIL — unresolved references.

- [ ] **Step 3: Create the models + helper**

`WirdProgress.kt`:

```kotlin
package com.kutubuddin.sabeel.domain.model

/** A wird item resolved for display with today's derived progress folded in. */
data class WirdProgressItem(
    val dhikrKey: String,
    val displayName: String,
    val arabicText: String,
    val transliteration: String?,
    val target: Int,
    val countToday: Int,
    val position: Int
) {
    val isComplete: Boolean get() = countToday >= target
}

/** The whole wird's progress for today. */
data class WirdProgress(val items: List<WirdProgressItem>) {
    val completed: Int get() = items.count { it.isComplete }
    val total: Int get() = items.size
    val allComplete: Boolean get() = items.isNotEmpty() && items.all { it.isComplete }
    /** Sum-based bar fill: each item contributes at most its target. */
    val countedSum: Int get() = items.sumOf { minOf(it.countToday, it.target) }
    val targetSum: Int get() = items.sumOf { it.target }
}

/**
 * The live, not-yet-saved round's contribution to an item's today count.
 * Adds the active count only when it belongs to this item AND the round is still
 * in progress (1 until target) — a just-completed round is already a saved session,
 * so counting it here would double it. Mirrors HomeViewModel.displayedToday.
 */
fun liveContribution(itemKey: String, activeKey: String, activeCount: Int, activeTarget: Int): Int =
    if (itemKey == activeKey && activeCount in 1 until activeTarget) activeCount else 0
```

- [ ] **Step 4: Run — verify pass, gate, commit**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdProgressTest*"` → PASS.
Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/domain/model/WirdProgress.kt \
        app/src/test/java/com/kutubuddin/sabeel/domain/model/WirdProgressTest.kt
git commit -m "$(cat <<'EOF'
feat(wird): WirdProgress projection models + live-count helper

Pure progress types (isComplete, completed/total, sum-based fill clamped per
item) and the shared liveContribution guard that folds the in-progress round in
without double-counting a just-completed one.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 6: `ObserveWirdProgress` use case

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/domain/usecase/ObserveWirdProgress.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/domain/usecase/ObserveWirdProgressTest.kt`

**Interfaces:**
- Consumes: `WirdRepository.observeWird()`, `DhikrRepository.getAllDhikr()`, `SessionRepository.getCountsByKeyForDate()`, `TasbihRepository.activeDhikr`, `TasbihRepository.activeCount`.
- Produces: `ObserveWirdProgress.invoke(dateKey: String): Flow<WirdProgress>`

- [ ] **Step 1: Write the failing test (with fakes)**

`ObserveWirdProgressTest.kt`:

```kotlin
package com.kutubuddin.sabeel.domain.usecase

import com.kutubuddin.sabeel.data.local.db.entity.CustomDhikrEntity
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCategory
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.DhikrMeaning
import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.model.WirdItem
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveWirdProgressTest {

    private fun catalogItem(key: String, target: Int) = DhikrItem(
        key = key, arabicText = "ar-$key", displayName = "name-$key",
        transliteration = "tr-$key",
        meaning = DhikrMeaning(en = "m"), defaultTarget = target,
        spiritualReward = LocalizedText(en = "r"), hadithRef = "", category = DhikrCategory.DAILY
    )

    private fun fakeDhikrRepo(items: List<DhikrItem>) = object : DhikrRepository {
        override fun getAllDhikr() = flowOf(items)
        override fun getCustomDhikr() = flowOf(emptyList<CustomDhikrEntity>())
        override suspend fun saveCustomDhikr(dhikr: CustomDhikrEntity) {}
        override suspend fun deleteCustomDhikr(dhikr: CustomDhikrEntity) {}
    }

    private fun fakeWirdRepo(items: List<WirdItem>) = object : WirdRepository {
        override fun observeWird() = flowOf(items)
        override suspend fun addToWird(dhikrKey: String, target: Int) {}
        override suspend fun updateTarget(dhikrKey: String, target: Int) {}
        override suspend fun removeFromWird(dhikrKey: String) {}
        override suspend fun reorder(orderedKeys: List<String>) {}
        override suspend fun seedDefaultIfEmpty() {}
    }

    private fun fakeSessionRepo(counts: Map<String, Int>) = object : SessionRepository {
        override fun getSessionsForDate(dateKey: String) = flowOf(emptyList<DhikrSessionEntity>())
        override fun getTotalCountForDate(dateKey: String) = flowOf(0)
        override fun getTotalAllTime() = flowOf(0)
        override fun getTotalSessionCount() = flowOf(0)
        override fun getCountsByKeyForDate(dateKey: String) = flowOf(counts)
        override suspend fun insertSession(session: DhikrSessionEntity) {}
    }

    private fun fakeTasbihRepo(active: ActiveDhikr, count: Int) = object : TasbihRepository {
        override val activeCount: Flow<Int> = flowOf(count)
        override val activeDhikr: Flow<ActiveDhikr> = flowOf(active)
        override val isSmartFlowEnabled = flowOf(true)
        override val smartFlowVariant = flowOf(SmartFlowVariant.CLASSIC)
        override val isPocketModeActive = flowOf(false)
        override val streak: Flow<Streak?> = flowOf(null)
        override fun getDailyTargetFlow(date: String, dhikrKey: String) = flowOf(null)
        override suspend fun incrementCount(date: String) = 0
        override suspend fun decrementCount() = 0
        override suspend fun resetCount() {}
        override suspend fun setDhikr(key: String, targetOverride: Int?) {}
        override suspend fun setSmartFlowEnabled(enabled: Boolean) {}
        override suspend fun setSmartFlowVariant(variant: SmartFlowVariant) {}
        override suspend fun setPocketModeActive(active: Boolean) {}
        override suspend fun completeDhikrTarget(date: String, dhikrKey: String, targetCount: Int) {}
    }

    private fun active(key: String, target: Int) =
        ActiveDhikr(key, "ar", "name", target, LocalizedText(en = ""), "")

    @Test
    fun projects_savedPlusLive_resolvesDisplay_ordersByPlan() = runTest {
        val useCase = ObserveWirdProgress(
            wirdRepository = fakeWirdRepo(listOf(WirdItem("A", 33, 0), WirdItem("B", 100, 1))),
            dhikrRepository = fakeDhikrRepo(listOf(catalogItem("A", 33), catalogItem("B", 100))),
            sessionRepository = fakeSessionRepo(mapOf("A" to 33, "B" to 10)),
            tasbihRepository = fakeTasbihRepo(active("B", 100), count = 15) // live +15 on B
        )
        val progress = useCase("2026-07-03").first()

        assertEquals(listOf("A", "B"), progress.items.map { it.dhikrKey })
        assertEquals("name-A", progress.items[0].displayName)
        assertEquals(33, progress.items[0].countToday)   // saved only
        assertEquals(25, progress.items[1].countToday)   // 10 saved + 15 live
        assertTrue(progress.items[0].isComplete)
    }

    @Test
    fun filtersOut_wirdItemsMissingFromCatalog() = runTest {
        val useCase = ObserveWirdProgress(
            wirdRepository = fakeWirdRepo(listOf(WirdItem("GONE", 33, 0))),
            dhikrRepository = fakeDhikrRepo(emptyList()), // key not resolvable
            sessionRepository = fakeSessionRepo(emptyMap()),
            tasbihRepository = fakeTasbihRepo(active("X", 33), count = 0)
        )
        assertTrue(useCase("2026-07-03").first().items.isEmpty())
    }
}
```

- [ ] **Step 2: Run — verify fail**

Run: `./gradlew :app:testDebugUnitTest --tests "*ObserveWirdProgressTest*"`
Expected: FAIL — `ObserveWirdProgress` unresolved (and `TasbihRepository.setDhikr(key, targetOverride)` signature — Task 7 changes it; for now the fake matches the NEW signature, so this test also drives Task 7's interface. If compilation blocks on the old `setDhikr(key)` signature, proceed to define `ObserveWirdProgress`; the interface change lands in Task 7 and this test compiles then. To keep this task self-contained, temporarily give the fake `override suspend fun setDhikr(key: String) {}` and update it in Task 7. **Chosen approach: change the fake to the current single-arg `setDhikr(key)` here, and Task 7 updates both the interface and this fake.**)

> **Note for implementer:** In Step 1 above, if Task 7 hasn't run yet, change the fake's `override suspend fun setDhikr(key: String, targetOverride: Int?) {}` to `override suspend fun setDhikr(key: String) {}` so this task compiles against the current interface. Task 7 flips both.

- [ ] **Step 3: Create the use case**

`ObserveWirdProgress.kt`:

```kotlin
package com.kutubuddin.sabeel.domain.usecase

import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import com.kutubuddin.sabeel.domain.model.liveContribution
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Derived daily-wird progress: the plan joined with today's per-key session sums
 * and the merged catalog, with the live in-progress round folded in. Items whose
 * key no longer resolves (e.g. a deleted custom dhikr) are dropped.
 */
class ObserveWirdProgress @Inject constructor(
    private val wirdRepository: WirdRepository,
    private val dhikrRepository: DhikrRepository,
    private val sessionRepository: SessionRepository,
    private val tasbihRepository: TasbihRepository
) {
    operator fun invoke(dateKey: String): Flow<WirdProgress> = combine(
        wirdRepository.observeWird(),
        dhikrRepository.getAllDhikr(),
        sessionRepository.getCountsByKeyForDate(dateKey),
        tasbihRepository.activeDhikr,
        tasbihRepository.activeCount
    ) { plan, catalog, savedByKey, activeDhikr, activeCount ->
        val catalogByKey = catalog.associateBy { it.key }
        val items = plan.mapNotNull { item ->
            val d = catalogByKey[item.dhikrKey] ?: return@mapNotNull null
            val saved = savedByKey[item.dhikrKey] ?: 0
            val live = liveContribution(item.dhikrKey, activeDhikr.key, activeCount, activeDhikr.target)
            WirdProgressItem(
                dhikrKey = item.dhikrKey,
                displayName = d.displayName,
                arabicText = d.arabicText,
                transliteration = d.transliteration,
                target = item.target,
                countToday = saved + live,
                position = item.position
            )
        }
        WirdProgress(items)
    }
}
```

- [ ] **Step 4: Run — verify pass, gate, commit**

Run: `./gradlew :app:testDebugUnitTest --tests "*ObserveWirdProgressTest*"` → PASS (2 tests).
Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/domain/usecase/ObserveWirdProgress.kt \
        app/src/test/java/com/kutubuddin/sabeel/domain/usecase/ObserveWirdProgressTest.kt
git commit -m "$(cat <<'EOF'
feat(wird): ObserveWirdProgress use case (derived projection)

Combines plan + per-key session sums + merged catalog + live round into
WirdProgress; drops items whose key no longer resolves.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 7: Tap-to-count target override

**Files:**
- Modify: `app/src/main/java/com/kutubuddin/sabeel/data/local/datastore/CounterDataStore.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/domain/repository/TasbihRepository.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/data/repository/TasbihRepositoryImpl.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/tasbih/TasbihContract.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/tasbih/TasbihViewModel.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/domain/model/TargetOverrideTest.kt`
- Update: the fake in `ObserveWirdProgressTest.kt` (setDhikr signature)

**Interfaces — Produces:**
- `TasbihRepository.setDhikr(key: String, targetOverride: Int? = null)` (replaces `setDhikr(key)`)
- `TasbihIntent.SetDhikr(val key: String, val target: Int? = null)`
- `fun applyTargetOverride(active: ActiveDhikr, override: Int?): ActiveDhikr` (pure helper in `TasbihRepositoryImpl` file's package or a small util)

- [ ] **Step 1: Write the failing pure-helper test**

`TargetOverrideTest.kt`:

```kotlin
package com.kutubuddin.sabeel.domain.model

import com.kutubuddin.sabeel.data.repository.applyTargetOverride
import org.junit.Assert.assertEquals
import org.junit.Test

class TargetOverrideTest {

    private fun active(target: Int) =
        ActiveDhikr("A", "ar", "name", target, LocalizedText(en = ""), "")

    @Test
    fun override_replacesTarget_whenPresentAndPositive() {
        assertEquals(100, applyTargetOverride(active(33), 100).target)
    }

    @Test
    fun override_ignored_whenNullOrNonPositive() {
        assertEquals(33, applyTargetOverride(active(33), null).target)
        assertEquals(33, applyTargetOverride(active(33), 0).target)
    }
}
```

- [ ] **Step 2: Run — verify fail**

Run: `./gradlew :app:testDebugUnitTest --tests "*TargetOverrideTest*"`
Expected: FAIL — `applyTargetOverride` unresolved.

- [ ] **Step 3: Add the override to `CounterDataStore`**

In `CounterDataStore.kt`: add to the `companion object`:

```kotlin
        val KEY_ACTIVE_TARGET_OVERRIDE = intPreferencesKey("active_target_override")
```

Add a flow (null when absent or 0):

```kotlin
    val activeTargetOverrideFlow: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[KEY_ACTIVE_TARGET_OVERRIDE]?.takeIf { it > 0 }
    }
```

Change `setDhikrKey` to clear the override, and add a with-target setter:

```kotlin
    suspend fun setDhikrKey(key: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_DHIKR] = key
            preferences[KEY_ACTIVE_TARGET_OVERRIDE] = 0   // library launches use the default target
        }
    }

    suspend fun setDhikrKeyWithTarget(key: String, target: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_DHIKR] = key
            preferences[KEY_ACTIVE_TARGET_OVERRIDE] = target
        }
    }
```

- [ ] **Step 4: Apply the override in `TasbihRepositoryImpl` (+ pure helper)**

In `TasbihRepositoryImpl.kt`: add imports `import kotlinx.coroutines.flow.combine`. Add the top-level pure helper (below the imports, above the class, same file):

```kotlin
/** Returns [active] with its target replaced by [override] when the override is a positive value. */
fun applyTargetOverride(active: ActiveDhikr, override: Int?): ActiveDhikr =
    if (override != null && override > 0) active.copy(target = override) else active
```

Change `activeDhikr` to combine key + override:

```kotlin
    override val activeDhikr: Flow<ActiveDhikr> = combine(
        counterDataStore.activeDhikrKeyFlow,
        counterDataStore.activeTargetOverrideFlow
    ) { key, override -> applyTargetOverride(DhikrCatalog.resolve(key), override) }
```

In `incrementCount`, apply the override to the resolved target:

```kotlin
    override suspend fun incrementCount(date: String): Int = withContext(ioDispatcher) {
        counterDataStore.incrementCounter()
        val currentCount = counterDataStore.counterValueFlow.first()
        val key          = counterDataStore.activeDhikrKeyFlow.first()
        val override     = counterDataStore.activeTargetOverrideFlow.first()
        val resolved     = applyTargetOverride(DhikrCatalog.resolve(key), override)

        sakinahDao.insertDailyTarget(
            DailyTargetEntity(
                id           = "${key}_$date",
                date         = date,
                dhikrType    = key,
                currentCount = currentCount,
                targetCount  = resolved.target,
                isCompleted  = currentCount >= resolved.target
            )
        )
        currentCount
    }
```

Change `setDhikr` to accept the override and route to the right datastore setter:

```kotlin
    override suspend fun setDhikr(key: String, targetOverride: Int?) = withContext(ioDispatcher) {
        if (targetOverride != null && targetOverride > 0) {
            counterDataStore.setDhikrKeyWithTarget(key, targetOverride)
        } else {
            counterDataStore.setDhikrKey(key)
        }
        counterDataStore.resetCounter()
    }
```

- [ ] **Step 5: Update the `TasbihRepository` interface**

In `TasbihRepository.kt`, change the `setDhikr` signature to:

```kotlin
    suspend fun setDhikr(key: String, targetOverride: Int? = null)
```

- [ ] **Step 6: Thread the target through the intent + ViewModel**

In `TasbihContract.kt`, change:

```kotlin
    data class SetDhikr(val key: String, val target: Int? = null) : TasbihIntent
```

In `TasbihViewModel.kt`, change the dispatch line `is TasbihIntent.SetDhikr -> handleSetDhikr(intent.key)` to:

```kotlin
            is TasbihIntent.SetDhikr -> handleSetDhikr(intent.key, intent.target)
```

and change `handleSetDhikr`:

```kotlin
    private fun handleSetDhikr(key: String, target: Int? = null) {
        expectingReset = true
        lastSentCount = -1
        _state.update { it.copy(stepIndex = 0) }
        viewModelScope.launch {
            repositoryMutex.withLock { repository.setDhikr(key, target) }
        }
    }
```

- [ ] **Step 7: Fix the `ObserveWirdProgressTest` fake**

In `ObserveWirdProgressTest.kt`, ensure the fake `TasbihRepository` overrides the new signature:

```kotlin
        override suspend fun setDhikr(key: String, targetOverride: Int?) {}
```

- [ ] **Step 8: Run — verify pass, gate, commit**

Run: `./gradlew :app:testDebugUnitTest --tests "*TargetOverrideTest*"` → PASS.
Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/data/local/datastore/CounterDataStore.kt \
        app/src/main/java/com/kutubuddin/sabeel/domain/repository/TasbihRepository.kt \
        app/src/main/java/com/kutubuddin/sabeel/data/repository/TasbihRepositoryImpl.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/tasbih/TasbihContract.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/tasbih/TasbihViewModel.kt \
        app/src/test/java/com/kutubuddin/sabeel/domain/model/TargetOverrideTest.kt \
        app/src/test/java/com/kutubuddin/sabeel/domain/usecase/ObserveWirdProgressTest.kt
git commit -m "$(cat <<'EOF'
feat(wird): per-active target override for tap-to-count

SetDhikr(key, target?) persists an active_target_override that activeDhikr and
incrementCount apply (override ?: defaultTarget); library launches clear it.
One override point cascades to display, completion, and the saved session.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 8: Retire the single-number daily goal

**Files:**
- Modify: `app/src/main/java/com/kutubuddin/sabeel/domain/repository/SettingsRepository.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/data/repository/SettingsRepositoryImpl.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/settings/SettingsState.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/settings/SettingsViewModel.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/settings/SettingsScreen.kt`

No new unit test (removal); the compile gate + existing suite guard it. This task deliberately breaks `HomeViewModel`/`HomeState` references to `dailyGoal` — Task 9 immediately repairs them, so **run the full gate only at the end of Task 9**. Within this task, compile will fail until Task 9; that's expected. (If you prefer each task to compile, do Tasks 8 and 9 as one commit — they are the single "goal migration" phase.)

> **Recommended:** treat Tasks 8+9 as one atomic commit ("goal migration"). The steps are split for readability; stage and commit them together at the end of Task 9.

- [ ] **Step 1: Remove from the repository interface + impl**

In `SettingsRepository.kt`, delete the `val dailyGoal: Flow<Int>` line and the `suspend fun setDailyGoal(count: Int)` line.

In `SettingsRepositoryImpl.kt`, delete: the `KEY_DAILY_GOAL` companion entry, the `override val dailyGoal = ...` line, and the `override suspend fun setDailyGoal(...)` line. (Leaving the orphaned DataStore key unread is harmless.)

- [ ] **Step 2: Remove from settings state + intent + ViewModel**

In `SettingsState.kt`: delete `val dailyGoal: Int = 200,` from `SettingsState` and `data class SetDailyGoal(val count: Int) : SettingsIntent()` from `SettingsIntent`.

In `SettingsViewModel.kt`: change the second inner `combine` to drop `repository.dailyGoal` and reindex — replace the whole `combine(...)` block for extras with:

```kotlin
        combine(repository.translitEnabled, repository.autoReset, repository.soundEnabled, repository.showStreaks) {
            translit, autoReset, sound, showStreaks -> listOf<Any>(translit, autoReset, sound, showStreaks)
        }
    ) { (theme, lang, haptics), extras ->
        SettingsState(
            theme           = theme as String,
            language        = lang as String,
            hapticsLevel    = haptics as String,
            translitEnabled = extras[0] as Boolean,
            autoReset       = extras[1] as Boolean,
            soundEnabled    = extras[2] as Boolean,
            showStreaks     = extras[3] as Boolean
        )
```

and delete the `is SettingsIntent.SetDailyGoal-> repository.setDailyGoal(intent.count)` branch from `processIntent`.

- [ ] **Step 2b: Remove the Daily Goal section from `SettingsScreen`**

In `SettingsScreen.kt`, delete the entire "Daily Goal" section: the `SettingsSection`/`SectionHeader` for `strings.settingsDailyGoalHeader` and the `DailyGoalRow(...)` call, plus the `@Composable private fun DailyGoalRow(...)` definition itself and any now-unused imports it introduced. (Search the file for `DailyGoal`, `settingsDailyGoalHeader`, `settingsDailyTarget`, `SetDailyGoal` and remove those usages.)

- [ ] **Step 3: Proceed directly to Task 9** (do not run the full gate yet — compile is intentionally red until Home is repaired). Compile-check just the settings layer if desired: it will still fail because Home references `dailyGoal`. Continue.

---

## Task 9: Home consumes wird progress (summary card)

**Files:**
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/home/HomeState.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/home/HomeViewModel.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/home/HomeScreen.kt`
- Test: `app/src/test/java/com/kutubuddin/sabeel/ui/home/WirdSummaryTest.kt`

**Interfaces:**
- Consumes: `ObserveWirdProgress`, `WirdProgress`.
- Produces: `WirdSummary(completed, total, countedSum, targetSum, isEmpty)`; `fun WirdProgress.toSummary(): WirdSummary`; `HomeState.wird: WirdSummary`; `HomeScreen` `onOpenWird: () -> Unit`.

- [ ] **Step 1: Write the failing summary-mapping test**

`WirdSummaryTest.kt`:

```kotlin
package com.kutubuddin.sabeel.ui.home

import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WirdSummaryTest {

    private fun item(target: Int, count: Int) =
        WirdProgressItem("k$target", "n", "a", null, target, count, 0)

    @Test
    fun toSummary_mapsCountsAndEmptiness() {
        val s = WirdProgress(listOf(item(33, 33), item(100, 40))).toSummary()
        assertEquals(1, s.completed)
        assertEquals(2, s.total)
        assertEquals(73, s.countedSum)
        assertEquals(133, s.targetSum)
        assertFalse(s.isEmpty)
    }

    @Test
    fun toSummary_emptyWird() {
        val s = WirdProgress(emptyList()).toSummary()
        assertTrue(s.isEmpty)
        assertEquals(0, s.total)
    }
}
```

- [ ] **Step 2: Run — verify fail**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdSummaryTest*"`
Expected: FAIL — `WirdSummary` / `toSummary` unresolved.

- [ ] **Step 3: Add `WirdSummary` + mapping to `HomeState.kt`**

In `HomeState.kt`: add the import `import com.kutubuddin.sabeel.domain.model.WirdProgress`. Replace `val dailyGoal: Int = 200,` in `HomeState` with `val wird: WirdSummary = WirdSummary(),` and append:

```kotlin
/** Lean wird view for the Home summary card — numbers only, no item list. */
data class WirdSummary(
    val completed: Int = 0,
    val total: Int = 0,
    val countedSum: Int = 0,
    val targetSum: Int = 0,
    val isEmpty: Boolean = true
)

fun WirdProgress.toSummary(): WirdSummary = WirdSummary(
    completed = completed,
    total = total,
    countedSum = countedSum,
    targetSum = targetSum,
    isEmpty = items.isEmpty()
)
```

- [ ] **Step 4: Wire `ObserveWirdProgress` into `HomeViewModel`**

In `HomeViewModel.kt`:
- Add constructor param `private val observeWirdProgress: ObserveWirdProgress` and import `import com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress`.
- Add `import kotlinx.coroutines.flow.map`.
- Replace `settingsRepository.dailyGoal` in `counterGroup` with `observeWirdProgress(today).map { it.toSummary() }`:

```kotlin
    private val counterGroup = combine(
        observeWirdProgress(today).map { it.toSummary() },
        tasbihRepository.activeCount,
        tasbihRepository.activeDhikr,
        tasbihRepository.streak,
        settingsRepository.showStreaks
    ) { wird, lastCount, lastDhikr, streak, showStreaks ->
        listOf<Any?>(wird, lastCount, lastDhikr, streak, showStreaks)
    }
```

- In the final `combine`, change `val dailyGoal = c[0] as Int` to `val wird = c[0] as WirdSummary`, and in the `HomeState(...)` constructor replace `dailyGoal = dailyGoal,` with `wird = wird,`. Add `import com.kutubuddin.sabeel.ui.home.WirdSummary` is unnecessary (same package). `settingsRepository` is still used (for `language`, `showStreaks`).

- [ ] **Step 5: Update the Home card in `HomeScreen.kt`**

Add `onOpenWird: () -> Unit` to `HomeScreen`'s signature:

```kotlin
fun HomeScreen(
    onResumeCounting: () -> Unit,
    onOpenWird: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
```

Pass it into the stats card call: change `StreakGoalCard(state = state)` to `StreakGoalCard(state = state, onOpenWird = onOpenWird)`.

Replace the **Daily goal** portion of `StreakGoalCard` (the `Row` with `TrackChanges` + `strings.homeDailyGoal` and the `LinearProgressIndicator`) with a tappable wird summary. Full replacement for `StreakGoalCard`:

```kotlin
@Composable
private fun StreakGoalCard(state: HomeState, onOpenWird: () -> Unit) {
    val strings = LocalStrings.current
    val fraction = if (state.wird.targetSum > 0)
        (state.wird.countedSum.toFloat() / state.wird.targetSum).coerceIn(0f, 1f)
    else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = fraction, animationSpec = tween(800), label = "wird_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .clickable(onClick = onOpenWird)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.showStreaks) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.Spa, contentDescription = null,
                    tint = SabeelColors.AccentTeal, modifier = Modifier.size(18.dp))
                Text(strings.homeConsistency, fontSize = 11.sp, color = SabeelColors.TextSecondary)
                Spacer(Modifier.weight(1f))
                val streakDigits = state.currentStreak.toLocalizedNumerals(state.language)
                Text(
                    text = if (state.currentStreak == 1) strings.homeDayOne.format(streakDigits)
                           else strings.homeDayOther.format(streakDigits),
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SabeelColors.TextPrimary
                )
            }
            HorizontalDivider(color = SabeelColors.Divider)
        }

        // Today's Wird summary (tappable → Wird screen)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.TrackChanges, contentDescription = null,
                    tint = SabeelColors.TextSecondary, modifier = Modifier.size(16.dp))
                Text(strings.wirdTitle, fontSize = 13.sp, color = SabeelColors.TextSecondary)
            }
            Text(
                text = if (state.wird.isEmpty) strings.wirdSetup
                       else strings.wirdDoneOf.format(
                           state.wird.completed.toLocalizedNumerals(state.language),
                           state.wird.total.toLocalizedNumerals(state.language)
                       ),
                fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SabeelColors.TextPrimary
            )
        }
        if (!state.wird.isEmpty) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = SabeelColors.AccentTeal, trackColor = SabeelColors.ArcTrack,
                strokeCap = StrokeCap.Round, gapSize = 0.dp, drawStopIndicator = {}
            )
        }
    }
}
```

(`strings.wirdTitle`, `wirdSetup`, `wirdDoneOf` are added in Task 12. To keep this task compiling before Task 12, add the three fields in Task 12 **before** running the full gate, OR reorder so Task 12 precedes the UI tasks. **Chosen order:** do Task 12's string additions now if you hit unresolved `strings.wird*`. Simplest: run Task 12 immediately after Task 9. The gate at the end of Task 9 therefore tolerates unresolved wird strings only if Task 12 is folded in — see note below.)

> **Ordering note:** `strings.wird*` referenced here are defined in Task 12. To keep the bisect-safe rule, **fold Task 12's UiStrings/UiText additions into this commit** (add the three fields `wirdTitle`, `wirdSetup`, `wirdDoneOf` now; the rest of the wird strings can land with the UI tasks). The plan lists Task 12 separately for clarity, but any commit that *references* a new string must also *define* it.

- [ ] **Step 6: Update the NavHost call to Home** (compile dependency)

In `SabeelNavHost.kt`, the `composable(SabeelTab.Home.route)` block gains the callback — this is completed in Task 10 when the `wird` route exists. For now, to compile, pass a temporary no-op: `onOpenWird = {}`. Task 10 replaces it with real navigation.

- [ ] **Step 7: Run the FULL gate (Tasks 8+9 together) + commit**

Run: `./gradlew :app:testDebugUnitTest --tests "*WirdSummaryTest*"` → PASS.
Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/domain/repository/SettingsRepository.kt \
        app/src/main/java/com/kutubuddin/sabeel/data/repository/SettingsRepositoryImpl.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/settings/SettingsState.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/settings/SettingsViewModel.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/settings/SettingsScreen.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/home/HomeState.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/home/HomeViewModel.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/home/HomeScreen.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/navigation/SabeelNavHost.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiStrings.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiText.kt \
        app/src/test/java/com/kutubuddin/sabeel/ui/home/WirdSummaryTest.kt
git commit -m "$(cat <<'EOF'
feat(wird): retire single-number goal; Home shows wird summary

Removes dailyGoal from settings + Home; StreakGoalCard's goal row becomes a
tappable Today's Wird summary (sum-based bar, discrete completed/total). Adds
wirdTitle/wirdSetup/wirdDoneOf strings.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 10: Wird screen (view + count) + navigation

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdViewModel.kt`
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdScreen.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/navigation/SabeelNavHost.kt`

**Interfaces:**
- Consumes: `ObserveWirdProgress`, `WirdProgress`, `TasbihIntent.SetDhikr(key, target)`, `strings.wird*`.
- Produces: routes `"wird"`, `"wird/edit"`; `WirdViewModel.state: StateFlow<WirdProgress>` + `language: StateFlow<String>`.

No unit test (thin VM + Compose); compile gate + manual verification. Verify seed appears (Task 3) by launching and opening the wird card.

- [ ] **Step 1: Create `WirdViewModel`**

```kotlin
package com.kutubuddin.sabeel.ui.wird

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WirdViewModel @Inject constructor(
    observeWirdProgress: ObserveWirdProgress,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val today = LocalDate.now().toString()

    val state: StateFlow<WirdProgress> = observeWirdProgress(today).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WirdProgress(emptyList())
    )

    val language: StateFlow<String> = settingsRepository.language.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "en"
    )
}
```

- [ ] **Step 2: Create `WirdScreen`**

```kotlin
package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

@Composable
fun WirdScreen(
    onCountItem: (key: String, target: Int) -> Unit,
    onEdit: () -> Unit,
    viewModel: WirdViewModel = hiltViewModel()
) {
    val progress by viewModel.state.collectAsState()
    val language by viewModel.language.collectAsState()
    val strings = LocalStrings.current

    Column(Modifier.fillMaxSize().background(SabeelColors.Background)) {
        // Header: title + completed/total + sum-based bar
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(strings.wirdTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = SabeelColors.TextPrimary)
                Text(
                    strings.wirdDoneOf.format(
                        progress.completed.toLocalizedNumerals(language),
                        progress.total.toLocalizedNumerals(language)
                    ),
                    fontSize = 13.sp, color = SabeelColors.TextSecondary
                )
            }
            if (progress.total > 0) {
                val fraction = if (progress.targetSum > 0)
                    (progress.countedSum.toFloat() / progress.targetSum).coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = SabeelColors.AccentTeal, trackColor = SabeelColors.ArcTrack,
                    strokeCap = StrokeCap.Round, gapSize = 0.dp, drawStopIndicator = {}
                )
            }
        }
        HorizontalDivider(color = SabeelColors.Divider)

        if (progress.items.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(strings.wirdEmpty, fontSize = 15.sp, color = SabeelColors.TextSecondary)
                Spacer(Modifier.height(6.dp))
                Text(strings.wirdEmptyHint, fontSize = 12.sp, color = SabeelColors.TextHint)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onEdit, colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal)) {
                    Text(strings.wirdAddDhikr, color = SabeelColors.Background)
                }
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(progress.items, key = { it.dhikrKey }) { item ->
                    WirdItemRow(item, language) { onCountItem(item.dhikrKey, item.target) }
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable(onClick = onEdit).padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = SabeelColors.AccentTeal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.wirdEditTitle, color = SabeelColors.AccentTeal, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun WirdItemRow(item: WirdProgressItem, language: String, onClick: () -> Unit) {
    val done = item.isComplete
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .clickable(enabled = !done, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (done) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (done) SabeelColors.SageGreen else SabeelColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(item.displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                color = if (done) SabeelColors.SageGreen else SabeelColors.TextPrimary)
            Text(item.arabicText, maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = arabicStyle.copy(fontSize = 16.sp, lineHeight = 24.sp), color = SabeelColors.ArabicText,
                modifier = Modifier.fillMaxWidth())
        }
        Text(
            "${item.countToday.toLocalizedNumerals(language)} / ${item.target.toLocalizedNumerals(language)}",
            fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = if (done) SabeelColors.SageGreen else SabeelColors.AccentTeal
        )
    }
}
```

- [ ] **Step 3: Add the `wird` route + wire Home's `onOpenWird`**

In `SabeelNavHost.kt`: add imports `import com.kutubuddin.sabeel.ui.wird.WirdScreen`. Change the Home composable and add the wird route:

```kotlin
            composable(SabeelTab.Home.route) {
                HomeScreen(
                    onResumeCounting = { navController.switchToCountTab() },
                    onOpenWird = { navController.navigate("wird") }
                )
            }

            composable("wird") {
                WirdScreen(
                    onCountItem = { key, target ->
                        tasbihViewModel.processIntent(com.kutubuddin.sabeel.ui.tasbih.TasbihIntent.SetDhikr(key, target))
                        navController.switchToCountTab()
                    },
                    onEdit = { navController.navigate("wird/edit") }
                )
            }
```

- [ ] **Step 4: Gate + manual verify + commit**

Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.
Manual: launch app → Home → tap "Today's Wird" → see seeded 33/33/34 → tap an item → counts on Count screen to that target → return, item shows progress.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdViewModel.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdScreen.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/navigation/SabeelNavHost.kt
git commit -m "$(cat <<'EOF'
feat(wird): Wird checklist screen + navigation

Tappable per-item checklist with sum-based header bar; tapping an incomplete
item sets the dhikr with its wird target and jumps to Count. Reached from the
Home wird card.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 11: Edit Wird screen (build/edit + library picker)

**Files:**
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdEditState.kt`
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdEditViewModel.kt`
- Create: `app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdEditScreen.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/navigation/SabeelNavHost.kt`

**Interfaces:**
- Consumes: `WirdRepository`, `DhikrRepository.getAllDhikr()`, `SettingsRepository.language`, `strings.wird*`.
- Produces: route `"wird/edit"`; `WirdEditViewModel` with `state`, `addDhikr(key,target)`, `updateTarget(key,target)`, `remove(key)`, `moveUp(key)`/`moveDown(key)`.

We use simple **move up/down** rather than free drag (drag-reorder is a large Compose add; up/down delivers reordering with far less surface — YAGNI). `WirdEditState` exposes the resolved current items + the pickable catalog (non-Smart-Flow, not-already-added).

- [ ] **Step 1: Create `WirdEditState`**

```kotlin
package com.kutubuddin.sabeel.ui.wird

import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.domain.model.WirdItem

/** A wird row resolved for editing (plan + display name). */
data class WirdEditRow(
    val dhikrKey: String,
    val displayName: String,
    val target: Int,
    val position: Int
)

data class WirdEditState(
    val rows: List<WirdEditRow> = emptyList(),
    val pickable: List<DhikrItem> = emptyList(), // non-SmartFlow, not already added
    val language: String = "en"
)
```

- [ ] **Step 2: Create `WirdEditViewModel`**

```kotlin
package com.kutubuddin.sabeel.ui.wird

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WirdEditViewModel @Inject constructor(
    private val wirdRepository: WirdRepository,
    dhikrRepository: DhikrRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val state: StateFlow<WirdEditState> = combine(
        wirdRepository.observeWird(),
        dhikrRepository.getAllDhikr(),
        settingsRepository.language
    ) { plan, catalog, language ->
        val byKey = catalog.associateBy { it.key }
        val rows = plan.mapNotNull { item ->
            byKey[item.dhikrKey]?.let {
                WirdEditRow(item.dhikrKey, it.displayName, item.target, item.position)
            }
        }
        val addedKeys = plan.map { it.dhikrKey }.toSet()
        val pickable = catalog.filter { !it.isSmartFlow && it.key !in addedKeys }
        WirdEditState(rows = rows, pickable = pickable, language = language)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WirdEditState())

    fun addDhikr(key: String, target: Int) = viewModelScope.launch { wirdRepository.addToWird(key, target) }
    fun updateTarget(key: String, target: Int) = viewModelScope.launch { wirdRepository.updateTarget(key, target) }
    fun remove(key: String) = viewModelScope.launch { wirdRepository.removeFromWird(key) }

    fun move(key: String, up: Boolean) = viewModelScope.launch {
        val order = state.value.rows.map { it.dhikrKey }.toMutableList()
        val i = order.indexOf(key)
        if (i < 0) return@launch
        val j = if (up) i - 1 else i + 1
        if (j !in order.indices) return@launch
        order[i] = order[j].also { order[j] = order[i] }
        wirdRepository.reorder(order)
    }
}
```

- [ ] **Step 3: Create `WirdEditScreen`** (list + steppers + remove + move + add picker sheet)

```kotlin
package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WirdEditScreen(
    viewModel: WirdEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val strings = LocalStrings.current
    var showPicker by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(SabeelColors.Background)) {
        Text(
            strings.wirdEditTitle,
            fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = SabeelColors.TextPrimary,
            modifier = Modifier.padding(20.dp)
        )
        HorizontalDivider(color = SabeelColors.Divider)

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.rows, key = { it.dhikrKey }) { row ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(SabeelColors.Surface).padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column {
                        Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = strings.wirdReorder,
                            tint = SabeelColors.TextSecondary,
                            modifier = Modifier.size(18.dp).clickable { viewModel.move(row.dhikrKey, up = true) })
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = strings.wirdReorder,
                            tint = SabeelColors.TextSecondary,
                            modifier = Modifier.size(18.dp).clickable { viewModel.move(row.dhikrKey, up = false) })
                    }
                    Text(row.displayName, Modifier.weight(1f), fontSize = 14.sp,
                        fontWeight = FontWeight.Medium, color = SabeelColors.TextPrimary)
                    // Target stepper
                    Icon(Icons.Filled.Remove, contentDescription = strings.wirdTargetA11y,
                        tint = SabeelColors.AccentTeal,
                        modifier = Modifier.size(22.dp).clickable { viewModel.updateTarget(row.dhikrKey, row.target - 1) })
                    Text(row.target.toLocalizedNumerals(state.language), fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, color = SabeelColors.TextPrimary,
                        modifier = Modifier.widthIn(min = 32.dp))
                    Icon(Icons.Filled.Add, contentDescription = strings.wirdTargetA11y,
                        tint = SabeelColors.AccentTeal,
                        modifier = Modifier.size(22.dp).clickable { viewModel.updateTarget(row.dhikrKey, row.target + 1) })
                    Icon(Icons.Outlined.Delete, contentDescription = strings.wirdRemove,
                        tint = SabeelColors.TextSecondary,
                        modifier = Modifier.size(20.dp).clickable { viewModel.remove(row.dhikrKey) })
                }
            }
        }

        Button(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = SabeelColors.Background)
            Spacer(Modifier.width(6.dp))
            Text(strings.wirdAddDhikr, color = SabeelColors.Background, fontWeight = FontWeight.Bold)
        }
    }

    if (showPicker) {
        ModalBottomSheet(onDismissRequest = { showPicker = false }, containerColor = SabeelColors.Surface) {
            LazyColumn(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                items(state.pickable, key = { it.key }) { d ->
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            viewModel.addDhikr(d.key, d.defaultTarget)
                            showPicker = false
                        }.padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(d.displayName, fontSize = 14.sp, color = SabeelColors.TextPrimary)
                        Text("${d.defaultTarget.toLocalizedNumerals(state.language)}×",
                            fontSize = 13.sp, color = SabeelColors.AccentTeal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 4: Add the `wird/edit` route**

In `SabeelNavHost.kt`: add `import com.kutubuddin.sabeel.ui.wird.WirdEditScreen` and:

```kotlin
            composable("wird/edit") {
                WirdEditScreen()
            }
```

- [ ] **Step 5: Gate + manual verify + commit**

Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.
Manual: Wird → Edit → add a dhikr (picker excludes Smart-Flow + already-added), change a target with ±, move an item up/down, delete one; return to Wird and confirm changes + progress.

```bash
git add app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdEditState.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdEditViewModel.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/wird/WirdEditScreen.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/navigation/SabeelNavHost.kt
git commit -m "$(cat <<'EOF'
feat(wird): Edit Wird screen (add/remove/retarget/reorder)

Library-backed add picker (excludes Smart-Flow + already-added), per-item
target stepper, up/down reorder, and remove. Reuses the merged catalog.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Task 12: Localization — wird strings (+ remove dead goal strings)

> **If you folded the three referenced strings (`wirdTitle`, `wirdSetup`, `wirdDoneOf`) into Task 9 as instructed, this task adds the REMAINING wird strings** (`wirdEditTitle`, `wirdAddDhikr`, `wirdEmpty`, `wirdEmptyHint`, `wirdReorder`, `wirdRemove`, `wirdTargetA11y`) **and removes the dead goal strings.** Adjust the staged set accordingly.

**Files:**
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiStrings.kt`
- Modify: `app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiText.kt`

**Interfaces — Produces:** `UiStrings.wirdTitle, wirdDoneOf, wirdSetup, wirdEditTitle, wirdAddDhikr, wirdEmpty, wirdEmptyHint, wirdReorder, wirdRemove, wirdTargetA11y`.

- [ ] **Step 1: Add fields to `UiStrings`**

In `UiStrings.kt`, add a `// ── Daily Wird ──` section to the `UiStrings` data class and **remove** `homeDailyGoal`, `settingsDailyGoalHeader`, `settingsDailyTarget`:

```kotlin
    // ── Daily Wird ──
    val wirdTitle: String,
    val wirdDoneOf: String,      // template: "%1$s / %2$s done"
    val wirdSetup: String,
    val wirdEditTitle: String,
    val wirdAddDhikr: String,
    val wirdEmpty: String,
    val wirdEmptyHint: String,
    val wirdReorder: String,     // a11y
    val wirdRemove: String,      // a11y
    val wirdTargetA11y: String,  // a11y
```

- [ ] **Step 2: Add dictionary entries + wire `resolve()` in `UiText.kt`; remove dead entries**

Remove `homeDailyGoal`, `settingsDailyGoalHeader`, `settingsDailyTarget` `val`s and their `resolve()` lines. Add a `// ── Daily Wird ──` block (drafted ur/bn, reviewed as a final pass like Q1 Task 9):

```kotlin
    // ── Daily Wird ──
    val wirdTitle = LocalizedText(en = "Today's Wird", ur = "آج کا ورد", bn = "আজকের ওয়ির্দ")
    val wirdDoneOf = LocalizedText(en = "%1\$s / %2\$s done", ur = "%1\$s / %2\$s مکمل", bn = "%1\$s / %2\$s সম্পন্ন")
    val wirdSetup = LocalizedText(en = "Set up your wird", ur = "اپنا ورد ترتیب دیں", bn = "আপনার ওয়ির্দ সাজান")
    val wirdEditTitle = LocalizedText(en = "Edit Wird", ur = "ورد میں ترمیم", bn = "ওয়ির্দ সম্পাদনা")
    val wirdAddDhikr = LocalizedText(en = "Add dhikr", ur = "ذکر شامل کریں", bn = "যিকির যোগ করুন")
    val wirdEmpty = LocalizedText(en = "Your wird is empty", ur = "آپ کا ورد خالی ہے", bn = "আপনার ওয়ির্দ খালি")
    val wirdEmptyHint = LocalizedText(en = "Add adhkar to build your daily routine", ur = "اپنا روزانہ معمول بنانے کے لیے اذکار شامل کریں", bn = "দৈনিক রুটিন তৈরি করতে যিকির যোগ করুন")
    val wirdReorder = LocalizedText(en = "Reorder", ur = "ترتیب بدلیں", bn = "ক্রম বদলান")
    val wirdRemove = LocalizedText(en = "Remove from wird", ur = "ورد سے ہٹائیں", bn = "ওয়ির্দ থেকে সরান")
    val wirdTargetA11y = LocalizedText(en = "Target", ur = "ہدف", bn = "লক্ষ্য")
```

And in `resolve(...)` add the ten `wird* = wird*.get(lang),` lines (and delete the three removed `home/settings` goal lines).

- [ ] **Step 3: Run the completeness test + gate**

Run: `./gradlew :app:testDebugUnitTest --tests "*UiTextTest*"` → PASS (no blank ur/bn).
Run: `./gradlew :app:compileDebugKotlin && ./gradlew :app:testDebugUnitTest` → baseline only.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiStrings.kt \
        app/src/main/java/com/kutubuddin/sabeel/ui/i18n/UiText.kt
git commit -m "$(cat <<'EOF'
feat(i18n): localize daily wird chrome; drop dead goal strings

Adds en/ur/bn for the wird screens/card and a11y labels; removes the now-unused
homeDailyGoal / settingsDailyGoalHeader / settingsDailyTarget. Completeness test
green.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Self-Review

**Spec coverage:**
- §2.1 persistence (entity/DAO/migration/seed) → Tasks 1–3. ✓
- §2.2 domain (WirdItem/WirdProgress/WirdRepository) → Tasks 2, 5. ✓
- §2.3 projection (getCountsByKeyForDate, live helper, ObserveWirdProgress) → Tasks 4–6. ✓
- §2.4 counting override (SetDhikr target, CounterDataStore, resolution) → Task 7. ✓
- §3.1 Home summary → Task 9. §3.2 Wird screen → Task 10. §3.3 Edit screen → Task 11. §3.4 empty state → Tasks 10–11. §3.5 nav routes → Tasks 10–11. ✓
- §4 goal migration → Tasks 8–9. ✓
- §5 localization (add + remove) → Task 12 (+ 3 strings folded into Task 9). ✓
- §6 error handling: dangling key filtered → Task 6 test; empty wird → Tasks 10–11; target≥1 → Task 2 (`coerceAtLeast(1)`); duplicate/SmartFlow exclusion → Task 11 picker filter; live double-count guard → Task 5. ✓
- §7 testing → tests in Tasks 1,2,4,5,6,7,9. ✓ (Formal Room migration test omitted — no `room-testing` dep and `exportSchema=false`; the DAO tests exercise the schema via the create path. **Known gap**, noted in the spec's testing section.)
- §8 build order → the 12 tasks follow the 7 phases. ✓

**Placeholder scan:** No "TBD/TODO"; every code step shows complete code. The two ordering notes (Task 6 fake signature; Task 9/12 string definitions) are explicit reconciliation instructions, not placeholders.

**Type consistency:** `setDhikr(key, targetOverride)` consistent across `TasbihRepository`, impl, ViewModel handler, and the `ObserveWirdProgressTest` fake (reconciled in Task 7 Step 7). `WirdSummary` field names match between `HomeState` and `HomeScreen`. `getCountsByKeyForDate` returns `Flow<List<KeyCountRow>>` (DAO) vs `Flow<Map<String,Int>>` (repository) — intentional, mapped in `SessionRepositoryImpl`; the use case consumes the `Map`. `liveContribution` signature identical in helper, test, and use case.

**Known deviations from spec:** (1) Reorder uses **up/down** buttons, not free drag (YAGNI; noted in Task 11). (2) Three wird strings are defined in the Task 9 commit rather than Task 12, to keep every commit bisect-safe (a commit that references a string must define it).
