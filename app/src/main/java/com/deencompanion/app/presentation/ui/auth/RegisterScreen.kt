package com.deencompanion.app.presentation.ui.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deencompanion.app.domain.model.User
import com.deencompanion.app.presentation.ui.auth.components.AuthTextField
import com.deencompanion.app.util.UiState

/**
 * LEARNING NOTE:
 * RegisterScreen handles user onboarding. It implements a live password strength visualizer
 * and requirement checklist to guide users in creating secure passwords.
 * Form submission triggers VM registration and maps Hilt-mediated Firebase outcomes.
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsState()
    val authResult by viewModel.authResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authResult) {
        when (val result = authResult) {
            is UiState.Success -> {
                onRegisterSuccess(result.data)
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(result.message)
            }
            else -> {}
        }
    }

    val isLoading = authResult is UiState.Loading
    val isFormValid = formState.fullName.isNotBlank() &&
                      formState.email.isNotEmpty() &&
                      formState.password.isNotEmpty() &&
                      formState.confirmPassword.isNotEmpty() &&
                      formState.emailError == null &&
                      formState.passwordError == null &&
                      formState.confirmPasswordError == null &&
                      formState.nameError == null

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = "Begin your journey with Deen Companion",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name input
            AuthTextField(
                value = formState.fullName,
                onValueChange = { viewModel.onNameChanged(it) },
                label = "Full Name",
                error = formState.nameError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "User Name Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email input
            AuthTextField(
                value = formState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = "Email Address",
                error = formState.emailError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input
            AuthTextField(
                value = formState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = "Password",
                error = null, // Handled below the field via checklist/bars for premium design
                isPassword = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Live Password Strength Indicator (3-segment bar)
            PasswordStrengthBar(strength = formState.passwordStrength)

            Spacer(modifier = Modifier.height(12.dp))

            // Password requirements checklist
            PasswordChecklist(formState = formState)

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password input
            AuthTextField(
                value = formState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                label = "Confirm Password",
                error = formState.confirmPasswordError,
                isPassword = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password Confirm Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            Button(
                onClick = { viewModel.register() },
                shape = MaterialTheme.shapes.large,
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Back to Login redirect
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin, enabled = !isLoading) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Renders a segmented bar displaying the current strength of the password.
 */
@Composable
fun PasswordStrengthBar(strength: PasswordStrength) {
    val numSegmentsToColor = when (strength) {
        PasswordStrength.NONE -> 0
        PasswordStrength.WEAK -> 1
        PasswordStrength.MEDIUM -> 2
        PasswordStrength.STRONG -> 3
    }

    val color = when (strength) {
        PasswordStrength.NONE -> MaterialTheme.colorScheme.outlineVariant
        PasswordStrength.WEAK -> MaterialTheme.colorScheme.error // Red
        PasswordStrength.MEDIUM -> Color(0xFFF9A825) // Gold/Orange
        PasswordStrength.STRONG -> Color(0xFF2E7D32) // Green
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until 3) {
                val segmentColor by animateColorAsState(
                    targetValue = if (i < numSegmentsToColor) color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    label = "segmentColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(segmentColor)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (strength) {
                PasswordStrength.NONE -> "Password Strength: Enter characters"
                PasswordStrength.WEAK -> "Password Strength: Weak"
                PasswordStrength.MEDIUM -> "Password Strength: Medium"
                PasswordStrength.STRONG -> "Password Strength: Strong"
            },
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = if (strength == PasswordStrength.NONE) MaterialTheme.colorScheme.onSurfaceVariant else color,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

/**
 * Renders the checklist mapping criteria requirements to their satisfied statuses.
 */
@Composable
fun PasswordChecklist(formState: AuthFormState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ChecklistItem(text = "Minimum 8 characters", isSatisfied = formState.pwHasMinLength)
        ChecklistItem(text = "At least 1 capital letter", isSatisfied = formState.pwHasCapitalLetter)
        ChecklistItem(text = "At least 1 number", isSatisfied = formState.pwHasNumber)
        ChecklistItem(text = "At least 1 special character", isSatisfied = formState.pwHasSpecialChar)
    }
}

@Composable
fun ChecklistItem(text: String, isSatisfied: Boolean) {
    val color = if (isSatisfied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Icon(
            imageVector = if (isSatisfied) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}
