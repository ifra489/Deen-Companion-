package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.NisabBasis
import com.deencompanion.app.domain.model.ZakatInput
import com.deencompanion.app.domain.model.ZakatRates
import com.deencompanion.app.domain.model.ZakatResult
import javax.inject.Inject

class CalculateZakatUseCase @Inject constructor() {

    // Standard Nisab thresholds
    private val goldNisabGrams = 87.48
    private val silverNisabGrams = 612.36
    private val zakatRate = 0.025 // 2.5%

    operator fun invoke(input: ZakatInput, rates: ZakatRates): ZakatResult {
        val goldValue = input.goldGrams * rates.goldPricePerGramPkr
        val silverValue = input.silverGrams * rates.silverPricePerGramPkr

        val totalWealth = input.cashAndBank + goldValue + silverValue + input.businessAssets - input.liabilities

        val nisabThreshold = when (input.nisabBasis) {
            NisabBasis.GOLD -> goldNisabGrams * rates.goldPricePerGramPkr
            NisabBasis.SILVER -> silverNisabGrams * rates.silverPricePerGramPkr
        }

        val isObligatory = totalWealth >= nisabThreshold
        val zakatDue = if (isObligatory) totalWealth * zakatRate else 0.0

        return ZakatResult(
            totalWealth = totalWealth,
            nisabThreshold = nisabThreshold,
            isZakatObligatory = isObligatory,
            zakatDue = zakatDue
        )
    }
}