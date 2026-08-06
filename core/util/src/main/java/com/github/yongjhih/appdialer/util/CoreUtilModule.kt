package com.github.yongjhih.appdialer.util

import org.koin.dsl.module

val coreUtilModule = module {
    single<CjkTransliterator> { DefaultCjkTransliterator }
    single<RecentAppsManager> { InMemoryRecentAppsManager() }
    single { T9TrieCache() }
}
