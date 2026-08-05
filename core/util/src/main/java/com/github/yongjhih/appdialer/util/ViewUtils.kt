package com.github.yongjhih.appdialer.util

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
