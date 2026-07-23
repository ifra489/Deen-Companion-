package com.deencompanion.app.presentation.ui.tasbeeh

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.domain.model.TasbeehItem
import com.deencompanion.app.util.UiState
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasbeehScreen(
    navController: NavController,
    viewModel: TasbeehViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val itemsState by viewModel.tasbeehItemsState.collectAsState()
    val selectedId by viewModel.selectedItemId.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showCustomTargetDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<TasbeehItem?>(null) }

    var newDhikrName by remember { mutableStateOf("") }
    var customTargetInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.celebrationEvent.collect { message ->
            Toast.makeText(context, "🎉 $message", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tasbeeh Counter",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = itemsState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* ViewModel restarts on emission */ }) {
                            Text("Retry")
                        }
                    }
                }
                is UiState.Success -> {
                    val items = state.data
                    val selectedItem = items.find { it.id == selectedId } ?: items.first()
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Dhikr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(items, key = { it.id }) { item ->
                                FilterChip(
                                    selected = item.id == selectedId,
                                    onClick = { viewModel.selectItem(item.id) },
                                    label = { Text(item.displayName, style = MaterialTheme.typography.bodySmall) },
                                    shape = MaterialTheme.shapes.large,
                                    trailingIcon = if (item.isCustom) {
                                        {
                                            IconButton(
                                                onClick = { showDeleteConfirmDialog = item },
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Delete,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }

                            item {
                                AssistChip(
                                    onClick = {
                                        newDhikrName = ""
                                        showAddDialog = true
                                    },
                                    label = { Text("Add Custom", style = MaterialTheme.typography.bodySmall) },
                                    shape = MaterialTheme.shapes.large,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Target Goal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )

                        val targets = listOf(33, 100, 500, 1000)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            targets.forEach { target ->
                                val isSelected = selectedItem.targetCount == target
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { viewModel.updateTarget(selectedItem.id, target) }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$target",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }

                            val isCustomTarget = selectedItem.targetCount !in targets
                            Box(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(if (isCustomTarget) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        customTargetInput = "${selectedItem.targetCount}"
                                        showCustomTargetDialog = true
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isCustomTarget) "${selectedItem.targetCount}" else "Custom",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isCustomTarget) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isCustomTarget) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.incrementCount(selectedItem.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            val activeBeadCount = selectedItem.count % 33
                            val hasCompletedLoop = selectedItem.count >= 33

                            Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                                val center = Offset(size.width / 2, size.height / 2)
                                val radius = size.width / 2 - 12.dp.toPx()
                                val numBeads = 33

                                for (i in 0 until numBeads) {
                                    val angle = (i * 360f / numBeads - 90f) * (Math.PI / 180f)
                                    val x = center.x + radius * cos(angle).toFloat()
                                    val y = center.y + radius * sin(angle).toFloat()

                                    val isActive = if (activeBeadCount == 0) hasCompletedLoop else i < activeBeadCount
                                    val isCurrent = if (activeBeadCount == 0) hasCompletedLoop && i == 32 else i == activeBeadCount - 1

                                    val beadRadius = if (isCurrent) 10.dp.toPx() else 7.dp.toPx()
                                    val color = when {
                                        isCurrent -> Color(0xFFFACC15)
                                        isActive -> Color(0xFFD4AF37)
                                        else -> Color(0xFF334155).copy(alpha = 0.3f)
                                    }

                                    drawCircle(color = color, radius = beadRadius, center = Offset(x, y))
                                    if (isCurrent) {
                                        drawCircle(color = Color(0xFF1E293B), radius = beadRadius - 3.dp.toPx(), center = Offset(x, y))
                                    }
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = selectedItem.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                                Text(
                                    text = "${selectedItem.count}",
                                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Goal: ${selectedItem.targetCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        val isTargetMet = selectedItem.count >= selectedItem.targetCount
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isTargetMet) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Session Progress",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isTargetMet) "🎉 Goal Reached!" else "Tap to count",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = if (isTargetMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "${selectedItem.count}/${selectedItem.targetCount}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        IconButton(
                            onClick = { showResetConfirmDialog = true },
                            modifier = Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Reset",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(48.dp))
                    }

                    if (showAddDialog) {
                        AlertDialog(
                            onDismissRequest = { showAddDialog = false },
                            title = { Text("Add Custom Dhikr") },
                            text = {
                                OutlinedTextField(
                                    value = newDhikrName,
                                    onValueChange = { newDhikrName = it },
                                    placeholder = { Text("e.g. Astaghfirullah") },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (newDhikrName.isNotBlank()) {
                                            viewModel.addCustomDhikr(newDhikrName)
                                            showAddDialog = false
                                        }
                                    }
                                ) { Text("Add") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                            }
                        )
                    }

                    if (showCustomTargetDialog) {
                        AlertDialog(
                            onDismissRequest = { showCustomTargetDialog = false },
                            title = { Text("Set Target") },
                            text = {
                                OutlinedTextField(
                                    value = customTargetInput,
                                    onValueChange = { customTargetInput = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val target = customTargetInput.toIntOrNull()
                                        if (target != null && target > 0) {
                                            viewModel.updateTarget(selectedItem.id, target)
                                            showCustomTargetDialog = false
                                        }
                                    }
                                ) { Text("Set") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showCustomTargetDialog = false }) { Text("Cancel") }
                            }
                        )
                    }

                    if (showResetConfirmDialog) {
                        AlertDialog(
                            onDismissRequest = { showResetConfirmDialog = false },
                            title = { Text("Reset Count") },
                            text = { Text("Reset \"${selectedItem.displayName}\" count to 0?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.resetCount(selectedItem.id)
                                        showResetConfirmDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) { Text("Reset") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showResetConfirmDialog = false }) { Text("Cancel") }
                            }
                        )
                    }

                    showDeleteConfirmDialog?.let { item ->
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirmDialog = null },
                            title = { Text("Delete Dhikr") },
                            text = { Text("Delete \"${item.displayName}\"?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.deleteCustomDhikr(item.id)
                                        showDeleteConfirmDialog = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) { Text("Delete") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirmDialog = null }) { Text("Cancel") }
                            }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
