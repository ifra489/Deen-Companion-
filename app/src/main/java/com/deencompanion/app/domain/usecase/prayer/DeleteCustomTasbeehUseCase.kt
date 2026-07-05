package com.deencompanion.app.domain.usecase.prayer



import com.deencompanion.app.domain.repository.TasbeehRepository
import javax.inject.Inject

class DeleteCustomTasbeehUseCase @Inject constructor(
    private val repository: TasbeehRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteCustomDhikr(id)
    }
}