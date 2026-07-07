package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.ZakatRates
import com.deencompanion.app.domain.repository.ZakatRepository
import javax.inject.Inject

class GetZakatRatesUseCase @Inject constructor(
    private val repository: ZakatRepository
) {
    suspend operator fun invoke(): Result<ZakatRates> = repository.getCurrentRates()
}