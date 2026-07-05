package com.deencompanion.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple60,
    onPrimary = White,
    primaryContainer = Purple20,
    onPrimaryContainer = Purple80,
    secondary = Violet60,
    onSecondary = White,
    secondaryContainer = Violet20,
    onSecondaryContainer = Violet80,
    tertiary = Gold60,
    onTertiary = NightBlack,
    tertiaryContainer = Gold40,
    onTertiaryContainer = Gold80,
    background = NightBlack,
    onBackground = TextPrimary,
    surface = NightSurface,
    onSurface = TextPrimary,
    surfaceVariant = NightCard,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = White,
    outline = Purple40
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = White,
    primaryContainer = OffWhite,
    onPrimaryContainer = Purple20,
    secondary = Violet40,
    onSecondary = White,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Violet20,
    tertiary = Gold40,
    onTertiary = White,
    tertiaryContainer = Color(0xFFFFF8E1),
    onTertiaryContainer = Gold40,
    background = Color(0xFFFAF5FF),
    onBackground = Color(0xFF1A0050),
    surface = White,
    onSurface = Color(0xFF1A0050),
    surfaceVariant = Color(0xFFF3E5F5),
    onSurfaceVariant = Purple40,
    error = Color(0xFFB00020),
    onError = White,
    outline = Purple60
)

/**
 * LEARNING NOTE:
 * This composable function is the theme wrapper for the entire application.
 * It determines whether to use LightColorScheme or DarkColorScheme based on the system setting.
 * Dynamic coloring is disabled so that Deen Companion always utilizes its customized premium green and gold palette,
 * and compiles the standard MD3 typography, shapes, and colors into a cohesive style context.
 */
@Composable
fun DeenCompanionTheme(
    darkTheme: Boolean = true, // Default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}