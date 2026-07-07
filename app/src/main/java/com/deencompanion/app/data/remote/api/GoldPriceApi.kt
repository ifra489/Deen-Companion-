package com.deencompanion.app.data.remote.api



import com.deencompanion.app.data.model.MetalPriceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GoldPriceApi {
    @GET("price/{symbol}")
    suspend fun getMetalPrice(@Path("symbol") symbol: String): Response<MetalPriceResponse>
}