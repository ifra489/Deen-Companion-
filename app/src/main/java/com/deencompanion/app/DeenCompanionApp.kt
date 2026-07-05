package com.deencompanion.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.deencompanion.app.util.Constants
import dagger.hilt.android.HiltAndroidApp

/**
 * LEARNING NOTE:
 * This is the Application subclass for Deen Companion, which acts as the entry point of the process.
 * It is annotated with @HiltAndroidApp, triggering Hilt's code generation for dependency injection.
 * In onCreate(), it registers notification channels (for Android 8.0+ / API 26+) for daily prayers,
 * spiritual reminders, and content notifications to ensure users receive timely notifications even when the app is backgrounded.
 */
@HiltAndroidApp
class DeenCompanionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // Notification channels are only required for Android Oreo (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Prayer Channel (HIGH importance, vibration and sound enabled for Adhan calls)
            val prayerChannel = NotificationChannel(
                Constants.PRAYER_NOTIFICATION_CHANNEL_ID,
                "Prayer Times & Adhan",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily notifications for prayer timings and Call to Prayer (Adhan)"
                enableVibration(true)
                
                // Configure default notification sound attributes
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(defaultSoundUri, audioAttributes)
            }

            // 2. Content Channel (DEFAULT importance for daily verses, Hadiths, Quran quotes)
            val contentChannel = NotificationChannel(
                Constants.CONTENT_NOTIFICATION_CHANNEL_ID,
                "Daily Content & Inspiration",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily Islamic quotes, Hadith of the day, and Quranic verse suggestions"
            }

            // 3. Reminder Channel (DEFAULT importance for personal habits, tasbeeh, and journal logs)
            val reminderChannel = NotificationChannel(
                Constants.REMINDER_NOTIFICATION_CHANNEL_ID,
                "Personal Goals & Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to complete daily goals, reflect in your journal, and update habit trackers"
            }

            // Register channels with system
            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(contentChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }
}
