package com.github.yongjhih.appdialer.ui

import android.graphics.drawable.ColorDrawable
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
import androidx.compose.material.icons.automirrored.filled.Backspace
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yongjhih.appdialer.feature.dialer.R
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition
import com.github.yongjhih.appdialer.model.KeypadValues
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.ui.theme.cardBorder
import com.github.yongjhih.appdialer.ui.theme.keypadButtonBackground
import com.github.yongjhih.appdialer.ui.theme.keypadButtonTextSecondary
import com.github.yongjhih.appdialer.ui.theme.matchedHighlight

fun KeyLabelPosition.toAlignment(): Alignment = when (this) {
    KeyLabelPosition.TOP_LEFT -> Alignment.TopStart
    KeyLabelPosition.TOP_RIGHT -> Alignment.TopEnd
    KeyLabelPosition.CENTER -> Alignment.Center
    KeyLabelPosition.BOTTOM_LEFT -> Alignment.BottomStart
    KeyLabelPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
    KeyLabelPosition.HIDDEN -> Alignment.Center
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDialerScreen(
    apps: List<AppModel>,
    searchQuery: String = "",
    settingsTriggerKey: String = AppDefaults.SETTINGS_TRIGGER_KEY,
    isZhuyinEnabled: Boolean = false,
    lettersPos: KeyLabelPosition = DefaultKeyLayout.letters,
    numberPos: KeyLabelPosition = DefaultKeyLayout.number,
    zhuyinPos: KeyLabelPosition = DefaultKeyLayout.zhuyin,
    functionPos: KeyLabelPosition = DefaultKeyLayout.function,
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
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
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
                            text = stringResource(R.string.no_matching_apps),
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeypadValues.rows.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowKeys.forEach { key ->
                                val isSettingsTrigger = KeypadValues.matchesSettingsTrigger(key, settingsTriggerKey)
                                if (key == KeypadValues.KEY_BACKSPACE) {
                                    KeypadButton(
                                        icon = Icons.AutoMirrored.Filled.Backspace,
                                        subtitleIcon = if (isSettingsTrigger) Icons.Default.Settings else null,
                                        numberPos = numberPos,
                                        lettersPos = lettersPos,
                                        zhuyinPos = zhuyinPos,
                                        functionPos = functionPos,
                                        onClick = onDeleteOneDigit,
                                        onLongClick = if (isSettingsTrigger) onOpenDialerSettings else onClearAllDigits,
                                        modifier = Modifier.weight(1f, fill = true)
                                    )
                                } else {
                                    val config = KeypadValues.configFor(key)
                                    KeypadButton(
                                        number = config.number,
                                        letters = config.letters,
                                        zhuyin = if (isZhuyinEnabled) config.zhuyin else null,
                                        subtitleIcon = if (isSettingsTrigger) Icons.Default.Settings else null,
                                        numberPos = numberPos,
                                        lettersPos = lettersPos,
                                        zhuyinPos = zhuyinPos,
                                        functionPos = functionPos,
                                        onClick = { onDigitPressed(config.key) },
                                        onLongClick = if (isSettingsTrigger) onOpenDialerSettings else null,
                                        modifier = Modifier.weight(1f, fill = true)
                                    )
                                }
                            }
                        }
                    }
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
    numberPos: KeyLabelPosition = DefaultKeyLayout.number,
    lettersPos: KeyLabelPosition = DefaultKeyLayout.letters,
    zhuyinPos: KeyLabelPosition = DefaultKeyLayout.zhuyin,
    functionPos: KeyLabelPosition = DefaultKeyLayout.function,
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
        shape = RoundedCornerShape(20.dp),
        color = colorScheme.keypadButtonBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            // 1. Backspace Icon <x (Always fixed in Center of Key 1)
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.backspace),
                    tint = colorScheme.onSurface,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center)
                )
            }

            // 2. Settings Gear Icon (Positioned at functionPos, or BottomEnd if Center)
            if (subtitleIcon != null) {
                val align = if (functionPos == KeyLabelPosition.CENTER) Alignment.BottomEnd else functionPos.toAlignment()
                Icon(
                    imageVector = subtitleIcon,
                    contentDescription = stringResource(R.string.settings),
                    tint = colorScheme.primary.copy(alpha = 0.95f),
                    modifier = Modifier
                        .size(14.dp)
                        .align(align)
                )
            }

            // 3. Number Badge
            if (!number.isNullOrEmpty()) {
                val align = numberPos.toAlignment()
                val isCenter = align == Alignment.Center
                Text(
                    text = number,
                    fontSize = if (isCenter) 18.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCenter) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(align)
                )
            }

            // 4. Letters Badge (ABC)
            if (!letters.isNullOrEmpty()) {
                val align = lettersPos.toAlignment()
                val isCenter = align == Alignment.Center
                Text(
                    text = letters,
                    fontSize = if (isCenter) 16.sp else 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCenter) colorScheme.onSurface else colorScheme.keypadButtonTextSecondary,
                    modifier = Modifier.align(align)
                )
            }

            // 5. Zhuyin Badge (ㄅㄆㄇㄈ)
            if (!zhuyin.isNullOrEmpty()) {
                val align = zhuyinPos.toAlignment()
                val isCenter = align == Alignment.Center
                Text(
                    text = zhuyin,
                    fontSize = if (isCenter) 14.sp else 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCenter) colorScheme.primary else colorScheme.primary.copy(alpha = 0.9f),
                    modifier = Modifier.align(align)
                )
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
            settingsTriggerKey = AppDefaults.SETTINGS_TRIGGER_KEY,
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
            number = KeypadValues.KEY_9,
            letters = KeypadValues.LETTER_9,
            zhuyin = KeypadValues.ZHUYIN_9,
            subtitleIcon = Icons.Default.Settings,
            onClick = {}
        )
    }
}
