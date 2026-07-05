package com.deencompanion.app.domain.usecase

import com.deencompanion.app.domain.repository.TasbeehRepository
import javax.inject.Inject

class ResetTasbeehUseCase @Inject constructor(
    private val repository: TasbeehRepository
) {
    suspend operator fun invoke(id: String) {
        repository.resetCount(id)
    }
}