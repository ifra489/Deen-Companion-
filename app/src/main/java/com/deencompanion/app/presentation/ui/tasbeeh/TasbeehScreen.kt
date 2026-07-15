package com.deencompanion.app.presentation.ui.tasbeeh

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* ViewModel restarts on emission */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                is UiState.Empty -> {
                    Text(
                        text = "No dhikr records found.",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                        // Dhikr Selector Row
                        Text(
                            text = "Select Dhikr",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(items, key = { it.id }) { item ->
                                FilterChip(
                                    selected = item.id == selectedId,
                                    onClick = { viewModel.selectItem(item.id) },
                                    label = { Text(item.displayName) },
                                    trailingIcon = if (item.isCustom) {
                                        {
                                            IconButton(
                                                onClick = { showDeleteConfirmDialog = item },
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Custom",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }

                            item {
                                InputChip(
                                    selected = false,
                                    onClick = {
                                        newDhikrName = ""
                                        showAddDialog = true
                                    },
                                    label = { Text("Custom +") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Custom",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Target Selector Goal Row
                        Text(
                            text = "Target Goal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )

                        val targets = listOf(33, 100, 500, 1000)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            targets.forEach { target ->
                                val isSelected = selectedItem.targetCount == target
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .combinedClickable(
                                            onClick = { viewModel.updateTarget(selectedItem.id, target) }
                                        )
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$target",
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            val isCustomTarget = selectedItem.targetCount !in targets
                            Box(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCustomTarget) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .combinedClickable(
                                        onClick = {
                                            customTargetInput = "${selectedItem.targetCount}"
                                            showCustomTargetDialog = true
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isCustomTarget) "${selectedItem.targetCount}" else "Custom...",
                                    color = if (isCustomTarget) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isCustomTarget) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Circular Tasbeeh Bead Ring UI representation
                        val ringBackgroundColor = MaterialTheme.colorScheme.surface
                        val activeBeadColor = MaterialTheme.colorScheme.primary
                        val inactiveBeadColor = MaterialTheme.colorScheme.outlineVariant

                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .clip(CircleShape)
                                .background(ringBackgroundColor)
                                .combinedClickable(
                                    onClick = { viewModel.incrementCount(selectedItem.id) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val activeBeadCount = selectedItem.count % 33
                            val hasCompletedLoop = selectedItem.count >= 33

                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                            ) {
                                val center = Offset(size.width / 2, size.height / 2)
                                val radius = size.width / 2 - 12.dp.toPx()
                                val numBeads = 33

                                for (i in 0 until numBeads) {
                                    val angle = (i * 360f / numBeads - 90f) * (Math.PI / 180f)
                                    val x = center.x + radius * cos(angle).toFloat()
                                    val y = center.y + radius * sin(angle).toFloat()

                                    val isActive = if (activeBeadCount == 0) {
                                        hasCompletedLoop
                                    } else {
                                        i < activeBeadCount
                                    }

                                    val isCurrent = if (activeBeadCount == 0) {
                                        hasCompletedLoop && i == 32
                                    } else {
                                        i == activeBeadCount - 1
                                    }

                                    val beadRadius = if (isCurrent) 10.dp.toPx() else 7.dp.toPx()
                                    val color = when {
                                        isCurrent -> Color(0xFFFF9800)
                                        isActive -> activeBeadColor
                                        else -> inactiveBeadColor
                                    }

                                    drawCircle(
                                        color = color,
                                        radius = beadRadius,
                                        center = Offset(x, y)
                                    )

                                    if (isCurrent) {
                                        drawCircle(
                                            color = ringBackgroundColor,
                                            radius = beadRadius - 3.dp.toPx(),
                                            center = Offset(x, y)
                                        )
                                    }
                                }
                            }

                            // Centered display inside Tasbeeh ring
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = selectedItem.displayName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${selectedItem.count}",
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Goal: ${selectedItem.targetCount}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Session Progress Card
                        val isTargetMet = selectedItem.count >= selectedItem.targetCount
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isTargetMet) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Session Progress",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isTargetMet) "🎉 Goal Reached!" else "Tap the bead ring to count",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTargetMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "${selectedItem.count} / ${selectedItem.targetCount}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Quick Reset Button
                        IconButton(
                            onClick = { showResetConfirmDialog = true },
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Counter",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Dialogs
                    if (showAddDialog) {
                        AlertDialog(
                            onDismissRequest = { showAddDialog = false },
                            title = { Text("Add Custom Dhikr") },
                            text = {
                                Column {
                                    Text(
                                        text = "Enter a custom name for your dhikr:",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    OutlinedTextField(
                                        value = newDhikrName,
                                        onValueChange = { newDhikrName = it },
                                        placeholder = { Text("e.g. Astaghfirullah") },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            focusedLabelColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (newDhikrName.isNotBlank()) {
                                            viewModel.addCustomDhikr(newDhikrName)
                                            showAddDialog = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Add", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddDialog = false }) {
                                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        )
                    }

                    if (showCustomTargetDialog) {
                        AlertDialog(
                            onDismissRequest = { showCustomTargetDialog = false },
                            title = { Text("Set Custom Target") },
                            text = {
                                Column {
                                    Text(
                                        text = "Enter target goal value:",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    OutlinedTextField(
                                        value = customTargetInput,
                                        onValueChange = { customTargetInput = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            focusedLabelColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val target = customTargetInput.toIntOrNull()
                                        if (target != null && target > 0) {
                                            viewModel.updateTarget(selectedItem.id, target)
                                            showCustomTargetDialog = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Set", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showCustomTargetDialog = false }) {
                                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        )
                    }

                    if (showResetConfirmDialog) {
                        AlertDialog(
                            onDismissRequest = { showResetConfirmDialog = false },
                            title = { Text("Reset Count") },
                            text = {
                                Text("Do you want to reset \"${selectedItem.displayName}\" count back to 0?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.resetCount(selectedItem.id)
                                        showResetConfirmDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Reset", color = MaterialTheme.colorScheme.onError)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showResetConfirmDialog = false }) {
                                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        )
                    }

                    showDeleteConfirmDialog?.let { item ->
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirmDialog = null },
                            title = { Text("Delete Dhikr") },
                            text = {
                                Text("Do you want to delete \"${item.displayName}\" and its stored counts?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.deleteCustomDhikr(item.id)
                                        showDeleteConfirmDialog = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Delete", color = MaterialTheme.colorScheme.onError)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}