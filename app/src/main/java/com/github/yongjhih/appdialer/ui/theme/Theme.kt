package com.github.yongjhih.appdialer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Background must stay fully transparent so the activity can float over the
 * home screen. Only the dialer card uses [surface] (semi-opaque).
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700),
    secondary = Color(0xFF28282E),
    background = Color.Transparent,
    surface = Color(0xF018181C),
    surfaceVariant = Color(0xFF28282E),
    onSurface = Color.White,
    onBackground = Color.White
)

@Composable
fun AppDialerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
