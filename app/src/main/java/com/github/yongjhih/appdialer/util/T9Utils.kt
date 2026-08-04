package com.github.yongjhih.appdialer.util

import com.github.yongjhih.appdialer.model.AppModel
import java.util.Locale

fun Char.toT9(): Char = when (this) {
    '1', '2', 'a', 'b', 'c', 'A', 'B', 'C' -> '2'
    'd', 'e', 'f', 'D', 'E', 'F' -> '3'
    'g', 'h', 'i', 'G', 'H', 'I' -> '4'
    'j', 'k', 'l', 'J', 'K', 'L' -> '5'
    'm', 'n', 'o', 'M', 'N', 'O' -> '6'
    'p', 'q', 'r', 's', 'P', 'Q', 'R', 'S' -> '7'
    't', 'u', 'v', 'T', 'U', 'V' -> '8'
    'w', 'x', 'y', 'z', 'W', 'X', 'Y', 'Z' -> '9'
    in '0'..'9' -> this
    else -> ' '
}

fun String.toT9(): String = map { it.toT9() }
    .filterNot { it == ' ' }
    .joinToString("")

fun String.toT9Words(): List<String> = split(Regex("[^a-zA-Z0-9]+"))
    .filter { it.isNotEmpty() }
    .map { it.toT9() }
    .filter { it.isNotEmpty() }

fun String.toT9Initials(): String = split(Regex("[^a-zA-Z0-9]+"))
    .filter { it.isNotEmpty() }
    .mapNotNull { word -> word.firstOrNull()?.toT9()?.takeIf { it != ' ' } }
    .joinToString("")

fun String.toCjkT9Full(): String {
    val latin = CjkTransliterator.toLatin(this)
    return latin.toT9()
}

fun String.toCjkT9Initials(): String {
    val latin = CjkTransliterator.toLatin(this)
    return latin.toT9Initials()
}

/**
 * Filter and score apps with support for recent apps ordering, character highlight indexing,
 * fuzzy T9 matching, and CJK (Chinese & Japanese) Pinyin/Romaji transliteration matching.
 */
fun List<AppModel>.filterAndScore(
    query: String,
    recentPackageNames: List<String> = emptyList(),
    isFuzzyEnabled: Boolean = false
): List<AppModel> = query.trim().let { q ->
    if (q.isEmpty()) {
        val recentOrderMap = recentPackageNames.withIndex().associate { it.value to it.index }
        sortedWith(
            compareBy<AppModel> { app -> recentOrderMap[app.packageName] ?: Int.MAX_VALUE }
                .thenBy { it.label.lowercase(Locale.getDefault()) }
        ).map { it.copy(matchScore = 0, matchedIndices = emptyList()) }
    } else {
        asSequence()
            .mapNotNull { app ->
                app.calculateMatch(q, isFuzzyEnabled)?.let { (score, indices) ->
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

private fun AppModel.calculateMatch(query: String, isFuzzyEnabled: Boolean): Pair<Int, List<Int>>? {
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

    // 3. CJK Transliteration Initials Match (e.g. "地圖" -> DT "38", "相機" -> XJ "95", "カメラ" -> KM "56", "設定" -> ST "78")
    if (t9CjkInitials.isNotEmpty()) {
        val cjkInitIdx = t9CjkInitials.indexOf(cleanQuery)
        if (cjkInitIdx != -1) {
            val matchedChars = (cjkInitIdx until (cjkInitIdx + cleanQuery.length))
                .filter { it < label.length }
            val score = if (cjkInitIdx == 0) 950 else (850 - cjkInitIdx)
            return Pair(score, matchedChars)
        }
    }

    // 4. CJK Transliteration Full Match (e.g. "カメラ" -> Kamera "526372")
    if (t9CjkFull.isNotEmpty()) {
        val cjkFullIdx = t9CjkFull.indexOf(cleanQuery)
        if (cjkFullIdx != -1) {
            val matchedChars = label.indices.toList()
            val score = if (cjkFullIdx == 0) 850 else (700 - cjkFullIdx)
            return Pair(score, matchedChars)
        }
    }

    // 5. Fuzzy match (Non-contiguous character matching)
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
