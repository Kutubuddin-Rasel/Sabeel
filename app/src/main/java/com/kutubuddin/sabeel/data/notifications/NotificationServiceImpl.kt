package com.kutubuddin.sabeel.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.kutubuddin.sabeel.MainActivity
import com.kutubuddin.sabeel.R
import com.kutubuddin.sabeel.domain.notifications.NotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationService {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for streaks and daily goals"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showStreakProtectorNotification(streakDays: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_STREAK,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (streakDays >= 7) "A full week of consistency! 💎" else "Keep your momentum! 🔥"
        val message = "Take 2 minutes to protect your $streakDays-day streak."

        showNotification(NOTIFICATION_ID_STREAK, title, message, pendingIntent)
    }

    override fun showDailyGoalFinisherNotification(dhikrName: String, remainingTarget: Int, dhikrKey: String) {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("sabeel://count?dhikrKey=$dhikrKey&target=$remainingTarget"),
            context,
            MainActivity::class.java
        )

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                REQUEST_CODE_GOAL,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val title = "Almost there! 🎯"
        val message = "You're just $remainingTarget $dhikrName away from finishing today's goal."

        showNotification(NOTIFICATION_ID_GOAL, title, message, pendingIntent)
    }

    override fun showGentleNudgeNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_NUDGE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Evening reminder 🌙"
        val message = "Take a moment for your Daily Goal."

        showNotification(NOTIFICATION_ID_NUDGE, title, message, pendingIntent)
    }

    private fun showNotification(id: Int, title: String, message: String, pendingIntent: PendingIntent?) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Using launcher icon temporarily
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

        notificationManager.notify(id, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "sabeel_reminders"
        private const val NOTIFICATION_ID_STREAK = 1001
        private const val NOTIFICATION_ID_GOAL = 1002
        private const val NOTIFICATION_ID_NUDGE = 1003

        private const val REQUEST_CODE_STREAK = 2001
        private const val REQUEST_CODE_GOAL = 2002
        private const val REQUEST_CODE_NUDGE = 2003
    }
}
