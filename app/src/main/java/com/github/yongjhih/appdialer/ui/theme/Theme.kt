package com.github.yongjhih.appdialer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700),
    secondary = Color(0xFF28282E),
    background = Color.Transparent,
    surface = Color(0xF018181C),
    onSurface = Color.White
)

@Composable
fun AppDialerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
