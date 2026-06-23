package com.example.piringku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val Inter = FontFamily.SansSerif
val JetBrainsMono = FontFamily.Monospace

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.04f).em,
    ),
    headlineLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02f).em,
    ),
    headlineMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.05f.em,
    ),
)
