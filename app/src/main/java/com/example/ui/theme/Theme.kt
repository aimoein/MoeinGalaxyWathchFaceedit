package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PersianMint,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0F3D3E),
    onPrimaryContainer = Color(0xFF99F6E4),
    secondary = PersianTurquoise,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0B2433),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = PersianGold,
    background = GalaxyBackground,
    onBackground = TextLightPrimary,
    surface = GalaxySurface,
    onSurface = TextLightPrimary,
    surfaceVariant = GalaxySurfaceVariant,
    onSurfaceVariant = TextLightSecondary,
    outline = GalaxyCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent Galaxy Watch branding
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
