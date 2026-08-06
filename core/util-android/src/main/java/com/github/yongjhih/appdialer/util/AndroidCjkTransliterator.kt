package com.github.yongjhih.appdialer.util

import android.icu.text.Transliterator
import android.os.Build

/**
 * Android ICU implementation of [CjkTransliterator].
 *
 * Supports API 29+ directly via public Android ICU [Transliterator],
 * and API 24..28 via reflection fallback on hidden system ICU [Transliterator].
 */
object AndroidCjkTransliterator : CjkTransliterator {

    private fun getTransliteratorInstance(transform: String): Any? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Transliterator.getInstance(transform)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val clazz = Class.forName("android.icu.text.Transliterator")
                val getInstanceMethod = clazz.getMethod("getInstance", String::class.java)
                getInstanceMethod.invoke(null, transform)
            } else {
                null
            }
        } catch (e: Throwable) {
            null
        }
    }

    private fun transliterateString(instance: Any?, text: String): String {
        if (instance == null) return text
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && instance is Transliterator) {
                instance.transliterate(text)
            } else {
                val method = instance.javaClass.getMethod("transliterate", String::class.java)
                method.invoke(instance, text) as? String ?: text
            }
        } catch (e: Throwable) {
            text
        }
    }

    private val hanToLatin: Any? by lazy {
        getTransliteratorInstance("Han-Latin; NFD; [:Nonspacing Mark:] Remove; Lower()")
    }

    private val kanaToLatin: Any? by lazy {
        getTransliteratorInstance("Hiragana-Latin; Katakana-Latin; Lower()")
    }

    override fun toLatin(text: String): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return text
        var result = text
        hanToLatin?.let { result = transliterateString(it, result) }
        kanaToLatin?.let { result = transliterateString(it, result) }
        return result
    }
}
