package com.example.scrolllist.units.enemy

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
@Stable
class GhostBody(
    view: ImageBitmap,
    position: Offset,
    dstSize: IntSize,
):Body(view, position,dstSize) {
    var alpha: Float = 1f
    override fun update() {
        alpha -= 0.05f
    }

    override fun addBias(power: Int, angle: Float, powerWeapon: Float) {
        return
    }
    override fun DrawScope.draw() {
        scale(0.9f,0.9f, pivot = this@GhostBody.center) {
            drawImage(
                dstSize = dstSize,
                dstOffset = position.round(),
                image = view,
                filterQuality = FilterQuality.None,
                colorFilter = ColorFilter.tint(
                    Color.Black.copy(alpha = 0.7f),
                    blendMode = BlendMode.SrcAtop
                ),
                alpha = alpha.coerceAtLeast(0f)
            )
        }
    }
}