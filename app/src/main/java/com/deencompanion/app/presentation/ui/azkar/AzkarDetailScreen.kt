package com.deencompanion.app.presentation.ui.azkar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
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
                title = { 
                    Text(
                        text = title, 
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
        when (val s = state) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(s.message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
            else -> {}
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = azkar.arabic,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.End,
                    textDirection = TextDirection.Rtl
                ),
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 38.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = azkar.english, 
                style = MaterialTheme.typography.bodyLarge, 
                color = MaterialTheme.colorScheme.onSurface, 
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = azkar.reference, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isComplete) "🎉 Completed" else "Count: $currentCount / ${azkar.repeatCount}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isComplete) Color(0xFF22C55E) else MaterialTheme.colorScheme.primary
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isComplete) {
                        Icon(
                            imageVector = Icons.Rounded.Check, 
                            contentDescription = null, 
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    IconButton(onClick = onReset, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh, 
                            contentDescription = "Reset", 
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}
