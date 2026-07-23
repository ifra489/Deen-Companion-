package com.deencompanion.app.presentation.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.deencompanion.app.presentation.navigation.NavRoutes

data class MoreMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(navController: NavController) {
    val menuItems = listOf(
        MoreMenuItem("Bookmarks", Icons.Rounded.Bookmark, NavRoutes.Bookmarks.route),
        MoreMenuItem("Qibla Direction", Icons.Rounded.Explore, NavRoutes.Qibla.route),
        MoreMenuItem("Tasbeeh Counter", Icons.Rounded.Loop, NavRoutes.Tasbeeh.route),
        MoreMenuItem("Hadith", Icons.Rounded.MenuBook, NavRoutes.Hadith.route),
        MoreMenuItem("Morning Azkar", Icons.Rounded.WbSunny, NavRoutes.AzkarDetail.createRoute("morning")),
        MoreMenuItem("Evening Azkar", Icons.Rounded.NightsStay, NavRoutes.AzkarDetail.createRoute("evening")),
        MoreMenuItem("Habit Tracker", Icons.Rounded.CheckCircle, NavRoutes.Tracker.route),
        MoreMenuItem("My Goals", Icons.Rounded.Flag, NavRoutes.Goals.route),
        MoreMenuItem("Qaza Namaz Tracker", Icons.Rounded.EventAvailable, NavRoutes.Journal.route),
        MoreMenuItem("Zakat Calculator", Icons.Rounded.AttachMoney, NavRoutes.Zakat.route),
        MoreMenuItem("Hijri Calendar", Icons.Rounded.CalendarMonth, NavRoutes.HijriCalendar.route),
        MoreMenuItem("Achievements", Icons.Rounded.EmojiEvents, NavRoutes.Achievements.route),
        MoreMenuItem("Settings", Icons.Rounded.Settings, NavRoutes.AppSettings.route)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "More", 
                        style = MaterialTheme.typography.displayLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { menuItem ->
                MoreMenuCard(item = menuItem) {
                    navController.navigate(menuItem.route)
                }
            }
        }
    }
}

@Composable
fun MoreMenuCard(item: MoreMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}
