package com.deencompanion.app.util

import com.deencompanion.app.R

/**
 * LEARNING NOTE:
 * This object holds all project-wide static constant values, avoiding duplication and magic strings.
 * It centralizes values like database names, notification channel IDs, API URLs, and Quran constants.
 * Any layer in the application (data, domain, or presentation) can reference these values to ensure consistency.
 */
object Constants {
    const val APP_NAME = "Deen Companion"
    const val DATABASE_NAME = "deen_companion_db"
    
    // Notification Channels
    const val PRAYER_NOTIFICATION_CHANNEL_ID = "prayer_channel"
    const val CONTENT_NOTIFICATION_CHANNEL_ID = "content_channel"
    const val REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_channel"
    
    // DataStore Preferences
    const val DATASTORE_NAME = "deen_companion_prefs"
    
    // Google Sign-In Web Client ID reference
    val WEB_CLIENT_ID = R.string.google_web_client_id
    
    // Quran Audio Base URL (Sheikh Mishary Rashid Alafasy)
    const val MP3QURAN_BASE_URL = "https://server8.mp3quran.net/afs/"
    
    // Quran Metadata
    const val QURAN_TOTAL_AYAHS = 6236
    const val QURAN_TOTAL_SURAHS = 114
}
