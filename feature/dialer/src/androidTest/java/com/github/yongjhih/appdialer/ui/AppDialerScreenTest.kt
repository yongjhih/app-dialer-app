package com.github.yongjhih.appdialer.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import org.junit.Rule
import org.junit.Test

class AppDialerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testKeypadInputAndAppFiltering() {
        var clickedApp: AppModel? = null

        val sampleApp = AppModel(
            label = "Camera",
            packageName = "com.android.camera",
            className = "CameraActivity",
            t9Full = "226372"
        )

        composeTestRule.setContent {
            AppDialerTheme {
                MainAppWidget(
                    loadApps = { listOf(sampleApp) },
                    onDismiss = { }
                )
            }
        }

        // Verify key 2 button is displayed and clickable
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").performClick()

        // Verify "Camera" app is rendered in filtered list
        composeTestRule.onNodeWithText("Camera").assertIsDisplayed()
    }
}
