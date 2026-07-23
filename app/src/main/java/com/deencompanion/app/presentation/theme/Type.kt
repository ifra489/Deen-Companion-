package com.deencompanion.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.deencompanion.app.R

/**
 * Redesigned Typography using Inter font.
 * NOTE: Ensure inter_bold, inter_semibold, inter_medium, and inter_regular .ttf files 
 * are present in res/font folder.
 */
val Inter = FontFamily(
    Font(resId = R.font.inter_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(resId = R.font.inter_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(resId = R.font.inter_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(resId = R.font.inter_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp
    )
)
