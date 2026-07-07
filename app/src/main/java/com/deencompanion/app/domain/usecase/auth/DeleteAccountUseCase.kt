package com.deencompanion.app.domain.usecase.auth



import com.deencompanion.app.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String): Result<Unit> {
        return repository.deleteAccount(currentPassword)
    }
}