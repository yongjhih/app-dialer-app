package com.github.yongjhih.appdialer.model

object KeypadValues {
    const val BACKSPACE = "X"

    const val TWO = "2"
    const val TWO_LABEL = "12"
    const val TWO_LETTERS = "ABC"
    const val TWO_ZHUYIN = "ㄅㄆㄇㄈ"

    const val THREE = "3"
    const val THREE_LETTERS = "DEF"
    const val THREE_ZHUYIN = "ㄉㄊㄋㄌ"

    const val FOUR = "4"
    const val FOUR_LETTERS = "GHI"
    const val FOUR_ZHUYIN = "ㄍㄎㄏ"

    const val FIVE = "5"
    const val FIVE_LETTERS = "JKL"
    const val FIVE_ZHUYIN = "ㄐㄑㄒ"

    const val SIX = "6"
    const val SIX_LETTERS = "MNO"
    const val SIX_ZHUYIN = "ㄓㄔㄕㄖ"

    const val SEVEN = "7"
    const val SEVEN_LETTERS = "PQRS"
    const val SEVEN_ZHUYIN = "ㄗㄘㄙ"

    const val EIGHT = "8"
    const val EIGHT_LETTERS = "TUV"
    const val EIGHT_ZHUYIN = "ㄚㄛㄜㄝ"

    const val NINE = "9"
    const val NINE_LETTERS = "WXYZ"
    const val NINE_ZHUYIN = "ㄞㄟㄠㄡ"

    fun isTwoTrigger(value: String): Boolean = value == TWO || value == TWO_LABEL
}
