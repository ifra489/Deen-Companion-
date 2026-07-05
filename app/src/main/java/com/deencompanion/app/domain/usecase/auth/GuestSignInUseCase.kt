package com.deencompanion.app.domain.usecase.auth

import com.deencompanion.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * GuestSignInUseCase coordinates anonymous user authentication.
 */
class GuestSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<FirebaseUser> {
        return repository.loginAsGuest()
    }
}
