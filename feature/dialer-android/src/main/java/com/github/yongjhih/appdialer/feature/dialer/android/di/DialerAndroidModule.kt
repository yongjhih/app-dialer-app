package com.github.yongjhih.appdialer.feature.dialer.android.di

import com.github.yongjhih.appdialer.feature.dialer.android.AndroidAppLauncher
import com.github.yongjhih.appdialer.ui.AppLauncher
import org.koin.dsl.module

/**
 * Koin DI module for Android Feature module (:feature:dialer-android).
 */
val dialerAndroidModule = module {
    factory<AppLauncher> { (onDismiss: () -> Unit) ->
        AndroidAppLauncher(get(), onDismiss)
    }
}
