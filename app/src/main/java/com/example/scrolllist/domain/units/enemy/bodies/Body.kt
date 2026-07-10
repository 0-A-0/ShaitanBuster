package com.example.scrolllist.domain.units.enemy.bodies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.domain.DrawableWithZ

abstract class Body(
    position: Offset,
    val dstSize: IntSize,
): DrawableWithZ {
    var lifeTimeMl = 2000f
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
    open fun update(delta:Float){
        lifeTimeMl -= delta
//        if(steps > 0){
//            position += bias
//            bias *= 0.9f
//            steps--
//        }
    }
}