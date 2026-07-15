

package com.deencompanion.app.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.deencompanion.app.R

class AdhanPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val CHANNEL_ID = "adhan_playing_channel"
        const val NOTIFICATION_ID = 5001
        const val ACTION_STOP = "com.deencompanion.app.ACTION_STOP_ADHAN"
        const val EXTRA_PRAYER_NAME = "prayer_name"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopAdhan()
            return START_NOT_STICKY
        }

        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer"
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(prayerName))
        playAdhan()

        return START_NOT_STICKY
    }

    private fun playAdhan() {
        try {
            Log.d("AdhanPlayerService", "Starting Adhan audio playback...")
            mediaPlayer?.release()
            
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                val afd = resources.openRawResourceFd(R.raw.adhan_sound)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setWakeMode(this@AdhanPlayerService, android.os.PowerManager.PARTIAL_WAKE_LOCK)
                prepare()
                setOnCompletionListener {
                    Log.d("AdhanPlayerService", "Adhan playback completed.")
                    stopAdhan()
                }
                start()
            }
            Log.d("AdhanPlayerService", "MediaPlayer started successfully.")
        } catch (e: Exception) {
            Log.e("AdhanPlayerService", "Error playing Adhan: ${e.message}", e)
            stopAdhan()
        }
    }

    private fun stopAdhan() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(prayerName: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Time for $prayerName")
            .setContentText("Adhan is playing...")
            .setSmallIcon(R.drawable.app_icon)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                0,
                "Stop",
                getStopPendingIntent()
            )
            .build()

    private fun getStopPendingIntent(): PendingIntent {
        val stopIntent = Intent(this, AdhanPlayerService::class.java).apply {
            action = ACTION_STOP
        }
        return PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Adhan Playing",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows when Adhan is currently playing"
                setSound(null, null) // MediaPlayer khud audio chala raha hai, notification sound alag se nahi chahiye
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}