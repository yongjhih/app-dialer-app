package com.github.yongjhih.appdialer.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup

/**
 * Extension function to clear residual opaque backgrounds from activity content view
 * and its direct child views, allowing translucent themes to show through properly.
 */
fun Activity.clearContentBackgrounds() {
    findViewById<View>(android.R.id.content)?.let { content ->
        content.setBackgroundColor(Color.TRANSPARENT)
        content.background = null
        if (content is ViewGroup) {
            for (i in 0 until content.childCount) {
                content.getChildAt(i)?.let { child ->
                    child.setBackgroundColor(Color.TRANSPARENT)
                    child.background = null
                }
            }
        }
    }
}
