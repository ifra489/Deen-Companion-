package com.deencompanion.app.domain.usecase.auth

import javax.inject.Inject

/**
 * LEARNING NOTE:
 * AuthUseCases wraps all individual authentication business rules in a single container.
 * This simplifies Hilt injection inside ViewModels, as they only need to inject this wrapper
 * rather than 10 separate use-case classes.
 */
data class AuthUseCases @Inject constructor(
    val login: LoginUseCase,
    val register: RegisterUseCase,
    val googleSignIn: GoogleSignInUseCase,
    val guestSignIn: GuestSignInUseCase,
    val linkGuestAccount: LinkGuestAccountUseCase,
    val resetPassword: ResetPasswordUseCase,
    val getCurrentUser: GetCurrentUserUseCase,
    val logout: LogoutUseCase,
    val validateEmail: ValidateEmailUseCase,
    val validatePassword: ValidatePasswordUseCase
)
