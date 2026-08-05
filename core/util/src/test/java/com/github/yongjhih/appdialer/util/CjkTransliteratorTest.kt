package com.github.yongjhih.appdialer.util

import kotlin.test.Test
import kotlin.test.assertEquals

class CjkTransliteratorTest {

    @Test
    fun testDefaultCjkTransliterator() {
        val transliterator = DefaultCjkTransliterator
        assertEquals("地圖", transliterator.toLatin("地圖"))
        assertEquals("カメラ", transliterator.toLatin("カメラ"))

        // Default parameter injection in T9Utils
        assertEquals("3488", "地圖".toCjkT9Full())
    }

    @Test
    fun testCustomCjkTransliteratorParameterInjection() {
        val customTransliterator = CjkTransliterator { text ->
            when (text) {
                "地圖" -> "ditu"
                "カメラ" -> "kamera"
                else -> text
            }
        }

        assertEquals("ditu", customTransliterator.toLatin("地圖"))
        assertEquals("kamera", customTransliterator.toLatin("カメラ"))

        // Parameter injection into T9Utils extension functions
        assertEquals("3488", "地圖".toCjkT9Full(customTransliterator))
        assertEquals("38", "地圖".toCjkT9Initials(customTransliterator))
        assertEquals("33", "地圖".toZhuyinT9Initials(customTransliterator))
    }
}
