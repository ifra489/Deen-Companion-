package com.deencompanion.app.domain.usecase.auth

import com.deencompanion.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * LoginUseCase coordinates the email/password login business flow.
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        return repository.loginWithEmail(email, password)
    }
}
