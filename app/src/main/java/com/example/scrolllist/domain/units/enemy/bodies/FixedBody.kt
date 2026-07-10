package com.example.scrolllist.domain.units.enemy.bodies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
class FixedBody(
    position: Offset,
    dstSize: IntSize,
): Body(position, dstSize) {
    override fun addBias(power: Int, angle: Float, powerWeapon: Float) {
        return
    }
    override fun update(delta:Float) {
        super.update(delta)
        return
    }
}