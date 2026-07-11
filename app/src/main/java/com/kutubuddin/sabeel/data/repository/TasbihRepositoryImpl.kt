package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.datastore.CounterDataStore
import com.kutubuddin.sabeel.data.local.db.dao.DhikrSessionDao
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import com.kutubuddin.sabeel.di.ApplicationScope
import com.kutubuddin.sabeel.di.IoDispatcher
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DailyTarget
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val dhikrSessionDao: DhikrSessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val applicationScope: CoroutineScope
) : TasbihRepository {

    // Fast-path in-memory state for UI responsiveness
    private val _activeCount = MutableStateFlow(0)
    override val activeCount: Flow<Int> = _activeCount.asStateFlow()

    // Trigger for debounced database flush
    private val pendingSyncTrigger = MutableSharedFlow<Unit>(
        replay = 1, 
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // Current date cache to avoid passing date constantly
    private var lastKnownDate: String = LocalDate.now().toString()

    override val activeDhikr: StateFlow<ActiveDhikr> = combine(
        counterDataStore.activeDhikrKeyFlow,
        counterDataStore.activeTargetOverrideFlow
    ) { key, override -> applyTargetOverride(DhikrCatalog.resolve(key), override) }
    .stateIn(
        scope = applicationScope,
        started = SharingStarted.Eagerly,
        initialValue = applyTargetOverride(DhikrCatalog.resolve(DhikrType.SUBHANALLAH.name), null)
    )

    override val isSmartFlowEnabled: Flow<Boolean> = counterDataStore.isSmartFlowEnabledFlow
    override val smartFlowVariant: Flow<SmartFlowVariant> = counterDataStore.smartFlowVariantFlow
    override val isPocketModeActive: Flow<Boolean> = counterDataStore.isPocketModeActiveFlow
    override val activeStepIndex: Flow<Int> = counterDataStore.activeStepIndexFlow

    init {
        // Seed initial count
        applicationScope.launch(ioDispatcher) {
            _activeCount.value = counterDataStore.counterValueFlow.first()
        }

        // Background debouncer (Write-Behind)
        applicationScope.launch(ioDispatcher) {
            pendingSyncTrigger
                .debounce(1500L) // Wait 1.5s after last tap
                .collect {
                    flushToDisk()
                }
        }
    }

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

    override suspend fun incrementCount(date: String): Int {
        lastKnownDate = date
        _activeCount.update { it + 1 }
        pendingSyncTrigger.tryEmit(Unit)
        return _activeCount.value
    }

    override suspend fun decrementCount(): Int {
        _activeCount.update { if (it > 0) it - 1 else 0 }
        pendingSyncTrigger.tryEmit(Unit)
        return _activeCount.value
    }

    override suspend fun resetCount() {
        _activeCount.value = 0
        pendingSyncTrigger.tryEmit(Unit)
    }

    override suspend fun setCount(value: Int): Unit = withContext(ioDispatcher) {
        counterDataStore.setCounter(value)
        _activeCount.value = value
        pendingSyncTrigger.tryEmit(Unit)
    }

    override suspend fun setDhikr(key: String, targetOverride: Int?) = withContext(ioDispatcher) {
        val currentActiveKey = activeDhikr.value.key
        val currentCount = _activeCount.value
        val date = lastKnownDate

        if (key != currentActiveKey) {
            // Save partial session if switching away
            if (currentCount > 0) {
                dhikrSessionDao.insertSession(
                    DhikrSessionEntity(
                        dhikrKey = currentActiveKey,
                        count = currentCount,
                        target = activeDhikr.value.target,
                        isComplete = false,
                        dateKey = date,
                        endedAt = System.currentTimeMillis()
                    )
                )
            }
            
            if (targetOverride != null && targetOverride > 0) {
                counterDataStore.setDhikrKeyWithTarget(key, targetOverride)
            } else {
                counterDataStore.setDhikrKey(key)
            }
            counterDataStore.setStepIndex(0) // Reset sequence whenever dhikr changes
            counterDataStore.resetCounter()
            _activeCount.value = 0
        } else {
            // Same Dhikr. Just update target if provided, but do NOT reset the count.
            if (targetOverride != null && targetOverride > 0) {
                counterDataStore.setDhikrKeyWithTarget(key, targetOverride)
            }
        }
    }

    override suspend fun setStepIndex(index: Int) = withContext(ioDispatcher) {
        counterDataStore.setStepIndex(index)
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
     * Executes the actual disk writes for the accumulated counts.
     * Safely runs in IO dispatcher via applicationScope.
     */
    override suspend fun flushToDisk() = withContext(ioDispatcher) {
        val currentCount = _activeCount.value
        val active = activeDhikr.value
        val date = lastKnownDate
        
        counterDataStore.setCounter(currentCount)

        sakinahDao.insertDailyTarget(
            DailyTargetEntity(
                id           = "${active.key}_$date",
                date         = date,
                dhikrType    = active.key,
                currentCount = currentCount,
                targetCount  = active.target,
                isCompleted  = currentCount >= active.target
            )
        )
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
        // Ensure disk is fully synchronized before completing target
        flushToDisk()
        
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
