package com.kutubuddin.sabeel.data.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kutubuddin.sabeel.domain.notifications.NotificationService
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.StreakObserver
import com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val observeWirdProgress: ObserveWirdProgress,
    private val sessionRepository: SessionRepository,
    private val streakObserver: StreakObserver,
    private val settingsRepository: SettingsRepository,
    private val notificationService: NotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val remindersEnabled = settingsRepository.dailyReminderEnabled.first()
        if (!remindersEnabled) {
            return Result.success()
        }

        val today = LocalDate.now().toString()
        val countToday = sessionRepository.getTotalCountForDate(today).first()
        val currentStreak = streakObserver.streak.first()?.count ?: 0
        
        val wirdProgress = observeWirdProgress(today).first()
        val isDailyGoalComplete = wirdProgress.allComplete
        val hasDailyGoal = wirdProgress.total > 0

        // Priority 1: Streak Protector
        if (countToday == 0 && currentStreak >= 3) {
            notificationService.showStreakProtectorNotification(currentStreak)
            return Result.success()
        }

        // Priority 2: Goal Finisher
        if (countToday > 0 && hasDailyGoal && !isDailyGoalComplete) {
            val nextIncompleteItem = wirdProgress.nextIncompleteItem
            if (nextIncompleteItem != null) {
                // Determine missing target by subtracting recorded counts for that dhikr today
                val completedCount = sessionRepository.getCountsByKeyForDate(today).first()[nextIncompleteItem.dhikrKey] ?: 0
                val remainingTarget = maxOf(0, nextIncompleteItem.target - completedCount)
                
                val lang = settingsRepository.language.first()
                notificationService.showDailyGoalFinisherNotification(
                    dhikrName = nextIncompleteItem.displayName.get(lang),
                    remainingTarget = remainingTarget,
                    dhikrKey = nextIncompleteItem.dhikrKey
                )
            }
            return Result.success()
        }

        // Priority 3: Gentle Nudge
        if (countToday == 0 && currentStreak < 3 && hasDailyGoal) {
            notificationService.showGentleNudgeNotification()
            return Result.success()
        }

        return Result.success()
    }
}
