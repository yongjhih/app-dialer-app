package com.github.yongjhih.appdialer.model

import androidx.compose.ui.Alignment

/**
 * A stable, type-safe identifier for a position within a keypad button.
 * [preferenceValue] preserves values written by earlier app versions.
 */
enum class KeyLabelPosition(val preferenceValue: String, val alignment: Alignment) {
    TOP_LEFT("TopLeft", Alignment.TopStart),
    TOP_RIGHT("TopRight", Alignment.TopEnd),
    CENTER("Center", Alignment.Center),
    BOTTOM_LEFT("BottomLeft", Alignment.BottomStart),
    BOTTOM_RIGHT("BottomRight", Alignment.BottomEnd),
    HIDDEN("None", Alignment.Center);

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
