package com.example.scrolllist.domain.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.domain.Accumulator

class FirePoint(
    position: Offset,
    animationSize: Int,
): DinamicObject(position, animationSize) {
    override val animation: Accumulator = Accumulator(150f)
    override val dstSize: IntSize = IntSize(200,200)
}