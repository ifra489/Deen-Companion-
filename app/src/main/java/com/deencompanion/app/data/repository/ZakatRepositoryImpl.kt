package com.deencompanion.app.data.repository


import com.deencompanion.app.data.local.dao.OfflineCacheDao
import com.deencompanion.app.data.local.entity.OfflineCacheEntity
import com.deencompanion.app.data.remote.api.ExchangeRateApi
import com.deencompanion.app.data.remote.api.GoldPriceApi
import com.deencompanion.app.domain.model.ZakatRates
import com.deencompanion.app.domain.repository.ZakatRepository
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZakatRepositoryImpl @Inject constructor(
    private val goldPriceApi: GoldPriceApi,
    private val exchangeRateApi: ExchangeRateApi,
    private val offlineCacheDao: OfflineCacheDao,
    private val gson: Gson
) : ZakatRepository {

    private val gramsPerTroyOunce = 31.1035
    private val cacheKey = "zakat_rates"

    override suspend fun getCurrentRates(): Result<ZakatRates> {
        return try {
            val goldResponse = goldPriceApi.getMetalPrice("XAU")
            val silverResponse = goldPriceApi.getMetalPrice("XAG")
            val rateResponse = exchangeRateApi.getRates("USD")

            if (!goldResponse.isSuccessful || !silverResponse.isSuccessful || !rateResponse.isSuccessful) {
                return loadFromCache()
            }

            val goldPriceUsdPerOz = goldResponse.body()?.price
                ?: return loadFromCache()
            val silverPriceUsdPerOz = silverResponse.body()?.price
                ?: return loadFromCache()
            val usdToPkr = rateResponse.body()?.rates?.get("PKR")
                ?: return loadFromCache()

            val goldPricePerGramPkr = (goldPriceUsdPerOz / gramsPerTroyOunce) * usdToPkr
            val silverPricePerGramPkr = (silverPriceUsdPerOz / gramsPerTroyOunce) * usdToPkr

            val rates = ZakatRates(goldPricePerGramPkr, silverPricePerGramPkr)
            
            // Save to cache
            offlineCacheDao.insertOrUpdate(OfflineCacheEntity(cacheKey, gson.toJson(rates)))
            
            Result.success(rates)
        } catch (e: Exception) {
            loadFromCache()
        }
    }

    private suspend fun loadFromCache(): Result<ZakatRates> {
        val cached = offlineCacheDao.getCachedData(cacheKey)
        return if (cached != null) {
            Result.success(gson.fromJson(cached.jsonData, ZakatRates::class.java))
        } else {
            // Hardcoded fallback rates (approximate) so it works even if never connected
            // Gold: ~20,000 PKR/gram, Silver: ~250 PKR/gram
            val fallbackRates = ZakatRates(goldPricePerGramPkr = 21500.0, silverPricePerGramPkr = 260.0)
            Result.success(fallbackRates)
        }
    }
}