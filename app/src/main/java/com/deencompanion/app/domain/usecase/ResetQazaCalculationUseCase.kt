package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.repository.QazaNamazRepository
import javax.inject.Inject

class ResetQazaCalculationUseCase @Inject constructor(private val repository: QazaNamazRepository) {
    suspend operator fun invoke() = repository.resetCalculation()
}