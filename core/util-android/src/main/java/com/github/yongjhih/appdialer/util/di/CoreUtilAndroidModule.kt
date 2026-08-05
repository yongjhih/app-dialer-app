package com.github.yongjhih.appdialer.util.di

import com.github.yongjhih.appdialer.util.AndroidCjkTransliterator
import com.github.yongjhih.appdialer.util.AndroidRecentAppsManager
import com.github.yongjhih.appdialer.util.CjkTransliterator
import com.github.yongjhih.appdialer.util.RecentAppsManager
import org.koin.dsl.module

val coreUtilAndroidModule = module {
    single<CjkTransliterator> { AndroidCjkTransliterator }
    single<RecentAppsManager> { AndroidRecentAppsManager(get()) }
}
