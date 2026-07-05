package com.deencompanion.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * LEARNING NOTE:
 * This file implements the Material Design 3 Bottom Navigation Bar for the main application layout.
 * It houses 5 tabs: Home, Quran, Prayer, Dua, and More (Settings/Dashboard).
 * By using navigationController.currentBackStackEntryAsState(), the bar automatically updates its
 * highlighted state when the user navigates between screens.
 * The BadgedBox component is pre-configured to show numeric counts or dots (e.g. prayer reminders or reading streaks).
 */
sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val hasBadge: Boolean = false,
    val badgeCount: Int = 0
) {
    object Home : BottomNavItem("Home", NavRoutes.Home.route, Icons.Rounded.Home)
    
    object Quran : BottomNavItem("Quran", NavRoutes.Quran.route, Icons.Rounded.MenuBook)
    
    object Prayer : BottomNavItem("Prayer", NavRoutes.Prayer.route, Icons.Rounded.AccessTime)
    
    object Dua : BottomNavItem("Dua", NavRoutes.Dua.route, Icons.Rounded.Favorite)
    
    object More : BottomNavItem("More", NavRoutes.Settings.route, Icons.Rounded.GridView, hasBadge = false)
}

// Global list of main bottom navigation tabs
val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Quran,
    BottomNavItem.Prayer,
    BottomNavItem.Dua,
    BottomNavItem.More
)

@Composable
fun BottomNavBar(
    navController: NavController,
    onNavigate: (BottomNavItem) -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            // Highlight item if current route matches
            val selected = currentRoute == item.route
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.hasBadge) {
                                Badge {
                                    val text = if (item.badgeCount > 0) item.badgeCount.toString() else ""
                                    Text(text = text)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    Text(text = item.title)
                }
            )
        }
    }
}
