package com.github.yongjhih.appdialer.util

/**
 * Interface and registry for CJK (Chinese/Japanese/Korean) transliteration.
 */
interface CjkTransliterator {

    fun toLatin(text: String): String

    companion object : CjkTransliterator {
        private var currentInstance: CjkTransliterator = DefaultCjkTransliterator

        fun register(transliterator: CjkTransliterator) {
            currentInstance = transliterator
        }

        override fun toLatin(text: String): String = currentInstance.toLatin(text)
    }
}

object DefaultCjkTransliterator : CjkTransliterator {
    override fun toLatin(text: String): String = text
}
