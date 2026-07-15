//
//package com.deencompanion.app
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.viewModels
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.core.view.WindowCompat
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import com.deencompanion.app.presentation.navigation.BottomNavBar
//import com.deencompanion.app.presentation.navigation.NavGraph
//import com.deencompanion.app.presentation.theme.DeenCompanionTheme
//import com.deencompanion.app.presentation.ui.auth.AuthViewModel
//import com.deencompanion.app.util.UiState
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    private val authViewModel: AuthViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        setContent {
//            DeenCompanionTheme {
//                val authResult by authViewModel.authResult.collectAsState()
//                val navController = rememberNavController()
//                val currentBackStackEntry by navController.currentBackStackEntryAsState()
//                val currentRoute = currentBackStackEntry?.destination?.route
//
//                Scaffold(
//                    modifier = Modifier.fillMaxSize(),
//                    bottomBar = {
//                        val isLoggedIn = authResult is UiState.Success
//                        // Hide bottom nav on the Surah detail (ayah reading) screen
//                        // so the reading view isn't cramped.
//                        val hideBottomBar = currentRoute?.startsWith("quran_surah") == true
//                        if (isLoggedIn && !hideBottomBar) {
//                            BottomNavBar(
//                                navController = navController,
//                                onNavigate = { item ->
//                                    navController.navigate(item.route) {
//                                        popUpTo(navController.graph.startDestinationId) {
//                                            saveState = true
//                                        }
//                                        launchSingleTop = true
//                                        restoreState = true
//                                    }
//                                }
//                            )
//                        }
//                    }
//                ) { innerPadding ->
//                    Box(modifier = Modifier.fillMaxSize()) {
//                        NavGraph(
//                            navController = navController,
//                            viewModel = authViewModel,
//                            isLoggedIn = authResult is UiState.Success,
//                            modifier = Modifier.padding(innerPadding)
//                        )
//
//                        if (authResult is UiState.Loading) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(MaterialTheme.colorScheme.background),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                CircularProgressIndicator(
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}



package com.deencompanion.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deencompanion.app.presentation.navigation.BottomNavBar
import com.deencompanion.app.presentation.navigation.NavGraph
import com.deencompanion.app.presentation.theme.DeenCompanionTheme
import com.deencompanion.app.presentation.ui.auth.AuthViewModel
import com.deencompanion.app.presentation.ui.settings.ThemeSettingsViewModel
import com.deencompanion.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val themeSettingsViewModel: ThemeSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val isDarkTheme by themeSettingsViewModel.isDarkTheme.collectAsState()

            DeenCompanionTheme(darkTheme = isDarkTheme) {
                val authResult by authViewModel.authResult.collectAsState()
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val isLoggedIn = authResult is UiState.Success
                        val hideBottomBar = currentRoute?.startsWith("quran_surah") == true
                        if (isLoggedIn && !hideBottomBar) {
                            BottomNavBar(
                                navController = navController,
                                onNavigate = { item ->
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavGraph(
                            navController = navController,
                            viewModel = authViewModel,
                            isLoggedIn = authResult is UiState.Success,
                            darkTheme = isDarkTheme,
                            onThemeToggle = { themeSettingsViewModel.toggleTheme(it) },
                            modifier = Modifier.padding(innerPadding)
                        )

                        if (authResult is UiState.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}