package com.github.yongjhih.appdialer.feature.dialer.android

import android.view.View
import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.github.yongjhih.appdialer.ui.MainAppWidget
import com.github.yongjhih.appdialer.util.AndroidRecentAppsManager
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.Logger
import com.github.yongjhih.appdialer.util.RecentAppsManager

@Composable
fun AndroidMainAppWidget(
    resetSignal: Int = 0,
    onDismiss: () -> Unit = {},
    onApplyTransparentWindow: () -> Unit = {},
    recentAppsManager: RecentAppsManager? = null
) {
    val context = LocalContext.current
    val view = LocalView.current

    Logger.d("AppDialerTime") { "AndroidMainAppWidget Composable executed" }

    val manager = remember(recentAppsManager, context) {
        recentAppsManager ?: AndroidRecentAppsManager(context)
    }

    val androidLauncher = remember(context, onDismiss) {
        AndroidAppLauncher(context, onDismiss)
    }

    SideEffect {
        (view.parent as? View)?.let { parent ->
            parent.setBackgroundColor(AndroidColor.TRANSPARENT)
            parent.background = null
        }
        view.setBackgroundColor(AndroidColor.TRANSPARENT)
        view.background = null
        onApplyTransparentWindow()
        Logger.d("AppDialerTime") { "AndroidMainAppWidget SideEffect executed" }
    }

    MainAppWidget(
        resetSignal = resetSignal,
        loadApps = { AppLoader.loadInstalledApps(context) },
        loadAppsSync = { AppLoader.loadInstalledAppsSync(context) },
        recentAppsManager = manager,
        appLauncher = androidLauncher,
        onDismiss = onDismiss
    )
}
