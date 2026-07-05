package com.deencompanion.app.domain.usecase


import com.deencompanion.app.domain.repository.QazaNamazRepository
import javax.inject.Inject

class MarkQazaCompletedUseCase @Inject constructor(private val repository: QazaNamazRepository) {
    suspend operator fun invoke(prayerType: String, amount: Int) {
        repository.markCompleted(prayerType, amount)
    }
}