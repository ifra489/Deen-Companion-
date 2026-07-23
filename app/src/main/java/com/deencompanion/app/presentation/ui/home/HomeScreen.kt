package com.deencompanion.app.presentation.ui.home

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.presentation.ui.home.components.*
import com.deencompanion.app.presentation.ui.settings.AdhanToggleSetting
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            viewModel.loadPrayerTimesByLocation()
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    LaunchedEffect(notificationPermissionState?.status) {
        if (notificationPermissionState != null && !notificationPermissionState.status.isGranted) {
            notificationPermissionState.launchPermissionRequest()
        }
    }

    val prayerTimesState by viewModel.prayerTimesState.collectAsState()
    val hijriDateState by viewModel.hijriDateState.collectAsState()
    val dailyAyahState by viewModel.dailyAyahState.collectAsState()
    val dailyHadith by viewModel.dailyHadith.collectAsState()
    val dailyDua by viewModel.dailyDua.collectAsState()
    val countdown by viewModel.countdownTimer.collectAsState()
    val userName by viewModel.userName.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Assalamu Alaikum",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Prayer Times Card
            item {
                PrayerTimesCard(
                    state = prayerTimesState,
                    countdown = countdown,
                    onRetry = { viewModel.loadPrayerTimesByLocation() }
                )
            }

            // Date Card
            item {
                DateCard(state = hijriDateState)
            }

            // Adhan Toggle Card (Moved below DateCard to avoid horizontal squeezing)
            item {
                AdhanToggleSetting()
            }

            // Daily Ayah
            item {
                DailyAyahCard(
                    state = dailyAyahState,
                    onRefresh = { viewModel.refreshAyah() }
                )
            }

            // Daily Hadith
            item {
                DailyHadithCard(hadith = dailyHadith)
            }

            // Daily Dua
            item {
                DailyDuaCard(dua = dailyDua)
            }

            // Quick Access Grid
            item {
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                QuickAccessGrid(navController = navController)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
