package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.AppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * T9 Prefix Trie Cache for sub-millisecond T9 search queries.
 *
 * Each app is indexed under its T9 keys (full, initials, words, CJK/Zhuyin).
 * All **suffixes** of each key are also inserted so contiguous substring queries
 * (same semantics as [filterAndScore]'s `indexOf`) resolve via a simple prefix walk.
 */
class T9TrieCache {

    private class TrieNode {
        val children = HashMap<Char, TrieNode>(8)
        /** Apps whose some T9 key has a path through this node. Deduped by package/class. */
        val apps = LinkedHashMap<String, AppModel>()
    }

    private val root = TrieNode()

    @Volatile
    private var size: Int = 0

    /** Number of apps currently indexed (after last successful [rebuild]/[insert]). */
    val indexedAppCount: Int get() = size

    fun clear() {
        synchronized(this) {
            root.children.clear()
            root.apps.clear()
            size = 0
        }
    }

    private fun appKey(app: AppModel): String = "${app.packageName}/${app.className}"

    private fun collectT9Keys(app: AppModel): Set<String> {
        val t9Keys = LinkedHashSet<String>()
        if (app.t9Full.isNotEmpty()) t9Keys.add(app.t9Full)
        if (app.t9Initials.isNotEmpty()) t9Keys.add(app.t9Initials)
        app.t9Words.forEach { if (it.isNotEmpty()) t9Keys.add(it) }

        // Force lazy CJK/Zhuyin evaluation so disk-cached models with providers are indexed.
        val cjkFull = app.t9CjkFull
        val cjkInitials = app.t9CjkInitials
        val zhuyinInitials = app.t9ZhuyinInitials
        if (cjkFull.isNotEmpty()) t9Keys.add(cjkFull)
        if (cjkInitials.isNotEmpty()) t9Keys.add(cjkInitials)
        if (zhuyinInitials.isNotEmpty()) t9Keys.add(zhuyinInitials)

        return t9Keys
    }

    /**
     * Inserts an [AppModel] into the T9 Trie under all of its T9 keys and their suffixes.
     */
    fun insert(app: AppModel) {
        synchronized(this) {
            insertUnlocked(app)
            size = root.apps.size.coerceAtLeast(size)
        }
    }

    private fun insertUnlocked(app: AppModel) {
        val id = appKey(app)
        root.apps.getOrPut(id) { app }

        for (key in collectT9Keys(app)) {
            // All suffixes → substring query "528" hits mid-key of "2252852867"
            for (start in key.indices) {
                var node = root
                var hasDigit = false
                for (i in start until key.length) {
                    val ch = key[i]
                    if (!ch.isDigit()) continue
                    hasDigit = true
                    node = node.children.getOrPut(ch) { TrieNode() }
                    node.apps.getOrPut(id) { app }
                }
                if (!hasDigit) continue
            }
        }
    }

    /**
     * Rebuilds the entire T9 Trie with a list of [AppModel]s.
     */
    fun rebuild(apps: List<AppModel>) {
        synchronized(this) {
            root.children.clear()
            root.apps.clear()
            apps.forEach { insertUnlocked(it) }
            size = root.apps.size
        }
    }

    /**
     * Queries the Trie for apps matching the T9 digit query as a contiguous substring
     * of any indexed key. Returns matching apps in O(K) walk time where K = query.length.
     *
     * @return matching apps, or empty list if none; never null for non-empty [query]
     */
    fun searchPrefix(query: String): List<AppModel> {
        if (query.isEmpty()) return emptyList()
        synchronized(this) {
            var node = root
            for (ch in query) {
                if (!ch.isDigit()) continue
                node = node.children[ch] ?: return emptyList()
            }
            return node.apps.values.toList()
        }
    }

    /**
     * Whether the trie has been built with at least one app (safe to use for pruning).
     */
    fun isReady(): Boolean = size > 0

    /**
     * Pre-warms the trie (and optionally touches recent query paths) on [Dispatchers.Default].
     */
    suspend fun preWarmRecentQueries(
        allApps: List<AppModel>,
        recentQueries: List<String> = emptyList()
    ) = withContext(Dispatchers.Default) {
        rebuild(allApps)
        for (q in recentQueries) {
            if (q.isNotEmpty()) searchPrefix(q)
        }
    }
}
