package com.deencompanion.app.data.remote.api



import com.deencompanion.app.data.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v6/latest/{base}")
    suspend fun getRates(@Path("base") base: String): Response<ExchangeRateResponse>
}