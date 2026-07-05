package com.deencompanion.app.domain.usecase.auth

import javax.inject.Inject

/**
 * ValidatePasswordUseCase enforces password security rules:
 * - Minimum 8 characters
 * - At least 1 uppercase letter
 * - At least 1 digit
 * - At least 1 special character
 *
 * It returns a structured result so the UI can drive checklist items and a strength indicator.
 */
class ValidatePasswordUseCase @Inject constructor() {

    data class ValidationResult(
        val hasMinLength: Boolean,
        val hasCapitalLetter: Boolean,
        val hasNumber: Boolean,
        val hasSpecialChar: Boolean
    ) {
        val isValid: Boolean get() = hasMinLength && hasCapitalLetter && hasNumber && hasSpecialChar
        
        val errorMessage: String? get() {
            if (isValid) return null
            val errors = mutableListOf<String>()
            if (!hasMinLength) errors.add("at least 8 characters")
            if (!hasCapitalLetter) errors.add("1 capital letter")
            if (!hasNumber) errors.add("1 number")
            if (!hasSpecialChar) errors.add("1 special character")
            return "Must contain " + errors.joinToString(", ")
        }
    }

    operator fun invoke(password: String): ValidationResult {
        val hasMinLength = password.length >= 8
        val hasCapitalLetter = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }

        return ValidationResult(
            hasMinLength = hasMinLength,
            hasCapitalLetter = hasCapitalLetter,
            hasNumber = hasNumber,
            hasSpecialChar = hasSpecialChar
        )
    }
}
