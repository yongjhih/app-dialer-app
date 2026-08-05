package com.github.yongjhih.appdialer.util

import android.content.Context
import android.content.SharedPreferences
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition

/**
 * Android SharedPreferences implementation of [RecentAppsManager].
 */
class AndroidRecentAppsManager(private val context: Context) : RecentAppsManager {

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun addRecentApp(packageName: String) {
        val prefs = getPrefs()
        val current = getRecentApps().toMutableList()
        current.remove(packageName)
        current.add(0, packageName)
        if (current.size > MAX_RECENT_APPS) {
            current.removeAt(current.lastIndex)
        }
        prefs.edit().putString(KEY_RECENT_PACKAGES, current.joinToString(",")).apply()
    }

    override fun getRecentApps(): List<String> {
        val prefs = getPrefs()
        val saved = prefs.getString(KEY_RECENT_PACKAGES, "") ?: ""
        if (saved.isEmpty()) return emptyList()
        return saved.split(",").filter { it.isNotEmpty() }
    }

    override fun isFuzzySearchEnabled(): Boolean {
        return getPrefs().getBoolean(KEY_FUZZY_SEARCH, true)
    }

    override fun setFuzzySearchEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_FUZZY_SEARCH, enabled).apply()
    }

    override fun isZhuyinModeEnabled(): Boolean {
        return getPrefs().getBoolean(KEY_ZHUYIN_MODE, false)
    }

    override fun setZhuyinModeEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_ZHUYIN_MODE, enabled).apply()
    }

    override fun isDisablePinyinOnZhuyinEnabled(): Boolean {
        return getPrefs().getBoolean(KEY_DISABLE_PINYIN_ON_ZHUYIN, false)
    }

    override fun setDisablePinyinOnZhuyinEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_DISABLE_PINYIN_ON_ZHUYIN, enabled).apply()
    }

    override fun getSettingsTriggerKey(): String {
        return getPrefs().getString(KEY_SETTINGS_TRIGGER_KEY, AppDefaults.SETTINGS_TRIGGER_KEY)
            ?: AppDefaults.SETTINGS_TRIGGER_KEY
    }

    override fun setSettingsTriggerKey(key: String) {
        getPrefs().edit().putString(KEY_SETTINGS_TRIGGER_KEY, key).apply()
    }

    override fun getLettersPosition(): KeyLabelPosition =
        getPosition(KEY_POS_LETTERS, DefaultKeyLayout.letters)

    override fun setLettersPosition(pos: KeyLabelPosition) =
        setPosition(KEY_POS_LETTERS, pos)

    override fun getNumberPosition(): KeyLabelPosition =
        getPosition(KEY_POS_NUMBER, DefaultKeyLayout.number)

    override fun setNumberPosition(pos: KeyLabelPosition) =
        setPosition(KEY_POS_NUMBER, pos)

    override fun getZhuyinPosition(): KeyLabelPosition =
        getPosition(KEY_POS_ZHUYIN, DefaultKeyLayout.zhuyin)

    override fun setZhuyinPosition(pos: KeyLabelPosition) =
        setPosition(KEY_POS_ZHUYIN, pos)

    override fun getFunctionPosition(): KeyLabelPosition =
        getPosition(KEY_POS_FUNCTION, DefaultKeyLayout.function)

    override fun setFunctionPosition(pos: KeyLabelPosition) =
        setPosition(KEY_POS_FUNCTION, pos)

    override fun isHapticFeedbackEnabled(): Boolean {
        return getPrefs().getBoolean(KEY_HAPTIC_FEEDBACK, true)
    }

    override fun setHapticFeedbackEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_HAPTIC_FEEDBACK, enabled).apply()
    }

    override fun getBackgroundDimAmount(): Float {
        return getPrefs().getFloat(KEY_BACKGROUND_DIM_AMOUNT, AppDefaults.BACKGROUND_DIM_AMOUNT)
    }

    override fun setBackgroundDimAmount(amount: Float) {
        getPrefs().edit().putFloat(KEY_BACKGROUND_DIM_AMOUNT, amount).apply()
    }

    override fun isBackspaceSingleTapClearAllEnabled(): Boolean {
        return getPrefs().getBoolean(KEY_BACKSPACE_SINGLE_TAP_CLEAR_ALL, false)
    }

    override fun setBackspaceSingleTapClearAllEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_BACKSPACE_SINGLE_TAP_CLEAR_ALL, enabled).apply()
    }

    private fun getPosition(key: String, default: KeyLabelPosition): KeyLabelPosition =
        KeyLabelPosition.fromPreference(getPrefs().getString(key, default.preferenceValue), default)

    private fun setPosition(key: String, position: KeyLabelPosition) {
        getPrefs().edit().putString(key, position.preferenceValue).apply()
    }

    companion object {
        private const val PREF_NAME = "app_dialer_recent_apps"
        private const val KEY_RECENT_PACKAGES = "recent_packages"
        private const val KEY_FUZZY_SEARCH = "fuzzy_search"
        private const val KEY_ZHUYIN_MODE = "zhuyin_mode"
        private const val KEY_DISABLE_PINYIN_ON_ZHUYIN = "disable_pinyin_on_zhuyin"
        private const val KEY_SETTINGS_TRIGGER_KEY = "settings_trigger_key"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_BACKGROUND_DIM_AMOUNT = "background_dim_amount"
        private const val KEY_BACKSPACE_SINGLE_TAP_CLEAR_ALL = "backspace_single_tap_clear_all"

        private const val KEY_POS_LETTERS = "pos_letters"
        private const val KEY_POS_NUMBER = "pos_number"
        private const val KEY_POS_ZHUYIN = "pos_zhuyin"
        private const val KEY_POS_FUNCTION = "pos_function"

        private const val MAX_RECENT_APPS = 20
    }
}
