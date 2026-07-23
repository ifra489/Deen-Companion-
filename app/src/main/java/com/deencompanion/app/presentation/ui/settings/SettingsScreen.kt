package com.deencompanion.app.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.BuildConfig
import com.deencompanion.app.presentation.ui.auth.AuthViewModel
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
                title = { 
                    Text(
                        text = "Settings", 
                        style = MaterialTheme.typography.displayLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Account Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsSectionTitle("Account")
                Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Text(text = userName, style = MaterialTheme.typography.headlineMedium)
                            if (userEmail.isNotBlank()) {
                                Text(text = userEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        SettingsActionRow(icon = Icons.Rounded.Lock, label = "Change Password") {
                            showChangePasswordDialog = true
                        }
                        SettingsActionRow(icon = Icons.Rounded.Logout, label = "Logout") {
                            showLogoutDialog = true
                        }
                        SettingsActionRow(
                            icon = Icons.Rounded.DeleteForever,
                            label = "Delete Account",
                            tint = MaterialTheme.colorScheme.error
                        ) {
                            showDeleteAccountDialog = true
                        }
                    }
                }
            }

            // Appearance Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsSectionTitle("Appearance")
                Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Dark Mode", style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = if (darkTheme) "Navy theme enabled" else "Light theme enabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = onThemeToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            // Preferences Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsSectionTitle("Preferences")
                Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Default Translation Language", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("en" to "English", "ur" to "Urdu", "hi" to "Hindi").forEach { (code, label) ->
                                FilterChip(
                                    selected = defaultLanguage == code,
                                    onClick = { settingsViewModel.setDefaultTranslationLanguage(code) },
                                    label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                                    shape = MaterialTheme.shapes.large,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Support Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsSectionTitle("Support")
                Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingsActionRow(icon = Icons.Rounded.Share, label = "Share with Friends") {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Try Deen Companion, an all-in-one Islamic app for prayer times, Quran, Duas, and more.")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                        }
                        SettingsActionRow(icon = Icons.Rounded.StarRate, label = "Rate on Play Store") {
                            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                            context.startActivity(playStoreIntent)
                        }
                        SettingsActionRow(icon = Icons.Rounded.HelpOutline, label = "Help & FAQ") {
                            showHelpDialog = true
                        }
                    }
                }
            }

            // About Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsSectionTitle("About")
                Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Deen Companion", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Version ${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SettingsActionRow(icon = Icons.Rounded.Email, label = "Send Feedback") {
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
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Text(
                    text = "We will send a password reset link to $userEmail. Use that link to set a new password.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(onClick = { authViewModel.sendPasswordResetLink(userEmail) }) {
                    if (changePasswordResult is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
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

    if (showDeleteAccountDialog) {
        var password by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "This will permanently delete your account and all cloud data. This cannot be undone.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
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

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Your data will stay saved on this device. You can log back in anytime.", style = MaterialTheme.typography.bodyLarge) },
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Prayer times are based on your device location.", style = MaterialTheme.typography.bodyLarge)
                    Text("• Quran content is cached automatically for offline reading.", style = MaterialTheme.typography.bodyLarge)
                    Text("• Tasbeeh, Habits, and Goals are saved on this device.", style = MaterialTheme.typography.bodyLarge)
                    Text("• For further help, use the Send Feedback option.", style = MaterialTheme.typography.bodyLarge)
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
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
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
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.titleLarge, color = tint, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
    }
}
