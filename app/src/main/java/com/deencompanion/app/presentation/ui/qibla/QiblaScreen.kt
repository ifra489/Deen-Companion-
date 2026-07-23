package com.deencompanion.app.presentation.ui.qibla

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
                title = { 
                    Text(
                        text = "Qibla Direction", 
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
                            onRequestPermission = { locationPermissionState.launchPermissionRequest() }
                        )
                    }
                    uiState.isLoadingLocation -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Finding location...", 
                                style = MaterialTheme.typography.bodyLarge, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    uiState.locationError != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.locationError ?: "Error finding location",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { viewModel.loadLocationAndQibla() }) {
                                Icon(Icons.Rounded.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
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
                                text = "Rotate your phone until the indicator lines up with the top marker.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )

                            Box(
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                QiblaCompassDial(
                                    deviceHeading = uiState.deviceHeading,
                                    qiblaBearing = uiState.qiblaBearing ?: 0f
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            val qiblaOffsetFromForward = remember(uiState.deviceHeading, uiState.qiblaBearing) {
                                val bearing = uiState.qiblaBearing ?: 0f
                                var diff = (bearing - uiState.deviceHeading + 360f) % 360f
                                diff.roundToInt()
                            }
                            val isAligned = qiblaOffsetFromForward in 0..4 || qiblaOffsetFromForward in 356..360

                            Text(
                                text = if (isAligned) "✓ Facing Qibla" else "Qibla is $qiblaOffsetFromForward° away",
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (isAligned) Color(0xFF22C55E) else MaterialTheme.colorScheme.primary
                            )

                            if (uiState.isCalibrating) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Move phone in figure-8 to calibrate",
                                    style = MaterialTheme.typography.bodySmall,
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
            imageVector = Icons.Rounded.LocationOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sensor Not Found",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Your device lacks a compass sensor. We've provided the static bearing below.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        qiblaBearing?.let { bearing ->
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Qibla Bearing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${bearing.roundToInt()}°",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Clockwise from North",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Rounded.Map, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("View on Google Maps")
            }
        }
    }
}

@Composable
private fun PermissionRationale(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.MyLocation,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Location Access Required",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "We need your location to calculate the precise Qibla direction from your current position.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRequestPermission, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Allow Location Access")
        }
    }
}

@Composable
private fun QiblaCompassDial(deviceHeading: Float, qiblaBearing: Float) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outlineVariant
    val density = LocalDensity.current

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f * 0.85f
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
                    val outer = Offset(center.x + (radius * cos(rad)).toFloat(), center.y + (radius * sin(rad)).toFloat())
                    val inner = Offset(center.x + ((radius - 12.dp.toPx()) * cos(rad)).toFloat(), center.y + ((radius - 12.dp.toPx()) * sin(rad)).toFloat())
                    drawLine(color = outline, start = inner, end = outer, strokeWidth = 2.dp.toPx())
                }

                val labelRadius = radius - 32.dp.toPx()
                listOf(0f to "N", 90f to "E", 180f to "S", 270f to "W").forEach { (angle, label) ->
                    val rad = Math.toRadians(angle.toDouble() - 90.0)
                    val pos = Offset(center.x + (labelRadius * cos(rad)).toFloat(), center.y + (labelRadius * sin(rad)).toFloat())
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = if (label == "N") primary.toArgb() else onSurface.toArgb()
                            textSize = with(density) { 14.sp.toPx() }
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(label, pos.x, pos.y + (paint.textSize / 3f), paint)
                    }
                }

                val kaabaRad = Math.toRadians(qiblaBearing.toDouble() - 90.0)
                val kaabaPos = Offset(center.x + ((radius - 4.dp.toPx()) * cos(kaabaRad)).toFloat(), center.y + ((radius - 4.dp.toPx()) * sin(kaabaRad)).toFloat())
                
                drawCircle(color = primary, radius = 10.dp.toPx(), center = kaabaPos)
                drawLine(color = primary, start = center, end = kaabaPos, strokeWidth = 4.dp.toPx())
            }

            drawCircle(color = onSurface, radius = 6.dp.toPx(), center = center)
        }

        Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)) {
            Icon(
                imageVector = Icons.Rounded.ArrowDownward, 
                contentDescription = null, 
                tint = primary, 
                modifier = Modifier.size(24.dp).rotate(180f)
            )
        }
    }
}
