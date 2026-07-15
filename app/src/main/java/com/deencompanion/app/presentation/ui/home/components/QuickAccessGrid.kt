package com.deencompanion.app.presentation.ui.home.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Title: "Quick Access" (medium, dark)
        Text(
            text = "Quick Access",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                icon = Icons.Rounded.Favorite,
                label = "Prayer",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.Prayer.route) }
            )
            QuickAccessCard(
                icon = Icons.Rounded.MenuBook,
                label = "Quran",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.Quran.route) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                icon = Icons.Rounded.Favorite,
                label = "Duas",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.Dua.route) }
            )
            QuickAccessCard(
                icon = Icons.Rounded.Loop,
                label = "Tasbeeh",
                color = MaterialTheme.colorScheme.primary,
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
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}