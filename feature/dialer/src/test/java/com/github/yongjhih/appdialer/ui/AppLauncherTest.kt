package com.github.yongjhih.appdialer.ui

import com.github.yongjhih.appdialer.model.AppModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppLauncherTest {

    private class FakeAppLauncher : AppLauncher {
        var launchedApp: AppModel? = null
        var openedAppDetails: AppModel? = null
        var openedSystemSettings = false
        var lastToastMessage: String? = null

        override fun launchApp(app: AppModel) {
            launchedApp = app
        }

        override fun openAppDetails(app: AppModel) {
            openedAppDetails = app
        }

        override fun openSystemAppSettings() {
            openedSystemSettings = true
        }

        override fun showToast(message: String) {
            lastToastMessage = message
        }
    }

    @Test
    fun testAppLauncherCallbacks() {
        val launcher = FakeAppLauncher()
        val app = AppModel("YouTube", "com.google.android.youtube", "MainActivity")

        launcher.launchApp(app)
        assertEquals("YouTube", launcher.launchedApp?.label)

        launcher.openAppDetails(app)
        assertEquals("com.google.android.youtube", launcher.openedAppDetails?.packageName)

        launcher.openSystemAppSettings()
        assertTrue(launcher.openedSystemSettings)

        launcher.showToast("Test Toast")
        assertEquals("Test Toast", launcher.lastToastMessage)
    }
}
