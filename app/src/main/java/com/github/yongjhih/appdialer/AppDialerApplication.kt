package com.github.yongjhih.appdialer

import android.app.Application
import android.util.Log
import com.github.yongjhih.appdialer.util.di.coreUtilAndroidModule
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
        Log.d("AppDialerTime", "[t=0ms] Application.onCreate started")
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AppDialerApplication)
            modules(coreUtilAndroidModule)
        }
        Log.d("AppDialerTime", "[t=${System.currentTimeMillis() - appStartTime}ms] Application.onCreate finished (Koin initialized)")
    }
}
