package com.example.scrolllist.ui.assets

import androidx.compose.ui.graphics.ImageBitmap

class EnemyAssets(
    val frontAnimation : List<ImageBitmap>? = null,
    val frontAnimationMirrored : List<ImageBitmap>? = null,
    val spawnAnimation: List<ImageBitmap>,
    val view: ImageBitmap? = null,
    val bodyView: ImageBitmap,
)