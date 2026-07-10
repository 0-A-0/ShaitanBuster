package com.example.scrolllist.domain.units.enemy.bodies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
class FlyableBody(
    position: Offset,
    dstSize: IntSize,
): Body(position,dstSize) {
    override var bias: Offset = Offset(0f,10f)
    override fun update(delta: Float) {
        super.update(delta)
        position += bias
    }
}