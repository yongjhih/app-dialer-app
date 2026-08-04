package com.github.yongjhih.appdialer

import android.graphics.drawable.ColorDrawable
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.util.filterAndScore
import com.github.yongjhih.appdialer.util.toCjkT9Full
import com.github.yongjhih.appdialer.util.toCjkT9Initials
import com.github.yongjhih.appdialer.util.toT9
import com.github.yongjhih.appdialer.util.toT9Initials
import com.github.yongjhih.appdialer.util.toT9Words
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class T9UtilsTest {

    @Test
    fun testCharToT9() {
        assertEquals('2', 'a'.toT9())
        assertEquals('2', 'B'.toT9())
        assertEquals('2', 'c'.toT9())

        assertEquals('3', 'd'.toT9())
        assertEquals('3', 'e'.toT9())
        assertEquals('3', 'f'.toT9())

        assertEquals('4', 'g'.toT9())
        assertEquals('5', 'j'.toT9())
        assertEquals('6', 'm'.toT9())
        assertEquals('7', 'p'.toT9())
        assertEquals('8', 't'.toT9())
        assertEquals('9', 'w'.toT9())

        assertEquals('0', '0'.toT9())
        assertEquals('9', '9'.toT9())
    }

    @Test
    fun testStringToT9() {
        assertEquals("9688823", "YouTube".toT9())
        assertEquals("4664536277", "Google Maps".toT9())
    }

    @Test
    fun testInitialsT9() {
        assertEquals("46", "Google Maps".toT9Initials())
        assertEquals("3", "Dialer".toT9Initials())
    }

    @Test
    fun testFilterAndScoreExtension() {
        val dummyDrawable = ColorDrawable()
        val apps = listOf(
            createApp("YouTube", dummyDrawable),
            createApp("Google Maps", dummyDrawable),
            createApp("Chrome", dummyDrawable),
            createApp("Spotify", dummyDrawable)
        )

        // Query "968" -> YouTube
        val result1 = apps.filterAndScore("968")
        assertTrue(result1.isNotEmpty())
        assertEquals("YouTube", result1[0].label)
        assertEquals(listOf(0, 1, 2), result1[0].matchedIndices)

        // Query "46" -> Google Maps (Initials G M = 4 6)
        val result2 = apps.filterAndScore("46")
        assertTrue(result2.isNotEmpty())
        assertEquals("Google Maps", result2[0].label)

        // Query "247" -> Chrome (C H R = 2 4 7)
        val result3 = apps.filterAndScore("247")
        assertTrue(result3.isNotEmpty())
        assertEquals("Chrome", result3[0].label)
    }

    @Test
    fun testCjkT9Matching() {
        val dummyDrawable = ColorDrawable()
        val apps = listOf(
            createApp("地圖", dummyDrawable, t9CjkInitials = "38", t9CjkFull = "3488"),
            createApp("相機", dummyDrawable, t9CjkInitials = "95", t9CjkFull = "9426454"),
            createApp("カメラ", dummyDrawable, t9CjkInitials = "56", t9CjkFull = "526372"),
            createApp("設定", dummyDrawable, t9CjkInitials = "78", t9CjkFull = "738834")
        )

        // Query "38" (D T) -> 地圖
        val res1 = apps.filterAndScore("38")
        assertTrue(res1.isNotEmpty())
        assertEquals("地圖", res1[0].label)

        // Query "95" (X J) -> 相機
        val res2 = apps.filterAndScore("95")
        assertTrue(res2.isNotEmpty())
        assertEquals("相機", res2[0].label)

        // Query "56" (K M) -> カメラ
        val res3 = apps.filterAndScore("56")
        assertTrue(res3.isNotEmpty())
        assertEquals("カメラ", res3[0].label)

        // Query "78" (S T) -> 設定
        val res4 = apps.filterAndScore("78")
        assertTrue(res4.isNotEmpty())
        assertEquals("設定", res4[0].label)
    }

    @Test
    fun testRecentAppsOrderWhenQueryEmpty() {
        val dummyDrawable = ColorDrawable()
        val apps = listOf(
            createApp("Chrome", dummyDrawable),
            createApp("Google Maps", dummyDrawable),
            createApp("YouTube", dummyDrawable)
        )
        val recentPackages = listOf(
            "com.example.youtube",
            "com.example.googlemaps"
        )

        val result = apps.filterAndScore("", recentPackageNames = recentPackages)
        assertEquals("YouTube", result[0].label)
        assertEquals("Google Maps", result[1].label)
        assertEquals("Chrome", result[2].label)
    }

    @Test
    fun testFuzzySearch() {
        val dummyDrawable = ColorDrawable()
        val apps = listOf(
            createApp("Calculator", dummyDrawable),
            createApp("Settings", dummyDrawable)
        )

        // "2" (C) "7" (r) -> Calculator (C...r)
        val result = apps.filterAndScore("27", isFuzzyEnabled = true)
        assertTrue(result.isNotEmpty())
        assertEquals("Calculator", result[0].label)
        assertEquals(listOf(0, 9), result[0].matchedIndices)
    }

    private fun createApp(
        label: String,
        icon: ColorDrawable,
        t9CjkInitials: String = "",
        t9CjkFull: String = ""
    ): AppModel {
        return AppModel(
            label = label,
            packageName = "com.example.${label.lowercase().replace(" ", "")}",
            className = "MainActivity",
            icon = icon,
            t9Full = label.toT9(),
            t9Initials = label.toT9Initials(),
            t9Words = label.toT9Words(),
            t9CjkInitials = t9CjkInitials,
            t9CjkFull = t9CjkFull
        )
    }
}
