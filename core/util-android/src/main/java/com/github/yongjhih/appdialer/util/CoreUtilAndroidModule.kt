package com.github.yongjhih.appdialer.util

import org.koin.dsl.module

val coreUtilAndroidModule = module {
    single<CjkTransliterator> { AndroidCjkTransliterator }
    single<RecentAppsManager> { AndroidRecentAppsManager(get()) }
}
