package com.deencompanion.app.presentation.ui.zakat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.deencompanion.app.domain.model.NisabBasis
import com.deencompanion.app.util.UiState
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatScreen(
    navController: NavController,
    viewModel: ZakatViewModel = hiltViewModel()
) {
    val ratesState by viewModel.ratesState.collectAsState()
    val result by viewModel.result.collectAsState()

    var cash by remember { mutableStateOf("") }
    var goldTola by remember { mutableStateOf("") }
    var silverTola by remember { mutableStateOf("") }
    var businessAssets by remember { mutableStateOf("") }
    var liabilities by remember { mutableStateOf("") }
    var nisabBasis by remember { mutableStateOf(NisabBasis.SILVER) }

    val pkrFormat = remember { NumberFormat.getNumberInstance(Locale("en", "PK")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Zakat Calculator", 
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
                .padding(16.dp)
        ) {
            when (val rs = ratesState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is UiState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), 
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(rs.message, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadRates() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    val tolaInGrams = 11.6638038
                    val goldPricePerTola = rs.data.goldPricePerGramPkr * tolaInGrams
                    val silverPricePerTola = rs.data.silverPricePerGramPkr * tolaInGrams

                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Live Rates (PKR)", 
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold, 
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Gold: Rs ${pkrFormat.format(goldPricePerTola.toInt())} / tola", style = MaterialTheme.typography.bodyLarge)
                            Text("Silver: Rs ${pkrFormat.format(silverPricePerTola.toInt())} / tola", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Enter Your Assets", 
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    ZakatInputField("Cash & Bank Savings (PKR)", cash) { cash = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    ZakatInputField("Gold Owned (tola)", goldTola) { goldTola = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    ZakatInputField("Silver Owned (tola)", silverTola) { silverTola = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    ZakatInputField("Business Assets (PKR)", businessAssets) { businessAssets = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    ZakatInputField("Debts / Liabilities (PKR)", liabilities) { liabilities = it }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Nisab Basis", 
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = nisabBasis == NisabBasis.SILVER,
                            onClick = { nisabBasis = NisabBasis.SILVER },
                            label = { Text("Silver (Prec.)", style = MaterialTheme.typography.bodySmall) },
                            shape = MaterialTheme.shapes.large,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary, 
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = nisabBasis == NisabBasis.GOLD,
                            onClick = { nisabBasis = NisabBasis.GOLD },
                            label = { Text("Gold", style = MaterialTheme.typography.bodySmall) },
                            shape = MaterialTheme.shapes.large,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary, 
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val goldGrams = (goldTola.toDoubleOrNull() ?: 0.0) * tolaInGrams
                            val silverGrams = (silverTola.toDoubleOrNull() ?: 0.0) * tolaInGrams
                            viewModel.calculate(
                                cashAndBank = cash.toDoubleOrNull() ?: 0.0,
                                goldGrams = goldGrams,
                                silverGrams = silverGrams,
                                businessAssets = businessAssets.toDoubleOrNull() ?: 0.0,
                                liabilities = liabilities.toDoubleOrNull() ?: 0.0,
                                nisabBasis = nisabBasis
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("Calculate Zakat", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }

                    result?.let { res ->
                        Spacer(modifier = Modifier.height(32.dp))
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = if (res.isZakatObligatory) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (res.isZakatObligatory) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = "Total Zakatable Wealth", 
                                    style = MaterialTheme.typography.bodySmall, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Rs ${pkrFormat.format(res.totalWealth.toInt())}", 
                                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 24.sp), 
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Nisab Threshold", 
                                    style = MaterialTheme.typography.bodySmall, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Rs ${pkrFormat.format(res.nisabThreshold.toInt())}", 
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                if (res.isZakatObligatory) {
                                    Text(
                                        text = "Zakat Due (2.5%)", 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = MaterialTheme.colorScheme.primary, 
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Rs ${pkrFormat.format(res.zakatDue.toInt())}", 
                                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp), 
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        text = "Wealth is below Nisab. Zakat is not obligatory.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ZakatInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}
