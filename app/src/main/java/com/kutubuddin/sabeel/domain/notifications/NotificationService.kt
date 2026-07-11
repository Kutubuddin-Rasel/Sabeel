package com.kutubuddin.sabeel.domain.notifications

interface NotificationService {
    /**
     * Shows a notification designed to protect a user's streak.
     * Tapping it opens the Home screen.
     */
    fun showStreakProtectorNotification(streakDays: Int)

    /**
     * Shows a notification designed to help the user finish their daily goal.
     * Tapping it deep links directly to the specific Dhikr in the count screen.
     */
    fun showDailyGoalFinisherNotification(dhikrName: String, remainingTarget: Int, dhikrKey: String)

    /**
     * Shows a gentle nudge to a new user who has an incomplete daily goal.
     * Tapping it opens the Home screen.
     */
    fun showGentleNudgeNotification()
}
