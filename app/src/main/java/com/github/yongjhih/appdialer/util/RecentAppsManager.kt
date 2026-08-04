package com.github.yongjhih.appdialer.util

import android.content.Context
import android.content.SharedPreferences
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition
import com.github.yongjhih.appdialer.model.AppDefaults

object RecentAppsManager {
    private const val PREF_NAME = "app_dialer_recent_apps"
    private const val KEY_RECENT_PACKAGES = "recent_packages"
    private const val KEY_FUZZY_SEARCH = "fuzzy_search"
    private const val KEY_ZHUYIN_MODE = "zhuyin_mode"
    private const val KEY_DISABLE_PINYIN_ON_ZHUYIN = "disable_pinyin_on_zhuyin"
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

    fun isDisablePinyinOnZhuyinEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_DISABLE_PINYIN_ON_ZHUYIN, false)
    }

    fun setDisablePinyinOnZhuyinEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DISABLE_PINYIN_ON_ZHUYIN, enabled).apply()
    }

    fun getSettingsTriggerKey(context: Context): String {
        return getPrefs(context).getString(KEY_SETTINGS_TRIGGER_KEY, AppDefaults.SETTINGS_TRIGGER_KEY)
            ?: AppDefaults.SETTINGS_TRIGGER_KEY
    }

    fun setSettingsTriggerKey(context: Context, key: String) {
        getPrefs(context).edit().putString(KEY_SETTINGS_TRIGGER_KEY, key).apply()
    }

    // Element Position Configs
    fun getLettersPosition(context: Context): KeyLabelPosition =
        getPosition(context, KEY_POS_LETTERS, DefaultKeyLayout.letters)
    fun setLettersPosition(context: Context, pos: KeyLabelPosition) = setPosition(context, KEY_POS_LETTERS, pos)

    fun getNumberPosition(context: Context): KeyLabelPosition =
        getPosition(context, KEY_POS_NUMBER, DefaultKeyLayout.number)
    fun setNumberPosition(context: Context, pos: KeyLabelPosition) = setPosition(context, KEY_POS_NUMBER, pos)

    fun getZhuyinPosition(context: Context): KeyLabelPosition =
        getPosition(context, KEY_POS_ZHUYIN, DefaultKeyLayout.zhuyin)
    fun setZhuyinPosition(context: Context, pos: KeyLabelPosition) = setPosition(context, KEY_POS_ZHUYIN, pos)

    fun getFunctionPosition(context: Context): KeyLabelPosition =
        getPosition(context, KEY_POS_FUNCTION, DefaultKeyLayout.function)
    fun setFunctionPosition(context: Context, pos: KeyLabelPosition) = setPosition(context, KEY_POS_FUNCTION, pos)

    private fun getPosition(context: Context, key: String, default: KeyLabelPosition): KeyLabelPosition =
        KeyLabelPosition.fromPreference(getPrefs(context).getString(key, default.preferenceValue), default)

    private fun setPosition(context: Context, key: String, position: KeyLabelPosition) {
        getPrefs(context).edit().putString(key, position.preferenceValue).apply()
    }
}
