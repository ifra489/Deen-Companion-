package com.deencompanion.app.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.User
import com.deencompanion.app.domain.usecase.auth.AuthUseCases
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LEARNING NOTE:
 * AuthViewModel manages state for Login, Registration, and Password Reset screens.
 * It exposes state flows for form input states and async network results.
 * It is annotated with @HiltViewModel, so Hilt handles injecting the AuthUseCases.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    private val _authResult = MutableStateFlow<UiState<User>>(UiState.Loading)
    val authResult: StateFlow<UiState<User>> = _authResult.asStateFlow()

    init {
        // Check if there is an active session on start to auto-login
        checkCurrentUser()
    }

    fun onEmailChanged(email: String) {
        _formState.update { state ->
            val error = if (email.isEmpty()) {
                null
            } else {
                authUseCases.validateEmail(email).exceptionOrNull()?.message
            }
            state.copy(email = email, emailError = error)
        }
    }
    private val _changePasswordResult = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val changePasswordResult: StateFlow<UiState<Unit>> = _changePasswordResult.asStateFlow()

    private val _deleteAccountResult = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val deleteAccountResult: StateFlow<UiState<Unit>> = _deleteAccountResult.asStateFlow()

    fun changePassword(currentPassword: String, newPassword: String) {
        _changePasswordResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.changePassword(currentPassword, newPassword)
                .onSuccess { _changePasswordResult.value = UiState.Success(Unit) }
                .onFailure { error -> _changePasswordResult.value = UiState.Error(error.message ?: "Failed to change password") }
        }
    }

    fun deleteAccount(currentPassword: String) {
        _deleteAccountResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.deleteAccount(currentPassword)
                .onSuccess {
                    _deleteAccountResult.value = UiState.Success(Unit)
                    _authResult.value = UiState.Empty
                }
                .onFailure { error -> _deleteAccountResult.value = UiState.Error(error.message ?: "Failed to delete account") }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordResult.value = UiState.Empty
    }

    fun resetDeleteAccountState() {
        _deleteAccountResult.value = UiState.Empty
    }

    fun sendPasswordResetLink(email: String) {
        _changePasswordResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.resetPassword(email)
                .onSuccess { _changePasswordResult.value = UiState.Success(Unit) }
                .onFailure { error -> _changePasswordResult.value = UiState.Error(error.message ?: "Failed to send reset link") }
        }
    }
    fun onPasswordChanged(password: String) {
        _formState.update { state ->
            val validation = authUseCases.validatePassword(password)
            val score = listOf(
                validation.hasMinLength,
                validation.hasCapitalLetter,
                validation.hasNumber,
                validation.hasSpecialChar
            ).count { it }

            val strength = when {
                password.isEmpty() -> PasswordStrength.NONE
                score <= 2 -> PasswordStrength.WEAK
                score == 3 -> PasswordStrength.MEDIUM
                else -> PasswordStrength.STRONG
            }

            val error = if (password.isEmpty()) {
                null
            } else {
                validation.errorMessage
            }

            state.copy(
                password = password,
                passwordStrength = strength,
                passwordError = error,
                pwHasMinLength = validation.hasMinLength,
                pwHasCapitalLetter = validation.hasCapitalLetter,
                pwHasNumber = validation.hasNumber,
                pwHasSpecialChar = validation.hasSpecialChar,
                confirmPasswordError = if (state.confirmPassword.isNotEmpty() && password != state.confirmPassword) {
                    "Passwords do not match"
                } else {
                    null
                }
            )
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _formState.update { state ->
            val error = when {
                confirmPassword.isEmpty() -> null
                confirmPassword != state.password -> "Passwords do not match"
                else -> null
            }
            state.copy(confirmPassword = confirmPassword, confirmPasswordError = error)
        }
    }

    fun onNameChanged(name: String) {
        _formState.update { state ->
            val error = if (name.isBlank()) "Full name is required" else null
            state.copy(fullName = name, nameError = error)
        }
    }

    fun checkCurrentUser() {
        val user = authUseCases.getCurrentUser()
        if (user != null) {
            _authResult.value = UiState.Success(user)
        } else {
            _authResult.value = UiState.Empty
        }
    }

    fun login() {
        val email = _formState.value.email
        val password = _formState.value.password

        // Pre-validate email
        val emailResult = authUseCases.validateEmail(email)
        if (emailResult.isFailure) {
            _formState.update { it.copy(emailError = emailResult.exceptionOrNull()?.message) }
            return
        }

        if (password.isEmpty()) {
            _formState.update { it.copy(passwordError = "Password cannot be empty") }
            return
        }

        _authResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.login(email, password)
                .onSuccess { firebaseUser ->
                    val user = authUseCases.getCurrentUser()
                    if (user != null) {
                        _authResult.value = UiState.Success(user)
                    } else {
                        _authResult.value = UiState.Error("Failed to fetch user session")
                    }
                }
                .onFailure { error ->
                    _authResult.value = UiState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun register() {
        val name = _formState.value.fullName
        val email = _formState.value.email
        val password = _formState.value.password
        val confirmPassword = _formState.value.confirmPassword

        // Perform final registration validations
        val nameErr = if (name.isBlank()) "Full name is required" else null
        val emailResult = authUseCases.validateEmail(email)
        val emailErr = emailResult.exceptionOrNull()?.message
        
        val passwordResult = authUseCases.validatePassword(password)
        val passwordErr = passwordResult.errorMessage
        
        val confirmPasswordErr = if (password != confirmPassword) "Passwords do not match" else null

        if (nameErr != null || emailErr != null || passwordErr != null || confirmPasswordErr != null) {
            _formState.update {
                it.copy(
                    nameError = nameErr,
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr
                )
            }
            return
        }

        _authResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.register(name, email, password)
                .onSuccess { firebaseUser ->
                    val user = authUseCases.getCurrentUser()
                    if (user != null) {
                        _authResult.value = UiState.Success(user)
                    } else {
                        _authResult.value = UiState.Error("Registration succeeded but session is missing")
                    }
                }
                .onFailure { error ->
                    _authResult.value = UiState.Error(error.message ?: "Registration failed")
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        _authResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.googleSignIn(idToken)
                .onSuccess { firebaseUser ->
                    val user = authUseCases.getCurrentUser()
                    if (user != null) {
                        _authResult.value = UiState.Success(user)
                    } else {
                        _authResult.value = UiState.Error("Google Login succeeded but session is missing")
                    }
                }
                .onFailure { error ->
                    _authResult.value = UiState.Error(error.message ?: "Google Sign-In failed")
                }
        }
    }

    fun loginAsGuest() {
        _authResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.guestSignIn()
                .onSuccess { firebaseUser ->
                    val user = authUseCases.getCurrentUser()
                    if (user != null) {
                        _authResult.value = UiState.Success(user)
                    } else {
                        _authResult.value = UiState.Error("Guest entry succeeded but session is missing")
                    }
                }
                .onFailure { error ->
                    _authResult.value = UiState.Error(error.message ?: "Guest login failed")
                }
        }
    }

    fun linkGuestAccount() {
        val email = _formState.value.email
        val password = _formState.value.password
        val confirmPassword = _formState.value.confirmPassword

        val emailResult = authUseCases.validateEmail(email)
        val emailErr = emailResult.exceptionOrNull()?.message
        
        val passwordResult = authUseCases.validatePassword(password)
        val passwordErr = passwordResult.errorMessage
        
        val confirmPasswordErr = if (password != confirmPassword) "Passwords do not match" else null

        if (emailErr != null || passwordErr != null || confirmPasswordErr != null) {
            _formState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr
                )
            }
            return
        }

        _authResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.linkGuestAccount(email, password)
                .onSuccess { firebaseUser ->
                    val user = authUseCases.getCurrentUser()
                    if (user != null) {
                        _authResult.value = UiState.Success(user)
                    } else {
                        _authResult.value = UiState.Error("Account linking succeeded but session is missing")
                    }
                }
                .onFailure { error ->
                    _authResult.value = UiState.Error(error.message ?: "Linking account failed")
                }
        }
    }

    fun resetPassword() {
        val email = _formState.value.email
        val emailResult = authUseCases.validateEmail(email)
        if (emailResult.isFailure) {
            _formState.update { it.copy(emailError = emailResult.exceptionOrNull()?.message) }
            return
        }

        _authResult.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.resetPassword(email)
                .onSuccess {
                    // Temporarily keep loading or update status to empty/success to represent successful email dispatch
                    _authResult.value = UiState.Empty // Let screen know operation finished without errors
                }
                .onFailure { error ->
                    _authResult.value = UiState.Error(error.message ?: "Failed to send reset link")
                }
        }
    }

    fun logout() {
        authUseCases.logout()
        _authResult.value = UiState.Empty
        _formState.value = AuthFormState() // Reset input forms
    }
}
