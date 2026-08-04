package com.github.yongjhih.appdialer.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

        pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
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
