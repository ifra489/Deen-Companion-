package com.deencompanion.app.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qaza_prayers")
data class QazaPrayerEntity(
    @PrimaryKey val prayerType: String, // "Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"
    val totalMissed: Int,
    val completedCount: Int
)