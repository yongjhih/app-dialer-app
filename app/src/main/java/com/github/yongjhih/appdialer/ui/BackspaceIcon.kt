package com.github.yongjhih.appdialer.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BackspaceIcon: ImageVector
    get() {
        if (_backspaceIcon != null) return _backspaceIcon!!
        _backspaceIcon = ImageVector.Builder(
            name = "Backspace",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(22f, 3f)
                lineTo(7f, 3f)
                curveTo(6.17f, 3f, 5.43f, 3.48f, 5.07f, 4.22f)
                lineTo(0.5f, 12f)
                lineTo(5.07f, 19.78f)
                curveTo(5.43f, 20.52f, 6.17f, 21f, 7f, 21f)
                lineTo(22f, 21f)
                curveTo(23.1f, 21f, 24f, 20.1f, 24f, 19f)
                lineTo(24f, 5f)
                curveTo(24f, 3.9f, 23.1f, 3f, 22f, 3f)
                close()
                moveTo(19f, 15.59f)
                lineTo(17.59f, 17f)
                lineTo(14f, 13.41f)
                lineTo(10.41f, 17f)
                lineTo(9f, 15.59f)
                lineTo(12.59f, 12f)
                lineTo(9f, 8.41f)
                lineTo(10.41f, 7f)
                lineTo(14f, 10.59f)
                lineTo(17.59f, 7f)
                lineTo(19f, 8.41f)
                lineTo(15.41f, 12f)
                lineTo(19f, 15.59f)
                close()
            }
        }.build()
        return _backspaceIcon!!
    }

private var _backspaceIcon: ImageVector? = null
