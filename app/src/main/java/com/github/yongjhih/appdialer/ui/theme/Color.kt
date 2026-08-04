package com.github.yongjhih.appdialer.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Base Brand Palette
val AmberGold = Color(0xFFFFD700)
val DarkBackground = Color(0xFF121214)
val DarkSurfaceCard = Color(0xF018181C)
val DarkSurfaceVariant = Color(0xFF1C1C1E)
val DarkSurfaceContainer = Color(0xFF1E1E22)
val KeypadButtonBg = Color(0xFF28282E)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B8)
val TextMuted = Color(0xFFA0A0A0)
val BorderSubtle = Color(0x33FFFFFF)
val BorderKeypad = Color(0x1AFFFFFF)

// Semantic ColorScheme Extensions for AppDialer Design System
val ColorScheme.matchedHighlight: Color get() = primary
val ColorScheme.keypadButtonBackground: Color get() = secondaryContainer
val ColorScheme.keypadButtonTextSecondary: Color get() = onSurfaceVariant
val ColorScheme.settingsContainerBackground: Color get() = surfaceContainer
val ColorScheme.cardBorder: Color get() = outlineVariant
