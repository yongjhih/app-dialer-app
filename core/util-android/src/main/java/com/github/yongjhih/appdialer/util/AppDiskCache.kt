package com.github.yongjhih.appdialer.util

import android.content.Context
import com.github.yongjhih.appdialer.model.AppModel
import org.json.JSONArray
import org.json.JSONObject

/**
 * Disk cache helper for persisting non-bitmap [AppModel] metadata across app restarts.
 */
object AppDiskCache {

    private const val TAG = "AppDiskCache"
    private const val PREF_NAME = "app_dialer_disk_cache"
    private const val KEY_CACHED_APPS_JSON = "cached_apps_json"

    fun saveAppsToDisk(context: Context, apps: List<AppModel>) {
        try {
            val startTime = System.currentTimeMillis()
            val jsonArray = JSONArray()
            for (app in apps) {
                val jsonObject = JSONObject().apply {
                    put("label", app.label)
                    put("packageName", app.packageName)
                    put("className", app.className)
                    put("t9Full", app.t9Full)
                    put("t9Initials", app.t9Initials)
                    put("t9Words", JSONArray(app.t9Words))
                    put("t9CjkFull", app.t9CjkFull)
                    put("t9CjkInitials", app.t9CjkInitials)
                    put("t9ZhuyinInitials", app.t9ZhuyinInitials)
                }
                jsonArray.put(jsonObject)
            }

            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_CACHED_APPS_JSON, jsonArray.toString())
                .apply()

            val elapsed = System.currentTimeMillis() - startTime
            Logger.d(TAG) { "Persisted ${apps.size} apps to disk cache JSON in ${elapsed}ms." }
        } catch (e: Exception) {
            Logger.w(TAG, e) { "Failed to persist apps to disk cache" }
        }
    }

    fun loadAppsFromDisk(context: Context): List<AppModel>? {
        val startTime = System.currentTimeMillis()
        val jsonString = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CACHED_APPS_JSON, null) ?: return null

        val pm = context.packageManager

        return try {
            val jsonArray = JSONArray(jsonString)
            val apps = mutableListOf<AppModel>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val packageName = obj.getString("packageName")
                val wordsArray = obj.optJSONArray("t9Words")
                val words = mutableListOf<String>()
                if (wordsArray != null) {
                    for (j in 0 until wordsArray.length()) {
                        words.add(wordsArray.getString(j))
                    }
                }

                apps.add(
                    AppModel(
                        label = obj.getString("label"),
                        packageName = packageName,
                        className = obj.getString("className"),
                        t9Full = obj.optString("t9Full", ""),
                        t9Initials = obj.optString("t9Initials", ""),
                        t9Words = words,
                        rawT9CjkFull = obj.optString("t9CjkFull", ""),
                        rawT9CjkInitials = obj.optString("t9CjkInitials", ""),
                        rawT9ZhuyinInitials = obj.optString("t9ZhuyinInitials", ""),
                        iconProvider = {
                            try {
                                pm.getApplicationIcon(packageName).toImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        }
                    )
                )
            }
            val elapsed = System.currentTimeMillis() - startTime
            Logger.d(TAG) { "Loaded ${apps.size} apps from disk cache JSON in ${elapsed}ms." }
            apps.ifEmpty { null }
        } catch (e: Exception) {
            Logger.w(TAG, e) { "Failed to parse disk cache JSON" }
            null
        }
    }
}
