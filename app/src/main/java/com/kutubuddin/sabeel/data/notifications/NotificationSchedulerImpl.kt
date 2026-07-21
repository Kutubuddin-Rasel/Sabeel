package com.kutubuddin.sabeel.data.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kutubuddin.sabeel.domain.notifications.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleDailyReminder(timeHHmm: String) {
        val delay = calculateInitialDelay(timeHHmm)

        val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG_DAILY_REMINDER)
            .build()

        workManager.enqueueUniqueWork(
            WORK_TAG_DAILY_REMINDER,
            ExistingWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }

    override fun cancelDailyReminder() {
        workManager.cancelUniqueWork(WORK_TAG_DAILY_REMINDER)
    }

    private fun calculateInitialDelay(timeHHmm: String): Duration {
        val parts = timeHHmm.split(":")
        val hour = parts[0].toIntOrNull() ?: 20
        val minute = parts[1].toIntOrNull() ?: 30

        val now = ZonedDateTime.now(ZoneId.systemDefault())
        var targetTime = now.with(LocalTime.of(hour, minute)).withSecond(0).withNano(0)

        if (now.isAfter(targetTime) || now.isEqual(targetTime)) {
            // Target time has passed for today, schedule for tomorrow
            targetTime = targetTime.plusDays(1)
        }

        return Duration.between(now, targetTime)
    }

    companion object {
        private const val WORK_TAG_DAILY_REMINDER = "sabeel_daily_reminder_work"
    }
}
