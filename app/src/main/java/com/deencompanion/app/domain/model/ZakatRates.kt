package com.deencompanion.app.domain.model



data class ZakatRates(
    val goldPricePerGramPkr: Double,
    val silverPricePerGramPkr: Double
)

data class ZakatInput(
    val cashAndBank: Double,
    val goldGrams: Double,
    val silverGrams: Double,
    val businessAssets: Double,
    val liabilities: Double,
    val nisabBasis: NisabBasis
)

enum class NisabBasis { GOLD, SILVER }

data class ZakatResult(
    val totalWealth: Double,
    val nisabThreshold: Double,
    val isZakatObligatory: Boolean,
    val zakatDue: Double
)