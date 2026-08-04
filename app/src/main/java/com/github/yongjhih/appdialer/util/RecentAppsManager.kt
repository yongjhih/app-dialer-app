package com.github.yongjhih.appdialer.util

import android.content.Context
import android.content.SharedPreferences

object RecentAppsManager {
    private const val PREF_NAME = "app_dialer_recent_apps"
    private const val KEY_RECENT_PACKAGES = "recent_packages"
    private const val KEY_FUZZY_SEARCH = "fuzzy_search"
    private const val KEY_ZHUYIN_MODE = "zhuyin_mode"
    private const val KEY_SETTINGS_TRIGGER_KEY = "settings_trigger_key"

    private const val KEY_POS_LETTERS = "pos_letters"
    private const val KEY_POS_NUMBER = "pos_number"
    private const val KEY_POS_ZHUYIN = "pos_zhuyin"
    private const val KEY_POS_FUNCTION = "pos_function"

    private const val MAX_RECENT_APPS = 20

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun addRecentApp(context: Context, packageName: String) {
        val prefs = getPrefs(context)
        val current = getRecentApps(context).toMutableList()
        current.remove(packageName)
        current.add(0, packageName)
        if (current.size > MAX_RECENT_APPS) {
            current.removeAt(current.lastIndex)
        }
        prefs.edit().putString(KEY_RECENT_PACKAGES, current.joinToString(",")).apply()
    }

    fun getRecentApps(context: Context): List<String> {
        val prefs = getPrefs(context)
        val saved = prefs.getString(KEY_RECENT_PACKAGES, "") ?: ""
        if (saved.isEmpty()) return emptyList()
        return saved.split(",").filter { it.isNotEmpty() }
    }

    fun isFuzzySearchEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_FUZZY_SEARCH, true)
    }

    fun setFuzzySearchEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_FUZZY_SEARCH, enabled).apply()
    }

    fun isZhuyinModeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ZHUYIN_MODE, false)
    }

    fun setZhuyinModeEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ZHUYIN_MODE, enabled).apply()
    }

    fun getSettingsTriggerKey(context: Context): String {
        return getPrefs(context).getString(KEY_SETTINGS_TRIGGER_KEY, "9") ?: "9"
    }

    fun setSettingsTriggerKey(context: Context, key: String) {
        getPrefs(context).edit().putString(KEY_SETTINGS_TRIGGER_KEY, key).apply()
    }

    // Element Position Configs
    fun getLettersPosition(context: Context): String = getPrefs(context).getString(KEY_POS_LETTERS, "Center") ?: "Center"
    fun setLettersPosition(context: Context, pos: String) = getPrefs(context).edit().putString(KEY_POS_LETTERS, pos).apply()

    fun getNumberPosition(context: Context): String = getPrefs(context).getString(KEY_POS_NUMBER, "TopRight") ?: "TopRight"
    fun setNumberPosition(context: Context, pos: String) = getPrefs(context).edit().putString(KEY_POS_NUMBER, pos).apply()

    fun getZhuyinPosition(context: Context): String = getPrefs(context).getString(KEY_POS_ZHUYIN, "BottomLeft") ?: "BottomLeft"
    fun setZhuyinPosition(context: Context, pos: String) = getPrefs(context).edit().putString(KEY_POS_ZHUYIN, pos).apply()

    fun getFunctionPosition(context: Context): String = getPrefs(context).getString(KEY_POS_FUNCTION, "BottomRight") ?: "BottomRight"
    fun setFunctionPosition(context: Context, pos: String) = getPrefs(context).edit().putString(KEY_POS_FUNCTION, pos).apply()
}
