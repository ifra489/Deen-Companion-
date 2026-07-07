package com.deencompanion.app.domain.repository


import com.deencompanion.app.domain.model.ZakatRates

interface ZakatRepository {
    suspend fun getCurrentRates(): Result<ZakatRates>
}