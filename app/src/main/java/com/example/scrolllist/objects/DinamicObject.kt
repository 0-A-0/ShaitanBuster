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
abstract class DinamicObject(
    val position: Offset,
):DrawableWithZ {
    abstract val view:ImageBitmap
    abstract val dstSize: IntSize
    val collisionRect: Rect
        get() = Rect(
            left = position.x,
            top = position.y,
            right = position.x + dstSize.width,
            bottom = position.y + dstSize.height
        )
    val center: Offset
        get() = position + Offset(dstSize.width / 2f, dstSize.height / 2f)
    override val indexZ: Float
        get() = center.y
    override fun DrawScope.draw() {
        drawImage(
            image = view,
            dstSize = dstSize,
            dstOffset = position.round()
        )
    }
}