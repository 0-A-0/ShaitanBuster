package com.example.scrolllist.domain.objects

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

class BoxObject(
    startPosition: Offset,
): StaticObject(startPosition) {
    override val dstSize: IntSize = IntSize(100,100)
}