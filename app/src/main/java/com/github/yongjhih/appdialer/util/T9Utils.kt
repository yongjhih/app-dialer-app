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

/**
 * Pure functional extension on List<AppModel> to score and filter apps based on T9 search query.
 */
fun List<AppModel>.filterAndScore(query: String): List<AppModel> = query.trim().let { q ->
    if (q.isEmpty()) {
        sortedBy { it.label.lowercase(Locale.getDefault()) }
    } else {
        asSequence()
            .mapNotNull { app -> app.calculateScore(q)?.let { score -> app.copy(matchScore = score) } }
            .sortedWith(
                compareByDescending<AppModel> { it.matchScore }
                    .thenBy { it.label.lowercase(Locale.getDefault()) }
            )
            .toList()
    }
}

private fun AppModel.calculateScore(query: String): Int? {
    val initialsScore = when {
        t9Initials.startsWith(query) -> 1000 - t9Initials.length
        t9Initials.contains(query) -> 800 - t9Initials.indexOf(query)
        else -> 0
    }

    val wordScore = t9Words.withIndex()
        .mapNotNull { (index, wordT9) ->
            when {
                wordT9.startsWith(query) -> if (index == 0) 900 else 850
                wordT9.contains(query) -> 700 - wordT9.indexOf(query)
                else -> null
            }
        }
        .maxOrNull() ?: 0

    val fullIndex = t9Full.indexOf(query)
    val fullScore = if (fullIndex != -1) {
        if (fullIndex == 0) 950 else 600 - fullIndex
    } else 0

    val rawIndex = label.lowercase(Locale.getDefault()).indexOf(query)
    val rawScore = if (rawIndex != -1) {
        if (rawIndex == 0) 920 else 500 - rawIndex
    } else 0

    val maxScore = maxOf(initialsScore, wordScore, fullScore, rawScore)
    return maxScore.takeIf { it > 0 }
}
