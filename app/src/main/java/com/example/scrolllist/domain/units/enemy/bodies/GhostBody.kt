package com.example.scrolllist.domain.units.enemy.bodies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

class GhostBody(
    position: Offset,
    dstSize: IntSize,
): Body(position,dstSize) {
    var alpha: Float = 1f
    override fun update(delta: Float) {
        super.update(delta)
        alpha -= 0.05f
    }

    override fun addBias(power: Int, angle: Float, powerWeapon: Float) {
        return
    }
}