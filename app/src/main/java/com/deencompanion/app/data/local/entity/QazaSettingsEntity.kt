package com.deencompanion.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qaza_settings")
data class QazaSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val currentAge: Int,
    val obligationAge: Int,
    val ageStartedPraying: Int,
    val calculatedAt: Long
)