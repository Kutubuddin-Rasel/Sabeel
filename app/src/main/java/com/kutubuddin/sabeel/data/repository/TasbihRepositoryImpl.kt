package com.kutubuddin.sabeel.data.repository

import com.kutubuddin.sabeel.data.local.datastore.CounterDataStore
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import com.kutubuddin.sabeel.data.local.db.entity.DailyTargetEntity
import com.kutubuddin.sabeel.data.local.db.entity.StreakEntity
import com.kutubuddin.sabeel.di.IoDispatcher
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasbihRepositoryImpl @Inject constructor(
    private val counterDataStore: CounterDataStore,
    private val sakinahDao: SakinahDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TasbihRepository {

    override val activeCount: Flow<Int> = counterDataStore.counterValueFlow
    override val activeDhikr: Flow<DhikrType> = counterDataStore.activeDhikrFlow
    override val isSmartFlowEnabled: Flow<Boolean> = counterDataStore.isSmartFlowEnabledFlow
    override val isPocketModeActive: Flow<Boolean> = counterDataStore.isPocketModeActiveFlow

    override val streak: Flow<StreakEntity?> = sakinahDao.getStreakFlow("current_streak")
        .flowOn(ioDispatcher)

    override fun getDailyTargetFlow(date: String, dhikrType: DhikrType): Flow<DailyTargetEntity?> {
        return sakinahDao.getDailyTargetFlow("${dhikrType.name}_$date")
            .flowOn(ioDispatcher)
    }

    override suspend fun incrementCount(date: String): Int = withContext(ioDispatcher) {
        counterDataStore.incrementCounter()
        val currentCount = counterDataStore.counterValueFlow.first()
        val activeDhikr = counterDataStore.activeDhikrFlow.first()
        
        // Sync incremental state to SQLite without blocking UI
        sakinahDao.insertDailyTarget(
            DailyTargetEntity(
                id = "${activeDhikr.name}_$date",
                date = date,
                dhikrType = activeDhikr.name,
                currentCount = currentCount,
                targetCount = activeDhikr.defaultTarget,
                isCompleted = currentCount >= activeDhikr.defaultTarget
            )
        )
        currentCount
    }

    override suspend fun resetCount() = withContext(ioDispatcher) {
        counterDataStore.resetCounter()
    }

    override suspend fun setDhikr(dhikr: DhikrType) = withContext(ioDispatcher) {
        counterDataStore.setDhikr(dhikr)
        counterDataStore.resetCounter()
    }

    override suspend fun setSmartFlowEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        counterDataStore.setSmartFlowEnabled(enabled)
    }

    override suspend fun setPocketModeActive(active: Boolean) = withContext(ioDispatcher) {
        counterDataStore.setPocketModeActive(active)
    }

    override suspend fun completeDhikrTarget(date: String, dhikrType: DhikrType, targetCount: Int) = withContext(ioDispatcher) {
        val currentStreak = sakinahDao.getStreak("current_streak")
        val today = LocalDate.parse(date)
        
        val newStreakCount = if (currentStreak == null) {
            1
        } else {
            val lastActive = LocalDate.parse(currentStreak.lastActiveDate)
            val daysBetween = ChronoUnit.DAYS.between(lastActive, today)
            
            when {
                daysBetween == 0L -> currentStreak.count // Already completed a target today, streak remains same
                daysBetween == 1L -> currentStreak.count + 1 // Consecutive day, increment streak
                else -> 1 // Gap in activity, reset to 1
            }
        }
        
        val newLongest = currentStreak?.let {
            if (newStreakCount > it.longestStreak) newStreakCount else it.longestStreak
        } ?: newStreakCount

        sakinahDao.updateProgressAndStreak(
            date = date,
            progress = targetCount,
            target = targetCount,
            streakCount = newStreakCount,
            dhikrType = dhikrType.name,
            longestStreak = newLongest
        )
    }
}
