package com.deencompanion.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.deencompanion.app.presentation.navigation.BottomNavBar
import com.deencompanion.app.presentation.navigation.NavGraph
import com.deencompanion.app.presentation.theme.DeenCompanionTheme
import com.deencompanion.app.presentation.ui.auth.AuthViewModel
import com.deencompanion.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint

/**
 * LEARNING NOTE:
 * This is the Single Activity for the application.
 * Annotated with @AndroidEntryPoint, Hilt will inject dependencies directly into this activity.
 * It manages the app shell, configuring the Splash Screen API, setting up edge-to-edge window decor layout,
 * and housing the main Scaffold.
 * It observes the real Firebase Authentication state from AuthViewModel to determine start destination and bottom bar visibility.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private var isSessionReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Install Splash Screen before super.onCreate()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isSessionReady }
        
        super.onCreate(savedInstanceState)

        // 2. Enable Edge-to-Edge display compatibility
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DeenCompanionTheme {
                // Observe real Firebase authentication state
                val authResult by authViewModel.authResult.collectAsState()

                // Wait for the authentication session to be resolved before initializing Navigation.
                // This prevents state restoration failures caused by dynamic startDestination changes
                // during process death recovery.
                if (authResult !is UiState.Loading) {
                    // SideEffect to update the splash screen condition once the session is resolved
                    androidx.compose.runtime.SideEffect {
                        isSessionReady = true
                    }

                    val isLoggedIn = authResult is UiState.Success
                    val navController = rememberNavController()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            // Display bottom bar only for logged-in users (main graph flow)
                            if (isLoggedIn) {
                                BottomNavBar(
                                    navController = navController,
                                    onNavigate = { item ->
                                        navController.navigate(item.route) {
                                            // Avoid building a huge stack of screens in memory
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
                        NavGraph(
                            navController = navController,
                            viewModel = authViewModel,
                            isLoggedIn = isLoggedIn,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
