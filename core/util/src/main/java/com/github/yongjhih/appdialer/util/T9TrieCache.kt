package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.AppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * T9 Prefix Trie Cache for sub-millisecond T9 search queries.
 */
class T9TrieCache {

    private class TrieNode {
        val children = HashMap<Char, TrieNode>()
        val apps = mutableListOf<AppModel>()
    }

    private val root = TrieNode()

    fun clear() {
        synchronized(this) {
            root.children.clear()
            root.apps.clear()
        }
    }

    /**
     * Inserts an [AppModel] into the T9 Trie cache under its T9 search strings.
     */
    fun insert(app: AppModel) {
        synchronized(this) {
            val t9Keys = mutableSetOf<String>()
            if (app.t9Full.isNotEmpty()) t9Keys.add(app.t9Full)
            if (app.t9Initials.isNotEmpty()) t9Keys.add(app.t9Initials)
            app.t9Words.forEach { if (it.isNotEmpty()) t9Keys.add(it) }

            // Include CJK T9 keys if pre-evaluated
            if (app.t9CjkFull.isNotEmpty()) t9Keys.add(app.t9CjkFull)
            if (app.t9CjkInitials.isNotEmpty()) t9Keys.add(app.t9CjkInitials)
            if (app.t9ZhuyinInitials.isNotEmpty()) t9Keys.add(app.t9ZhuyinInitials)

            for (key in t9Keys) {
                var node = root
                for (ch in key) {
                    if (!ch.isDigit()) continue
                    node = node.children.getOrPut(ch) { TrieNode() }
                    if (!node.apps.contains(app)) {
                        node.apps.add(app)
                    }
                }
            }
        }
    }

    /**
     * Rebuilds the entire T9 Trie cache with a list of [AppModel]s.
     */
    fun rebuild(apps: List<AppModel>) {
        synchronized(this) {
            clear()
            apps.forEach { insert(it) }
        }
    }

    /**
     * Queries the Trie for apps matching the T9 digit query prefix.
     * Returns matching apps in O(K) time where K = query.length.
     */
    fun searchPrefix(query: String): List<AppModel>? {
        if (query.isEmpty()) return null
        synchronized(this) {
            var node = root
            for (ch in query) {
                node = node.children[ch] ?: return emptyList()
            }
            return node.apps.toList()
        }
    }

    /**
     * Pre-warms recent query results in background coroutine.
     */
    suspend fun preWarmRecentQueries(
        allApps: List<AppModel>,
        recentQueries: List<String>
    ) = withContext(Dispatchers.Default) {
        rebuild(allApps)
        for (q in recentQueries) {
            searchPrefix(q)
        }
    }
}
