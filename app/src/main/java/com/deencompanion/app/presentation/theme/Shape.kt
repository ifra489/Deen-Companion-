package com.deencompanion.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Updated shapes for Deen Companion redesign.
 * Using 16dp rounded corners for consistent Material 3 styling.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp), // Main card corner radius
    extraLarge = RoundedCornerShape(24.dp)
)
