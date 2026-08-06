package com.github.yongjhih.appdialer.di

import com.github.yongjhih.appdialer.feature.dialer.android.AndroidAppLauncher
import com.github.yongjhih.appdialer.ui.AppLauncher
import com.github.yongjhih.appdialer.util.AndroidCjkTransliterator
import com.github.yongjhih.appdialer.util.AndroidRecentAppsManager
import com.github.yongjhih.appdialer.util.CjkTransliterator
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.T9TrieCache
import org.koin.dsl.module

/**
 * Main Koin DI module for AppDialer Application.
 * Centralizes all core and feature component bindings.
 */
val appModule = module {
    single<CjkTransliterator> { AndroidCjkTransliterator }
    single<RecentAppsManager> { AndroidRecentAppsManager(get()) }
    single { T9TrieCache() }
    factory<AppLauncher> { (onDismiss: () -> Unit) ->
        AndroidAppLauncher(get(), onDismiss)
    }
}
