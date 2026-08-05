package com.github.yongjhih.appdialer.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.util.InMemoryRecentAppsManager
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.filterAndScore

object Destinations {
    const val DIALER = "dialer"
    const val SETTINGS = "settings"
}

@Composable
fun MainAppWidget(
    resetSignal: Int = 0,
    loadApps: suspend () -> List<AppModel> = { emptyList() },
    recentAppsManager: RecentAppsManager = InMemoryRecentAppsManager(),
    appLauncher: AppLauncher? = null,
    onDismiss: () -> Unit = {}
) {
    val navController = rememberNavController()

    var allApps by remember { mutableStateOf<List<AppModel>>(emptyList()) }
    var filteredApps by remember { mutableStateOf<List<AppModel>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    var settingsTriggerKey by remember { mutableStateOf(recentAppsManager.getSettingsTriggerKey()) }
    var isZhuyinEnabled by remember { mutableStateOf(recentAppsManager.isZhuyinModeEnabled()) }
    var isDisablePinyinOnZhuyinEnabledState by remember { mutableStateOf(recentAppsManager.isDisablePinyinOnZhuyinEnabled()) }

    var lettersPos by remember { mutableStateOf(recentAppsManager.getLettersPosition()) }
    var numberPos by remember { mutableStateOf(recentAppsManager.getNumberPosition()) }
    var zhuyinPos by remember { mutableStateOf(recentAppsManager.getZhuyinPosition()) }
    var functionPos by remember { mutableStateOf(recentAppsManager.getFunctionPosition()) }

    fun filterApps() {
        val recentPackages = recentAppsManager.getRecentApps()
        val isFuzzy = recentAppsManager.isFuzzySearchEnabled()
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
        recentAppsManager.addRecentApp(app.packageName)
        appLauncher?.launchApp(app)
    }

    fun openAppSettings(app: AppModel) {
        appLauncher?.openAppDetails(app)
    }

    AppDialerTheme {
        NavHost(
            navController = navController,
            startDestination = Destinations.DIALER,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
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
                    isFuzzySearchEnabled = recentAppsManager.isFuzzySearchEnabled(),
                    onFuzzySearchToggle = { enabled ->
                        recentAppsManager.setFuzzySearchEnabled(enabled)
                        filterApps()
                    },
                    isZhuyinModeEnabled = isZhuyinEnabled,
                    onZhuyinModeToggle = { enabled ->
                        recentAppsManager.setZhuyinModeEnabled(enabled)
                        isZhuyinEnabled = enabled
                        filterApps()
                    },
                    isDisablePinyinOnZhuyinEnabled = isDisablePinyinOnZhuyinEnabledState,
                    onDisablePinyinOnZhuyinToggle = { enabled ->
                        recentAppsManager.setDisablePinyinOnZhuyinEnabled(enabled)
                        isDisablePinyinOnZhuyinEnabledState = enabled
                        filterApps()
                    },
                    settingsTriggerKey = settingsTriggerKey,
                    onSettingsTriggerKeyChange = { key ->
                        recentAppsManager.setSettingsTriggerKey(key)
                        settingsTriggerKey = key
                    },
                    lettersPos = lettersPos,
                    onLettersPosChange = { pos ->
                        recentAppsManager.setLettersPosition(pos)
                        lettersPos = pos
                    },
                    numberPos = numberPos,
                    onNumberPosChange = { pos ->
                        recentAppsManager.setNumberPosition(pos)
                        numberPos = pos
                    },
                    zhuyinPos = zhuyinPos,
                    onZhuyinPosChange = { pos ->
                        recentAppsManager.setZhuyinPosition(pos)
                        zhuyinPos = pos
                    },
                    functionPos = functionPos,
                    onFunctionPosChange = { pos ->
                        recentAppsManager.setFunctionPosition(pos)
                        functionPos = pos
                    }
                )
            }
        }
    }
}
