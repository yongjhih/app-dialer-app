package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.AppModel
import java.util.Locale

fun Char.toT9(): Char = when (this.lowercaseChar()) {
    in 'a'..'c' -> '2'
    in 'd'..'f' -> '3'
    in 'g'..'i' -> '4'
    in 'j'..'l' -> '5'
    in 'm'..'o' -> '6'
    in 'p'..'s' -> '7'
    in 't'..'v' -> '8'
    in 'w'..'z' -> '9'
    in '0'..'9' -> this
    else -> ' '
}

fun String.toT9(): String = map { it.toT9() }.filter { it != ' ' }.joinToString("")

fun String.toT9Initials(): String = split(Regex("[^a-zA-Z0-9]+"))
    .filter { it.isNotEmpty() }
    .mapNotNull { word -> word.firstOrNull()?.toT9()?.takeIf { it != ' ' } }
    .joinToString("")

fun String.toT9Words(): List<String> = split(Regex("[^a-zA-Z0-9]+"))
    .filter { it.isNotEmpty() }
    .map { word -> word.toT9() }
    .filter { it.isNotEmpty() }

fun String.toCjkT9Full(transliterator: CjkTransliterator = DefaultCjkTransliterator): String {
    val latin = transliterator.toLatin(this)
    return latin.toT9()
}

fun String.toCjkT9Initials(transliterator: CjkTransliterator = DefaultCjkTransliterator): String {
    val latin = transliterator.toLatin(this)
    return latin.toT9Initials()
}

fun Char.pinyinInitialToZhuyinT9(): Char = when (this.lowercaseChar()) {
    'b', 'p', 'm', 'f' -> '2'
    'd', 't', 'n', 'l' -> '3'
    'g', 'k', 'h' -> '4'
    'j', 'q', 'x' -> '5'
    'r' -> '6'
    'z', 'c', 's' -> '7'
    'a', 'o', 'e' -> '8'
    'w', 'y' -> '9'
    in '0'..'9' -> this
    else -> ' '
}

fun String.toZhuyinT9Initials(transliterator: CjkTransliterator = DefaultCjkTransliterator): String {
    val latin = transliterator.toLatin(this)
    val words = latin.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotEmpty() }
    return words.mapNotNull { word ->
        val lower = word.lowercase(Locale.getDefault())
        when {
            lower.startsWith("zh") || lower.startsWith("ch") || lower.startsWith("sh") -> '6'
            else -> lower.firstOrNull()?.pinyinInitialToZhuyinT9()?.takeIf { it != ' ' }
        }
    }.joinToString("")
}

/**
 * Filter and score apps with support for recent apps ordering, character highlight indexing,
 * fuzzy T9 matching, and CJK (Chinese & Japanese) Pinyin/Romaji/Zhuyin transliteration matching.
 *
 * When [trie] is ready and fuzzy is off, candidates are pruned via [T9TrieCache.searchPrefix]
 * (O(K) digit walk) before scoring — same match semantics, far less work on large app lists.
 * Fuzzy mode always scans the full list (non-contiguous matches are not trie-indexable).
 */
fun List<AppModel>.filterAndScore(
    query: String,
    recentPackageNames: List<String> = emptyList(),
    isFuzzyEnabled: Boolean = false,
    isZhuyinEnabled: Boolean = false,
    isDisablePinyinOnZhuyin: Boolean = false,
    trie: T9TrieCache? = null
): List<AppModel> = query.trim().let { q ->
    if (q.isEmpty()) {
        val recentOrderMap = recentPackageNames.withIndex().associate { it.value to it.index }
        sortedWith(
            compareBy<AppModel> { app -> recentOrderMap[app.packageName] ?: Int.MAX_VALUE }
                .thenBy { it.label.lowercase(Locale.getDefault()) }
        ).map { it.copy(matchScore = 0, matchedIndices = emptyList()) }
    } else {
        val candidates: List<AppModel> = when {
            // Fuzzy = non-contiguous; cannot prune with contiguous T9 trie
            isFuzzyEnabled -> this
            trie != null && trie.isReady() -> trie.searchPrefix(q)
            else -> this
        }

        candidates
            .asSequence()
            .mapNotNull { app ->
                app.calculateMatch(q, isFuzzyEnabled, isZhuyinEnabled, isDisablePinyinOnZhuyin)?.let { (score, indices) ->
                    app.copy(matchScore = score, matchedIndices = indices)
                }
            }
            .sortedWith(
                compareByDescending<AppModel> { it.matchScore }
                    .thenBy { it.label.lowercase(Locale.getDefault()) }
            )
            .toList()
    }
}

