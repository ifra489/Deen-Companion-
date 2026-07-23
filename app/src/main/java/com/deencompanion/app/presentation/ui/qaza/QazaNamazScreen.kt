package com.deencompanion.app.presentation.ui.qaza

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.domain.model.QazaPrayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QazaNamazScreen(
    navController: NavController,
    viewModel: QazaNamazViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val prayers by viewModel.prayers.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Qaza Tracker", 
                        style = MaterialTheme.typography.displayLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack, 
                            contentDescription = "Back", 
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (settings != null) {
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh, 
                                contentDescription = "Reset", 
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (settings == null) {
                QazaSetupForm(
                    onCalculate = { current, obligation, startedAge -> viewModel.calculate(current, obligation, startedAge) }
                )
            } else {
                QazaPrayerList(
                    prayers = prayers,
                    onMarkCompleted = { type, amount -> viewModel.markCompleted(type, amount) }
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Recalculate?") },
            text = { Text("This will reset your current progress and recalculate misseds prayers. Continue?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetCalculation(); showResetDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Reset") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun QazaSetupForm(onCalculate: (Int, Int, Int) -> Unit) {
    var currentAge by remember { mutableStateOf("") }
    var obligationAge by remember { mutableStateOf("12") }
    var ageStartedPraying by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Setup Missed Prayers",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Provide details to estimate your missed prayers since the age of obligation.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentAge,
            onValueChange = { currentAge = it },
            label = { Text("Your current age", style = MaterialTheme.typography.bodySmall) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        OutlinedTextField(
            value = obligationAge,
            onValueChange = { obligationAge = it },
            label = { Text("Age of obligation (typically 12-15)", style = MaterialTheme.typography.bodySmall) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        OutlinedTextField(
            value = ageStartedPraying,
            onValueChange = { ageStartedPraying = it },
            label = { Text("Age you started regular prayer", style = MaterialTheme.typography.bodySmall) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val current = currentAge.toIntOrNull() ?: 0
                val obligation = obligationAge.toIntOrNull() ?: 12
                val started = ageStartedPraying.toIntOrNull() ?: obligation
                if (current > 0) onCalculate(current, obligation, started)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Calculate Missed Prayers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QazaPrayerList(
    prayers: List<QazaPrayer>,
    onMarkCompleted: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(prayers, key = { it.prayerType }) { prayer ->
            QazaPrayerCard(prayer = prayer, onMarkCompleted = onMarkCompleted)
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

@Composable
fun QazaPrayerCard(prayer: QazaPrayer, onMarkCompleted: (String, Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = prayer.prayerType, style = MaterialTheme.typography.titleLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { prayer.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Remaining", 
                        style = MaterialTheme.typography.bodySmall, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${prayer.remaining} / ${prayer.totalMissed}", 
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onMarkCompleted(prayer.prayerType, 1) },
                        enabled = prayer.remaining > 0,
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) { 
                        Text("+1", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) 
                    }
                    Button(
                        onClick = { onMarkCompleted(prayer.prayerType, 10) },
                        enabled = prayer.remaining > 0,
                        shape = MaterialTheme.shapes.medium
                    ) { 
                        Text("+10", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) 
                    }
                }
            }
        }
    }
}
