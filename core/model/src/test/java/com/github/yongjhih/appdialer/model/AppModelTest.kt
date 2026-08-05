package com.github.yongjhih.appdialer.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppModelTest {

    @Test
    fun testAppModelFieldsAndDefaults() {
        val app = AppModel(
            label = "Test App",
            packageName = "com.example.test",
            className = "MainActivity",
            t9Full = "8378"
        )

        assertEquals("Test App", app.label)
        assertEquals("com.example.test", app.packageName)
        assertEquals("MainActivity", app.className)
        assertNull(app.icon)
        assertEquals("8378", app.t9Full)
        assertEquals("", app.t9Initials)
        assertEquals(emptyList(), app.t9Words)
        assertEquals(emptyList(), app.matchedIndices)
    }

    @Test
    fun testDeferredAppModelProviders() {
        var iconEvaluated = false
        var cjkFullEvaluated = false

        val app = AppModel(
            label = "地圖",
            packageName = "com.google.android.apps.maps",
            className = "MapsActivity",
            t9Full = "3488",
            t9Initials = "3",
            t9Words = listOf("3488"),
            iconProvider = {
                iconEvaluated = true
                "MockIcon"
            },
            t9CjkFullProvider = {
                cjkFullEvaluated = true
                "3488"
            }
        )

        // Heavy providers should NOT be evaluated upon construction
        assertFalse(iconEvaluated)
        assertFalse(cjkFullEvaluated)

        // Providers are lazily evaluated only when accessed
        assertEquals("MockIcon", app.icon)
        assertTrue(iconEvaluated)

        assertEquals("3488", app.t9CjkFull)
        assertTrue(cjkFullEvaluated)
    }

    @Test
    fun testAppDefaultsConstants() {
        assertEquals("9", AppDefaults.SETTINGS_TRIGGER_KEY)
        assertEquals(0.4f, AppDefaults.BACKGROUND_DIM_AMOUNT)
        assertEquals("package:", AppDefaults.PACKAGE_URI_SCHEME)
    }
}
