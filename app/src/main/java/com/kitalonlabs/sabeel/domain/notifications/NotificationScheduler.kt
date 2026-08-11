package com.kitalonlabs.sabeel.domain.notifications

interface NotificationScheduler {
    /**
     * Schedules the daily reminder to run at the specified time (HH:mm).
     * If a reminder is already scheduled, it is updated/replaced.
     */
    fun scheduleDailyReminder(timeHHmm: String)

    /**
     * Cancels any pending daily reminders.
     */
    fun cancelDailyReminder()
}
