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
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Foreground service that intercepts hardware volume keys to act as a
 * eyes-free counter while the screen is off (Pocket Mode).
 *
 * Architecture:
 * - Uses MediaSessionCompat + VolumeProviderCompat(VOLUME_CONTROL_RELATIVE) to
 *   intercept volume events WITHOUT Accessibility Service (as per PRD §3B).
 * - Both Vol Up and Vol Down increment the counter — this is intentional so
 *   users can count with either thumb regardless of phone orientation.
 * - Haptic feedback fires synchronously; repository write is async via coroutine.
 * - SRP: this service owns ONLY the volume-key-to-increment bridge. All state
 *   management remains in TasbihViewModel/TasbihRepository.
 */
@AndroidEntryPoint
class PocketModeService : Service() {

    @Inject lateinit var hapticEngine: HapticEngine
    @Inject lateinit var repository: TasbihRepository

    private var mediaSession: MediaSessionCompat? = null
    private val channelId = "pocket_mode_channel"
    private val notificationId = 1001

    // Scoped to the service lifetime; cancelled in onDestroy()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sabeel — Tasbih Active")
            .setContentText("Volume keys count · Screen can be off")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "PocketModeSession").apply {
            val volumeProvider = object : VolumeProviderCompat(
                VOLUME_CONTROL_RELATIVE,
                100,
                50
            ) {
                override fun onAdjustVolume(direction: Int) {
                    // Both Vol Up (+1) and Vol Down (-1) increment the counter.
                    // This is intentional: either thumb, any orientation, counts.
                    if (direction == 1 || direction == -1) {
                        // Fire haptic synchronously — user expects immediate tactile feedback
                        hapticEngine.playIncrementTick()
                        // Persist increment asynchronously — never block the haptic thread
                        serviceScope.launch {
                            repository.incrementCount(LocalDate.now().toString())
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
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
