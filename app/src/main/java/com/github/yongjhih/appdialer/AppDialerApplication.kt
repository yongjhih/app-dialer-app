package com.github.yongjhih.appdialer

import android.app.Application
import android.util.Log
import com.github.yongjhih.appdialer.feature.dialer.android.di.dialerAndroidModule
import com.github.yongjhih.appdialer.util.Logger
import com.github.yongjhih.appdialer.util.coreUtilAndroidModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppDialerApplication : Application() {

    companion object {
        var appStartTime: Long = 0L
    }

    override fun onCreate() {
        appStartTime = System.currentTimeMillis()
        super.onCreate()

        // Configure Logger logHandler to route to android.util.Log
        Logger.logHandler = { tag, level, message, throwable ->
            when (level) {
                "DEBUG" -> Log.d(tag, message)
                "INFO" -> Log.i(tag, message)
                "WARN" -> if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
                "ERROR" -> if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
            }
        }

        Logger.d("AppDialerTime") { "[t=0ms] Application.onCreate started" }
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AppDialerApplication)
            modules(coreUtilAndroidModule, dialerAndroidModule)
        }
        Logger.d("AppDialerTime") { "[t=${System.currentTimeMillis() - appStartTime}ms] Application.onCreate finished (Koin initialized)" }
    }
}
