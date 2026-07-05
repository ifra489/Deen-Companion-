package com.deencompanion.app.domain.usecase.prayer



import com.deencompanion.app.domain.repository.TasbeehRepository
import javax.inject.Inject

class UpdateTasbeehTargetUseCase @Inject constructor(
    private val repository: TasbeehRepository
) {
    suspend operator fun invoke(id: String, target: Int) {
        repository.updateTargetCount(id, target)
    }
}