package com.deencompanion.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * LEARNING NOTE:
 * This BroadcastReceiver listens for the system boot broadcast (ACTION_BOOT_COMPLETED).
 * When Android devices reboot, all pending alarms registered with AlarmManager are cleared.
 * This class will catch that broadcast and, in future phases, call the appropriate schedulers
 * (via WorkManager or Services) to rebuild the daily prayer alarms and goal reminder timers.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // LEARNING NOTE:
            // This is a placeholder log to verify the receiver works.
            // In future phases, we will inject a WorkManager task or AlarmScheduler helper
            // here to read prayer times from our local database and reschedule system alarms.
            Toast.makeText(
                context,
                "Deen Companion: System reboot detected. Rescheduling prayer times...",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}