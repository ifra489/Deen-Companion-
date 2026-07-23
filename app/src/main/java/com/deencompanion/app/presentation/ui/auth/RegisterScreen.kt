package com.deencompanion.app.presentation.ui.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
            is UiState.Success -> onRegisterSuccess(result.data)
            is UiState.Error -> snackbarHostState.showSnackbar(result.message)
            else -> {}
        }
    }

    val isLoading = authResult is UiState.Loading
    val isFormValid = formState.fullName.isNotBlank() &&
                      formState.email.isNotEmpty() &&
                      formState.password.isNotEmpty() &&
                      formState.confirmPassword.isNotEmpty() &&
                      formState.emailError == null

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = "Begin your journey with Deen Companion",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            AuthTextField(
                value = formState.fullName,
                onValueChange = { viewModel.onNameChanged(it) },
                label = "Full Name",
                error = formState.nameError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = formState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = "Email Address",
                error = formState.emailError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = formState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = "Password",
                error = null,
                isPassword = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordStrengthBar(strength = formState.passwordStrength)

            Spacer(modifier = Modifier.height(12.dp))

            PasswordChecklist(formState = formState)

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = formState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                label = "Confirm Password",
                error = formState.confirmPasswordError,
                isPassword = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.register() },
                shape = MaterialTheme.shapes.large,
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

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
        PasswordStrength.WEAK -> MaterialTheme.colorScheme.error
        PasswordStrength.MEDIUM -> Color(0xFFFACC15)
        PasswordStrength.STRONG -> Color(0xFF22C55E)
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
                PasswordStrength.NONE -> "Strength: Enter password"
                PasswordStrength.WEAK -> "Strength: Weak"
                PasswordStrength.MEDIUM -> "Strength: Medium"
                PasswordStrength.STRONG -> "Strength: Strong"
            },
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = if (strength == PasswordStrength.NONE) MaterialTheme.colorScheme.onSurfaceVariant else color,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

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
    val color = if (isSatisfied) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Icon(
            imageVector = if (isSatisfied) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
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
