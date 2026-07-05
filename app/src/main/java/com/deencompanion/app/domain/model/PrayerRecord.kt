package com.deencompanion.app.domain.model

data class PrayerRecord(
    val date: String,
    val prayerName: String,
    val isPrayed: Boolean,
    val prayedAtTimestamp: Long?
)