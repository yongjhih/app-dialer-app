package com.github.yongjhih.appdialer.feature.dialer.android

import android.view.View
import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.github.yongjhih.appdialer.ui.MainAppWidget
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.RecentAppsManager

@Composable
fun AndroidMainAppWidget(
    resetSignal: Int = 0,
    onDismiss: () -> Unit = {},
    onApplyTransparentWindow: () -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current

    val androidLauncher = AndroidAppLauncher(context, onDismiss)

    SideEffect {
        (view.parent as? View)?.let { parent ->
            parent.setBackgroundColor(AndroidColor.TRANSPARENT)
            parent.background = null
        }
        view.setBackgroundColor(AndroidColor.TRANSPARENT)
        view.background = null
        onApplyTransparentWindow()
    }

    MainAppWidget(
        resetSignal = resetSignal,
        loadApps = { AppLoader.loadInstalledApps(context) },
        getRecentApps = { RecentAppsManager.getRecentApps(context) },
        addRecentApp = { packageName -> RecentAppsManager.addRecentApp(context, packageName) },
        isFuzzySearchEnabled = { RecentAppsManager.isFuzzySearchEnabled(context) },
        setFuzzySearchEnabled = { enabled -> RecentAppsManager.setFuzzySearchEnabled(context, enabled) },
        isZhuyinModeEnabled = { RecentAppsManager.isZhuyinModeEnabled(context) },
        setZhuyinModeEnabled = { enabled -> RecentAppsManager.setZhuyinModeEnabled(context, enabled) },
        isDisablePinyinOnZhuyinEnabled = { RecentAppsManager.isDisablePinyinOnZhuyinEnabled(context) },
        setDisablePinyinOnZhuyinEnabled = { enabled -> RecentAppsManager.setDisablePinyinOnZhuyinEnabled(context, enabled) },
        getSettingsTriggerKey = { RecentAppsManager.getSettingsTriggerKey(context) },
        setSettingsTriggerKey = { key -> RecentAppsManager.setSettingsTriggerKey(context, key) },
        getLettersPosition = { RecentAppsManager.getLettersPosition(context) },
        setLettersPosition = { pos -> RecentAppsManager.setLettersPosition(context, pos) },
        getNumberPosition = { RecentAppsManager.getNumberPosition(context) },
        setNumberPosition = { pos -> RecentAppsManager.setNumberPosition(context, pos) },
        getZhuyinPosition = { RecentAppsManager.getZhuyinPosition(context) },
        setZhuyinPosition = { pos -> RecentAppsManager.setZhuyinPosition(context, pos) },
        getFunctionPosition = { RecentAppsManager.getFunctionPosition(context) },
        setFunctionPosition = { pos -> RecentAppsManager.setFunctionPosition(context, pos) },
        appLauncher = androidLauncher,
        onDismiss = onDismiss
    )
}
