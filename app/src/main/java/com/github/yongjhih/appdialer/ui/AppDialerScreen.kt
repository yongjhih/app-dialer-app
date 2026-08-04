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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDialerScreen(
    apps: List<AppModel>,
    onDigitPressed: (String) -> Unit,
    onClear: () -> Unit,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onOpenDialerSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    // Full-screen transparent hit area; only the bottom Card is opaque.
    // Do not use Material Surface here — its default would paint a solid fill.
    Box(
        modifier = Modifier
            .fillMaxSize()
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
            colors = CardDefaults.cardColors(containerColor = Color(0xF018181C)),
            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
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
                            color = Color(0x80FFFFFF),
                            fontSize = 14.sp
                        )
                    } else {
                        LazyRow(
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
                // Row 1: X (Settings on long press) / 12 ABC / 3 DEF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeypadButton(
                        icon = Icons.Default.Close,
                        subtitleIcon = Icons.Default.Settings,
                        onClick = onClear,
                        onLongClick = onOpenDialerSettings,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "12",
                        letters = "ABC",
                        onClick = { onDigitPressed("2") },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "3",
                        letters = "DEF",
                        onClick = { onDigitPressed("3") },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: 4 GHI / 5 JKL / 6 MNO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeypadButton(
                        number = "4",
                        letters = "GHI",
                        onClick = { onDigitPressed("4") },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "5",
                        letters = "JKL",
                        onClick = { onDigitPressed("5") },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "6",
                        letters = "MNO",
                        onClick = { onDigitPressed("6") },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 3: 7 PQRS / 8 TUV / 9 WXYZ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeypadButton(
                        number = "7",
                        letters = "PQRS",
                        onClick = { onDigitPressed("7") },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "8",
                        letters = "TUV",
                        onClick = { onDigitPressed("8") },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                    KeypadButton(
                        number = "9",
                        letters = "WXYZ",
                        onClick = { onDigitPressed("9") },
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

    val annotatedLabel = remember(app.label, app.matchedIndices) {
        buildAnnotatedString {
            val matchedSet = app.matchedIndices.toSet()
            app.label.forEachIndexed { index, char ->
                if (index in matchedSet) {
                    withStyle(style = SpanStyle(color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)) {
                        append(char)
                    }
                } else {
                    withStyle(style = SpanStyle(color = Color(0xFFEEEEEE))) {
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
    subtitleIcon: ImageVector? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Surface(
        modifier = modifier
            .height(56.dp)
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
        color = Color(0xFF28282E),
        border = BorderStroke(0.5.dp, Color(0x1AFFFFFF))
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
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else if (!number.isNullOrEmpty()) {
                Text(
                    text = number,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (subtitleIcon != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Icon(
                    imageVector = subtitleIcon,
                    contentDescription = "Settings",
                    tint = Color(0xFFB0B0B8),
                    modifier = Modifier.size(12.dp)
                )
            } else if (!letters.isNullOrEmpty()) {
                Text(
                    text = letters,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB0B0B8)
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
            onDigitPressed = {},
            onClear = {},
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
            number = "12",
            letters = "ABC",
            onClick = {}
        )
    }
}
