package com.deencompanion.app.util

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * LEARNING NOTE:
 * This file contains Kotlin extension functions that add new functionality to existing classes without modifying them.
 * For example, Context.showToast allows calling toast display directly, and String.toArabicNumerals converts digit characters.
 * These clean up boilerplate code and make the code more readable and expressive in other parts of the application.
 */

/**
 * Shows a standard Toast notification.
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Converts English digits (0-9) inside a string to Eastern Arabic numerals (٠-٩).
 * Useful for rendering Quranic verse numbers and Arabic text.
 */
fun String.toArabicNumerals(): String {
    val arabicChars = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val builder = StringBuilder()
    for (char in this) {
        if (char in '0'..'9') {
            builder.append(arabicChars[char - '0'])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}

/**
 * Formats a millisecond timestamp into a human-readable date string.
 */
fun Long.toFormattedDate(pattern: String = "dd MMM yyyy"): String {
    val date = Date(this)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(date)
}

/**
 * Converts an index range (0-4) to its corresponding Islamic daily prayer name.
 */
fun Int.toPrayerName(): String {
    return when (this) {
        0 -> "Fajr"
        1 -> "Dhuhr"
        2 -> "Asr"
        3 -> "Maghrib"
        4 -> "Isha"
        else -> "Sunrise" // Fallback / supplementary prayer timing
    }
}
