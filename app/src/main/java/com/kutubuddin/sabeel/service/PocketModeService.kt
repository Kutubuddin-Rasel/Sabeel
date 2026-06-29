package com.kutubuddin.sabeel.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.VolumeProviderCompat
import com.kutubuddin.sabeel.MainActivity
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PocketModeService : Service() {

    @Inject
    lateinit var hapticEngine: HapticEngine

    private var mediaSession: MediaSessionCompat? = null
    private val channelId = "pocket_mode_channel"
    private val notificationId = 1001

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notificationId, 
                notification, 
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(notificationId, notification)
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Pocket Mode Active",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps eyes-free counter volume controls active when screen is off"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.testClass())
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sabeel Pocket Mode")
            .setContentText("Volume keys act as counters while screen is off")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "PocketModeSession").apply {
            val volumeProvider = object : VolumeProviderCompat(
                VOLUME_CONTROL_RELATIVE,
                100, // Max volume limit representation
                50   // Current virtual level
            ) {
                override fun onAdjustVolume(direction: Int) {
                    when (direction) {
                        1 -> { // Volume Up click
                            hapticEngine.playIncrementTick()
                        }
                        -1 -> { // Volume Down click
                            hapticEngine.playMilestoneClick()
                        }
                    }
                }
            }
            setPlaybackToRemote(volumeProvider)
            isActive = true
        }
    }

    override fun onDestroy() {
        mediaSession?.apply {
            isActive = false
            release()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

private fun kotlin.reflect.KClass<*>.testClass(): Class<*> {
    return try {
        Class.forName("com.kutubuddin.sabeel.MainActivity")
    } catch (e: Exception) {
        java
    }
}
