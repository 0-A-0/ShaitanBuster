package com.example.scrolllist.objects

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

@Stable
class FirePoint(
    position: Offset,
    val fireAnimation:List<ImageBitmap>,
):DinamicObject(position) {
    var index = 0
    override val view: ImageBitmap
        get() = fireAnimation[index]
    override val dstSize: IntSize = IntSize(200,200)
}