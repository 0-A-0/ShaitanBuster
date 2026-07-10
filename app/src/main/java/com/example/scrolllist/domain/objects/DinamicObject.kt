package com.example.scrolllist.domain.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.domain.DrawableWithZ
import com.example.scrolllist.domain.utils.Accumulator

abstract class DinamicObject(
    val startPosition: Offset,
    val animationSize: Int,
): DrawableWithZ {
    var indexFrame = 0
    val position = startPosition.round()
    abstract val dstSize: IntSize
    val collisionRect: Rect
        get() = Rect(
            left = startPosition.x,
            top = startPosition.y,
            right = startPosition.x + dstSize.width,
            bottom = startPosition.y + dstSize.height
        )
    abstract val animation: Accumulator
    val center: Offset
        get() = startPosition + Offset(dstSize.width / 2f, dstSize.height / 2f)
    override val indexZ: Float
        get() = center.y
    fun update(delta:Float){
        animation.update(delta){
            indexFrame = (indexFrame + 1) % animationSize
        }
    }
}