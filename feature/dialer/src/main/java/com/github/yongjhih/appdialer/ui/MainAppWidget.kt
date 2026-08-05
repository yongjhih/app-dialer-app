package com.github.yongjhih.appdialer.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.yongjhih.appdialer.feature.dialer.R
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.filterAndScore

object Destinations {
    const val DIALER = "dialer"
    const val SETTINGS = "settings"
}

@Composable
fun MainAppWidget(
    resetSignal: Int = 0,
    onDismiss: () -> Unit = {},
    onApplyTransparentWindow: () -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val navController = rememberNavController()

    var allApps by remember { mutableStateOf<List<AppModel>>(emptyList()) }
    var filteredApps by remember { mutableStateOf<List<AppModel>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    var settingsTriggerKey by remember { mutableStateOf(RecentAppsManager.getSettingsTriggerKey(context)) }
    var isZhuyinEnabled by remember { mutableStateOf(RecentAppsManager.isZhuyinModeEnabled(context)) }
    var isDisablePinyinOnZhuyinEnabled by remember { mutableStateOf(RecentAppsManager.isDisablePinyinOnZhuyinEnabled(context)) }

    var lettersPos by remember { mutableStateOf(RecentAppsManager.getLettersPosition(context)) }
    var numberPos by remember { mutableStateOf(RecentAppsManager.getNumberPosition(context)) }
    var zhuyinPos by remember { mutableStateOf(RecentAppsManager.getZhuyinPosition(context)) }
    var functionPos by remember { mutableStateOf(RecentAppsManager.getFunctionPosition(context)) }

    fun filterApps() {
        val recentPackages = RecentAppsManager.getRecentApps(context)
        val isFuzzy = RecentAppsManager.isFuzzySearchEnabled(context)
        filteredApps = allApps.filterAndScore(
            query = searchQuery,
            recentPackageNames = recentPackages,
            isFuzzyEnabled = isFuzzy,
            isZhuyinEnabled = isZhuyinEnabled,
            isDisablePinyinOnZhuyin = isDisablePinyinOnZhuyinEnabled
        )
    }

    // Load installed apps once
    LaunchedEffect(Unit) {
        allApps = AppLoader.loadInstalledApps(context)
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

    SideEffect {
        (view.parent as? View)?.let { parent ->
            parent.setBackgroundColor(AndroidColor.TRANSPARENT)
            parent.background = null
        }
        view.setBackgroundColor(AndroidColor.TRANSPARENT)
        view.background = null
        onApplyTransparentWindow()
    }

    fun launchApp(app: AppModel) {
        try {
            RecentAppsManager.addRecentApp(context, app.packageName)
            val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
                onDismiss()
            } else {
                Toast.makeText(context, context.getString(R.string.cannot_launch_app, app.label), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_launching_app, e.message.orEmpty()), Toast.LENGTH_SHORT).show()
        }
    }

    fun openAppSettings(app: AppModel) {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(AppDefaults.PACKAGE_URI_SCHEME + app.packageName)
            )
            context.startActivity(intent)
            onDismiss()
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.cannot_open_app_settings, app.label), Toast.LENGTH_SHORT).show()
        }
    }

    fun openSystemAppSettings() {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(AppDefaults.PACKAGE_URI_SCHEME + context.packageName)
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, R.string.cannot_open_settings, Toast.LENGTH_SHORT).show()
        }
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
                    onOpenSystemAppSettings = { openSystemAppSettings() },
                    isFuzzySearchEnabled = RecentAppsManager.isFuzzySearchEnabled(context),
                    onFuzzySearchToggle = { enabled ->
                        RecentAppsManager.setFuzzySearchEnabled(context, enabled)
                        filterApps()
                    },
                    isZhuyinModeEnabled = isZhuyinEnabled,
                    onZhuyinModeToggle = { enabled ->
                        RecentAppsManager.setZhuyinModeEnabled(context, enabled)
                        isZhuyinEnabled = enabled
                        filterApps()
                    },
                    isDisablePinyinOnZhuyinEnabled = isDisablePinyinOnZhuyinEnabled,
                    onDisablePinyinOnZhuyinToggle = { enabled ->
                        RecentAppsManager.setDisablePinyinOnZhuyinEnabled(context, enabled)
                        isDisablePinyinOnZhuyinEnabled = enabled
                        filterApps()
                    },
                    settingsTriggerKey = settingsTriggerKey,
                    onSettingsTriggerKeyChange = { key ->
                        RecentAppsManager.setSettingsTriggerKey(context, key)
                        settingsTriggerKey = key
                    },
                    lettersPos = lettersPos,
                    onLettersPosChange = { pos ->
                        RecentAppsManager.setLettersPosition(context, pos)
                        lettersPos = pos
                    },
                    numberPos = numberPos,
                    onNumberPosChange = { pos ->
                        RecentAppsManager.setNumberPosition(context, pos)
                        numberPos = pos
                    },
                    zhuyinPos = zhuyinPos,
                    onZhuyinPosChange = { pos ->
                        RecentAppsManager.setZhuyinPosition(context, pos)
                        zhuyinPos = pos
                    },
                    functionPos = functionPos,
                    onFunctionPosChange = { pos ->
                        RecentAppsManager.setFunctionPosition(context, pos)
                        functionPos = pos
                    }
                )
            }
        }
    }
}
