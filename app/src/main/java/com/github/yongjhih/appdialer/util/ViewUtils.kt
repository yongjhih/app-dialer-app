package com.github.yongjhih.appdialer.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

/**
 * Extension function to clear residual opaque backgrounds from activity content view
 * and its direct child views using Android KTX children extension, allowing translucent
 * themes to show through properly.
 */
fun Activity.clearContentBackgrounds() {
    findViewById<View>(android.R.id.content)?.let { content ->
        content.setBackgroundColor(Color.TRANSPARENT)
        content.background = null
        (content as? ViewGroup)?.children?.forEach { child ->
            child.setBackgroundColor(Color.TRANSPARENT)
            child.background = null
        }
    }
}
