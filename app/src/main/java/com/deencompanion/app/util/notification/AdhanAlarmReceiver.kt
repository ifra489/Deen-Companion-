package com.deencompanion.app.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdhanAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var adhanScheduler: AdhanScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: "Prayer"
        Log.d("AdhanAlarmReceiver", "Received alarm for: $prayerName")

        // Start the foreground service to play full Adhan audio with a Stop button
        val serviceIntent = Intent(context, AdhanPlayerService::class.java).apply {
            putExtra(AdhanPlayerService.EXTRA_PRAYER_NAME, prayerName)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Reschedule tomorrow's alarm for this prayer
        adhanScheduler.scheduleTomorrowAlarm(prayerName)
    }
}