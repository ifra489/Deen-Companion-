package com.deencompanion.app.presentation.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.deencompanion.app.presentation.navigation.NavRoutes

@Composable
fun QuickAccessGrid(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                icon = Icons.Rounded.Favorite,
                label = "Prayer",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.Prayer.route) }
            )
            QuickAccessCard(
                icon = Icons.Rounded.MenuBook,
                label = "Quran",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.Quran.route) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                icon = Icons.Rounded.VolunteerActivism,
                label = "Duas",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.Dua.route) }
            )
            QuickAccessCard(
                icon = Icons.Rounded.Loop,
                label = "Tasbeeh",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.Tasbeeh.route) }
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp)
            )
        }
    }
}
