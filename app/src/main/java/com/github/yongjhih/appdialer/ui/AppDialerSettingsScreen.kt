package com.github.yongjhih.appdialer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDialerSettingsScreen(
    onNavigateBack: () -> Unit = {},
    onOpenSystemAppSettings: () -> Unit = {},
    isFuzzySearchEnabled: Boolean = true,
    onFuzzySearchToggle: (Boolean) -> Unit = {}
) {
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var autoCloseOnLaunch by remember { mutableStateOf(true) }
    var includeSystemApps by remember { mutableStateOf(false) }
    var autoLaunchSingleMatch by remember { mutableStateOf(false) }
    var fuzzySearch by remember(isFuzzySearchEnabled) { mutableStateOf(isFuzzySearchEnabled) }
    var bgDimAmount by remember { mutableFloatStateOf(0.4f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "App Dialer Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                )
            )
        },
        containerColor = Color(0xFF121214)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Section 1: Appearance & Touch
            SettingsCategoryHeader(title = "Appearance & Touch", icon = Icons.Default.Settings)

            SettingsSwitchItem(
                title = "Haptic Feedback",
                subtitle = "Vibrate keypress on dialer keypad",
                checked = hapticFeedbackEnabled,
                onCheckedChange = { hapticFeedbackEnabled = it }
            )

            SettingsSliderItem(
                title = "Background Dim Amount",
                value = bgDimAmount,
                onValueChange = { bgDimAmount = it },
                valueDisplay = "${(bgDimAmount * 100).toInt()}%"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Search Preferences
            SettingsCategoryHeader(title = "Search & Indexing", icon = Icons.Default.Search)

            SettingsSwitchItem(
                title = "Fuzzy Search (模糊搜尋)",
                subtitle = "Allow non-contiguous character matching (允許間隔字元匹配)",
                checked = fuzzySearch,
                onCheckedChange = {
                    fuzzySearch = it
                    onFuzzySearchToggle(it)
                }
            )

            SettingsSwitchItem(
                title = "Include System Apps",
                subtitle = "Show pre-installed system apps in search results",
                checked = includeSystemApps,
                onCheckedChange = { includeSystemApps = it }
            )

            SettingsSwitchItem(
                title = "Auto Launch Single Match",
                subtitle = "Automatically open app when only 1 match remains",
                checked = autoLaunchSingleMatch,
                onCheckedChange = { autoLaunchSingleMatch = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: Behavior
            SettingsCategoryHeader(title = "Behavior", icon = Icons.Default.Settings)

            SettingsSwitchItem(
                title = "Close Dialer after Launch",
                subtitle = "Dismiss AppDialer automatically when an app opens",
                checked = autoCloseOnLaunch,
                onCheckedChange = { autoCloseOnLaunch = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: About & System Info
            SettingsCategoryHeader(title = "About", icon = Icons.Default.Info)

            SettingsClickableItem(
                title = "Open App System Details",
                subtitle = "Manage permissions, notifications, and battery",
                onClick = onOpenSystemAppSettings
            )

            SettingsClickableItem(
                title = "App Version",
                subtitle = "1.0.0 (Build 1)",
                onClick = {}
            )
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFFD700),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color(0xFF1E1E22),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = true)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFFA0A0A0)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueDisplay: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color(0xFF1E1E22),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.weight(1f, fill = true)
                )
                Text(
                    text = valueDisplay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0.1f..0.9f
            )
        }
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color(0xFF1E1E22),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFFA0A0A0)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppDialerSettingsScreenPreview() {
    AppDialerTheme {
        AppDialerSettingsScreen()
    }
}
