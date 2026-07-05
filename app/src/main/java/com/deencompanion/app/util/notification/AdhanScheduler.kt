package com.deencompanion.app.util.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.deencompanion.app.domain.model.PrayerTimes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdhanScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val prefs = context.getSharedPreferences("adhan_scheduler_prefs", Context.MODE_PRIVATE)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    companion object {
        private const val TAG = "AdhanScheduler"
        val PRAYER_CODES = mapOf(
            "Fajr" to 100,
            "Dhuhr" to 101,
            "Asr" to 102,
            "Maghrib" to 103,
            "Isha" to 104
        )
    }

    fun scheduleAllPrayerAlarms(prayerTimes: PrayerTimes) {
        // Save times for rescheduling later
        prefs.edit().apply {
            putString("Fajr", prayerTimes.fajr)
            putString("Dhuhr", prayerTimes.dhuhr)
            putString("Asr", prayerTimes.asr)
            putString("Maghrib", prayerTimes.maghrib)
            putString("Isha", prayerTimes.isha)
            apply()
        }

        scheduleAlarm("Fajr", prayerTimes.fajr)
        scheduleAlarm("Dhuhr", prayerTimes.dhuhr)
        scheduleAlarm("Asr", prayerTimes.asr)
        scheduleAlarm("Maghrib", prayerTimes.maghrib)
        scheduleAlarm("Isha", prayerTimes.isha)
    }

    private fun scheduleAlarm(prayerName: String, timeStr: String) {
        val requestCode = PRAYER_CODES[prayerName] ?: return
        try {
            val localTime = LocalTime.parse(timeStr, timeFormatter)
            val today = LocalDate.now()
            var alarmDateTime = LocalDateTime.of(today, localTime)

            // If time has already passed today, schedule for tomorrow
            if (alarmDateTime.isBefore(LocalDateTime.now())) {
                alarmDateTime = alarmDateTime.plusDays(1)
            }

            val epochMillis = alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val intent = Intent(context, AdhanAlarmReceiver::class.java).apply {
                putExtra("prayer_name", prayerName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    epochMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm for $prayerName at $alarmDateTime (epoch: $epochMillis)")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: exact alarm permission not granted", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm for $prayerName", e)
        }
    }

    fun scheduleTomorrowAlarm(prayerName: String) {
        val timeStr = prefs.getString(prayerName, null) ?: return
        val requestCode = PRAYER_CODES[prayerName] ?: return
        try {
            val localTime = LocalTime.parse(timeStr, timeFormatter)
            val tomorrow = LocalDate.now().plusDays(1)
            val alarmDateTime = LocalDateTime.of(tomorrow, localTime)
            val epochMillis = alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val intent = Intent(context, AdhanAlarmReceiver::class.java).apply {
                putExtra("prayer_name", prayerName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    epochMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled tomorrow's alarm for $prayerName at $alarmDateTime")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: exact alarm permission not granted", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule tomorrow's alarm for $prayerName", e)
        }
    }

    fun cancelAllAlarms() {
        for ((prayerName, requestCode) in PRAYER_CODES) {
            val intent = Intent(context, AdhanAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d(TAG, "Cancelled alarm for $prayerName")
            }
        }
    }
}
