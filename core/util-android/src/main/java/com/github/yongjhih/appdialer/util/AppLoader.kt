package com.github.yongjhih.appdialer.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.github.yongjhih.appdialer.model.AppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object AppLoader {

    @Volatile
    private var cachedApps: List<AppModel>? = null

    suspend fun loadInstalledApps(
        context: Context,
        transliterator: CjkTransliterator = AndroidCjkTransliterator,
        forceRefresh: Boolean = false
    ): List<AppModel> = withContext(Dispatchers.IO) {
        val currentCache = cachedApps
        if (!forceRefresh && currentCache != null) {
            return@withContext currentCache
        }

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
            .map { info ->
                val label = info.loadLabel(pm).toString()
                AppModel(
                    label = label,
                    packageName = info.activityInfo.packageName,
                    className = info.activityInfo.name,
                    icon = info.loadIcon(pm).toImageBitmap(),
                    t9Full = label.toT9(),
                    t9Initials = label.toT9Initials(),
                    t9Words = label.toT9Words(),
                    t9CjkFull = label.toCjkT9Full(transliterator),
                    t9CjkInitials = label.toCjkT9Initials(transliterator),
                    t9ZhuyinInitials = label.toZhuyinT9Initials(transliterator)
                )
            }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }

        cachedApps = apps
        apps
    }

    fun clearCache() {
        cachedApps = null
    }
}
