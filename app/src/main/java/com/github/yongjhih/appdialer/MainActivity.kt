package com.github.yongjhih.appdialer

import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.AppDialerScreen
import com.github.yongjhih.appdialer.ui.AppDialerSettingsScreen
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.filterAndScore
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor

enum class Screen { DIALER, SETTINGS }

class MainActivity : ComponentActivity() {

    private var allApps: List<AppModel> = emptyList()
    private var filteredApps by mutableStateOf<List<AppModel>>(emptyList())
    private var searchQuery by mutableStateOf("")
    private var currentScreen by mutableStateOf(Screen.DIALER)
    private var settingsTriggerKey by mutableStateOf("9")
    private var isZhuyinEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super so launch transition doesn't flash an opaque frame.
        disablePendingTransition()
        super.onCreate(savedInstanceState)

        settingsTriggerKey = RecentAppsManager.getSettingsTriggerKey(this)
        isZhuyinEnabled = RecentAppsManager.isZhuyinModeEnabled(this)

        applyTransparentWindow()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Re-apply after edge-to-edge, which can reset background / bar colors.
        applyTransparentWindow()

        setContent {
            // ComposeView / parent containers often inherit an opaque theme background
            // (black in night mode). Clear them so only the dialer card is visible.
            val view = LocalView.current
            SideEffect {
                (view.parent as? View)?.let { parent ->
                    parent.setBackgroundColor(AndroidColor.TRANSPARENT)
                    parent.background = null
                }
                view.setBackgroundColor(AndroidColor.TRANSPARENT)
                view.background = null
                applyTransparentWindow()
            }

            AppDialerTheme {
                when (currentScreen) {
                    Screen.DIALER -> AppDialerScreen(
                        apps = filteredApps,
                        searchQuery = searchQuery,
                        settingsTriggerKey = settingsTriggerKey,
                        isZhuyinEnabled = isZhuyinEnabled,
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
                        onOpenDialerSettings = { currentScreen = Screen.SETTINGS },
                        onDismiss = { finish() }
                    )
                    Screen.SETTINGS -> AppDialerSettingsScreen(
                        onNavigateBack = { currentScreen = Screen.DIALER },
                        onOpenSystemAppSettings = { openSystemAppSettings() },
                        isFuzzySearchEnabled = RecentAppsManager.isFuzzySearchEnabled(this@MainActivity),
                        onFuzzySearchToggle = { enabled ->
                            RecentAppsManager.setFuzzySearchEnabled(this@MainActivity, enabled)
                            filterApps()
                        },
                        isZhuyinModeEnabled = isZhuyinEnabled,
                        onZhuyinModeToggle = { enabled ->
                            RecentAppsManager.setZhuyinModeEnabled(this@MainActivity, enabled)
                            isZhuyinEnabled = enabled
                            filterApps()
                        },
                        settingsTriggerKey = settingsTriggerKey,
                        onSettingsTriggerKeyChange = { key ->
                            RecentAppsManager.setSettingsTriggerKey(this@MainActivity, key)
                            settingsTriggerKey = key
                        }
                    )
                }
            }
        }

        // Content view exists after setContent; clear residual opaque backgrounds.
        clearContentBackgrounds()
        loadApps()
    }

    /**
     * Make the activity window truly translucent so home screen shows through.
     * Without TRANSLUCENT pixel format, transparent Compose pixels often render as black.
     */
    private fun applyTransparentWindow() {
        window.setFormat(PixelFormat.TRANSLUCENT)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        // Full-screen overlay (theme already has windowIsFloating=false).
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Dim behind the floating card (matches theme backgroundDimAmount).
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = window.attributes.apply {
            dimAmount = 0.4f
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
        filteredApps = allApps.filterAndScore(
            query = searchQuery,
            recentPackageNames = recentPackages,
            isFuzzyEnabled = isFuzzy,
            isZhuyinEnabled = isZhuyin
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
                Toast.makeText(this, "Cannot launch ${app.label}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error launching app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAppSettings(app: AppModel) {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${app.packageName}")
            )
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open settings for ${app.label}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSystemAppSettings() {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open settings", Toast.LENGTH_SHORT).show()
        }
    }
}
