package com.github.yongjhih.appdialer.feature.dialer.di

import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.AppLauncher
import org.koin.dsl.module

/**
 * Pure JVM fallback implementation of [AppLauncher] for previews and JVM targets.
 */
object DefaultAppLauncher : AppLauncher {
    override fun launchApp(app: AppModel) {}
    override fun openAppDetails(app: AppModel) {}
    override fun showToast(message: String) {}
}

/**
 * Koin DI module for pure Kotlin/Compose Feature module (:feature:dialer).
 */
val dialerModule = module {
    factory<AppLauncher> { DefaultAppLauncher }
}
