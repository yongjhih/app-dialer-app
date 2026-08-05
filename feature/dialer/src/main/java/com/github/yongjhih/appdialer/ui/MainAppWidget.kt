package com.github.yongjhih.appdialer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.util.filterAndScore

object Destinations {
    const val DIALER = "dialer"
    const val SETTINGS = "settings"
}

@Composable
fun MainAppWidget(
    resetSignal: Int = 0,
    loadApps: suspend () -> List<AppModel> = { emptyList() },
    getRecentApps: () -> List<String> = { emptyList() },
    addRecentApp: (String) -> Unit = {},
    isFuzzySearchEnabled: () -> Boolean = { true },
    setFuzzySearchEnabled: (Boolean) -> Unit = {},
    isZhuyinModeEnabled: () -> Boolean = { true },
    setZhuyinModeEnabled: (Boolean) -> Unit = {},
    isDisablePinyinOnZhuyinEnabled: () -> Boolean = { false },
    setDisablePinyinOnZhuyinEnabled: (Boolean) -> Unit = {},
    getSettingsTriggerKey: () -> String = { AppDefaults.SETTINGS_TRIGGER_KEY },
    setSettingsTriggerKey: (String) -> Unit = {},
    getLettersPosition: () -> KeyLabelPosition = { DefaultKeyLayout.letters },
    setLettersPosition: (KeyLabelPosition) -> Unit = {},
    getNumberPosition: () -> KeyLabelPosition = { DefaultKeyLayout.number },
    setNumberPosition: (KeyLabelPosition) -> Unit = {},
    getZhuyinPosition: () -> KeyLabelPosition = { DefaultKeyLayout.zhuyin },
    setZhuyinPosition: (KeyLabelPosition) -> Unit = {},
    getFunctionPosition: () -> KeyLabelPosition = { DefaultKeyLayout.function },
    setFunctionPosition: (KeyLabelPosition) -> Unit = {},
    appLauncher: AppLauncher? = null,
    onDismiss: () -> Unit = {}
) {
    val navController = rememberNavController()

    var allApps by remember { mutableStateOf<List<AppModel>>(emptyList()) }
    var filteredApps by remember { mutableStateOf<List<AppModel>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    var settingsTriggerKey by remember { mutableStateOf(getSettingsTriggerKey()) }
    var isZhuyinEnabled by remember { mutableStateOf(isZhuyinModeEnabled()) }
    var isDisablePinyinOnZhuyinEnabledState by remember { mutableStateOf(isDisablePinyinOnZhuyinEnabled()) }

    var lettersPos by remember { mutableStateOf(getLettersPosition()) }
    var numberPos by remember { mutableStateOf(getNumberPosition()) }
    var zhuyinPos by remember { mutableStateOf(getZhuyinPosition()) }
    var functionPos by remember { mutableStateOf(getFunctionPosition()) }

    fun filterApps() {
        val recentPackages = getRecentApps()
        val isFuzzy = isFuzzySearchEnabled()
        filteredApps = allApps.filterAndScore(
            query = searchQuery,
            recentPackageNames = recentPackages,
            isFuzzyEnabled = isFuzzy,
            isZhuyinEnabled = isZhuyinEnabled,
            isDisablePinyinOnZhuyin = isDisablePinyinOnZhuyinEnabledState
        )
    }

    // Load installed apps once
    LaunchedEffect(Unit) {
        allApps = loadApps()
        filterApps()
    }

    // Reset screen backstack on re-launch (onNewIntent signal)
    LaunchedEffect(resetSignal) {
        if (resetSignal > 0) {
            searchQuery = ""
            filterApps()
            navController.navigate(Destinations.DIALER) {
                popUpTo(Destinations.DIALER) { inclusive = true }
            }
        }
    }

    fun launchApp(app: AppModel) {
        addRecentApp(app.packageName)
        appLauncher?.launchApp(app)
    }

    fun openAppSettings(app: AppModel) {
        appLauncher?.openAppDetails(app)
    }

    AppDialerTheme {
        NavHost(
            navController = navController,
            startDestination = Destinations.DIALER
        ) {
            composable(Destinations.DIALER) {
                AppDialerScreen(
                    apps = filteredApps,
                    searchQuery = searchQuery,
                    settingsTriggerKey = settingsTriggerKey,
                    isZhuyinEnabled = isZhuyinEnabled,
                    lettersPos = lettersPos,
                    numberPos = numberPos,
                    zhuyinPos = zhuyinPos,
                    functionPos = functionPos,
                    onDigitPressed = { digit ->
                        searchQuery += digit
                        filterApps()
                    },
                    onDeleteOneDigit = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = searchQuery.dropLast(1)
                            filterApps()
                        }
                    },
                    onClearAllDigits = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = ""
                            filterApps()
                        }
                    },
                    onAppClick = { app -> launchApp(app) },
                    onAppLongClick = { app -> openAppSettings(app) },
                    onOpenDialerSettings = { navController.navigate(Destinations.SETTINGS) },
                    onDismiss = onDismiss
                )
            }

            composable(Destinations.SETTINGS) {
                AppDialerSettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOpenSystemAppSettings = { appLauncher?.openSystemAppSettings() },
                    isFuzzySearchEnabled = isFuzzySearchEnabled(),
                    onFuzzySearchToggle = { enabled ->
                        setFuzzySearchEnabled(enabled)
                        filterApps()
                    },
                    isZhuyinModeEnabled = isZhuyinEnabled,
                    onZhuyinModeToggle = { enabled ->
                        setZhuyinModeEnabled(enabled)
                        isZhuyinEnabled = enabled
                        filterApps()
                    },
                    isDisablePinyinOnZhuyinEnabled = isDisablePinyinOnZhuyinEnabledState,
                    onDisablePinyinOnZhuyinToggle = { enabled ->
                        setDisablePinyinOnZhuyinEnabled(enabled)
                        isDisablePinyinOnZhuyinEnabledState = enabled
                        filterApps()
                    },
                    settingsTriggerKey = settingsTriggerKey,
                    onSettingsTriggerKeyChange = { key ->
                        setSettingsTriggerKey(key)
                        settingsTriggerKey = key
                    },
                    lettersPos = lettersPos,
                    onLettersPosChange = { pos ->
                        setLettersPosition(pos)
                        lettersPos = pos
                    },
                    numberPos = numberPos,
                    onNumberPosChange = { pos ->
                        setNumberPosition(pos)
                        numberPos = pos
                    },
                    zhuyinPos = zhuyinPos,
                    onZhuyinPosChange = { pos ->
                        setZhuyinPosition(pos)
                        zhuyinPos = pos
                    },
                    functionPos = functionPos,
                    onFunctionPosChange = { pos ->
                        setFunctionPosition(pos)
                        functionPos = pos
                    }
                )
            }
        }
    }
}
