package com.example.scrolllist.domain.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
class FlyableBody(
    position: Offset,
    dstSize: IntSize,
):Body(position,dstSize) {
    override var bias: Offset = Offset(0f,10f)
    override fun update(delta: Float) {
        super.update(delta)
        position += bias
    }
}