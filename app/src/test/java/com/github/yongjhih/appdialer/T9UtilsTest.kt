package com.github.yongjhih.appdialer

import android.graphics.drawable.ColorDrawable
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.util.filterAndScore
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

        // Query "46" -> Google Maps (Initials G M = 4 6)
        val result2 = apps.filterAndScore("46")
        assertTrue(result2.isNotEmpty())
        assertEquals("Google Maps", result2[0].label)

        // Query "247" -> Chrome (C H R = 2 4 7)
        val result3 = apps.filterAndScore("247")
        assertTrue(result3.isNotEmpty())
        assertEquals("Chrome", result3[0].label)
    }

    private fun createApp(label: String, icon: ColorDrawable): AppModel {
        return AppModel(
            label = label,
            packageName = "com.example.${label.lowercase().replace(" ", "")}",
            className = "MainActivity",
            icon = icon,
            t9Full = label.toT9(),
            t9Initials = label.toT9Initials(),
            t9Words = label.toT9Words()
        )
    }
}
