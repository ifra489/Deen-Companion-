package com.deencompanion.app.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

import com.deencompanion.app.presentation.ui.home.components.DailyAyahCard
import com.deencompanion.app.presentation.ui.home.components.DailyDuaCard

import com.deencompanion.app.presentation.ui.home.components.QuickAccessGrid
import com.deencompanion.app.presentation.ui.home.components.DailyHadithCard
import com.deencompanion.app.presentation.ui.home.components.DateCard
import com.deencompanion.app.presentation.ui.home.components.PrayerTimesCard

import android.Manifest
import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

import com.deencompanion.app.presentation.ui.settings.AdhanToggleSetting
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
        containerColor = Color(0xFFF5F5F5) // Off-white/light gray background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Item 1: Greeting Text
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "AssalamuAlaikum",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userName,
                        color = Color(0xFF212121),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Item 2: PrayerTimesCard
            item {
                val countdownTimer = ""
                PrayerTimesCard(
                    state = prayerTimesState,
                    countdown = countdownTimer,

                    onRetry = { viewModel.loadPrayerTimesByLocation() }
                )
            }

            // Item 3: DateCard
            item {
                DateCard(state = hijriDateState)
            }
            item {
                AdhanToggleSetting(modifier = Modifier.padding(vertical = 8.dp))
            }
            // Item 4: DailyAyahCard
            item {
                DailyAyahCard(
                    state = dailyAyahState,
                    onRefresh = { viewModel.refreshAyah() }
                )
            }

            // Item 5: DailyHadithCard
            item {
                DailyHadithCard(hadith = dailyHadith)
            }

            // Item 6: DailyDuaCard
            item {
                DailyDuaCard(dua = dailyDua)
            }

            // Item 7: QuickAccessGrid
            item {
                QuickAccessGrid(navController = navController)
            }

            // Item 8: Bottom Navigation spacer
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}


