package com.example.scrolllist.units

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

@Immutable
data class Animation(
    val back:List<ImageBitmap>,
    val right:List<ImageBitmap>,
    val left:List<ImageBitmap>,
    val front:List<ImageBitmap>,
)
