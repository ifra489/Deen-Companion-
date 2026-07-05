package com.deencompanion.app.domain.usecase.auth

import com.deencompanion.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * LinkGuestAccountUseCase upgrades a guest account to a permanent email/password account.
 */
class LinkGuestAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        return repository.linkGuestToEmail(email, password)
    }
}
