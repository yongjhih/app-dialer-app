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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDialerScreen(
    apps: List<AppModel>,
    searchQuery: String,
    onDigitPressed: (String) -> Unit,
    onClearChar: () -> Unit,
    onClearAll: () -> Unit,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onOpenDialerSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    // Outer semi-transparent container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .combinedClickable(
                onClick = onDismiss,
                onLongClick = {}
            )
    ) {
        // Floating dialer card anchored at bottom center
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
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
                // App Search Results Grid Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    if (apps.isEmpty()) {
                        Text(
                            text = "No matching apps",
                            color = Color(0x80FFFFFF),
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

                // Search Query Display Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x1FFFFFFF)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "Type digits..." else searchQuery,
                            color = if (searchQuery.isEmpty()) Color(0x60FFFFFF) else Color(0xFFFFD700),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .combinedClickable(
                                    onClick = onClearChar,
                                    onLongClick = onClearAll
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3x3 Keypad Grid
                // Row 1: CLEAR (SETTING by long press) / 12 ABC / 3 DEF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeypadButton(
                        number = "CLEAR",
                        letters = "SETTING",
                        onClick = onClearChar,
                        onLongClick = onOpenDialerSettings,
                        modifier = Modifier.weight(1f)
                    )
                    KeypadButton(
                        number = "12",
                        letters = "ABC",
                        onClick = { onDigitPressed("2") },
                        modifier = Modifier.weight(1f)
                    )
                    KeypadButton(
                        number = "3",
                        letters = "DEF",
                        onClick = { onDigitPressed("3") },
                        modifier = Modifier.weight(1f)
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
                        modifier = Modifier.weight(1f)
                    )
                    KeypadButton(
                        number = "5",
                        letters = "JKL",
                        onClick = { onDigitPressed("5") },
                        modifier = Modifier.weight(1f)
                    )
                    KeypadButton(
                        number = "6",
                        letters = "MNO",
                        onClick = { onDigitPressed("6") },
                        modifier = Modifier.weight(1f)
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
                        modifier = Modifier.weight(1f)
                    )
                    KeypadButton(
                        number = "8",
                        letters = "TUV",
                        onClick = { onDigitPressed("8") },
                        modifier = Modifier.weight(1f)
                    )
                    KeypadButton(
                        number = "9",
                        letters = "WXYZ",
                        onClick = { onDigitPressed("9") },
                        modifier = Modifier.weight(1f)
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = app.label,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = app.label,
                fontSize = 11.sp,
                color = Color(0xFFEEEEEE),
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
    number: String,
    letters: String,
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
            Text(
                text = number,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (letters.isNotEmpty()) {
                Text(
                    text = letters,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0x9E9EA0)
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
            searchQuery = "2",
            onDigitPressed = {},
            onClearChar = {},
            onClearAll = {},
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
