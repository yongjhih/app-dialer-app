package com.github.yongjhih.appdialer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.AppDialerScreen
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.filterAndScore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var allApps: List<AppModel> = emptyList()
    private var filteredApps by mutableStateOf<List<AppModel>>(emptyList())
    private var searchQuery by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppDialerTheme {
                AppDialerScreen(
                    apps = filteredApps,
                    searchQuery = searchQuery,
                    onDigitPressed = { digit ->
                        searchQuery += digit
                        filterApps()
                    },
                    onClearChar = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = searchQuery.dropLast(1)
                            filterApps()
                        }
                    },
                    onClearAll = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = ""
                            filterApps()
                        }
                    },
                    onAppClick = { app -> launchApp(app) },
                    onAppLongClick = { app -> openAppSettings(app) },
                    onOpenDialerSettings = { openDialerSettings() },
                    onDismiss = { finish() }
                )
            }
        }

        loadApps()
    }

    private fun filterApps() {
        filteredApps = allApps.filterAndScore(searchQuery)
    }


    private fun loadApps() {
        lifecycleScope.launch {
            allApps = AppLoader.loadInstalledApps(this@MainActivity)
            filterApps()
        }
    }

    private fun launchApp(app: AppModel) {
        try {
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

    private fun openDialerSettings() {
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
