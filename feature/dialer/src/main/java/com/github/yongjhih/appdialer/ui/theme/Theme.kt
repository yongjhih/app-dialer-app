package com.github.yongjhih.appdialer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AmberGold,
    secondary = KeypadButtonBg,
    secondaryContainer = KeypadButtonBg,
    background = DarkBackground, // Solid dark obsidian #121214 for screens (Settings, etc.)
    surface = DarkSurfaceCard,
    surfaceVariant = DarkSurfaceVariant,
    surfaceContainer = DarkSurfaceContainer,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    onBackground = TextPrimary,
    outlineVariant = BorderSubtle
)

@Composable
fun AppDialerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
