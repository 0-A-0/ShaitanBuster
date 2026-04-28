package com.example.scrolllist.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
@Stable
class FlyableBody(
    view: ImageBitmap,
    position: Offset,
    dstSize: IntSize,
):Body(view, position,dstSize) {
    override var bias: Offset = Offset(0f,10f)
    override fun update() {
        position += bias
    }
}