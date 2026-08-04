package com.github.yongjhih.appdialer.util

import android.icu.text.Transliterator
import android.os.Build

object CjkTransliterator {

    private val hanToLatin: Transliterator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                Transliterator.getInstance("Han-Latin; NFD; [:Nonspacing Mark:] Remove; Lower()")
            } catch (e: Exception) {
                null
            }
        } else null
    }

    private val kanaToLatin: Transliterator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                Transliterator.getInstance("Hiragana-Latin; Katakana-Latin; Lower()")
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun toLatin(text: String): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return text
        return try {
            var result = text
            hanToLatin?.let { result = it.transliterate(result) }
            kanaToLatin?.let { result = it.transliterate(result) }
            result
        } catch (e: Exception) {
            text
        }
    }
}
