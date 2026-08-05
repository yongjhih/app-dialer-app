package com.github.yongjhih.appdialer.model

import kotlinx.coroutines.Deferred

/**
 * Domain model representing an installed app.
 * Supports deferred/lazy evaluation via [Deferred] fields or provider lambdas for heavy operations.
 */
data class AppModel(
    val label: String,
    val packageName: String,
    val className: String,
    val t9Full: String = "",
    val t9Initials: String = "",
    val t9Words: List<String> = emptyList(),
    val matchScore: Int = 0,
    val matchedIndices: List<Int> = emptyList(),
    private val rawIcon: Any? = null,
    private val rawT9CjkFull: String = "",
    private val rawT9CjkInitials: String = "",
    private val rawT9ZhuyinInitials: String = "",
    val iconDeferred: Deferred<Any?>? = null,
    val t9CjkFullDeferred: Deferred<String>? = null,
    val t9CjkInitialsDeferred: Deferred<String>? = null,
    val t9ZhuyinInitialsDeferred: Deferred<String>? = null,
    val iconProvider: (() -> Any?)? = null,
    private val t9CjkFullProvider: (() -> String)? = null,
    private val t9CjkInitialsProvider: (() -> String)? = null,
    private val t9ZhuyinInitialsProvider: (() -> String)? = null
) {
    // Secondary constructor for direct value initialization (backwards compatibility)
    constructor(
        label: String,
        packageName: String,
        className: String,
        icon: Any?,
        t9Full: String = "",
        t9Initials: String = "",
        t9Words: List<String> = emptyList(),
        t9CjkFull: String = "",
        t9CjkInitials: String = "",
        t9ZhuyinInitials: String = "",
        t9ZhuyinFull: String = "",
        matchScore: Int = 0,
        matchedIndices: List<Int> = emptyList()
    ) : this(
        label = label,
        packageName = packageName,
        className = className,
        t9Full = t9Full,
        t9Initials = t9Initials,
        t9Words = t9Words,
        matchScore = matchScore,
        matchedIndices = matchedIndices,
        rawIcon = icon,
        rawT9CjkFull = t9CjkFull,
        rawT9CjkInitials = t9CjkInitials,
        rawT9ZhuyinInitials = t9ZhuyinInitials,
        iconDeferred = null,
        t9CjkFullDeferred = null,
        t9CjkInitialsDeferred = null,
        t9ZhuyinInitialsDeferred = null,
        iconProvider = null,
        t9CjkFullProvider = null,
        t9CjkInitialsProvider = null,
        t9ZhuyinInitialsProvider = null
    )

    val icon: Any? by lazy { rawIcon }
    val t9CjkFull: String by lazy { rawT9CjkFull.ifEmpty { t9CjkFullProvider?.invoke() ?: "" } }
    val t9CjkInitials: String by lazy { rawT9CjkInitials.ifEmpty { t9CjkInitialsProvider?.invoke() ?: "" } }
    val t9ZhuyinInitials: String by lazy { rawT9ZhuyinInitials.ifEmpty { t9ZhuyinInitialsProvider?.invoke() ?: "" } }

    /**
     * Non-blocking async fetcher for CJK full string
     */
    suspend fun awaitCjkFull(): String {
        return rawT9CjkFull.ifEmpty {
            t9CjkFullDeferred?.await() ?: t9CjkFullProvider?.invoke() ?: ""
        }
    }

    /**
     * Non-blocking async fetcher for CJK initials string
     */
    suspend fun awaitCjkInitials(): String {
        return rawT9CjkInitials.ifEmpty {
            t9CjkInitialsDeferred?.await() ?: t9CjkInitialsProvider?.invoke() ?: ""
        }
    }

    /**
     * Non-blocking async fetcher for Zhuyin initials string
     */
    suspend fun awaitZhuyinInitials(): String {
        return rawT9ZhuyinInitials.ifEmpty {
            t9ZhuyinInitialsDeferred?.await() ?: t9ZhuyinInitialsProvider?.invoke() ?: ""
        }
    }

    /**
     * Non-blocking async fetcher for Icon asset
     */
    suspend fun awaitIcon(): Any? {
        return rawIcon ?: iconDeferred?.await() ?: iconProvider?.invoke()
    }
}
