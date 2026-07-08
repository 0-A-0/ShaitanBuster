package com.example.scrolllist.domain.objects

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

@Immutable
class Ammunition(
    val value: Int,
    val type: AmmunitionType,
    startPosition: Offset,
): StaticObject(startPosition) {
    override val dstSize: IntSize = IntSize(50,50)
}