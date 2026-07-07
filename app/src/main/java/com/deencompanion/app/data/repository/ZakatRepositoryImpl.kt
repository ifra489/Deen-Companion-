package com.deencompanion.app.data.repository


import com.deencompanion.app.data.remote.api.ExchangeRateApi
import com.deencompanion.app.data.remote.api.GoldPriceApi
import com.deencompanion.app.domain.model.ZakatRates
import com.deencompanion.app.domain.repository.ZakatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZakatRepositoryImpl @Inject constructor(
    private val goldPriceApi: GoldPriceApi,
    private val exchangeRateApi: ExchangeRateApi
) : ZakatRepository {

    private val gramsPerTroyOunce = 31.1035

    override suspend fun getCurrentRates(): Result<ZakatRates> {
        return try {
            val goldResponse = goldPriceApi.getMetalPrice("XAU")
            val silverResponse = goldPriceApi.getMetalPrice("XAG")
            val rateResponse = exchangeRateApi.getRates("USD")

            if (!goldResponse.isSuccessful || !silverResponse.isSuccessful || !rateResponse.isSuccessful) {
                return Result.failure(Exception("Failed to fetch live rates. Please check your internet connection."))
            }

            val goldPriceUsdPerOz = goldResponse.body()?.price
                ?: return Result.failure(Exception("Gold price unavailable"))
            val silverPriceUsdPerOz = silverResponse.body()?.price
                ?: return Result.failure(Exception("Silver price unavailable"))
            val usdToPkr = rateResponse.body()?.rates?.get("PKR")
                ?: return Result.failure(Exception("Currency rate unavailable"))

            val goldPricePerGramPkr = (goldPriceUsdPerOz / gramsPerTroyOunce) * usdToPkr
            val silverPricePerGramPkr = (silverPriceUsdPerOz / gramsPerTroyOunce) * usdToPkr

            Result.success(ZakatRates(goldPricePerGramPkr, silverPricePerGramPkr))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}