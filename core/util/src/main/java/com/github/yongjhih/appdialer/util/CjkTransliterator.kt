package com.github.yongjhih.appdialer.util

/**
 * Functional interface for CJK (Chinese/Japanese/Korean) transliteration.
 */
fun interface CjkTransliterator {
    fun toLatin(text: String): String
}

/**
 * Default no-op transliterator that returns the input text unchanged.
 */
object DefaultCjkTransliterator : CjkTransliterator {
    override fun toLatin(text: String): String = text
}
