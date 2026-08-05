package com.github.yongjhih.appdialer.util

import kotlin.test.Test
import kotlin.test.assertEquals

class CjkTransliteratorTest {

    @Test
    fun testDefaultCjkTransliterator() {
        CjkTransliterator.register(DefaultCjkTransliterator)
        assertEquals("地圖", CjkTransliterator.toLatin("地圖"))
        assertEquals("カメラ", CjkTransliterator.toLatin("カメラ"))
    }

    @Test
    fun testCustomCjkTransliteratorRegistration() {
        val customTransliterator = object : CjkTransliterator {
            override fun toLatin(text: String): String = when (text) {
                "地圖" -> "ditu"
                "カメラ" -> "kamera"
                else -> text
            }
        }

        CjkTransliterator.register(customTransliterator)
        assertEquals("ditu", CjkTransliterator.toLatin("地圖"))
        assertEquals("kamera", CjkTransliterator.toLatin("カメラ"))

        // Reset back to default
        CjkTransliterator.register(DefaultCjkTransliterator)
    }
}
