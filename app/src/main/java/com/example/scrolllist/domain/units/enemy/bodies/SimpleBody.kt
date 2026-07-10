package com.example.scrolllist.domain.units.enemy.bodies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
class SimpleBody(
    position: Offset,
    dstSize: IntSize,
): Body(position,dstSize) {
    override fun update(delta: Float) {
        super.update(delta)
        if(steps > 0){
            position += bias
            bias *= 0.9f
            steps--
        }
    }
}