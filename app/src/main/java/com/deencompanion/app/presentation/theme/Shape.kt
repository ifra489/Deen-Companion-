package com.deencompanion.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * LEARNING NOTE:
 * This file defines the custom shape schemes (corner radiuses) for Deen Companion components.
 * Shapes range from extra-small (for text fields and tooltips) to extra-large (for bottom sheets and dialogs).
 * Mapping these shapes to the MaterialTheme class guarantees that all Material Components automatically
 * inherit these corner radiuses, giving the app a polished, cohesive aesthetic.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
