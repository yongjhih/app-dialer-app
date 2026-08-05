package com.github.yongjhih.appdialer.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KeyLayoutTest {

    @Test
    fun testDefaultKeyLayoutPositions() {
        assertEquals(KeyLabelPosition.CENTER, DefaultKeyLayout.letters)
        assertEquals(KeyLabelPosition.TOP_RIGHT, DefaultKeyLayout.number)
        assertEquals(KeyLabelPosition.BOTTOM_LEFT, DefaultKeyLayout.zhuyin)
        assertEquals(KeyLabelPosition.BOTTOM_RIGHT, DefaultKeyLayout.function)
    }

    @Test
    fun testKeypadValuesMapping() {
        val key2 = KeypadValues.keys["2"]
        assertNotNull(key2)
        assertEquals("ABC", key2.letters)
        assertEquals("ㄅㄆㄇㄈ", key2.zhuyin)

        val key9 = KeypadValues.keys["9"]
        assertNotNull(key9)
        assertEquals("WXYZ", key9.letters)
        assertEquals("ㄞㄟㄠㄡ", key9.zhuyin)
    }
}
