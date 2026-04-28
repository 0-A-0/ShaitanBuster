package com.example.scrolllist

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.drawscope.DrawScope

@Stable
interface DrawableWithZ {
    val indexZ:Float
    fun DrawScope.draw()
}