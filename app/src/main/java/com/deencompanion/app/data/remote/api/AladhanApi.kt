package com.deencompanion.app.data.remote.api

import com.deencompanion.app.data.model.HijriDateResponse
import com.deencompanion.app.data.model.PrayerTimesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AladhanApi {

    @GET("v1/timingsByCity")
    suspend fun getPrayerTimes(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 1
    ): Response<PrayerTimesResponse>

    @GET("v1/gToH")
    suspend fun getHijriDate(
        @Query("date") date: String
    ): Response<HijriDateResponse>
}
