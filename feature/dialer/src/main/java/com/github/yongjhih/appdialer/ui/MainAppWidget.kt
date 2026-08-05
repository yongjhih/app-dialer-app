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
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.util.InMemoryRecentAppsManager
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.filterAndScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object Destinations {
    const val DIALER = "dialer"
    const val SETTINGS = "settings"
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
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

    // Load installed apps once
    LaunchedEffect(Unit) {
        allApps = loadApps()
    }

    // Reactive State Flow Pipeline with Debounce & Concurrency Protection
    LaunchedEffect(allApps, isZhuyinEnabled, isDisablePinyinOnZhuyinEnabledState) {
        snapshotFlow { searchQuery }
            .debounce(50L) // 50ms debouncing buffer for rapid T9 typing
            .flatMapLatest { query ->
                flow {
                    val recentPackages = recentAppsManager.getRecentApps()
                    val isFuzzy = recentAppsManager.isFuzzySearchEnabled()
                    val result = allApps.filterAndScore(
                        query = query,
                        recentPackageNames = recentPackages,
                        isFuzzyEnabled = isFuzzy,
                        isZhuyinEnabled = isZhuyinEnabled,
                        isDisablePinyinOnZhuyin = isDisablePinyinOnZhuyinEnabledState
                    )
                    emit(result)
                }.flowOn(Dispatchers.Default)
            }
            .collect { filtered ->
                filteredApps = filtered
            }
    }

    // Reset screen backstack on re-launch (onNewIntent signal)
    LaunchedEffect(resetSignal) {
        if (resetSignal > 0) {
            searchQuery = ""
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
                    },
                    onDeleteOneDigit = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = searchQuery.dropLast(1)
                        }
                    },
                    onClearAllDigits = {
                        searchQuery = ""
                    },
                    onAppClick = { app ->
                        launchApp(app)
                        onDismiss()
                    },
                    onAppLongClick = { app ->
                        openAppSettings(app)
                    },
                    onOpenDialerSettings = {
                        navController.navigate(Destinations.SETTINGS)
                    },
                    onDismiss = onDismiss
                )
            }

            composable(Destinations.SETTINGS) {
                AppDialerSettingsScreen(
                    settingsTriggerKey = settingsTriggerKey,
                    isZhuyinModeEnabled = isZhuyinEnabled,
                    isDisablePinyinOnZhuyinEnabled = isDisablePinyinOnZhuyinEnabledState,
                    isFuzzySearchEnabled = recentAppsManager.isFuzzySearchEnabled(),
                    lettersPos = lettersPos,
                    numberPos = numberPos,
                    zhuyinPos = zhuyinPos,
                    functionPos = functionPos,
                    onSettingsTriggerKeyChange = { key ->
                        settingsTriggerKey = key
                        recentAppsManager.setSettingsTriggerKey(key)
                    },
                    onZhuyinModeToggle = { enabled ->
                        isZhuyinEnabled = enabled
                        recentAppsManager.setZhuyinModeEnabled(enabled)
                    },
                    onDisablePinyinOnZhuyinToggle = { enabled ->
                        isDisablePinyinOnZhuyinEnabledState = enabled
                        recentAppsManager.setDisablePinyinOnZhuyinEnabled(enabled)
                    },
                    onFuzzySearchToggle = { enabled ->
                        recentAppsManager.setFuzzySearchEnabled(enabled)
                    },
                    onLettersPosChange = { pos ->
                        lettersPos = pos
                        recentAppsManager.setLettersPosition(pos)
                    },
                    onNumberPosChange = { pos ->
                        numberPos = pos
                        recentAppsManager.setNumberPosition(pos)
                    },
                    onZhuyinPosChange = { pos ->
                        zhuyinPos = pos
                        recentAppsManager.setZhuyinPosition(pos)
                    },
                    onFunctionPosChange = { pos ->
                        functionPos = pos
                        recentAppsManager.setFunctionPosition(pos)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
