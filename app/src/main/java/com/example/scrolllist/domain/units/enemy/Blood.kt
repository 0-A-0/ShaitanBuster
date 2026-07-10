package com.example.scrolllist.domain.units.enemy

import androidx.compose.ui.geometry.Offset
import com.example.scrolllist.domain.utils.Accumulator

data class Blood(
    val pivot: Offset,
    val position: Offset,
    val angle: Float,
    val power: Int,
){
    var lifeTimeMl = 10_000f
    var index = 0
    val animation = Accumulator(10f)
    fun update(delta:Float){
        lifeTimeMl -= delta
        animation.update(delta){
            if (index < power - 1) {
                index++
            }
        }
    }
}
