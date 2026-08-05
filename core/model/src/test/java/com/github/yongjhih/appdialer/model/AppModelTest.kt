package com.github.yongjhih.appdialer.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun testAppDefaultsConstants() {
        assertEquals("9", AppDefaults.SETTINGS_TRIGGER_KEY)
        assertEquals(0.4f, AppDefaults.BACKGROUND_DIM_AMOUNT)
        assertEquals("package:", AppDefaults.PACKAGE_URI_SCHEME)
    }
}
