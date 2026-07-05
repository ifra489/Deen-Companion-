package com.deencompanion.app.data.model

import com.google.gson.annotations.SerializedName

data class PrayerTimesResponse(
    val code: Int,
    val status: String,
    val data: PrayerTimesData?
)

data class PrayerTimesData(
    val timings: Timings?
)

data class Timings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String
)
