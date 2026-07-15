package com.deencompanion.app.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Navy + Gold Palette ──────────────────

// Navy shades
val NavyDarkest   = Color(0xFF070E1A)
val NavyDark      = Color(0xFF0A1628)
val NavyCard      = Color(0xFF0F1F3D)
val NavySurface   = Color(0xFF162040)
val NavyLight     = Color(0xFF1E2D52)
val NavyMedium    = Color(0xFF243560)

// Gold shades
val GoldPrimary   = Color(0xFFC9A84C)

// Legacy / Shortcut for screens
val PrimaryColor = GoldPrimary
val GoldLight     = Color(0xFFE8CC7A)
val GoldSoft      = Color(0xFFF5E6B8)
val GoldDark      = Color(0xFF9C7A2E)

// Neutrals
val PureWhite     = Color(0xFFFFFFFF)
val OffWhite      = Color(0xFFF8F9FF)
val LightSurface  = Color(0xFFEEF1FB)
val TextMutedDark = Color(0xFF8899BB)
val TextMutedLight = Color(0xFF5566AA)
val ErrorRed      = Color(0xFFCF6679)

// ── DARK Color Scheme ────────────────────
// Navy background + Gold accents
val DeenDarkColorScheme = darkColorScheme(
    primary              = GoldPrimary,
    onPrimary            = NavyDarkest,
    primaryContainer     = NavyCard,
    onPrimaryContainer   = GoldLight,
    secondary            = GoldLight,
    onSecondary          = NavyDarkest,
    secondaryContainer   = NavySurface,
    onSecondaryContainer = GoldSoft,
    tertiary             = GoldLight,
    onTertiary           = NavyDarkest,
    tertiaryContainer    = NavyLight,
    onTertiaryContainer  = GoldSoft,
    background           = NavyDark,
    onBackground         = PureWhite,
    surface              = NavyCard,
    onSurface            = PureWhite,
    surfaceVariant       = NavySurface,
    onSurfaceVariant     = TextMutedDark,
    error                = ErrorRed,
    onError              = PureWhite,
    outline              = GoldDark,
    outlineVariant       = NavyLight
)

// ── LIGHT Color Scheme ───────────────────
// White background + Navy text + Gold accents
val DeenLightColorScheme = lightColorScheme(
    primary              = NavyDark,
    onPrimary            = PureWhite,
    primaryContainer     = LightSurface,
    onPrimaryContainer   = NavyDarkest,
    secondary            = GoldDark,
    onSecondary          = PureWhite,
    secondaryContainer   = GoldSoft,
    onSecondaryContainer = NavyDarkest,
    tertiary             = GoldPrimary,
    onTertiary           = NavyDarkest,
    tertiaryContainer    = GoldSoft,
    onTertiaryContainer  = NavyDarkest,
    background           = OffWhite,
    onBackground         = NavyDark,
    surface              = PureWhite,
    onSurface            = NavyDark,
    surfaceVariant       = LightSurface,
    onSurfaceVariant     = TextMutedLight,
    error                = Color(0xFFB00020),
    onError              = PureWhite,
    outline              = NavyMedium,
    outlineVariant       = LightSurface
)
