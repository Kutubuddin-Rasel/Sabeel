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
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.VolumeProviderCompat
import com.kutubuddin.sabeel.MainActivity
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.domain.repository.TasbihCounterMutator
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
 * ISP + DIP: depends on [TasbihCounterMutator] — the ONE method this class
 * calls is [TasbihCounterMutator.incrementCount] — never the full
 * [com.kutubuddin.sabeel.domain.repository.TasbihRepository]. A foreground
 * service bridging volume keys has no business depending on Smart Flow
 * config, Pocket Mode config, or streak reads.
 */
@AndroidEntryPoint
class PocketModeService : Service() {

    @Inject lateinit var hapticEngine: HapticEngine
    @Inject lateinit var counterMutator: TasbihCounterMutator

    private var mediaSession: MediaSessionCompat? = null
    private val channelId = "pocket_mode_channel"
    private val notificationId = 1001

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
        // BATT-03: START_NOT_STICKY — Pocket Mode is user-initiated.
        // If the process is killed mid-session, the service should NOT auto-restart;
        // the user re-enables Pocket Mode explicitly. START_STICKY would cause a ghost
        // foreground service to restart while isPocketModeActive=true lingers in DataStore.
        return START_NOT_STICKY
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
        if (mediaSession != null) return   // LEAK-03: idempotency guard — prevents double-registration on START_STICKY restart
        mediaSession = MediaSessionCompat(this, "PocketModeSession").apply {
            val volumeProvider = object : VolumeProviderCompat(
                VOLUME_CONTROL_RELATIVE,
                100,
                50
            ) {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onAdjustVolume(direction: Int) {
                    if (direction == 1 || direction == -1) {
                        hapticEngine.playIncrementTick()
                        serviceScope.launch {
                            counterMutator.incrementCount(LocalDate.now().toString())
                        }
                    }
                }
            }
            setPlaybackToRemote(volumeProvider)
            isActive = true
        }
    }

    override fun onDestroy() {
        // LEAK-03: null the field first so any in-flight onAdjustVolume callbacks
        // that read `mediaSession` see null and skip — prevents access to a
        // deactivating session during the isActive=false → release() window.
        val session = mediaSession
        mediaSession = null
        session?.isActive = false
        session?.release()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}