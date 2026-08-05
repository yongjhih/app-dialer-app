package com.github.yongjhih.appdialer.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

/**
 * Returns a [Sequence] containing this [View] and its direct children (if it is a [ViewGroup]).
 */
val View.selfAndChildren: Sequence<View>
    get() = sequence {
        yield(this@selfAndChildren)
        (this@selfAndChildren as? ViewGroup)?.children?.let { yieldAll(it) }
    }

/**
 * Extension function to clear residual opaque backgrounds from activity content view
 * and its direct child views, allowing translucent themes to show through properly.
 */
fun Activity.clearContentBackgrounds() {
    findViewById<View>(android.R.id.content)?.selfAndChildren?.forEach { view ->
        view.setBackgroundColor(Color.TRANSPARENT)
        view.background = null
    }
}
