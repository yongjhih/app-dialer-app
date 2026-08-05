package com.github.yongjhih.appdialer.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log
import com.github.yongjhih.appdialer.model.AppModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Extension function to convert an Android [ResolveInfo] into an [AppModel].
 */
fun ResolveInfo.toAppModel(
    pm: PackageManager,
    transliterator: CjkTransliterator = AndroidCjkTransliterator
): AppModel {
    val label = loadLabel(pm).toString()
    return AppModel(
        label = label,
        packageName = activityInfo.packageName,
        className = activityInfo.name,
        t9Full = label.toT9(),
        t9Initials = label.toT9Initials(),
        t9Words = label.toT9Words(),
        iconProvider = { loadIcon(pm).toImageBitmap() },
        t9CjkFullProvider = { label.toCjkT9Full(transliterator) },
        t9CjkInitialsProvider = { label.toCjkT9Initials(transliterator) },
        t9ZhuyinInitialsProvider = { label.toZhuyinT9Initials(transliterator) }
    )
}

object AppLoader {

    private const val TAG = "AppLoader"

    @Volatile
    private var cachedApps: List<AppModel>? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Synchronously retrieves cached apps from memory or disk cache (< 1ms)
     * during [MainActivity.onCreate] so the first Compose frame renders immediately.
     */
    fun loadInstalledAppsSync(
        context: Context,
        transliterator: CjkTransliterator = AndroidCjkTransliterator
    ): List<AppModel>? {
        val currentCache = cachedApps
        if (currentCache != null) {
            Log.d(TAG, "Sync load hit memory cache with ${currentCache.size} apps.")
            return currentCache
        }

        val diskApps = AppDiskCache.loadAppsFromDisk(context)
        if (diskApps != null) {
            cachedApps = diskApps
            Log.d(TAG, "Sync load hit disk cache with ${diskApps.size} apps. Scheduling background scan in 2s...")
            scope.launch {
                delay(2000L) // Delay background scan so initial Compose launch has zero CPU contention
                scanPackageManager(context, transliterator)
            }
            return diskApps
        }
        return null
    }

    suspend fun loadInstalledApps(
        context: Context,
        transliterator: CjkTransliterator = AndroidCjkTransliterator,
        forceRefresh: Boolean = false
    ): List<AppModel> = withContext(Dispatchers.IO) {
        val currentCache = cachedApps
        if (!forceRefresh && currentCache != null) {
            Log.d(TAG, "Loaded ${currentCache.size} apps from memory cache.")
            return@withContext currentCache
        }

        // Instant 0ms Cold-Start Retrieval from Disk Cache
        val diskApps = if (!forceRefresh) AppDiskCache.loadAppsFromDisk(context) else null
        if (diskApps != null) {
            cachedApps = diskApps
            Log.d(TAG, "Loaded ${diskApps.size} apps instantly from disk cache. Scheduling background scan in 2s...")
            // Trigger background scan asynchronously after 2s without delaying UI launch
            scope.launch {
                delay(2000L)
                scanPackageManager(context, transliterator)
            }
            return@withContext diskApps
        }

        Log.d(TAG, "Disk cache miss. Performing concurrent PackageManager scan...")
        scanPackageManager(context, transliterator)
    }

    private suspend fun scanPackageManager(
        context: Context,
        transliterator: CjkTransliterator
    ): List<AppModel> = coroutineScope {
        val startTime = System.currentTimeMillis()
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_ALL
        } else {
            0
        }

        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, flags)
        }

        val filteredInfos = resolveInfos.filterNot { it.activityInfo.packageName == context.packageName }

        // Concurrent mapping across coroutine worker pool for sub-50ms label retrieval
        val apps = filteredInfos
            .chunked(15)
            .map { chunk ->
                async(Dispatchers.IO) {
                    chunk.map { info -> info.toAppModel(pm, transliterator) }
                }
            }
            .awaitAll()
            .flatten()
            .sortedBy { it.label.lowercase(Locale.getDefault()) }

        val elapsed = System.currentTimeMillis() - startTime
        Log.d(TAG, "Scanned ${apps.size} launcher activities in ${elapsed}ms.")
        cachedApps = apps
        AppDiskCache.saveAppsToDisk(context, apps)
        apps
    }

    fun clearCache() {
        Log.d(TAG, "Memory cache cleared.")
        cachedApps = null
    }
}
