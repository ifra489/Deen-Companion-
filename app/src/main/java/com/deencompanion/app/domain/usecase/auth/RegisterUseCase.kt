package com.deencompanion.app.domain.usecase.auth

import com.deencompanion.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * RegisterUseCase coordinates the user registration flow using email/password.
 */
class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<FirebaseUser> {
        return repository.registerWithEmail(name, email, password)
    }
}
