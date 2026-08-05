package com.github.yongjhih.appdialer.ui

import com.github.yongjhih.appdialer.model.AppModel

/**
 * Platform-independent abstraction for launching applications, opening settings,
 * displaying user notifications, and performing haptic feedback.
 */
interface AppLauncher {
    fun launchApp(app: AppModel)
    fun openAppDetails(app: AppModel)
    fun openSystemAppSettings() {}
    fun showToast(message: String)
    fun performHapticFeedback(isLongPress: Boolean = false) {}
}
