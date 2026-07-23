package com.deencompanion.app.data.repository

import com.deencompanion.app.data.local.dao.PrayerRecordDao
import com.deencompanion.app.data.local.entity.PrayerRecordEntity
import com.deencompanion.app.domain.model.PrayerRecord
import com.deencompanion.app.domain.repository.PrayerTrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTrackingRepositoryImpl @Inject constructor(
    private val dao: PrayerRecordDao
) : PrayerTrackingRepository {
    
    private val defaultPrayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

    override suspend fun markPrayerStatus(date: String, prayerName: String, isPrayed: Boolean) {
        val timestamp = if (isPrayed) System.currentTimeMillis() else null
        val record = PrayerRecordEntity(
            date = date,
            prayerName = prayerName,
            isPrayed = isPrayed,
            prayedAtTimestamp = timestamp
        )
        dao.upsertPrayerRecord(record)
    }

    override fun getTodayPrayerStatus(date: String): Flow<List<PrayerRecord>> {
        return dao.getPrayersForDate(date).map { entities ->
            entities.map { entity ->
                PrayerRecord(
                    date = entity.date,
                    prayerName = entity.prayerName,
                    isPrayed = entity.isPrayed,
                    prayedAtTimestamp = entity.prayedAtTimestamp
                )
            }
        }
    }

    override fun getPrayerHistory(startDate: String, endDate: String): Flow<List<PrayerRecord>> {
        return dao.getPrayersBetweenDates(startDate, endDate).map { entities ->
            entities.map { entity ->
                PrayerRecord(
                    date = entity.date,
                    prayerName = entity.prayerName,
                    isPrayed = entity.isPrayed,
                    prayedAtTimestamp = entity.prayedAtTimestamp
                )
            }
        }
    }

    override suspend fun getPrayedCountForDate(date: String): Int {
        return dao.getPrayedCountForDate(date)
    }

    override suspend fun getWeeklyProgress(): List<com.deencompanion.app.domain.model.DailyProgress> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(6)
        val records = dao.getPrayersBetweenDates(startDate.toString(), endDate.toString()).first()
        
        val grouped = records.groupBy { it.date }
        
        return (0..6).map { i ->
            val date = startDate.plusDays(i.toLong())
            val count = grouped[date.toString()]?.count { it.isPrayed } ?: 0
            com.deencompanion.app.domain.model.DailyProgress(date, count)
        }
    }

    override suspend fun getCurrentStreak(): Int {
        val allRecords = dao.getAllPrayerRecords().first()
        if (allRecords.isEmpty()) return 0

        val groupedByDate = allRecords.groupBy { it.date }
        
        fun isDateComplete(date: LocalDate): Boolean {
            val dateStr = date.toString()
            val dayRecords = groupedByDate[dateStr] ?: return false
            val prayedSet = dayRecords.filter { it.isPrayed }.map { it.prayerName }.toSet()
            return defaultPrayers.all { it in prayedSet }
        }

        val today = LocalDate.now()
        var streak = 0
        var currentDate = if (isDateComplete(today)) today else today.minusDays(1)

        while (isDateComplete(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }

        return streak
    }
}