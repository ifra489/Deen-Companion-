package com.deencompanion.app.domain.usecase

import com.deencompanion.app.domain.repository.QazaNamazRepository
import javax.inject.Inject

class CalculateQazaNamazUseCase @Inject constructor(private val repository: QazaNamazRepository) {
    suspend operator fun invoke(currentAge: Int, obligationAge: Int, ageStartedPraying: Int) {
        repository.calculateAndSave(currentAge, obligationAge, ageStartedPraying)
    }
}