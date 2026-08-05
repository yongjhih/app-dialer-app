package com.github.yongjhih.appdialer.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.theme.AppDialerTheme
import com.github.yongjhih.appdialer.util.InMemoryRecentAppsManager
import com.github.yongjhih.appdialer.util.Logger
import com.github.yongjhih.appdialer.util.RecentAppsManager
import com.github.yongjhih.appdialer.util.T9TrieCache
import com.github.yongjhih.appdialer.util.filterAndScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

object Destinations {
    const val DIALER = "dialer"
    const val SETTINGS = "settings"
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainAppWidget(
    resetSignal: Int = 0,
    loadApps: suspend () -> List<AppModel> = { emptyList() },
    loadAppsSync: () -> List<AppModel>? = { null },
    recentAppsManager: RecentAppsManager = InMemoryRecentAppsManager(),
    appLauncher: AppLauncher? = null,
    onDismiss: () -> Unit = {}
) {
    val navController = rememberNavController()

    var searchQuery by remember { mutableStateOf("") }

    var settingsTriggerKey by remember { mutableStateOf(recentAppsManager.getSettingsTriggerKey()) }
    var isZhuyinEnabled by remember { mutableStateOf(recentAppsManager.isZhuyinModeEnabled()) }
    var isDisablePinyinOnZhuyinEnabledState by remember { mutableStateOf(recentAppsManager.isDisablePinyinOnZhuyinEnabled()) }

    var lettersPos by remember { mutableStateOf(recentAppsManager.getLettersPosition()) }
    var numberPos by remember { mutableStateOf(recentAppsManager.getNumberPosition()) }
    var zhuyinPos by remember { mutableStateOf(recentAppsManager.getZhuyinPosition()) }
    var functionPos by remember { mutableStateOf(recentAppsManager.getFunctionPosition()) }

    // Shared T9 trie: rebuilt when [allApps] changes; used to prune search candidates.
    val trieCache = remember { T9TrieCache() }

    // Instant Frame 1 Initial State Retrieval (Pre-warmed from MainActivity.onCreate)
    val initialLoadedApps = remember {
        loadAppsSync() ?: emptyList()
    }

    val initialFilteredApps = remember(initialLoadedApps, isZhuyinEnabled, isDisablePinyinOnZhuyinEnabledState) {
        if (initialLoadedApps.isNotEmpty()) {
            val recents = recentAppsManager.getRecentApps()
            val isFuzzy = recentAppsManager.isFuzzySearchEnabled()
            // Empty query → recent-apps ordering (trie not needed yet)
            initialLoadedApps.filterAndScore(
                query = searchQuery,
                recentPackageNames = recents,
                isFuzzyEnabled = isFuzzy,
                isZhuyinEnabled = isZhuyinEnabled,
                isDisablePinyinOnZhuyin = isDisablePinyinOnZhuyinEnabledState,
                trie = null
            )
        } else {
            emptyList()
        }
    }

    var allApps by remember { mutableStateOf(initialLoadedApps) }
    var filteredApps by remember { mutableStateOf(initialFilteredApps) }
    var trieGeneration by remember { mutableStateOf(0) }

    SideEffect {
        Logger.d("AppDialerTime") { "MainAppWidget SideEffect executed (allApps=${allApps.size}, filteredApps=${filteredApps.size}, trie=${trieCache.indexedAppCount})" }
    }

    // Load installed apps if not already populated on Frame 1
    LaunchedEffect(Unit) {
        if (allApps.isEmpty()) {
            val start = System.currentTimeMillis()
            val loaded = loadApps()
            allApps = loaded
            Logger.d("AppDialerTime") { "loadApps() returned ${loaded.size} apps in ${System.currentTimeMillis() - start}ms" }
        }
    }

    // Build / rebuild T9 trie off the main thread whenever the app catalog changes.
    LaunchedEffect(allApps) {
        if (allApps.isEmpty()) {
            trieCache.clear()
            trieGeneration = 0
            return@LaunchedEffect
        }
        val start = System.currentTimeMillis()
        withContext(Dispatchers.Default) {
            trieCache.rebuild(allApps)
        }
        trieGeneration++
        Logger.d("AppDialerTime") {
            "T9TrieCache rebuilt for ${trieCache.indexedAppCount} apps in ${System.currentTimeMillis() - start}ms"
        }
    }

    // Reactive State Flow Pipeline with Instant Startup & Debounce Concurrency Protection
    LaunchedEffect(allApps, isZhuyinEnabled, isDisablePinyinOnZhuyinEnabledState, trieGeneration) {
        if (allApps.isEmpty()) return@LaunchedEffect

        val activeTrie = if (trieCache.isReady()) trieCache else null

        // Immediately compute and emit initial app list with 0ms delay if not already filtered
        val recentPackages = recentAppsManager.getRecentApps()
        val isFuzzy = recentAppsManager.isFuzzySearchEnabled()
        filteredApps = allApps.filterAndScore(
            query = searchQuery,
            recentPackageNames = recentPackages,
            isFuzzyEnabled = isFuzzy,
            isZhuyinEnabled = isZhuyinEnabled,
            isDisablePinyinOnZhuyin = isDisablePinyinOnZhuyinEnabledState,
            trie = activeTrie
        )

        snapshotFlow { searchQuery }
            .debounce { query -> if (query.isEmpty()) 0L else 50L }
            .flatMapLatest { query ->
                flow {
                    val recents = recentAppsManager.getRecentApps()
                    val fuzzy = recentAppsManager.isFuzzySearchEnabled()
                    val result = allApps.filterAndScore(
                        query = query,
                        recentPackageNames = recents,
                        isFuzzyEnabled = fuzzy,
                        isZhuyinEnabled = isZhuyinEnabled,
                        isDisablePinyinOnZhuyin = isDisablePinyinOnZhuyinEnabledState,
                        trie = if (trieCache.isReady()) trieCache else null
                    )
                    emit(result)
                }.flowOn(Dispatchers.Default)
            }
            .collect { filtered ->
                filteredApps = filtered
            }
    }

    // Reset screen backstack on re-launch (onNewIntent signal)
    LaunchedEffect(resetSignal) {
        if (resetSignal > 0) {
            searchQuery = ""
            navController.navigate(Destinations.DIALER) {
                popUpTo(Destinations.DIALER) { inclusive = true }
            }
        }
    }

    fun launchApp(app: AppModel) {
        recentAppsManager.addRecentApp(app.packageName)
        appLauncher?.launchApp(app)
    }

    fun openAppSettings(app: AppModel) {
        appLauncher?.openAppDetails(app)
    }

    AppDialerTheme {
        NavHost(
            navController = navController,
            startDestination = Destinations.DIALER,
            enterTransition = { EnterTransition.None },
            exitTransition = { fadeOut(animationSpec = tween(150)) },
            popEnterTransition = { fadeIn(animationSpec = tween(150)) },
            popExitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            composable(Destinations.DIALER) {
                AppDialerScreen(
                    apps = filteredApps,
                    searchQuery = searchQuery,
                    settingsTriggerKey = settingsTriggerKey,
                    isZhuyinEnabled = isZhuyinEnabled,
                    lettersPos = lettersPos,
                    numberPos = numberPos,
                    zhuyinPos = zhuyinPos,
                    functionPos = functionPos,
                    onDigitPressed = { digit ->
                        searchQuery += digit
                    },
                    onDeleteOneDigit = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = searchQuery.dropLast(1)
                        }
                    },
                    onClearAllDigits = {
                        searchQuery = ""
                    },
                    onAppClick = { app ->
                        launchApp(app)
                    },
                    onAppLongClick = { app ->
                        openAppSettings(app)
                    },
                    onOpenDialerSettings = {
                        navController.navigate(Destinations.SETTINGS)
                    },
                    onDismiss = onDismiss
                )
            }

            composable(Destinations.SETTINGS) {
                AppDialerSettingsScreen(
                    settingsTriggerKey = settingsTriggerKey,
                    isZhuyinModeEnabled = isZhuyinEnabled,
                    isDisablePinyinOnZhuyinEnabled = isDisablePinyinOnZhuyinEnabledState,
                    lettersPos = lettersPos,
                    numberPos = numberPos,
                    zhuyinPos = zhuyinPos,
                    functionPos = functionPos,
                    onSettingsTriggerKeyChange = { key ->
                        settingsTriggerKey = key
                        recentAppsManager.setSettingsTriggerKey(key)
                    },
                    onZhuyinModeToggle = { enabled ->
                        isZhuyinEnabled = enabled
                        recentAppsManager.setZhuyinModeEnabled(enabled)
                    },
                    onDisablePinyinOnZhuyinToggle = { enabled ->
                        isDisablePinyinOnZhuyinEnabledState = enabled
                        recentAppsManager.setDisablePinyinOnZhuyinEnabled(enabled)
                    },
                    onLettersPosChange = { lPos ->
                        lettersPos = lPos
                        recentAppsManager.setLettersPosition(lPos)
                    },
                    onNumberPosChange = { nPos ->
                        numberPos = nPos
                        recentAppsManager.setNumberPosition(nPos)
                    },
                    onZhuyinPosChange = { zPos ->
                        zhuyinPos = zPos
                        recentAppsManager.setZhuyinPosition(zPos)
                    },
                    onFunctionPosChange = { fPos ->
                        functionPos = fPos
                        recentAppsManager.setFunctionPosition(fPos)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onOpenSystemAppSettings = {
                        appLauncher?.openSystemAppSettings()
                    }
                )
            }
        }
    }
}
