package com.github.yongjhih.appdialer

import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition
import com.github.yongjhih.appdialer.ui.MainAppWidget
import com.github.yongjhih.appdialer.ui.Screen
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.filterAndScore
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor

class MainActivity : ComponentActivity() {

    private var allApps: List<AppModel> = emptyList()
    private var filteredApps by mutableStateOf<List<AppModel>>(emptyList())
    private var searchQuery by mutableStateOf("")
    private var currentScreen by mutableStateOf(Screen.DIALER)
    private var settingsTriggerKey by mutableStateOf(AppDefaults.SETTINGS_TRIGGER_KEY)
    private var isZhuyinEnabled by mutableStateOf(false)
    private var isDisablePinyinOnZhuyinEnabled by mutableStateOf(false)

    private var lettersPos by mutableStateOf(DefaultKeyLayout.letters)
    private var numberPos by mutableStateOf(DefaultKeyLayout.number)
    private var zhuyinPos by mutableStateOf(DefaultKeyLayout.zhuyin)
    private var functionPos by mutableStateOf(DefaultKeyLayout.function)

    override fun onCreate(savedInstanceState: Bundle?) {
        disablePendingTransition()
        super.onCreate(savedInstanceState)

        loadPreferences()
        applyTransparentWindow()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyTransparentWindow()

        setContent {
            MainAppWidget(
                currentScreen = currentScreen,
                onScreenChange = { screen -> currentScreen = screen },
                filteredApps = filteredApps,
                searchQuery = searchQuery,
                settingsTriggerKey = settingsTriggerKey,
                isZhuyinEnabled = isZhuyinEnabled,
                isDisablePinyinOnZhuyinEnabled = isDisablePinyinOnZhuyinEnabled,
                isFuzzySearchEnabled = RecentAppsManager.isFuzzySearchEnabled(this),
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
                onOpenSystemAppSettings = { openSystemAppSettings() },
                onFuzzySearchToggle = { enabled ->
                    RecentAppsManager.setFuzzySearchEnabled(this, enabled)
                    filterApps()
                },
                onZhuyinModeToggle = { enabled ->
                    RecentAppsManager.setZhuyinModeEnabled(this, enabled)
                    isZhuyinEnabled = enabled
                    filterApps()
                },
                onDisablePinyinOnZhuyinToggle = { enabled ->
                    RecentAppsManager.setDisablePinyinOnZhuyinEnabled(this, enabled)
                    isDisablePinyinOnZhuyinEnabled = enabled
                    filterApps()
                },
                onSettingsTriggerKeyChange = { key ->
                    RecentAppsManager.setSettingsTriggerKey(this, key)
                    settingsTriggerKey = key
                },
                onLettersPosChange = { pos ->
                    RecentAppsManager.setLettersPosition(this, pos)
                    lettersPos = pos
                },
                onNumberPosChange = { pos ->
                    RecentAppsManager.setNumberPosition(this, pos)
                    numberPos = pos
                },
                onZhuyinPosChange = { pos ->
                    RecentAppsManager.setZhuyinPosition(this, pos)
                    zhuyinPos = pos
                },
                onFunctionPosChange = { pos ->
                    RecentAppsManager.setFunctionPosition(this, pos)
                    functionPos = pos
                },
                onDismiss = { finish() },
                onApplyTransparentWindow = { applyTransparentWindow() }
            )
        }

        clearContentBackgrounds()
        loadApps()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        currentScreen = Screen.DIALER
        searchQuery = ""
        filterApps()
    }

    private fun loadPreferences() {
        settingsTriggerKey = RecentAppsManager.getSettingsTriggerKey(this)
        isZhuyinEnabled = RecentAppsManager.isZhuyinModeEnabled(this)
        isDisablePinyinOnZhuyinEnabled = RecentAppsManager.isDisablePinyinOnZhuyinEnabled(this)
        lettersPos = RecentAppsManager.getLettersPosition(this)
        numberPos = RecentAppsManager.getNumberPosition(this)
        zhuyinPos = RecentAppsManager.getZhuyinPosition(this)
        functionPos = RecentAppsManager.getFunctionPosition(this)
    }

    private fun applyTransparentWindow() {
        window.setFormat(PixelFormat.TRANSLUCENT)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = window.attributes.apply {
            dimAmount = AppDefaults.BACKGROUND_DIM_AMOUNT
            format = PixelFormat.TRANSLUCENT
        }

        window.decorView.setBackgroundColor(AndroidColor.TRANSPARENT)
        window.decorView.background = null
    }

    private fun clearContentBackgrounds() {
        findViewById<View>(android.R.id.content)?.let { content ->
            content.setBackgroundColor(AndroidColor.TRANSPARENT)
            content.background = null
            if (content is ViewGroup) {
                for (i in 0 until content.childCount) {
                    content.getChildAt(i)?.let { child ->
                        child.setBackgroundColor(AndroidColor.TRANSPARENT)
                        child.background = null
                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        disablePendingTransition()
    }

    private fun disablePendingTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun filterApps() {
        val recentPackages = RecentAppsManager.getRecentApps(this)
        val isFuzzy = RecentAppsManager.isFuzzySearchEnabled(this)
        val isZhuyin = RecentAppsManager.isZhuyinModeEnabled(this)
        val isDisablePinyin = RecentAppsManager.isDisablePinyinOnZhuyinEnabled(this)
        filteredApps = allApps.filterAndScore(
            query = searchQuery,
            recentPackageNames = recentPackages,
            isFuzzyEnabled = isFuzzy,
            isZhuyinEnabled = isZhuyin,
            isDisablePinyinOnZhuyin = isDisablePinyin
        )
    }

    private fun loadApps() {
        lifecycleScope.launch {
            allApps = AppLoader.loadInstalledApps(this@MainActivity)
            filterApps()
        }
    }

    private fun launchApp(app: AppModel) {
        try {
            RecentAppsManager.addRecentApp(this, app.packageName)
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                startActivity(launchIntent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.cannot_launch_app, app.label), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_launching_app, e.message.orEmpty()), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAppSettings(app: AppModel) {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(AppDefaults.PACKAGE_URI_SCHEME + app.packageName)
            )
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.cannot_open_app_settings, app.label), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSystemAppSettings() {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(AppDefaults.PACKAGE_URI_SCHEME + packageName)
            )
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.cannot_open_settings, Toast.LENGTH_SHORT).show()
        }
    }
}
