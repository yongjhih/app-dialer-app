package com.github.yongjhih.appdialer

import android.app.Application
import com.github.yongjhih.appdialer.util.di.coreUtilAndroidModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppDialerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AppDialerApplication)
            modules(coreUtilAndroidModule)
        }
    }
}
