package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.KeyLabelPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecentAppsManagerTest {

    @Test
    fun testInMemoryRecentAppsManager() {
        val manager = InMemoryRecentAppsManager()

        // Test recent apps LRU behavior
        manager.addRecentApp("com.example.app1")
        manager.addRecentApp("com.example.app2")
        assertEquals(listOf("com.example.app2", "com.example.app1"), manager.getRecentApps())

        // Test preference toggles
        assertTrue(manager.isFuzzySearchEnabled())
        manager.setFuzzySearchEnabled(false)
        assertFalse(manager.isFuzzySearchEnabled())

        assertFalse(manager.isZhuyinModeEnabled())
        manager.setZhuyinModeEnabled(true)
        assertTrue(manager.isZhuyinModeEnabled())

        // Test position configuration
        manager.setLettersPosition(KeyLabelPosition.TOP_LEFT)
        assertEquals(KeyLabelPosition.TOP_LEFT, manager.getLettersPosition())

        // Test haptic feedback and background dim amount
        assertTrue(manager.isHapticFeedbackEnabled())
        manager.setHapticFeedbackEnabled(false)
        assertFalse(manager.isHapticFeedbackEnabled())

        assertEquals(0.0f, manager.getBackgroundDimAmount())
        manager.setBackgroundDimAmount(0.6f)
        assertEquals(0.6f, manager.getBackgroundDimAmount())
        manager.setBackgroundDimAmount(0.0f)
        assertEquals(0.0f, manager.getBackgroundDimAmount())

        // Test backspace single tap clear all
        assertFalse(manager.isBackspaceSingleTapClearAllEnabled())
        manager.setBackspaceSingleTapClearAllEnabled(true)
        assertTrue(manager.isBackspaceSingleTapClearAllEnabled())
    }
}
