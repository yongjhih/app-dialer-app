package com.github.yongjhih.appdialer.ui

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.ui.theme.cardBorder
import com.github.yongjhih.appdialer.ui.theme.keypadButtonBackground
import com.github.yongjhih.appdialer.ui.theme.keypadButtonTextSecondary
import com.github.yongjhih.appdialer.ui.theme.matchedHighlight

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDialerScreen(
    apps: List<AppModel>,
    searchQuery: String = "",
    settingsTriggerKey: String = "9",
    isZhuyinEnabled: Boolean = false,
    onDigitPressed: (String) -> Unit,
    onDeleteOneDigit: () -> Unit,
    onClearAllDigits: () -> Unit,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onOpenDialerSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    // Automatically reset scroll position to index 0 on ANY keypress or search query change
    LaunchedEffect(searchQuery, apps) {
        if (apps.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    // Outer transparent container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .combinedClickable(
                onClick = onDismiss,
                onLongClick = {}
            )
    ) {
        // Floating dialer card anchored at bottom center with SafeArea navigationBarsPadding
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 24.dp)
                .combinedClickable(
                    onClick = {}, // Intercept click on card
                    onLongClick = {}
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            border = BorderStroke(1.dp, colorScheme.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // App Search Results: 1 Row Horizontal Scroll (LazyRow)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (apps.isEmpty()) {
                        Text(
                            text = "No matching apps",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    } else {
                        LazyRow(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(apps, key = { it.packageName + "/" + it.className }) { app ->
                                AppGridItem(
                                    app = app,
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3x3 Keypad Grid
                // Row 1: X / 12 ABC (ㄅㄆㄇㄈ) / 3 DEF (ㄉㄊㄋㄌ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeypadButton(
                        icon = Icons.Default.Close,
                        subtitleIcon = if (settingsTriggerKey == "X") Icons.Default.Settings else null,
                        onClick = onDeleteOneDigit,
                        onLongClick = if (settingsTriggerKey == "X") onOpenDialerSettings else onClearAllDigits,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "12",
                        letters = "ABC",
                        zhuyin = if (isZhuyinEnabled) "ㄅㄆㄇㄈ" else null,
                        subtitleIcon = if (settingsTriggerKey == "12" || settingsTriggerKey == "2") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("2") },
                        onLongClick = if (settingsTriggerKey == "12" || settingsTriggerKey == "2") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "3",
                        letters = "DEF",
                        zhuyin = if (isZhuyinEnabled) "ㄉㄊㄋㄌ" else null,
                        subtitleIcon = if (settingsTriggerKey == "3") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("3") },
                        onLongClick = if (settingsTriggerKey == "3") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: 4 GHI (ㄍㄎㄏ) / 5 JKL (ㄐㄑㄒ) / 6 MNO (ㄓㄔㄕㄖ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeypadButton(
                        number = "4",
                        letters = "GHI",
                        zhuyin = if (isZhuyinEnabled) "ㄍㄎㄏ" else null,
                        subtitleIcon = if (settingsTriggerKey == "4") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("4") },
                        onLongClick = if (settingsTriggerKey == "4") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "5",
                        letters = "JKL",
                        zhuyin = if (isZhuyinEnabled) "ㄐㄑㄒ" else null,
                        subtitleIcon = if (settingsTriggerKey == "5") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("5") },
                        onLongClick = if (settingsTriggerKey == "5") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "6",
                        letters = "MNO",
                        zhuyin = if (isZhuyinEnabled) "ㄓㄔㄕㄖ" else null,
                        subtitleIcon = if (settingsTriggerKey == "6") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("6") },
                        onLongClick = if (settingsTriggerKey == "6") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 3: 7 PQRS (ㄗㄘㄙ) / 8 TUV (ㄚㄛㄜㄝ) / 9 WXYZ (ㄞㄟㄠㄡ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeypadButton(
                        number = "7",
                        letters = "PQRS",
                        zhuyin = if (isZhuyinEnabled) "ㄗㄘㄙ" else null,
                        subtitleIcon = if (settingsTriggerKey == "7") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("7") },
                        onLongClick = if (settingsTriggerKey == "7") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "8",
                        letters = "TUV",
                        zhuyin = if (isZhuyinEnabled) "ㄚㄛㄜㄝ" else null,
                        subtitleIcon = if (settingsTriggerKey == "8") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("8") },
                        onLongClick = if (settingsTriggerKey == "8") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "9",
                        letters = "WXYZ",
                        zhuyin = if (isZhuyinEnabled) "ㄞㄟㄠㄡ" else null,
                        subtitleIcon = if (settingsTriggerKey == "9") Icons.Default.Settings else null,
                        onClick = { onDigitPressed("9") },
                        onLongClick = if (settingsTriggerKey == "9") onOpenDialerSettings else null,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppGridItem(
    app: AppModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val imageBitmap = remember(app) { app.icon.toImageBitmap() }
    val colorScheme = MaterialTheme.colorScheme
    val highlightColor = colorScheme.matchedHighlight
    val textColor = colorScheme.onSurface

    val annotatedLabel = remember(app.label, app.matchedIndices, highlightColor, textColor) {
        buildAnnotatedString {
            val matchedSet = app.matchedIndices.toSet()
            app.label.forEachIndexed { index, char ->
                if (index in matchedSet) {
                    withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                        append(char)
                    }
                } else {
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(char)
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .width(64.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = app.label,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = annotatedLabel,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeypadButton(
    number: String? = null,
    icon: ImageVector? = null,
    letters: String? = null,
    zhuyin: String? = null,
    subtitleIcon: ImageVector? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .height(58.dp)
            .combinedClickable(
                onClick = {
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                },
                onLongClick = if (onLongClick != null) {
                    {
                        view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                        onLongClick()
                    }
                } else null
            ),
        shape = RoundedCornerShape(14.dp),
        color = colorScheme.keypadButtonBackground,
        border = BorderStroke(0.5.dp, colorScheme.cardBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Clear",
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            } else if (!number.isNullOrEmpty()) {
                Text(
                    text = number,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
            }

            if (!letters.isNullOrEmpty() || subtitleIcon != null || !zhuyin.isNullOrEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (!letters.isNullOrEmpty()) {
                            Text(
                                text = letters,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.keypadButtonTextSecondary
                            )
                        }
                        if (subtitleIcon != null) {
                            if (!letters.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Icon(
                                imageVector = subtitleIcon,
                                contentDescription = "Settings",
                                tint = colorScheme.keypadButtonTextSecondary,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    if (!zhuyin.isNullOrEmpty()) {
                        Text(
                            text = zhuyin,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AppDialerScreenPreview() {
    val sampleApps = listOf(
        AppModel("YouTube", "com.google.android.youtube", "MainActivity", ColorDrawable(android.graphics.Color.RED), "9688823", "9", listOf("9688823")),
        AppModel("Google Maps", "com.google.android.apps.maps", "MainActivity", ColorDrawable(android.graphics.Color.GREEN), "4664536277", "46", listOf("466453", "6277")),
        AppModel("Chrome", "com.android.chrome", "MainActivity", ColorDrawable(android.graphics.Color.YELLOW), "247663", "2", listOf("247663")),
        AppModel("Spotify", "com.spotify.music", "MainActivity", ColorDrawable(android.graphics.Color.CYAN), "7768439", "7", listOf("7768439"))
    )

    AppDialerTheme {
        AppDialerScreen(
            apps = sampleApps,
            searchQuery = "",
            settingsTriggerKey = "9",
            isZhuyinEnabled = true,
            onDigitPressed = {},
            onDeleteOneDigit = {},
            onClearAllDigits = {},
            onAppClick = {},
            onAppLongClick = {},
            onOpenDialerSettings = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KeypadButtonPreview() {
    AppDialerTheme {
        KeypadButton(
            number = "9",
            letters = "WXYZ",
            zhuyin = "ㄞㄟㄠㄡ",
            subtitleIcon = Icons.Default.Settings,
            onClick = {}
        )
    }
}
