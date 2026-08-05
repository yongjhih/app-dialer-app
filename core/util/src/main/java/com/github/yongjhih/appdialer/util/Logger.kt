package com.github.yongjhih.appdialer.util

/**
 * High-performance zero-allocation inline logger.
 * Evaluates message lambdas lazily only when logging is enabled.
 */
object Logger {

    @Volatile
    var isDebug: Boolean = true

    @Volatile
    var logHandler: ((tag: String, level: String, message: String, throwable: Throwable?) -> Unit)? = null

    @JvmStatic
    inline fun d(tag: String = "AppDialer", message: () -> String) {
        if (isDebug) {
            val handler = logHandler
            if (handler != null) {
                handler(tag, "DEBUG", message(), null)
            } else {
                println("[$tag] DEBUG: ${message()}")
            }
        }
    }

    @JvmStatic
    inline fun i(tag: String = "AppDialer", message: () -> String) {
        if (isDebug) {
            val handler = logHandler
            if (handler != null) {
                handler(tag, "INFO", message(), null)
            } else {
                println("[$tag] INFO: ${message()}")
            }
        }
    }

    @JvmStatic
    inline fun w(tag: String = "AppDialer", throwable: Throwable? = null, message: () -> String) {
        if (isDebug) {
            val handler = logHandler
            if (handler != null) {
                handler(tag, "WARN", message(), throwable)
            } else {
                println("[$tag] WARN: ${message()}")
                throwable?.printStackTrace()
            }
        }
    }

    @JvmStatic
    inline fun e(tag: String = "AppDialer", throwable: Throwable? = null, message: () -> String) {
        if (isDebug) {
            val handler = logHandler
            if (handler != null) {
                handler(tag, "ERROR", message(), throwable)
            } else {
                println("[$tag] ERROR: ${message()}")
                throwable?.printStackTrace()
            }
        }
    }
}
