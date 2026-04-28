package com.example.scrolllist.objects

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.DrawableWithZ

@Stable
abstract class StaticObject(
    val view: ImageBitmap,
    val startPosition: Offset,
): DrawableWithZ {
    abstract val dstSize:IntSize
    val center: Offset
        get() = startPosition + Offset(dstSize.width / 2f, dstSize.height / 2f)
    val collisionRect: Rect
        get() = Rect(
            left = startPosition.x,
            top = startPosition.y,
            right = startPosition.x + view.width,
            bottom = startPosition.y + view.height
        )
    override val indexZ: Float
        get() = center.y
    override fun DrawScope.draw() {
        drawImage(
            image = view,
            dstSize = dstSize,
            dstOffset = startPosition.round()
        )
    }

}