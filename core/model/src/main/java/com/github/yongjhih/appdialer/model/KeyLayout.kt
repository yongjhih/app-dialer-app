package com.github.yongjhih.appdialer.model

/**
 * A stable, type-safe identifier for a position within a keypad button.
 * [preferenceValue] preserves values written by earlier app versions.
 */
enum class KeyLabelPosition(val preferenceValue: String) {
    TOP_LEFT("TopLeft"),
    TOP_RIGHT("TopRight"),
    CENTER("Center"),
    BOTTOM_LEFT("BottomLeft"),
    BOTTOM_RIGHT("BottomRight"),
    HIDDEN("None");

    companion object {
        fun fromPreference(value: String?, default: KeyLabelPosition): KeyLabelPosition =
            entries.firstOrNull { it.preferenceValue == value } ?: default
    }
}

object DefaultKeyLayout {
    val letters = KeyLabelPosition.CENTER
    val number = KeyLabelPosition.TOP_RIGHT
    val zhuyin = KeyLabelPosition.BOTTOM_LEFT
    val function = KeyLabelPosition.BOTTOM_RIGHT
}
