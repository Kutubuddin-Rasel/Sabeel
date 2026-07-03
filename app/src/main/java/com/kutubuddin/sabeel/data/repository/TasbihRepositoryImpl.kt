package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.datastore.CounterDataStore
import com.kutubuddin.sabeel.data.local.db.dao.DhikrSessionDao
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import com.kutubuddin.sabeel.di.IoDispatcher
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DailyTarget
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Returns [active] with its target replaced by [override] when the override is a positive value.
 *
 * Scoped to a single resolved target: multi-step sequences (Tasbīḥ after Salah) are governed by
 * their per-step targets and are unaffected by this override. The wird library picker (Task 11)
 * therefore excludes sequence entries, so a wird item never launches an override onto a sequence.
 */
fun applyTargetOverride(active: ActiveDhikr, override: Int?): ActiveDhikr =
    if (override != null && override > 0) active.copy(target = override) else active

/**
 * Concrete implementation of TasbihRepository.
 *
 * Threading: ALL suspend functions use withContext(ioDispatcher) to ensure
 * Room and DataStore operations never run on the main thread.
 *
 * Session persistence: completeDhikrTarget now writes both the streak record
 * (SakinahDao) and a DhikrSessionEntity (DhikrSessionDao), so the Home tab
 * receives live session history via its Flow observers.
 */
@Singleton
class TasbihRepositoryImpl @Inject constructor(
    private val counterDataStore: CounterDataStore,
    private val sakinahDao: SakinahDao,
    private val dhikrSessionDao: DhikrSessionDao,        // ← NEW: session persistence
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TasbihRepository {

    override val activeCount: Flow<Int>           = counterDataStore.counterValueFlow
    override val activeDhikr: Flow<ActiveDhikr>   = combine(
        counterDataStore.activeDhikrKeyFlow,
        counterDataStore.activeTargetOverrideFlow
    ) { key, override -> applyTargetOverride(DhikrCatalog.resolve(key), override) }
    override val isSmartFlowEnabled: Flow<Boolean> = counterDataStore.isSmartFlowEnabledFlow
    override val smartFlowVariant: Flow<SmartFlowVariant> = counterDataStore.smartFlowVariantFlow
    override val isPocketModeActive: Flow<Boolean> = counterDataStore.isPocketModeActiveFlow

    override val streak: Flow<Streak?> = sakinahDao.getStreakFlow("current_streak")
        .map { entity ->
            entity?.let {
                Streak(
                    id            = it.id,
                    count         = it.count,
                    lastActiveDate = it.lastActiveDate,
                    longestStreak = it.longestStreak
                )
            }
        }
        .flowOn(ioDispatcher)

    override fun getDailyTargetFlow(date: String, dhikrKey: String): Flow<DailyTarget?> =
        sakinahDao.getDailyTargetFlow("${dhikrKey}_$date")
            .map { entity ->
                entity?.let {
                    DailyTarget(
                        id           = it.id,
                        date         = it.date,
                        dhikrType    = it.dhikrType,
                        currentCount = it.currentCount,
                        targetCount  = it.targetCount,
                        isCompleted  = it.isCompleted
                    )
                }
            }
            .flowOn(ioDispatcher)

    override suspend fun incrementCount(date: String): Int = withContext(ioDispatcher) {
        counterDataStore.incrementCounter()
        val currentCount = counterDataStore.counterValueFlow.first()
        val key          = counterDataStore.activeDhikrKeyFlow.first()
        val override     = counterDataStore.activeTargetOverrideFlow.first()
        val resolved     = applyTargetOverride(DhikrCatalog.resolve(key), override)

        // Keep DailyTarget in sync for legacy streak logic
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

    override suspend fun decrementCount(): Int = withContext(ioDispatcher) {
        counterDataStore.decrementCounter()
        counterDataStore.counterValueFlow.first()
    }

    override suspend fun resetCount() = withContext(ioDispatcher) {
        counterDataStore.resetCounter()
    }

    override suspend fun setDhikr(key: String, targetOverride: Int?) = withContext(ioDispatcher) {
        if (targetOverride != null && targetOverride > 0) {
            counterDataStore.setDhikrKeyWithTarget(key, targetOverride)
        } else {
            counterDataStore.setDhikrKey(key)
        }
        counterDataStore.resetCounter()
    }

    override suspend fun setSmartFlowEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        counterDataStore.setSmartFlowEnabled(enabled)
    }

    override suspend fun setSmartFlowVariant(variant: SmartFlowVariant) = withContext(ioDispatcher) {
        counterDataStore.setSmartFlowVariant(variant)
    }

    override suspend fun setPocketModeActive(active: Boolean) = withContext(ioDispatcher) {
        counterDataStore.setPocketModeActive(active)
    }

    /**
     * Called every time a dhikr target is hit (standard or Smart Flow).
     *
     * Responsibilities:
     * 1. Update/create the streak record in SakinahDao.
     * 2. Insert a completed DhikrSessionEntity so HomeViewModel can display it.
     *
     * Both writes happen in a single withContext block → single IO dispatch, no double-hop.
     */
    override suspend fun completeDhikrTarget(
        date: String,
        dhikrKey: String,
        targetCount: Int
    ) = withContext(ioDispatcher) {
        val today       = LocalDate.parse(date)
        val curStreak   = sakinahDao.getStreak("current_streak")

        // ── Streak logic ──────────────────────────────────────────────────────
        val newStreakCount = if (curStreak == null) {
            1
        } else {
            val lastActive   = LocalDate.parse(curStreak.lastActiveDate)
            val daysBetween  = ChronoUnit.DAYS.between(lastActive, today)
            when {
                daysBetween == 0L -> curStreak.count          // same day — no increment
                daysBetween == 1L -> curStreak.count + 1      // consecutive day
                else              -> 1                        // streak broken
            }
        }
        val newLongest = curStreak?.let {
            if (newStreakCount > it.longestStreak) newStreakCount else it.longestStreak
        } ?: newStreakCount

        sakinahDao.updateProgressAndStreak(
            date         = date,
            progress     = targetCount,
            target       = targetCount,
            streakCount  = newStreakCount,
            dhikrType    = dhikrKey,
            longestStreak = newLongest
        )

        // ── Persist session ───────────────────────────────────────────────────
        // Insert a completed session so HomeViewModel's sessionRepository flows
        // update in real-time without any manual trigger.
        dhikrSessionDao.insertSession(
            DhikrSessionEntity(
                dhikrKey   = dhikrKey,
                count      = targetCount,
                target     = targetCount,
                isComplete = true,
                dateKey    = date,
                endedAt    = System.currentTimeMillis()
            )
        )
    }
}
