package com.deencompanion.app.domain.usecase.auth

import com.deencompanion.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * GoogleSignInUseCase coordinates authenticating a user with Google credentials.
 */
class GoogleSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<FirebaseUser> {
        return repository.loginWithGoogle(idToken)
    }
}
