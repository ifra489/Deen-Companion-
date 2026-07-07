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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private var isSessionReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isSessionReady }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DeenCompanionTheme {
                val authResult by authViewModel.authResult.collectAsState()

                if (authResult !is UiState.Loading) {
                    androidx.compose.runtime.SideEffect {
                        isSessionReady = true
                    }

                    val isLoggedIn = authResult is UiState.Success
                    val navController = rememberNavController()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (isLoggedIn) {
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