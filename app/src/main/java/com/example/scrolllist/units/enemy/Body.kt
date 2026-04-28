package com.example.scrolllist.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.DrawableWithZ

@Stable
abstract class Body(
    val view: ImageBitmap,
    position: Offset,
    val dstSize: IntSize,
):DrawableWithZ {
    var position = position
    val center: Offset
        get() = position + Offset(dstSize.width / 2f, dstSize.height / 2f)
    override val indexZ: Float
        get() = center.y
    val frames = 10
    var steps = 0
    open var bias: Offset = Offset.Zero
    val collisionRect: Rect
        get() = Rect(
            left = position.x,
            top = position.y,
            right = position.x + dstSize.width,
            bottom = position.y + dstSize.height
        )
//    init {
//        addBias(power,angle)
//    }
    open fun addBias(power: Int, angle: Float, powerWeapon: Float = 5f){
        val totalDistance = power * powerWeapon
        val radians = Math.toRadians(angle.toDouble())
        bias += Offset(
            x = (totalDistance * Math.sin(radians) / frames).toFloat(),
            y = (totalDistance * -Math.cos(radians) / frames).toFloat()
        )

        steps = frames
    }
    open fun update(){
        if(steps > 0){
            position += bias
            bias *= 0.9f
            steps--
        }
    }
    override fun DrawScope.draw() {
        scale(0.9f,0.9f, pivot = this@Body.center) {
            drawImage(
                dstSize = dstSize,
                dstOffset = position.round(),
                image = view,
                filterQuality = FilterQuality.None,
                colorFilter = ColorFilter.tint(
                    Color.Black.copy(alpha = 0.7f),
                    blendMode = BlendMode.SrcAtop
                )
            )
        }
    }
}