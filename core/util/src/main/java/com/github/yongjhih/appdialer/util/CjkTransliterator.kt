package com.github.yongjhih.appdialer.util

object CjkTransliterator {

    private val getInstanceMethod by lazy {
        try {
            val clazz = Class.forName("android.icu.text.Transliterator")
            clazz.getMethod("getInstance", String::class.java)
        } catch (e: Throwable) {
            null
        }
    }

    private val transliterateMethod by lazy {
        try {
            val clazz = Class.forName("android.icu.text.Transliterator")
            clazz.getMethod("transliterate", String::class.java)
        } catch (e: Throwable) {
            null
        }
    }

    private val hanToLatin: Any? by lazy {
        try {
            getInstanceMethod?.invoke(null, "Han-Latin; NFD; [:Nonspacing Mark:] Remove; Lower()")
        } catch (e: Throwable) {
            null
        }
    }

    private val kanaToLatin: Any? by lazy {
        try {
            getInstanceMethod?.invoke(null, "Hiragana-Latin; Katakana-Latin; Lower()")
        } catch (e: Throwable) {
            null
        }
    }

    fun toLatin(text: String): String {
        return try {
            var result = text
            hanToLatin?.let { instance ->
                result = transliterateMethod?.invoke(instance, result) as? String ?: result
            }
            kanaToLatin?.let { instance ->
                result = transliterateMethod?.invoke(instance, result) as? String ?: result
            }
            result
        } catch (e: Throwable) {
            text
        }
    }
}
