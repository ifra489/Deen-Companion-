package com.deencompanion.app.presentation.ui.settings


import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.BuildConfig
import com.deencompanion.app.presentation.ui.auth.AuthViewModel
import com.deencompanion.app.presentation.ui.settings.AdhanToggleSetting
import com.deencompanion.app.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    darkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val authResult by authViewModel.authResult.collectAsState()
    var showHelpDialog by remember { mutableStateOf(false) }
    val defaultLanguage by settingsViewModel.defaultTranslationLanguage.collectAsState()
    val changePasswordResult by authViewModel.changePasswordResult.collectAsState()
    val deleteAccountResult by authViewModel.deleteAccountResult.collectAsState()

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val userName = (authResult as? UiState.Success)?.data?.name ?: "Guest"
    val userEmail = (authResult as? UiState.Success)?.data?.email ?: ""

    LaunchedEffect(changePasswordResult) {
        if (changePasswordResult is UiState.Success) {
            showChangePasswordDialog = false
            authViewModel.resetChangePasswordState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Account Section
            SettingsSectionTitle("Account")
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(userName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    if (userEmail.isNotBlank()) {
                        Text(userEmail, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsActionRow(icon = Icons.Default.Lock, label = "Change Password") {
                        showChangePasswordDialog = true
                    }
                    SettingsActionRow(icon = Icons.AutoMirrored.Filled.Logout, label = "Logout") {
                        showLogoutDialog = true
                    }
                    SettingsActionRow(
                        icon = Icons.Default.DeleteForever,
                        label = "Delete Account",
                        tint = MaterialTheme.colorScheme.error
                    ) {
                        showDeleteAccountDialog = true
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

// Support Section
            SettingsSectionTitle("Support")
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsActionRow(icon = Icons.Default.Share, label = "Share App with Friends") {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Try Deen Companion, an all in one Islamic app for prayer times, Quran, Duas, and more.")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    }
                    SettingsActionRow(icon = Icons.Default.StarRate, label = "Rate Us on Play Store") {
                        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        context.startActivity(playStoreIntent)
                    }
                    SettingsActionRow(icon = Icons.AutoMirrored.Filled.HelpOutline, label = "Help & FAQ") {
                        showHelpDialog = true
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Appearance Section
            SettingsSectionTitle("Appearance")
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Dark Mode",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (darkTheme) "Navy theme enabled" else "Light theme enabled",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onThemeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notifications Section
            SettingsSectionTitle("Notifications")
            AdhanToggleSetting()

            Spacer(modifier = Modifier.height(20.dp))

            // Preferences Section
            SettingsSectionTitle("Preferences")
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Default Translation Language", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("en" to "English", "ur" to "Urdu", "hi" to "Hindi").forEach { (code, label) ->
                            FilterChip(
                                selected = defaultLanguage == code,
                                onClick = { settingsViewModel.setDefaultTranslationLanguage(code) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // About Section
            SettingsSectionTitle("About")
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Deen Companion", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Version ${BuildConfig.VERSION_NAME}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsActionRow(icon = Icons.Default.Email, label = "Send Feedback") {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:iframalik.903@gmail.com")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("iframalik.903@gmail.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "Deen Companion Feedback")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Feedback"))
                    }

                }
            }
        }
    }

    // Change Password Dialog
// Change Password Dialog — ab yeh reset email bhejta hai (reliable, forgot password jaisa)
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
                    Text(
                        "We will send a password reset link to $userEmail. Use that link to set a new password.",
                        fontSize = 13.sp
                    )
                    if (changePasswordResult is UiState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text((changePasswordResult as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                    if (changePasswordResult is UiState.Success) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Reset link sent! Check your email.", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { authViewModel.sendPasswordResetLink(userEmail) }) {
                    if (changePasswordResult is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Send Reset Link")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false; authViewModel.resetChangePasswordState() }) { Text("Cancel") }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        var password by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account") },
            text = {
                Column {
                    Text(
                        "This will permanently delete your account and all cloud data. This cannot be undone.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (deleteAccountResult is UiState.Error) {
                        Text((deleteAccountResult as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { authViewModel.deleteAccount(password) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    if (deleteAccountResult is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Delete Forever")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Your data (Tasbeeh, Habits, Goals, and more) will stay saved on this device. You can log back in anytime.") },
            confirmButton = {
                Button(onClick = { authViewModel.logout(); showLogoutDialog = false }) { Text("Logout") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & FAQ") },
            text = {
                Column {
                    Text("• Prayer times are based on your device location.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Quran content is cached automatically for offline reading.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Tasbeeh, Habits, and Goals are saved on this device.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• For further help, use the Send Feedback option below.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = tint, modifier = Modifier.weight(1f))
        TextButton(onClick = onClick) { Text("Open") }
    }
}
