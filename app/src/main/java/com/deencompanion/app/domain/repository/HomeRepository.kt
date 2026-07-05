package com.deencompanion.app.domain.repository

import android.content.Context
import com.deencompanion.app.domain.model.Ayah
import com.deencompanion.app.domain.model.DailyDua
import com.deencompanion.app.domain.model.DailyHadith
import com.deencompanion.app.domain.model.HijriDate
import com.deencompanion.app.domain.model.PrayerTimes
import com.deencompanion.app.util.UiState

interface HomeRepository {
    suspend fun getPrayerTimes(city: String, country: String): UiState<PrayerTimes>
    suspend fun getHijriDate(): UiState<HijriDate>
    suspend fun getDailyAyah(): UiState<Ayah>
    fun getDailyHadith(context: Context): DailyHadith
    fun getDailyDua(context: Context): DailyDua
}
