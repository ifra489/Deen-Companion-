package com.deencompanion.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_records",
    indices = [Index(value = ["date", "prayerName"], unique = true)]
)
data class PrayerRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // format "yyyy-MM-dd"
    val prayerName: String, // Fajr, Dhuhr, Asr, Maghrib, Isha
    val isPrayed: Boolean,
    val prayedAtTimestamp: Long?
)
