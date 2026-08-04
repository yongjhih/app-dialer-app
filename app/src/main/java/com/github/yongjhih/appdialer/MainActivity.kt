package com.github.yongjhih.appdialer

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        disablePendingTransition()
        super.onCreate(savedInstanceState)

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
                        }
                    )
                }
            }
        }

        loadApps()
    }

    override fun onResume() {
        super.onResume()
        applyTransparentWindow()
    }

    override fun finish() {
        super.finish()
        disablePendingTransition()
    }

    private fun applyTransparentWindow() {
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setBackgroundColor(AndroidColor.TRANSPARENT)
        window.decorView.background = null

        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window.setGravity(Gravity.BOTTOM)

        findViewById<View>(android.R.id.content)?.apply {
            background = null
            setBackgroundColor(AndroidColor.TRANSPARENT)
        }
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
        filteredApps = allApps.filterAndScore(
            query = searchQuery,
            recentPackageNames = recentPackages,
            isFuzzyEnabled = isFuzzy
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
