package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.AppModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class T9TrieCacheTest {

    @Test
    fun testTrieInsertionAndPrefixSearch() {
        val trie = T9TrieCache()

        val app1 = AppModel(
            label = "Camera",
            packageName = "com.android.camera",
            className = "CameraActivity",
            t9Full = "226372",
            t9Initials = "2",
            t9Words = listOf("226372")
        )

        val app2 = AppModel(
            label = "Calculator",
            packageName = "com.android.calculator2",
            className = "Calculator",
            t9Full = "2252852867",
            t9Initials = "2",
            t9Words = listOf("2252852867")
        )

        trie.rebuild(listOf(app1, app2))
        assertTrue(trie.isReady())
        assertEquals(2, trie.indexedAppCount)

        val resultsFor2 = trie.searchPrefix("2")
        assertEquals(2, resultsFor2.size)

        val resultsFor226 = trie.searchPrefix("226")
        assertEquals(1, resultsFor226.size)
        assertEquals("com.android.camera", resultsFor226[0].packageName)
    }

    @Test
    fun testSubstringViaSuffixIndex() {
        val trie = T9TrieCache()
        val calc = AppModel(
            label = "Calculator",
            packageName = "com.android.calculator2",
            className = "Calculator",
            t9Full = "2252852867",
            t9Initials = "2",
            t9Words = listOf("2252852867")
        )
        trie.rebuild(listOf(calc))

        // Mid-key contiguous digits (suffix of t9Full) must resolve
        val mid = trie.searchPrefix("528")
        assertEquals(1, mid.size)
        assertEquals("com.android.calculator2", mid[0].packageName)
    }

    @Test
    fun testPreWarmRecentQueries() = runBlocking {
        val trie = T9TrieCache()

        val app = AppModel(
            label = "Maps",
            packageName = "com.google.android.apps.maps",
            className = "MapsActivity",
            t9Full = "6277",
            t9Initials = "6",
            t9Words = listOf("6277")
        )

        trie.preWarmRecentQueries(listOf(app), listOf("6", "6277"))

        val result = trie.searchPrefix("6277")
        assertEquals(1, result.size)
        assertEquals("com.google.android.apps.maps", result[0].packageName)
    }

    @Test
    fun testFilterAndScoreUsesTrieCandidates() {
        val youtube = AppModel(
            label = "YouTube",
            packageName = "com.google.android.youtube",
            className = "Main",
            t9Full = "9688823",
            t9Initials = "9",
            t9Words = listOf("9688823")
        )
        val maps = AppModel(
            label = "Maps",
            packageName = "com.google.android.apps.maps",
            className = "Main",
            t9Full = "6277",
            t9Initials = "6",
            t9Words = listOf("6277")
        )
        val apps = listOf(youtube, maps)
        val trie = T9TrieCache().also { it.rebuild(apps) }

        val withTrie = apps.filterAndScore("968", trie = trie)
        val withoutTrie = apps.filterAndScore("968", trie = null)

        assertEquals(1, withTrie.size)
        assertEquals(withoutTrie.map { it.packageName }, withTrie.map { it.packageName })
        assertEquals("com.google.android.youtube", withTrie[0].packageName)
    }
}
