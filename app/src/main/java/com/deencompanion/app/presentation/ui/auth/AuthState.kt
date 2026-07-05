package com.deencompanion.app.presentation.ui.auth

/**
 * PasswordStrength describes the calculated complexity level of the input password.
 */
enum class PasswordStrength {
    NONE,
    WEAK,
    MEDIUM,
    STRONG
}

/**
 * LEARNING NOTE:
 * AuthFormState tracks the localized state of fields inside registration, login, and forgot password forms.
 * Field validation errors are updated dynamically to show real-time error borders and warnings.
 */
data class AuthFormState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val nameError: String? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE,
    val pwHasMinLength: Boolean = false,
    val pwHasCapitalLetter: Boolean = false,
    val pwHasNumber: Boolean = false,
    val pwHasSpecialChar: Boolean = false
)
