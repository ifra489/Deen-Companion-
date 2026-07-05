package com.deencompanion.app.data.remote.api



import com.deencompanion.app.data.model.WordByWordResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuranWordApi {
    @GET("verses/by_chapter/{chapterNumber}")
    suspend fun getWordByWordVerses(
        @Path("chapterNumber") chapterNumber: Int,
        @Query("language") language: String = "en",
        @Query("words") words: Boolean = true,
        @Query("word_fields") wordFields: String = "text_uthmani,translation",
        @Query("fields") fields: String = "text_uthmani",
        @Query("per_page") perPage: Int = 300
    ): Response<WordByWordResponse>
}