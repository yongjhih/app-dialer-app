package com.github.yongjhih.appdialer.ui

import android.view.View
import android.graphics.Color as AndroidColor
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.model.KeyLabelPosition
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme

enum class Screen { DIALER, SETTINGS }

@Composable
fun MainAppWidget(
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit,
    filteredApps: List<AppModel>,
    searchQuery: String,
    settingsTriggerKey: String,
    isZhuyinEnabled: Boolean,
    isDisablePinyinOnZhuyinEnabled: Boolean,
    isFuzzySearchEnabled: Boolean,
    lettersPos: KeyLabelPosition,
    numberPos: KeyLabelPosition,
    zhuyinPos: KeyLabelPosition,
    functionPos: KeyLabelPosition,
    onDigitPressed: (String) -> Unit,
    onDeleteOneDigit: () -> Unit,
    onClearAllDigits: () -> Unit,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onOpenSystemAppSettings: () -> Unit,
    onFuzzySearchToggle: (Boolean) -> Unit,
    onZhuyinModeToggle: (Boolean) -> Unit,
    onDisablePinyinOnZhuyinToggle: (Boolean) -> Unit,
    onSettingsTriggerKeyChange: (String) -> Unit,
    onLettersPosChange: (KeyLabelPosition) -> Unit,
    onNumberPosChange: (KeyLabelPosition) -> Unit,
    onZhuyinPosChange: (KeyLabelPosition) -> Unit,
    onFunctionPosChange: (KeyLabelPosition) -> Unit,
    onDismiss: () -> Unit,
    onApplyTransparentWindow: () -> Unit
) {
    val view = LocalView.current
    SideEffect {
        (view.parent as? View)?.let { parent ->
            parent.setBackgroundColor(AndroidColor.TRANSPARENT)
            parent.background = null
        }
        view.setBackgroundColor(AndroidColor.TRANSPARENT)
        view.background = null
        onApplyTransparentWindow()
    }

    AppDialerTheme {
        // Intercept Android System Back button on Settings Screen to return to Dialer Screen
        BackHandler(enabled = (currentScreen == Screen.SETTINGS)) {
            onScreenChange(Screen.DIALER)
        }

        when (currentScreen) {
            Screen.DIALER -> AppDialerScreen(
                apps = filteredApps,
                searchQuery = searchQuery,
                settingsTriggerKey = settingsTriggerKey,
                isZhuyinEnabled = isZhuyinEnabled,
                lettersPos = lettersPos,
                numberPos = numberPos,
                zhuyinPos = zhuyinPos,
                functionPos = functionPos,
                onDigitPressed = onDigitPressed,
                onDeleteOneDigit = onDeleteOneDigit,
                onClearAllDigits = onClearAllDigits,
                onAppClick = onAppClick,
                onAppLongClick = onAppLongClick,
                onOpenDialerSettings = { onScreenChange(Screen.SETTINGS) },
                onDismiss = onDismiss
            )

            Screen.SETTINGS -> AppDialerSettingsScreen(
                onNavigateBack = { onScreenChange(Screen.DIALER) },
                onOpenSystemAppSettings = onOpenSystemAppSettings,
                isFuzzySearchEnabled = isFuzzySearchEnabled,
                onFuzzySearchToggle = onFuzzySearchToggle,
                isZhuyinModeEnabled = isZhuyinEnabled,
                onZhuyinModeToggle = onZhuyinModeToggle,
                isDisablePinyinOnZhuyinEnabled = isDisablePinyinOnZhuyinEnabled,
                onDisablePinyinOnZhuyinToggle = onDisablePinyinOnZhuyinToggle,
                settingsTriggerKey = settingsTriggerKey,
                onSettingsTriggerKeyChange = onSettingsTriggerKeyChange,
                lettersPos = lettersPos,
                onLettersPosChange = onLettersPosChange,
                numberPos = numberPos,
                onNumberPosChange = onNumberPosChange,
                zhuyinPos = zhuyinPos,
                onZhuyinPosChange = onZhuyinPosChange,
                functionPos = functionPos,
                onFunctionPosChange = onFunctionPosChange
            )
        }
    }
}
