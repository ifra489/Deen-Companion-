package com.deencompanion.app.presentation.ui.azkar



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.domain.model.Azkar
import com.deencompanion.app.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarDetailScreen(
    navController: NavController,
    viewModel: AzkarDetailViewModel = hiltViewModel()
) {
    val state by viewModel.azkarState.collectAsState()
    val tallies by viewModel.tallies.collectAsState()
    val title = if (viewModel.type.equals("morning", ignoreCase = true)) "Morning Azkar" else "Evening Azkar"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold) },
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
        when (val s = state) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("No Azkar available", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.data, key = { it.id }) { azkar ->
                        AzkarCard(
                            azkar = azkar,
                            currentCount = tallies[azkar.id] ?: 0,
                            onTap = { viewModel.onTap(azkar.id, azkar.repeatCount) },
                            onReset = { viewModel.resetTally(azkar.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AzkarCard(
    azkar: Azkar,
    currentCount: Int,
    onTap: () -> Unit,
    onReset: () -> Unit
) {
    val isComplete = currentCount >= azkar.repeatCount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isComplete) onTap() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = azkar.arabic,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 34.sp,
                textAlign = TextAlign.End,
                style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = azkar.english, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = azkar.reference, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isComplete) "Completed" else "Tap to count: $currentCount / ${azkar.repeatCount}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    if (isComplete) {
                        Icon(Icons.Default.Check, contentDescription = "Done", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onReset) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}