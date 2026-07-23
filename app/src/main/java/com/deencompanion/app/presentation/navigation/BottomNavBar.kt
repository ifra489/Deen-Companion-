package com.deencompanion.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

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
    object More : BottomNavItem("More", NavRoutes.Settings.route, Icons.Rounded.GridView)
}

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

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.hasBadge) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    if (item.badgeCount > 0) Text(text = item.badgeCount.toString())
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
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