private fun AppModel.calculateMatch(
    query: String,
    isFuzzyEnabled: Boolean,
    isZhuyinEnabled: Boolean,
    isDisablePinyinOnZhuyin: Boolean
): Pair<Int, List<Int>>? {
    val cleanQuery = query.trim()
    if (cleanQuery.isEmpty()) return Pair(0, emptyList())

    val labelT9Str = label.map { it.toT9() }.joinToString("")

    // 1. Contiguous T9 substring match on Native Label (Highest priority)
    val fullIndex = labelT9Str.indexOf(cleanQuery)
    if (fullIndex != -1) {
        val indices = (fullIndex until fullIndex + cleanQuery.length).toList()
        val score = if (fullIndex == 0) 1000 else (800 - fullIndex)
        return Pair(score, indices)
    }

    // 2. Native Initials match
    val initialIndices = mutableListOf<Int>()
    val initialT9s = mutableListOf<Char>()
    for (i in label.indices) {
        if (i == 0 || (label[i - 1] in " ._-" && label[i] !in " ._-")) {
            val t9 = label[i].toT9()
            if (t9 != ' ') {
                initialIndices.add(i)
                initialT9s.add(t9)
            }
        }
    }
    val initialT9Str = initialT9s.joinToString("")
    val initialIdx = initialT9Str.indexOf(cleanQuery)
    if (initialIdx != -1 && initialIdx + cleanQuery.length <= initialIndices.size) {
        val indices = initialIndices.subList(initialIdx, initialIdx + cleanQuery.length)
        val score = if (initialIdx == 0) 900 else (750 - initialIdx)
        return Pair(score, indices)
    }

    // 3. Zhuyin (Bopomofo) Initials Match (when Zhuyin mode is ON)
    if (isZhuyinEnabled && t9ZhuyinInitials.isNotEmpty()) {
        val zhuyinInitIdx = t9ZhuyinInitials.indexOf(cleanQuery)
        if (zhuyinInitIdx != -1) {
            val matchedChars = (zhuyinInitIdx until (zhuyinInitIdx + cleanQuery.length))
                .filter { it < label.length }
            val score = if (zhuyinInitIdx == 0) 960 else (860 - zhuyinInitIdx)
            return Pair(score, matchedChars)
        }
    }

    val skipPinyin = isZhuyinEnabled && isDisablePinyinOnZhuyin

    // 4. CJK Pinyin / Romaji Transliteration Initials Match
    if (!skipPinyin && t9CjkInitials.isNotEmpty()) {
        val cjkInitIdx = t9CjkInitials.indexOf(cleanQuery)
        if (cjkInitIdx != -1) {
            val matchedChars = (cjkInitIdx until (cjkInitIdx + cleanQuery.length))
                .filter { it < label.length }
            val score = if (cjkInitIdx == 0) 950 else (850 - cjkInitIdx)
            return Pair(score, matchedChars)
        }
    }

    // 5. CJK Transliteration Full Match
    if (!skipPinyin && t9CjkFull.isNotEmpty()) {
        val cjkFullIdx = t9CjkFull.indexOf(cleanQuery)
        if (cjkFullIdx != -1) {
            val matchedChars = label.indices.toList()
            val score = if (cjkFullIdx == 0) 850 else (700 - cjkFullIdx)
            return Pair(score, matchedChars)
        }
    }

    // 6. Fuzzy match (Non-contiguous character matching)
    if (isFuzzyEnabled) {
        val matchedIndices = mutableListOf<Int>()
        var queryIdx = 0
        for (i in label.indices) {
            val charT9 = label[i].toT9()
            if (charT9 != ' ' && charT9 == cleanQuery[queryIdx]) {
                matchedIndices.add(i)
                queryIdx++
                if (queryIdx == cleanQuery.length) break
            }
        }

        if (queryIdx == cleanQuery.length) {
            val firstIdx = matchedIndices.first()
            val lastIdx = matchedIndices.last()
            val totalSpan = lastIdx - firstIdx + 1
            val gapLength = totalSpan - cleanQuery.length
            val score = (400 - gapLength * 10 - firstIdx).coerceAtLeast(1)
            return Pair(score, matchedIndices)
        }
    }

    return null
}
