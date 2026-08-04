package com.github.yongjhih.appdialer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.ui.theme.cardBorder
import com.github.yongjhih.appdialer.ui.theme.keypadButtonBackground
import com.github.yongjhih.appdialer.ui.theme.settingsContainerBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDialerSettingsScreen(
    onNavigateBack: () -> Unit = {},
    onOpenSystemAppSettings: () -> Unit = {},
    isFuzzySearchEnabled: Boolean = true,
    onFuzzySearchToggle: (Boolean) -> Unit = {},
    isZhuyinModeEnabled: Boolean = false,
    onZhuyinModeToggle: (Boolean) -> Unit = {},
    settingsTriggerKey: String = "9",
    onSettingsTriggerKeyChange: (String) -> Unit = {},
    lettersPos: String = "Center",
    onLettersPosChange: (String) -> Unit = {},
    numberPos: String = "TopRight",
    onNumberPosChange: (String) -> Unit = {},
    zhuyinPos: String = "BottomLeft",
    onZhuyinPosChange: (String) -> Unit = {},
    functionPos: String = "BottomRight",
    onFunctionPosChange: (String) -> Unit = {}
) {
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var autoCloseOnLaunch by remember { mutableStateOf(true) }
    var includeSystemApps by remember { mutableStateOf(false) }
    var autoLaunchSingleMatch by remember { mutableStateOf(false) }
    var fuzzySearch by remember(isFuzzySearchEnabled) { mutableStateOf(isFuzzySearchEnabled) }
    var zhuyinMode by remember(isZhuyinModeEnabled) { mutableStateOf(isZhuyinModeEnabled) }
    var selectedTriggerKey by remember(settingsTriggerKey) { mutableStateOf(settingsTriggerKey) }

    var selectedLettersPos by remember(lettersPos) { mutableStateOf(lettersPos) }
    var selectedNumberPos by remember(numberPos) { mutableStateOf(numberPos) }
    var selectedZhuyinPos by remember(zhuyinPos) { mutableStateOf(zhuyinPos) }
    var selectedFunctionPos by remember(functionPos) { mutableStateOf(functionPos) }

    var bgDimAmount by remember { mutableFloatStateOf(0.4f) }

    val colorScheme = MaterialTheme.colorScheme

    // Solid opaque background Surface covering the full screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "App Dialer Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.surfaceVariant
                    )
                )
            },
            containerColor = colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Section 1: Interactive Keypad Layout Customization
                SettingsCategoryHeader(title = "Keypad Visual Layout (視覺化按鍵佈局配置)", icon = Icons.Default.Settings)

                InteractiveKeyLayoutPicker(
                    lettersPos = selectedLettersPos,
                    onLettersPosChange = {
                        selectedLettersPos = it
                        onLettersPosChange(it)
                    },
                    numberPos = selectedNumberPos,
                    onNumberPosChange = {
                        selectedNumberPos = it
                        onNumberPosChange(it)
                    },
                    zhuyinPos = selectedZhuyinPos,
                    onZhuyinPosChange = {
                        selectedZhuyinPos = it
                        onZhuyinPosChange(it)
                    },
                    functionPos = selectedFunctionPos,
                    onFunctionPosChange = {
                        selectedFunctionPos = it
                        onFunctionPosChange(it)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsKeySelectorItem(
                    title = "Long-Press Settings Key (長按開啟設定按鈕)",
                    subtitle = "Select which keypad button long-press opens settings",
                    selectedKey = selectedTriggerKey,
                    onKeySelected = { key ->
                        selectedTriggerKey = key
                        onSettingsTriggerKeyChange(key)
                    }
                )

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
                    title = "Zhuyin Search (ㄅㄆㄇ注音搜尋)",
                    subtitle = "Display ㄅㄆㄇㄈ on keypad and match Chinese via Zhuyin (預設關閉)",
                    checked = zhuyinMode,
                    onCheckedChange = {
                        zhuyinMode = it
                        onZhuyinModeToggle(it)
                    }
                )

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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveKeyLayoutPicker(
    lettersPos: String,
    onLettersPosChange: (String) -> Unit,
    numberPos: String,
    onNumberPosChange: (String) -> Unit,
    zhuyinPos: String,
    onZhuyinPosChange: (String) -> Unit,
    functionPos: String,
    onFunctionPosChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedSlot by remember { mutableStateOf("Center") }

    fun getElementAtSlot(slot: String): String = when (slot) {
        lettersPos -> "Letters"
        numberPos -> "Number"
        zhuyinPos -> "Zhuyin"
        functionPos -> "Function"
        else -> "None"
    }

    fun assignElementToSlot(element: String, targetSlot: String) {
        // Clear targetSlot if another element was previously there
        if (lettersPos == targetSlot) onLettersPosChange("None")
        if (numberPos == targetSlot) onNumberPosChange("None")
        if (zhuyinPos == targetSlot) onZhuyinPosChange("None")
        if (functionPos == targetSlot) onFunctionPosChange("None")

        // Assign chosen element to targetSlot
        when (element) {
            "Letters" -> onLettersPosChange(targetSlot)
            "Number" -> onNumberPosChange(targetSlot)
            "Zhuyin" -> onZhuyinPosChange(targetSlot)
            "Function" -> onFunctionPosChange(targetSlot)
        }
    }

    val slotLabels = mapOf(
        "TopLeft" to "左上",
        "TopRight" to "右上",
        "Center" to "中央",
        "BottomLeft" to "左下",
        "BottomRight" to "右下"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = colorScheme.settingsContainerBackground,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap any position on keycard below, then select element (點選按鍵卡片上位置以配置內容)",
                fontSize = 13.sp,
                color = colorScheme.outline,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Keycard Preview Diagram
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.keypadButtonBackground,
                border = BorderStroke(1.dp, colorScheme.cardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    val slots = listOf(
                        "TopLeft" to Alignment.TopStart,
                        "TopRight" to Alignment.TopEnd,
                        "Center" to Alignment.Center,
                        "BottomLeft" to Alignment.BottomStart,
                        "BottomRight" to Alignment.BottomEnd
                    )

                    slots.forEach { (slotKey, alignment) ->
                        val isFocused = (selectedSlot == slotKey)
                        val element = getElementAtSlot(slotKey)
                        val badgeText = when (element) {
                            "Letters" -> "ABC"
                            "Number" -> "12"
                            "Zhuyin" -> "ㄅㄆㄇ"
                            "Function" -> "⚙"
                            else -> slotLabels[slotKey] ?: slotKey
                        }

                        Surface(
                            modifier = Modifier
                                .align(alignment)
                                .clickable { selectedSlot = slotKey },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isFocused) colorScheme.primary else if (element != "None") colorScheme.surfaceVariant else Color.Transparent,
                            border = BorderStroke(
                                width = if (isFocused) 1.5.dp else 0.5.dp,
                                color = if (isFocused) colorScheme.primary else colorScheme.outlineVariant
                            )
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = if (slotKey == "Center") 14.sp else 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFocused) Color.Black else if (element == "Zhuyin") colorScheme.primary else colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "正在配置「${slotLabels[selectedSlot]}」位置內容：",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Element Choice Chips
            val elementChoices = listOf(
                "Letters" to "ABC (字母)",
                "Number" to "12 (數字)",
                "Zhuyin" to "ㄅㄆㄇ (注音)",
                "Function" to "⚙ Settings Icon (設定圖示)",
                "None" to "無 (清空)"
            )

            val currentAssignedElement = getElementAtSlot(selectedSlot)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                elementChoices.forEach { (elemKey, elemLabel) ->
                    val isSelected = (currentAssignedElement == elemKey)
                    Surface(
                        modifier = Modifier
                            .clickable { assignElementToSlot(elemKey, selectedSlot) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = elemLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsKeySelectorItem(
    title: String,
    subtitle: String,
    selectedKey: String,
    onKeySelected: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val availableKeys = listOf("X", "2", "3", "4", "5", "6", "7", "8", "9")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = colorScheme.settingsContainerBackground,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colorScheme.outline
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                availableKeys.forEach { key ->
                    val isSelected = (key == selectedKey)
                    Surface(
                        modifier = Modifier
                            .clickable { onKeySelected(key) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = key,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String, icon: ImageVector) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
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
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = colorScheme.settingsContainerBackground,
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.outline
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
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = colorScheme.settingsContainerBackground,
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = true)
                )
                Text(
                    text = valueDisplay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
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
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = colorScheme.settingsContainerBackground,
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colorScheme.outline
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
