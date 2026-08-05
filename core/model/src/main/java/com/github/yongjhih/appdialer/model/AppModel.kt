package com.github.yongjhih.appdialer.model

data class AppModel(
    val label: String,
    val packageName: String,
    val className: String,
    val icon: Any? = null,
    val t9Full: String,
    val t9Initials: String,
    val t9Words: List<String>,
    val t9CjkFull: String = "",
    val t9CjkInitials: String = "",
    val t9ZhuyinInitials: String = "",
    val t9ZhuyinFull: String = "",
    val matchScore: Int = 0,
    val matchedIndices: List<Int> = emptyList()
)
