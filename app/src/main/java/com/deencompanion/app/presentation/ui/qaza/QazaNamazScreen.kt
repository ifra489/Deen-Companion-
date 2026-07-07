package com.deencompanion.app.presentation.ui.qaza

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
                title = { Text("Qaza Namaz Tracker", color = Color(0xFF212121), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF212121))
                    }
                },
                actions = {
                    if (settings != null) {
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recalculate", tint = Color(0xFF212121))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        if (settings == null) {
            QazaSetupForm(
                modifier = Modifier.padding(padding),
                onCalculate = { current, obligation, startedAge -> viewModel.calculate(current, obligation, startedAge) }
            )
        } else {
            QazaPrayerList(
                modifier = Modifier.padding(padding),
                prayers = prayers,
                onMarkCompleted = { type, amount -> viewModel.markCompleted(type, amount) }
            )
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Recalculate?") },
            text = { Text("This will reset your current progress and recalculate with new numbers. Continue?") },
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
fun QazaSetupForm(modifier: Modifier = Modifier, onCalculate: (Int, Int, Int) -> Unit) {
    var currentAge by remember { mutableStateOf("") }
    var obligationAge by remember { mutableStateOf("7") }
    var ageStartedPraying by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Enter your details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "This is an estimate based on the information you provide.",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = currentAge,
            onValueChange = { currentAge = it },
            label = { Text("Your current age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = obligationAge,
            onValueChange = { obligationAge = it },
            label = { Text("Age when prayer became obligatory (default 7)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = ageStartedPraying,
            onValueChange = { ageStartedPraying = it },
            label = { Text("Age from which you started praying regularly") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val current = currentAge.toIntOrNull() ?: 0
                val obligation = obligationAge.toIntOrNull() ?: 7
                val started = ageStartedPraying.toIntOrNull() ?: obligation
                if (current > 0) onCalculate(current, obligation, started)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C48)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }
    }
}

@Composable
fun QazaPrayerList(
    modifier: Modifier = Modifier,
    prayers: List<QazaPrayer>,
    onMarkCompleted: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(prayers, key = { it.prayerType }) { prayer ->
            QazaPrayerCard(prayer = prayer, onMarkCompleted = onMarkCompleted)
        }
    }
}

@Composable
fun QazaPrayerCard(prayer: QazaPrayer, onMarkCompleted: (String, Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(prayer.prayerType, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { prayer.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Color(0xFF141C48),
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remaining: ${prayer.remaining} / ${prayer.totalMissed}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onMarkCompleted(prayer.prayerType, 1) },
                        enabled = prayer.remaining > 0,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) { Text("+1", fontSize = 12.sp) }
                    Button(
                        onClick = { onMarkCompleted(prayer.prayerType, 10) },
                        enabled = prayer.remaining > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C48)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) { Text("+10", fontSize = 12.sp) }
                }
            }
        }
    }
}