package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.DefaultKeyLayout
import com.github.yongjhih.appdialer.model.KeyLabelPosition

/**
 * Interface contract for recent apps tracking and app preference settings.
 */
interface RecentAppsManager {
    fun addRecentApp(packageName: String)
    fun getRecentApps(): List<String>

    fun isFuzzySearchEnabled(): Boolean
    fun setFuzzySearchEnabled(enabled: Boolean)

    fun isZhuyinModeEnabled(): Boolean
    fun setZhuyinModeEnabled(enabled: Boolean)

    fun isDisablePinyinOnZhuyinEnabled(): Boolean
    fun setDisablePinyinOnZhuyinEnabled(enabled: Boolean)

    fun getSettingsTriggerKey(): String
    fun setSettingsTriggerKey(key: String)

    fun getLettersPosition(): KeyLabelPosition
    fun setLettersPosition(pos: KeyLabelPosition)

    fun getNumberPosition(): KeyLabelPosition
    fun setNumberPosition(pos: KeyLabelPosition)

    fun getZhuyinPosition(): KeyLabelPosition
    fun setZhuyinPosition(pos: KeyLabelPosition)

    fun getFunctionPosition(): KeyLabelPosition
    fun setFunctionPosition(pos: KeyLabelPosition)
}

/**
 * In-memory implementation of [RecentAppsManager] for testing, previews, and pure JVM targets.
 */
open class InMemoryRecentAppsManager : RecentAppsManager {
    private val recentApps = mutableListOf<String>()
    private var fuzzySearchEnabled: Boolean = true
    private var zhuyinModeEnabled: Boolean = false
    private var disablePinyinOnZhuyinEnabled: Boolean = false
    private var settingsTriggerKey: String = AppDefaults.SETTINGS_TRIGGER_KEY

    private var lettersPos: KeyLabelPosition = DefaultKeyLayout.letters
    private var numberPos: KeyLabelPosition = DefaultKeyLayout.number
    private var zhuyinPos: KeyLabelPosition = DefaultKeyLayout.zhuyin
    private var functionPos: KeyLabelPosition = DefaultKeyLayout.function

    override fun addRecentApp(packageName: String) {
        recentApps.remove(packageName)
        recentApps.add(0, packageName)
        if (recentApps.size > 20) {
            recentApps.removeAt(recentApps.lastIndex)
        }
    }

    override fun getRecentApps(): List<String> = recentApps.toList()

    override fun isFuzzySearchEnabled(): Boolean = fuzzySearchEnabled
    override fun setFuzzySearchEnabled(enabled: Boolean) { fuzzySearchEnabled = enabled }

    override fun isZhuyinModeEnabled(): Boolean = zhuyinModeEnabled
    override fun setZhuyinModeEnabled(enabled: Boolean) { zhuyinModeEnabled = enabled }

    override fun isDisablePinyinOnZhuyinEnabled(): Boolean = disablePinyinOnZhuyinEnabled
    override fun setDisablePinyinOnZhuyinEnabled(enabled: Boolean) { disablePinyinOnZhuyinEnabled = enabled }

    override fun getSettingsTriggerKey(): String = settingsTriggerKey
    override fun setSettingsTriggerKey(key: String) { settingsTriggerKey = key }

    override fun getLettersPosition(): KeyLabelPosition = lettersPos
    override fun setLettersPosition(pos: KeyLabelPosition) { lettersPos = pos }

    override fun getNumberPosition(): KeyLabelPosition = numberPos
    override fun setNumberPosition(pos: KeyLabelPosition) { numberPos = pos }

    override fun getZhuyinPosition(): KeyLabelPosition = zhuyinPos
    override fun setZhuyinPosition(pos: KeyLabelPosition) { zhuyinPos = pos }

    override fun getFunctionPosition(): KeyLabelPosition = functionPos
    override fun setFunctionPosition(pos: KeyLabelPosition) { functionPos = pos }
}
