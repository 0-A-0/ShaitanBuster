package com.example.scrolllist.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap

@Stable
data class Blood(
    var view: ImageBitmap?,
    val pivot: Offset,
    val position: Offset,
//    val type: EnemyType,
    val angle: Float,
    val power: Int,
){
    var index =-1
}
