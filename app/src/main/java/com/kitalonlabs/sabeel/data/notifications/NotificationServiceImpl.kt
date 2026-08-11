package com.kitalonlabs.sabeel.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.kitalonlabs.sabeel.MainActivity
import com.kitalonlabs.sabeel.R
import com.kitalonlabs.sabeel.domain.model.LocalizedText
import com.kitalonlabs.sabeel.domain.notifications.NotificationService
import com.kitalonlabs.sabeel.ui.i18n.toLocalizedNumerals
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

    private object Copy {
        val streakTitle =
            LocalizedText(en = "Consistency Maintained", bn = "ধারাবাহিকতা বজায় রয়েছে")
        val streakMessage = LocalizedText(
            en = "Take a moment to protect your %1\$s-day streak.",
            bn = "আপনার %1\$s দিনের ধারাবাহিকতা রক্ষা করতে কিছুক্ষণ সময় নিন।"
        )
        
        val goalTitle = LocalizedText(en = "Daily Goal", bn = "দৈনিক লক্ষ্য")
        val goalMessage = LocalizedText(
            en = "You are %1\$s %2\$s away from finishing today's goal.",
            bn = "আজকের লক্ষ্য পূরণে আর মাত্র %1\$s বার %2\$s বাকি।"
        )
        
        val nudgeTitle = LocalizedText(en = "Evening Reflection", bn = "সন্ধ্যার স্মরণ")
        val nudgeMessage = LocalizedText(
            en = "Take a moment for your Daily Goal.",
            bn = "আপনার দৈনিক লক্ষ্যের জন্য কিছু সময় বের করুন।"
        )
    }

    override fun showStreakProtectorNotification(streakDays: Int, language: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_STREAK,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = Copy.streakTitle.get(language)
        val message = Copy.streakMessage.get(language).format(streakDays.toLocalizedNumerals(language))

        showNotification(NOTIFICATION_ID_STREAK, title, message, pendingIntent)
    }

    override fun showDailyGoalFinisherNotification(dhikrName: String, remainingTarget: Int, dhikrKey: String, language: String) {
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

        val title = Copy.goalTitle.get(language)
        val message = Copy.goalMessage.get(language).format(remainingTarget.toLocalizedNumerals(language), dhikrName)

        showNotification(NOTIFICATION_ID_GOAL, title, message, pendingIntent)
    }

    override fun showGentleNudgeNotification(language: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_NUDGE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = Copy.nudgeTitle.get(language)
        val message = Copy.nudgeMessage.get(language)

        showNotification(NOTIFICATION_ID_NUDGE, title, message, pendingIntent)
    }

    private fun showNotification(id: Int, title: String, message: String, pendingIntent: PendingIntent?) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
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
