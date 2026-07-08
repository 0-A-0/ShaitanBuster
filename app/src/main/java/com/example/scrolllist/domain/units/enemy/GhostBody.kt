package com.example.scrolllist.domain.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
class GhostBody(
    position: Offset,
    dstSize: IntSize,
):Body(position,dstSize) {
    var alpha: Float = 1f
    override fun update(delta: Float) {
        super.update(delta)
        alpha -= 0.05f
    }

    override fun addBias(power: Int, angle: Float, powerWeapon: Float) {
        return
    }
}