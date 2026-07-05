package com.deencompanion.app.presentation.navigation
import com.deencompanion.app.presentation.ui.prayer.PrayerScreen
import com.deencompanion.app.presentation.ui.prayer.PrayerHistoryScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.deencompanion.app.presentation.ui.tasbeeh.TasbeehScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.deencompanion.app.presentation.ui.qaza.QazaNamazScreen
import androidx.navigation.navArgument
import com.deencompanion.app.presentation.ui.tracker.HabitTrackerScreen
import com.deencompanion.app.presentation.ui.goals.GoalsScreen
import com.deencompanion.app.presentation.ui.auth.AuthViewModel
import com.deencompanion.app.presentation.ui.auth.LoginScreen
import com.deencompanion.app.presentation.ui.auth.RegisterScreen
import com.deencompanion.app.presentation.ui.auth.ForgotPasswordScreen
import com.deencompanion.app.presentation.ui.dua.DuaDetailScreen
import com.deencompanion.app.presentation.ui.dua.DuaListScreen
import com.deencompanion.app.presentation.ui.more.MoreScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.deencompanion.app.presentation.ui.quran.QuranListScreen
import com.deencompanion.app.presentation.ui.quran.QuranListViewModel
import com.deencompanion.app.presentation.ui.quran.QuranDetailScreen
import com.deencompanion.app.presentation.ui.quran.QuranDetailViewModel
import com.deencompanion.app.presentation.ui.hadith.HadithListScreen
import com.deencompanion.app.presentation.ui.hadith.HadithDetailScreen
import com.deencompanion.app.presentation.ui.azkar.AzkarDetailScreen
/**
 * Central navigation graph definition for Deen Companion.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel,
    isLoggedIn: Boolean = false,
    onLoginSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val startDestination = if (isLoggedIn) "main_graph" else "auth_graph"

    // Real-time navigation routing on session status change
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            // Redirect to main dashboard graph if current route is inside auth
            val currentRoute = navController.currentDestination?.route
            val isCurrentInAuth = navController.currentDestination?.parent?.route == "auth_graph"
            if (isCurrentInAuth || currentRoute == "auth_graph") {
                navController.navigate("main_graph") {
                    popUpTo("auth_graph") { inclusive = true }
                }
            }
        } else {
            // Redirect back to login if current route is inside main app
            val isCurrentInMain = navController.currentDestination?.parent?.route == "main_graph"
            if (isCurrentInMain) {
                navController.navigate("auth_graph") {
                    popUpTo("main_graph") { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Nested Authentication Graph ---
        navigation(
            startDestination = NavRoutes.Login.route,
            route = "auth_graph"
        ) {
            composable(NavRoutes.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToRegister = {
                        navController.navigate(NavRoutes.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(NavRoutes.ForgotPassword.route)
                    },
                    onLoginSuccess = { user ->
                        onLoginSuccess()
                        navController.navigate("main_graph") {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    }
                )
            }
            composable(NavRoutes.Register.route) {
                RegisterScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = { user ->
                        onLoginSuccess()
                        navController.navigate("main_graph") {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    }
                )
            }
            composable(NavRoutes.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = viewModel,
                    onNavigateBackToLogin = {
                        navController.popBackStack()
                    }
                )
        }
    }

        // --- Nested Main App Graph ---
        navigation(
            startDestination = NavRoutes.Home.route,
            route = "main_graph"
        ) {
            composable(NavRoutes.Home.route) {
                com.deencompanion.app.presentation.ui.home.HomeScreen(
                    navController = navController
                )
            }
            composable(NavRoutes.Prayer.route) {
                PrayerScreen(navController = navController)
            }
            composable(NavRoutes.Quran.route) {
                val listViewModel: QuranListViewModel = hiltViewModel()
                QuranListScreen(
                    viewModel = listViewModel,
                    onSurahClick = { surahId ->
                        navController.navigate(NavRoutes.QuranSurah.createRoute(surahId))
                    }
                )
            }
            composable(NavRoutes.Dua.route) {
                DuaListScreen(navController = navController)
            }
            composable(NavRoutes.Hadith.route) {
                HadithListScreen(navController = navController)
            }
            composable(NavRoutes.Tasbeeh.route) {
                TasbeehScreen(navController = navController)
            }
            composable(NavRoutes.Goals.route) {
                GoalsScreen(navController = navController)
            }
            composable(NavRoutes.Tracker.route) {
                HabitTrackerScreen(navController = navController)
            }
            composable(NavRoutes.Journal.route) {
                QazaNamazScreen(navController = navController)
            }
            composable(NavRoutes.Mood.route) {
                PlaceholderScreen(name = "Mood Tracker Screen\nTODO(\"Replace with actual screen — Phase 10\")")
            }
            composable(NavRoutes.Journey.route) {
                PlaceholderScreen(name = "Spiritual Journey Screen\nTODO(\"Replace with actual screen — Phase 11\")")
            }
            composable(NavRoutes.Achievements.route) {
                PlaceholderScreen(name = "Achievements Screen\nTODO(\"Replace with actual screen — Phase 11\")")
            }
            composable(NavRoutes.Zakat.route) {
                PlaceholderScreen(name = "Zakat Calculator Screen\nTODO(\"Replace with actual screen — Phase 12\")")
            }
            composable(NavRoutes.HijriCalendar.route) {
                PlaceholderScreen(name = "Hijri Calendar Screen\nTODO(\"Replace with actual screen — Phase 12\")")
            }
            composable(NavRoutes.Settings.route) {
                MoreScreen(navController = navController)
            }

            // --- Parameterized Detail Screens ---
            composable(
                route = NavRoutes.QuranSurah.route,
                arguments = listOf(navArgument("surahId") { type = NavType.IntType })
            ) { backStackEntry ->
                val surahId = backStackEntry.arguments?.getInt("surahId") ?: 1
                val detailViewModel: QuranDetailViewModel = hiltViewModel()

                QuranDetailScreen(
                    surahId = surahId,
                    viewModel = detailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.QuranAyah.route,
                arguments = listOf(
                    navArgument("surahId") { type = NavType.IntType },
                    navArgument("ayahId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val surahId = backStackEntry.arguments?.getInt("surahId") ?: 1
                val ayahId = backStackEntry.arguments?.getInt("ayahId") ?: 1
                val detailViewModel: QuranDetailViewModel = hiltViewModel()

                LaunchedEffect(ayahId) {
                    detailViewModel.setSelectedAyahIndex(ayahId - 1)
                }

                QuranDetailScreen(
                    surahId = surahId,
                    viewModel = detailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.DuaDetail.route,
                arguments = listOf(navArgument("duaId") { type = NavType.IntType })
            ) {
                DuaDetailScreen(navController = navController)
            }

            composable(
                route = NavRoutes.HadithDetail.route,
                arguments = listOf(navArgument("hadithId") { type = NavType.IntType })
            ) {
                HadithDetailScreen(navController = navController)
            }

            composable(NavRoutes.PrayerHistory.route) {
                PrayerHistoryScreen(navController = navController)
            }

            composable(
                route = NavRoutes.AzkarDetail.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) {
                AzkarDetailScreen(navController = navController)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
