package com.deencompanion.app.domain.usecase.auth

import com.deencompanion.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * LogoutUseCase handles clearing the active authentication session.
 */
class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() {
        repository.logout()
    }
}
