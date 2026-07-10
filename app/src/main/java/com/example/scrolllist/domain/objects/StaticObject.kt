package com.example.scrolllist.domain.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.domain.DrawableWithZ

abstract class StaticObject(
    val startPosition: Offset,
): DrawableWithZ {
    val position = startPosition.round()
    abstract val dstSize:IntSize
    val center: Offset
        get() = startPosition + Offset(dstSize.width / 2f, dstSize.height / 2f)
    val collisionRect: Rect
        get() = Rect(
            left = startPosition.x,
            top = startPosition.y,
            right = startPosition.x + dstSize.width,
            bottom = startPosition.y + dstSize.height
        )
    override val indexZ: Float
        get() = center.y
}