package com.github.yongjhih.appdialer.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.github.yongjhih.appdialer.model.AppModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    @Volatile
    private var cachedApps: List<AppModel>? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun loadInstalledApps(
        context: Context,
        transliterator: CjkTransliterator = AndroidCjkTransliterator,
        forceRefresh: Boolean = false
    ): List<AppModel> = withContext(Dispatchers.IO) {
        val currentCache = cachedApps
        if (!forceRefresh && currentCache != null) {
            return@withContext currentCache
        }

        // Instant 0ms Cold-Start Retrieval from Disk Cache
        val diskApps = if (!forceRefresh) AppDiskCache.loadAppsFromDisk(context) else null
        if (diskApps != null) {
            cachedApps = diskApps
            // Trigger background scan to sync PackageManager changes asynchronously without delaying UI launch
            scope.launch {
                scanPackageManager(context, transliterator)
            }
            return@withContext diskApps
        }

        // Fallback if disk cache is empty (first boot)
        scanPackageManager(context, transliterator)
    }

    private fun scanPackageManager(
        context: Context,
        transliterator: CjkTransliterator
    ): List<AppModel> {
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

        val apps = resolveInfos
            .filterNot { it.activityInfo.packageName == context.packageName }
            .map { info -> info.toAppModel(pm, transliterator) }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }

        cachedApps = apps
        AppDiskCache.saveAppsToDisk(context, apps)
        return apps
    }

    fun clearCache() {
        cachedApps = null
    }
}
