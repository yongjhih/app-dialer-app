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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yongjhih.appdialer.R
import com.github.yongjhih.appdialer.BuildConfig
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition
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
    isDisablePinyinOnZhuyinEnabled: Boolean = false,
    onDisablePinyinOnZhuyinToggle: (Boolean) -> Unit = {},
    settingsTriggerKey: String = AppDefaults.SETTINGS_TRIGGER_KEY,
    onSettingsTriggerKeyChange: (String) -> Unit = {},
    lettersPos: KeyLabelPosition = DefaultKeyLayout.letters,
    onLettersPosChange: (KeyLabelPosition) -> Unit = {},
    numberPos: KeyLabelPosition = DefaultKeyLayout.number,
    onNumberPosChange: (KeyLabelPosition) -> Unit = {},
    zhuyinPos: KeyLabelPosition = DefaultKeyLayout.zhuyin,
    onZhuyinPosChange: (KeyLabelPosition) -> Unit = {},
    functionPos: KeyLabelPosition = DefaultKeyLayout.function,
    onFunctionPosChange: (KeyLabelPosition) -> Unit = {}
) {
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var autoCloseOnLaunch by remember { mutableStateOf(true) }
    var includeSystemApps by remember { mutableStateOf(false) }
    var autoLaunchSingleMatch by remember { mutableStateOf(false) }
    var fuzzySearch by remember(isFuzzySearchEnabled) { mutableStateOf(isFuzzySearchEnabled) }
    var zhuyinMode by remember(isZhuyinModeEnabled) { mutableStateOf(isZhuyinModeEnabled) }
    var disablePinyinOnZhuyin by remember(isDisablePinyinOnZhuyinEnabled) { mutableStateOf(isDisablePinyinOnZhuyinEnabled) }
    var selectedTriggerKey by remember(settingsTriggerKey) { mutableStateOf(settingsTriggerKey) }

    var selectedLettersPos by remember(lettersPos) { mutableStateOf(lettersPos) }
    var selectedNumberPos by remember(numberPos) { mutableStateOf(numberPos) }
    var selectedZhuyinPos by remember(zhuyinPos) { mutableStateOf(zhuyinPos) }
    var selectedFunctionPos by remember(functionPos) { mutableStateOf(functionPos) }

    var bgDimAmount by remember { mutableFloatStateOf(AppDefaults.BACKGROUND_DIM_AMOUNT) }

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
                            text = stringResource(R.string.settings_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
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
                SettingsCategoryHeader(
                    title = stringResource(R.string.category_keypad_layout),
                    icon = Icons.Default.Settings
                )

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

                VisualKeypadTriggerSelector(
                    title = stringResource(R.string.settings_key_trigger_title),
                    subtitle = stringResource(R.string.settings_key_trigger_subtitle),
                    selectedKey = selectedTriggerKey,
                    functionPos = selectedFunctionPos,
                    onKeySelected = { key ->
                        selectedTriggerKey = key
                        onSettingsTriggerKeyChange(key)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchItem(
                    title = stringResource(R.string.haptic_feedback_title),
                    subtitle = stringResource(R.string.haptic_feedback_subtitle),
                    checked = hapticFeedbackEnabled,
                    onCheckedChange = { hapticFeedbackEnabled = it }
                )

                SettingsSliderItem(
                    title = stringResource(R.string.bg_dim_amount_title),
                    value = bgDimAmount,
                    onValueChange = { bgDimAmount = it },
                    valueDisplay = "${(bgDimAmount * 100).toInt()}%"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Search Preferences
                SettingsCategoryHeader(
                    title = stringResource(R.string.category_search_indexing),
                    icon = Icons.Default.Search
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.zhuyin_search_title),
                    subtitle = stringResource(R.string.zhuyin_search_subtitle),
                    checked = zhuyinMode,
                    onCheckedChange = {
                        zhuyinMode = it
                        onZhuyinModeToggle(it)
                    }
                )

                if (zhuyinMode) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.disable_pinyin_on_zhuyin_title),
                        subtitle = stringResource(R.string.disable_pinyin_on_zhuyin_subtitle),
                        checked = disablePinyinOnZhuyin,
                        onCheckedChange = {
                            disablePinyinOnZhuyin = it
                            onDisablePinyinOnZhuyinToggle(it)
                        }
                    )
                }

                SettingsSwitchItem(
                    title = stringResource(R.string.fuzzy_search_title),
                    subtitle = stringResource(R.string.fuzzy_search_subtitle),
                    checked = fuzzySearch,
                    onCheckedChange = {
                        fuzzySearch = it
                        onFuzzySearchToggle(it)
                    }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.include_system_apps_title),
                    subtitle = stringResource(R.string.include_system_apps_subtitle),
                    checked = includeSystemApps,
                    onCheckedChange = { includeSystemApps = it }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.auto_launch_single_title),
                    subtitle = stringResource(R.string.auto_launch_single_subtitle),
                    checked = autoLaunchSingleMatch,
                    onCheckedChange = { autoLaunchSingleMatch = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Behavior
                SettingsCategoryHeader(
                    title = stringResource(R.string.category_behavior),
                    icon = Icons.Default.Settings
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.close_after_launch_title),
                    subtitle = stringResource(R.string.close_after_launch_subtitle),
                    checked = autoCloseOnLaunch,
                    onCheckedChange = { autoCloseOnLaunch = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 4: About & System Info
                SettingsCategoryHeader(
                    title = stringResource(R.string.category_about),
                    icon = Icons.Default.Info
                )

                SettingsClickableItem(
                    title = stringResource(R.string.open_app_details_title),
                    subtitle = stringResource(R.string.open_app_details_subtitle),
                    onClick = onOpenSystemAppSettings
                )

                SettingsClickableItem(
                    title = stringResource(R.string.app_version_title),
                    subtitle = stringResource(
                        R.string.app_version_format,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                    onClick = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveKeyLayoutPicker(
    lettersPos: KeyLabelPosition,
    onLettersPosChange: (KeyLabelPosition) -> Unit,
    numberPos: KeyLabelPosition,
    onNumberPosChange: (KeyLabelPosition) -> Unit,
    zhuyinPos: KeyLabelPosition,
    onZhuyinPosChange: (KeyLabelPosition) -> Unit,
    functionPos: KeyLabelPosition,
    onFunctionPosChange: (KeyLabelPosition) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedSlot by remember { mutableStateOf(KeyLabelPosition.CENTER) }

    fun getElementAtSlot(slot: KeyLabelPosition): String = when (slot) {
        lettersPos -> "Letters"
        numberPos -> "Number"
        zhuyinPos -> "Zhuyin"
        functionPos -> "Function"
        else -> "None"
    }

    fun assignElementToSlot(element: String, targetSlot: KeyLabelPosition) {
        val currentElemAtSlot = getElementAtSlot(targetSlot)

        // Settings Function Icon CANNOT be set to "None"!
        if (currentElemAtSlot == "Function" && element == "None") {
            return
        }

        // If targetSlot currently holds "Function" and user assigns another element,
        // automatically relocate "Function" to an available slot or default to "BottomRight".
        if (currentElemAtSlot == "Function" && element != "Function") {
            val newFunctionSlot = KeyLabelPosition.entries.firstOrNull {
                it != KeyLabelPosition.HIDDEN && it != targetSlot && getElementAtSlot(it) == "None"
            } ?: DefaultKeyLayout.function
            onFunctionPosChange(newFunctionSlot)
        }

        // Clear targetSlot if another element was previously there
        if (lettersPos == targetSlot) onLettersPosChange(KeyLabelPosition.HIDDEN)
        if (numberPos == targetSlot) onNumberPosChange(KeyLabelPosition.HIDDEN)
        if (zhuyinPos == targetSlot) onZhuyinPosChange(KeyLabelPosition.HIDDEN)

        // Assign chosen element to targetSlot
        when (element) {
            "Letters" -> onLettersPosChange(targetSlot)
            "Number" -> onNumberPosChange(targetSlot)
            "Zhuyin" -> onZhuyinPosChange(targetSlot)
            "Function" -> onFunctionPosChange(targetSlot)
        }
    }

    fun resetToDefaults() {
        onLettersPosChange(DefaultKeyLayout.letters)
        onNumberPosChange(DefaultKeyLayout.number)
        onZhuyinPosChange(DefaultKeyLayout.zhuyin)
        onFunctionPosChange(DefaultKeyLayout.function)
    }

    val slotLabels = mapOf(
        KeyLabelPosition.TOP_LEFT to stringResource(R.string.slot_top_left),
        KeyLabelPosition.TOP_RIGHT to stringResource(R.string.slot_top_right),
        KeyLabelPosition.CENTER to stringResource(R.string.slot_center),
        KeyLabelPosition.BOTTOM_LEFT to stringResource(R.string.slot_bottom_left),
        KeyLabelPosition.BOTTOM_RIGHT to stringResource(R.string.slot_bottom_right)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.key_layout_picker_hint),
                    fontSize = 12.sp,
                    color = colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { resetToDefaults() },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.5.dp, colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.reset),
                        tint = colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.reset_layout),
                        fontSize = 12.sp,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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
                    val slots = KeyLabelPosition.entries.filter { it != KeyLabelPosition.HIDDEN }

                    slots.forEach { slotKey ->
                        val isFocused = (selectedSlot == slotKey)
                        val element = getElementAtSlot(slotKey)
                        val badgeText = when (element) {
                            "Letters" -> "ABC"
                            "Number" -> "12"
                            "Zhuyin" -> "ㄅㄆㄇ"
                            "Function" -> "⚙"
                            else -> slotLabels[slotKey] ?: slotKey.preferenceValue
                        }

                        Surface(
                            modifier = Modifier
                                .align(slotKey.alignment)
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
                                fontSize = if (slotKey == KeyLabelPosition.CENTER) 14.sp else 10.sp,
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
                text = stringResource(R.string.configuring_position, slotLabels[selectedSlot] ?: ""),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Element Choice Chips
            val elementChoices = listOf(
                "Letters" to stringResource(R.string.elem_letters),
                "Number" to stringResource(R.string.elem_number),
                "Zhuyin" to stringResource(R.string.elem_zhuyin),
                "Function" to stringResource(R.string.elem_function),
                "None" to stringResource(R.string.elem_none)
            )

            val currentAssignedElement = getElementAtSlot(selectedSlot)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                elementChoices.forEach { (elemKey, elemLabel) ->
                    val isSelected = (currentAssignedElement == elemKey)
                    val isNoneDisabled = (currentAssignedElement == "Function" && elemKey == "None")

                    Surface(
                        modifier = Modifier
                            .clickable(enabled = !isNoneDisabled) { assignElementToSlot(elemKey, selectedSlot) },
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isSelected -> colorScheme.primary
                            isNoneDisabled -> colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            else -> colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = elemLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isSelected -> Color.Black
                                isNoneDisabled -> colorScheme.outline.copy(alpha = 0.5f)
                                else -> colorScheme.onSurface
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VisualKeypadTriggerSelector(
    title: String,
    subtitle: String,
    selectedKey: String,
    functionPos: KeyLabelPosition = DefaultKeyLayout.function,
    onKeySelected: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val keypadGrid = listOf(
        listOf("X", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    // Gear badge position synchronizes with functionPos (fallback to BottomEnd if Center)
    val gearAlignment = if (functionPos == KeyLabelPosition.CENTER) Alignment.BottomEnd else functionPos.toAlignment()

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
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3x3 Keypad Diagram Preview Selector
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                keypadGrid.forEach { rowKeys ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowKeys.forEach { key ->
                            val isSelected = (key == selectedKey)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { onKeySelected(key) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) colorScheme.primary else colorScheme.keypadButtonBackground,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) colorScheme.primary else colorScheme.cardBorder
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (key == "X") {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = stringResource(R.string.backspace),
                                            tint = if (isSelected) Color.Black else colorScheme.onSurface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = key,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else colorScheme.onSurface
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = stringResource(R.string.settings_key),
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(15.dp)
                                                .align(gearAlignment)
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
