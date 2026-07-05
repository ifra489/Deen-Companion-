package com.deencompanion.app.data.remote.api

import com.deencompanion.app.data.model.AyahResponse
import retrofit2.Response
import retrofit2.http.GET
import com.deencompanion.app.data.model.AudioSurahResponse
import retrofit2.http.Path
import com.deencompanion.app.data.model.SurahEditionsResponse

interface QuranApi {

    @GET("v1/ayah/random/editions/quran-simple,en.asad,ur.maududi")
    suspend fun getRandomAyah(): Response<AyahResponse>

    // ADD THIS: Fetch Surah Editions (Arabic + Urdu + English + Hindi)
    @GET("v1/surah/{surahNumber}/editions/quran-simple,ur.maududi,en.asad,hi.hindi")
    suspend fun getSurahEditions(
        @Path("surahNumber") surahNumber: Int
    ): Response<SurahEditionsResponse>

    // ADD THIS: Fetch Audio URLs (Alafasy Recitation)
    @GET("v1/surah/{surahNumber}/ar.alafasy")
    suspend fun getSurahAudio(
        @Path("surahNumber") surahNumber: Int
    ): Response<AudioSurahResponse>
}