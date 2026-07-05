package com.deencompanion.app.domain.usecase.auth

import javax.inject.Inject

/**
 * ValidateEmailUseCase provides business logic for verifying that an email is in a valid format.
 * Using a standard regex ensures it is testable under pure JVM tests without relying on android.util.Patterns.
 */
class ValidateEmailUseCase @Inject constructor() {
    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

    operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be blank"))
        }
        if (!emailRegex.matches(email)) {
            return Result.failure(Exception("Invalid email format"))
        }
        return Result.success(Unit)
    }
}
