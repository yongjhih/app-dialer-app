package com.github.yongjhih.appdialer.util.di

import com.github.yongjhih.appdialer.util.CjkTransliterator
import com.github.yongjhih.appdialer.util.DefaultCjkTransliterator
import com.github.yongjhih.appdialer.util.InMemoryRecentAppsManager
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.T9TrieCache
import org.koin.dsl.module

val coreUtilModule = module {
    single<CjkTransliterator> { DefaultCjkTransliterator }
    single<RecentAppsManager> { InMemoryRecentAppsManager() }
    single { T9TrieCache() }
}
