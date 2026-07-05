package com.deencompanion.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deencompanion.app.data.local.entity.PrayerRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrayerRecord(record: PrayerRecordEntity): Long

    @Query("SELECT * FROM prayer_records WHERE date = :date")
    fun getPrayersForDate(date: String): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records WHERE date BETWEEN :startDate AND :endDate")
    fun getPrayersBetweenDates(startDate: String, endDate: String): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records")
    fun getAllPrayerRecords(): Flow<List<PrayerRecordEntity>>

    @Query("SELECT COUNT(*) FROM prayer_records WHERE date = :date AND isPrayed = 1")
    suspend fun getPrayedCountForDate(date: String): Int
}
