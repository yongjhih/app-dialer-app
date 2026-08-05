package com.github.yongjhih.appdialer.util

import android.util.Log

/**
 * High-performance zero-allocation inline logger.
 * Evaluates message lambdas lazily only when logging is enabled.
 */
object Logger {

    @Volatile
    var isDebug: Boolean = true

    @JvmStatic
    inline fun d(tag: String = "AppDialer", message: () -> String) {
        if (isDebug) {
            Log.d(tag, message())
        }
    }

    @JvmStatic
    inline fun i(tag: String = "AppDialer", message: () -> String) {
        if (isDebug) {
            Log.i(tag, message())
        }
    }

    @JvmStatic
    inline fun w(tag: String = "AppDialer", throwable: Throwable? = null, message: () -> String) {
        if (isDebug) {
            if (throwable != null) {
                Log.w(tag, message(), throwable)
            } else {
                Log.w(tag, message())
            }
        }
    }

    @JvmStatic
    inline fun e(tag: String = "AppDialer", throwable: Throwable? = null, message: () -> String) {
        if (isDebug) {
            if (throwable != null) {
                Log.e(tag, message(), throwable)
            } else {
                Log.e(tag, message())
            }
        }
    }
}
