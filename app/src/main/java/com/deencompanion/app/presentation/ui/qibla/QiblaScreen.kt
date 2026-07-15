package com.deencompanion.app.presentation.ui.qibla

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QiblaScreen(
    navController: NavController,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            viewModel.loadLocationAndQibla()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startListening()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopListening()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qibla Direction", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !locationPermissionState.status.isGranted -> {
                        PermissionRationale(
                            showSettingsHint = locationPermissionState.status.shouldShowRationale,
                            onRequestPermission = { locationPermissionState.launchPermissionRequest() }
                        )
                    }
                    uiState.isLoadingLocation -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Finding your location...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    uiState.locationError != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                uiState.locationError ?: "",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadLocationAndQibla() }) {
                                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try Again")
                            }
                        }
                    }
                    !uiState.hasSensors -> {
                        NoSensorFallback(
                            qiblaBearing = uiState.qiblaBearing
                        )
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Point the top of your phone forward, then rotate until the Kaaba icon lines up with the marker.",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                QiblaCompassDial(
                                    deviceHeading = uiState.deviceHeading,
                                    qiblaBearing = uiState.qiblaBearing ?: 0f
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            val qiblaOffsetFromForward = remember(uiState.deviceHeading, uiState.qiblaBearing) {
                                val bearing = uiState.qiblaBearing ?: 0f
                                var diff = (bearing - uiState.deviceHeading + 360f) % 360f
                                diff.roundToInt()
                            }
                            val isAligned = qiblaOffsetFromForward in 0..4 || qiblaOffsetFromForward in 356..360

                            Text(
                                text = if (isAligned) "Facing Qibla ✓" else "Qibla is $qiblaOffsetFromForward° away",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )

                            if (uiState.isCalibrating) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Move your phone in a figure-8 to calibrate the compass.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoSensorFallback(qiblaBearing: Float?) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Compass Sensor Not Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Your device doesn't have a compass sensor, so we can't show a live rotating needle.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        qiblaBearing?.let { bearing ->
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Qibla Bearing",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "${bearing.roundToInt()}°",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "clockwise from North",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=21.4225,39.8262")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Map, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Qibla on Map")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Open Google Maps to see the direction from your location to the Kaaba.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PermissionRationale(
    showSettingsHint: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Deen Companion needs your location to calculate the exact Qibla direction from where you are.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text(if (showSettingsHint) "Grant Location Permission" else "Allow Location Access")
        }
    }
}

@Composable
private fun QiblaCompassDial(
    deviceHeading: Float,
    qiblaBearing: Float
) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val density = LocalDensity.current

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f * 0.9f
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(
                color = outline,
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            rotate(degrees = -deviceHeading, pivot = center) {
                for (angle in 0 until 360 step 30) {
                    val rad = Math.toRadians(angle.toDouble() - 90.0)
                    val outer = Offset(
                        center.x + (radius * cos(rad)).toFloat(),
                        center.y + (radius * sin(rad)).toFloat()
                    )
                    val inner = Offset(
                        center.x + ((radius - 12.dp.toPx()) * cos(rad)).toFloat(),
                        center.y + ((radius - 12.dp.toPx()) * sin(rad)).toFloat()
                    )
                    drawLine(
                        color = surfaceVariant,
                        start = inner,
                        end = outer,
                        strokeWidth = 2.dp.toPx()
                    )
                }

                val labelRadius = radius - 28.dp.toPx()
                val labels = listOf(0f to "N", 90f to "E", 180f to "S", 270f to "W")
                labels.forEach { (angle, label) ->
                    val rad = Math.toRadians(angle.toDouble() - 90.0)
                    val pos = Offset(
                        center.x + (labelRadius * cos(rad)).toFloat(),
                        center.y + (labelRadius * sin(rad)).toFloat()
                    )
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = if (label == "N") primary.toArgb() else onSurface.toArgb()
                            textSize = with(density) { 14.sp.toPx() }
                            isFakeBoldText = label == "N"
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(label, pos.x, pos.y + (paint.textSize / 3f), paint)
                    }
                }

                val kaabaRad = Math.toRadians(qiblaBearing.toDouble() - 90.0)
                val kaabaPos = Offset(
                    center.x + ((radius - 4.dp.toPx()) * cos(kaabaRad)).toFloat(),
                    center.y + ((radius - 4.dp.toPx()) * sin(kaabaRad)).toFloat()
                )
                drawCircle(color = primary, radius = 10.dp.toPx(), center = kaabaPos)

                drawLine(
                    color = primary,
                    start = center,
                    end = kaabaPos,
                    strokeWidth = 4.dp.toPx()
                )
            }

            drawCircle(color = onSurface, radius = 6.dp.toPx(), center = center)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Qibla marker",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
