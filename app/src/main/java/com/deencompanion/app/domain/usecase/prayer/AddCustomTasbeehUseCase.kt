package com.deencompanion.app.domain.usecase.prayer


import com.deencompanion.app.domain.repository.TasbeehRepository
import javax.inject.Inject

class AddCustomTasbeehUseCase @Inject constructor(
    private val repository: TasbeehRepository
) {
    suspend operator fun invoke(displayName: String, targetCount: Int = 33): String {
        return repository.addCustomDhikr(displayName, targetCount)
    }
}