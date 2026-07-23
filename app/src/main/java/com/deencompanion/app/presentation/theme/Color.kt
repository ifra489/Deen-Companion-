package com.deencompanion.app.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Dark Theme Colors ──────────────────
val DarkBackground = Color(0xFF0F172A)
val DarkSurface    = Color(0xFF1E293B)
val DarkPrimary    = Color(0xFFD4AF37)
val DarkSecondary  = Color(0xFFFACC15)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFFCBD5E1)
val DarkBorder     = Color(0xFF334155)
val DarkSuccess    = Color(0xFF22C55E)

// ── Light Theme Colors ─────────────────
val LightBackground = Color(0xFFF8FAFC)
val LightSurface    = Color(0xFFFFFFFF)
val LightPrimary    = Color(0xFFB8860B)
val LightAccent     = Color(0xFFD4AF37)
val LightTextPrimary = Color(0xFF1E293B)
val LightTextSecondary = Color(0xFF64748B)
val LightBorder     = Color(0xFFE2E8F0)
val LightSuccess    = Color(0xFF16A34A)

// ── Shared Colors ──────────────────────
val White = Color(0xFFFFFFFF)
val ProgressRemaining = Color(0xFF475569)
val ToggleTrackOff = Color(0xFFCBD5E1)

// ── DARK Color Scheme ────────────────────
val DeenDarkColorScheme = darkColorScheme(
    primary              = DarkPrimary,
    onPrimary            = White,
    primaryContainer     = DarkSurface,
    onPrimaryContainer   = DarkPrimary,
    secondary            = DarkSecondary,
    onSecondary          = DarkBackground,
    secondaryContainer   = DarkSurface,
    onSecondaryContainer = DarkSecondary,
    background           = DarkBackground,
    onBackground         = DarkTextPrimary,
    surface              = DarkSurface,
    onSurface            = DarkTextPrimary,
    surfaceVariant       = DarkSurface,
    onSurfaceVariant     = DarkTextSecondary,
    error                = Color(0xFFEF4444),
    onError              = White,
    outline              = DarkBorder,
    outlineVariant       = DarkBorder
)

// ── LIGHT Color Scheme ───────────────────
val DeenLightColorScheme = lightColorScheme(
    primary              = LightPrimary,
    onPrimary            = White,
    primaryContainer     = LightSurface,
    onPrimaryContainer   = LightPrimary,
    secondary            = LightAccent,
    onSecondary          = White,
    secondaryContainer   = LightSurface,
    onSecondaryContainer = LightAccent,
    background           = LightBackground,
    onBackground         = LightTextPrimary,
    surface              = LightSurface,
    onSurface            = LightTextPrimary,
    surfaceVariant       = LightSurface,
    onSurfaceVariant     = LightTextSecondary,
    error                = Color(0xFFDC2626),
    onError              = White,
    outline              = LightBorder,
    outlineVariant       = LightBorder
)
