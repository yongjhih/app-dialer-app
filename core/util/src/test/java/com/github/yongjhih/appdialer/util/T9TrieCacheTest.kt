package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.AppModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

        val resultsFor2 = trie.searchPrefix("2")
        assertNotNull(resultsFor2)
        assertEquals(2, resultsFor2.size)

        val resultsFor226 = trie.searchPrefix("226")
        assertNotNull(resultsFor226)
        assertEquals(1, resultsFor226.size)
        assertEquals("com.android.camera", resultsFor226[0].packageName)
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
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("com.google.android.apps.maps", result[0].packageName)
    }
}
