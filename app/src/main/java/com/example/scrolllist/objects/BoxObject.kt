package com.example.scrolllist.objects

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

@Immutable
class BoxObject(
    view: ImageBitmap,
    startPosition: Offset,
):DinamicObject(startPosition) {
    override val view: ImageBitmap = view
    override val dstSize: IntSize = IntSize(100,100)
}