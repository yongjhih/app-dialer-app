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

    suspend fun loadInstalledApps(context: Context): List<AppModel> = withContext(Dispatchers.IO) {
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

        resolveInfos
            .filterNot { it.activityInfo.packageName == context.packageName }
            .map { info ->
                val label = info.loadLabel(pm).toString()
                AppModel(
                    label = label,
                    packageName = info.activityInfo.packageName,
                    className = info.activityInfo.name,
                    icon = info.loadIcon(pm),
                    t9Full = label.toT9(),
                    t9Initials = label.toT9Initials(),
                    t9Words = label.toT9Words()
                )
            }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }
}
