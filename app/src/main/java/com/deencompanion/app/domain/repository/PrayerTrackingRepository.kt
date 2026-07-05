package com.deencompanion.app.domain.repository

import com.deencompanion.app.domain.model.PrayerRecord
import kotlinx.coroutines.flow.Flow

interface PrayerTrackingRepository {
    suspend fun markPrayerStatus(date: String, prayerName: String, isPrayed: Boolean)
    fun getTodayPrayerStatus(date: String): Flow<List<PrayerRecord>>
    fun getPrayerHistory(startDate: String, endDate: String): Flow<List<PrayerRecord>>
    suspend fun getPrayedCountForDate(date: String): Int
}