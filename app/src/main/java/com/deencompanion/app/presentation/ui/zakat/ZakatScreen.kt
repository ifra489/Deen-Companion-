package com.deencompanion.app.presentation.ui.zakat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
                title = { Text("Zakat Calculator", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (val rs = ratesState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is UiState.Error -> {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(rs.message, color = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadRates() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                                Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
                is UiState.Empty -> {}
                is UiState.Success -> {
                    val tolaInGrams = 11.6638038
                    val goldPricePerTola = rs.data.goldPricePerGramPkr * tolaInGrams
                    val silverPricePerTola = rs.data.silverPricePerGramPkr * tolaInGrams

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Live Rates (PKR)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Gold: Rs ${pkrFormat.format(goldPricePerTola.toInt())} / tola", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Silver: Rs ${pkrFormat.format(silverPricePerTola.toInt())} / tola", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Enter Your Assets", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(12.dp))

                    ZakatInputField("Cash & Bank Savings (PKR)", cash) { cash = it }
                    Spacer(modifier = Modifier.height(10.dp))
                    ZakatInputField("Gold Owned (tola)", goldTola) { goldTola = it }
                    Spacer(modifier = Modifier.height(10.dp))
                    ZakatInputField("Silver Owned (tola)", silverTola) { silverTola = it }
                    Spacer(modifier = Modifier.height(10.dp))
                    ZakatInputField("Business/Investment Assets (PKR)", businessAssets) { businessAssets = it }
                    Spacer(modifier = Modifier.height(10.dp))
                    ZakatInputField("Debts/Liabilities Owed (PKR)", liabilities) { liabilities = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Nisab Basis", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = nisabBasis == NisabBasis.SILVER,
                            onClick = { nisabBasis = NisabBasis.SILVER },
                            label = { Text("Silver (Precautionary)") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary)
                        )
                        FilterChip(
                            selected = nisabBasis == NisabBasis.GOLD,
                            onClick = { nisabBasis = NisabBasis.GOLD },
                            label = { Text("Gold") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calculate Zakat", color = MaterialTheme.colorScheme.onPrimary)
                    }

                    result?.let { res ->
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (res.isZakatObligatory) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Total Zakatable Wealth", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Rs ${pkrFormat.format(res.totalWealth.toInt())}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Nisab Threshold", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Rs ${pkrFormat.format(res.nisabThreshold.toInt())}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(16.dp))
                                if (res.isZakatObligatory) {
                                    Text("Zakat Due (2.5%)", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                    Text("Rs ${pkrFormat.format(res.zakatDue.toInt())}", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                } else {
                                    Text(
                                        "Your wealth is below Nisab. Zakat is not obligatory this year.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZakatInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}