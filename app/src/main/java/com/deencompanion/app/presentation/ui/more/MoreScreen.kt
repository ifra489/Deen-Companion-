package com.deencompanion.app.presentation.ui.more


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deencompanion.app.presentation.navigation.NavRoutes

import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.NightsStay
data class MoreMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(navController: NavController) {
    val menuItems = listOf(
        MoreMenuItem("Tasbeeh Counter", Icons.Rounded.Loop, NavRoutes.Tasbeeh.route),
        MoreMenuItem("Hadith", Icons.Rounded.Bookmark, NavRoutes.Hadith.route),
        MoreMenuItem("Morning Azkar", Icons.Rounded.WbSunny, NavRoutes.AzkarDetail.createRoute("morning")),
    MoreMenuItem("Evening Azkar", Icons.Rounded.NightsStay, NavRoutes.AzkarDetail.createRoute("evening")),
        MoreMenuItem("Habit Tracker", Icons.Rounded.CheckCircle, NavRoutes.Tracker.route),
        MoreMenuItem("My Goals", Icons.Rounded.Flag, NavRoutes.Goals.route),
        MoreMenuItem("Qaza Namaz Tracker", Icons.Rounded.EventAvailable, NavRoutes.Journal.route)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More", fontWeight = FontWeight.Bold, color = Color(0xFF212121)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color(0xFF7B1FA2)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}