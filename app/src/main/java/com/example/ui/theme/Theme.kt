package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OrorGold,
    onPrimary = Color(0xFF0B0B1A),
    secondary = OrorCrimson,
    onSecondary = Color.White,
    tertiary = OrorGreen,
    background = OrorBackground,
    onBackground = OrorTextPrimary,
    surface = OrorSurface,
    onSurface = OrorTextPrimary,
    surfaceVariant = OrorSurfaceVariant,
    onSurfaceVariant = OrorTextSecondary,
    outline = OrorCardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

