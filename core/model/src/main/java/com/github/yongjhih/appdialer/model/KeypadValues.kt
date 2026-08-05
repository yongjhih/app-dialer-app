package com.github.yongjhih.appdialer.model

data class KeypadKeyConfig(
    val key: String,
    val number: String = key,
    val letters: String,
    val zhuyin: String
)

object KeypadValues {
    const val KEY_BACKSPACE = "X"

    const val KEY_2 = "2"
    const val NUMBER_2 = "12"
    const val LETTER_2 = "ABC"
    const val ZHUYIN_2 = "ㄅㄆㄇㄈ"

    const val KEY_3 = "3"
    const val LETTER_3 = "DEF"
    const val ZHUYIN_3 = "ㄉㄊㄋㄌ"

    const val KEY_4 = "4"
    const val LETTER_4 = "GHI"
    const val ZHUYIN_4 = "ㄍㄎㄏ"

    const val KEY_5 = "5"
    const val LETTER_5 = "JKL"
    const val ZHUYIN_5 = "ㄐㄑㄒ"

    const val KEY_6 = "6"
    const val LETTER_6 = "MNO"
    const val ZHUYIN_6 = "ㄓㄔㄕㄖ"

    const val KEY_7 = "7"
    const val LETTER_7 = "PQRS"
    const val ZHUYIN_7 = "ㄗㄘㄙ"

    const val KEY_8 = "8"
    const val LETTER_8 = "TUV"
    const val ZHUYIN_8 = "ㄚㄛㄜㄝ"

    const val KEY_9 = "9"
    const val LETTER_9 = "WXYZ"
    const val ZHUYIN_9 = "ㄞㄟㄠㄡ"

    val rows = listOf(
        listOf(KEY_BACKSPACE, KEY_2, KEY_3),
        listOf(KEY_4, KEY_5, KEY_6),
        listOf(KEY_7, KEY_8, KEY_9)
    )

    private val keys = mapOf(
        KEY_2 to KeypadKeyConfig(KEY_2, NUMBER_2, LETTER_2, ZHUYIN_2),
        KEY_3 to KeypadKeyConfig(KEY_3, letters = LETTER_3, zhuyin = ZHUYIN_3),
        KEY_4 to KeypadKeyConfig(KEY_4, letters = LETTER_4, zhuyin = ZHUYIN_4),
        KEY_5 to KeypadKeyConfig(KEY_5, letters = LETTER_5, zhuyin = ZHUYIN_5),
        KEY_6 to KeypadKeyConfig(KEY_6, letters = LETTER_6, zhuyin = ZHUYIN_6),
        KEY_7 to KeypadKeyConfig(KEY_7, letters = LETTER_7, zhuyin = ZHUYIN_7),
        KEY_8 to KeypadKeyConfig(KEY_8, letters = LETTER_8, zhuyin = ZHUYIN_8),
        KEY_9 to KeypadKeyConfig(KEY_9, letters = LETTER_9, zhuyin = ZHUYIN_9)
    )

    fun configFor(key: String): KeypadKeyConfig = requireNotNull(keys[key])
}
