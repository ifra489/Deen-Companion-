package com.deencompanion.app.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AdhanToggleSetting(
    viewModel: AdhanSettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val isEnabled by viewModel.isEnabled.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Adhan Notifications",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF212121)
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = { checked ->
                viewModel.toggleAdhanNotifications(checked)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF212121),
                checkedTrackColor = Color(0xFFE0E0E0),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}
