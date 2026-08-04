package com.github.yongjhih.appdialer.model

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

    fun isKey2Trigger(value: String): Boolean = value == KEY_2 || value == NUMBER_2
}
