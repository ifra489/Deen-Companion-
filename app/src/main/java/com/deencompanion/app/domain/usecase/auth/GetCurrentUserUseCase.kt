package com.deencompanion.app.domain.usecase.auth

import com.deencompanion.app.domain.model.User
import com.deencompanion.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * GetCurrentUserUseCase retrieves the currently authenticated user session,
 * mapping the raw FirebaseUser SDK model to the clean domain User model.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): User? {
        val firebaseUser = repository.getCurrentUser() ?: return null
        return User(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: if (firebaseUser.isAnonymous) "Guest" else "",
            email = firebaseUser.email ?: "",
            isGuest = firebaseUser.isAnonymous,
            createdAt = firebaseUser.metadata?.creationTimestamp ?: 0L
        )
    }
}
