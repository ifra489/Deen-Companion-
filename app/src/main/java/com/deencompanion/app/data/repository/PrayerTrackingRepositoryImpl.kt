package com.deencompanion.app.data.repository

import com.deencompanion.app.data.local.dao.PrayerRecordDao
import com.deencompanion.app.data.local.entity.PrayerRecordEntity
import com.deencompanion.app.domain.model.PrayerRecord
import com.deencompanion.app.domain.repository.PrayerTrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTrackingRepositoryImpl @Inject constructor(
    private val dao: PrayerRecordDao
) : PrayerTrackingRepository {

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
}