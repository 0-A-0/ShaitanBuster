package com.example.scrolllist.ui.assets

import androidx.compose.ui.graphics.ImageBitmap

class WeaponAssets(
    val axe: ImageBitmap,
    val axe_low_effect: ImageBitmap,
    val axe_strong_effect: ImageBitmap,
    val axe_mirror: ImageBitmap,
    val axe_low_effect_mirror: ImageBitmap,
    val axe_strong_effect_mirror: ImageBitmap,
    val present_view_axe: ImageBitmap = axe,
    val viewRightAnimation: List<ImageBitmap>,
    val viewLeftAnimation: List<ImageBitmap>,
    val present_view_revolver: ImageBitmap,
    val viewLeftShotgun: ImageBitmap,
    val viewRightShotgun: ImageBitmap,
    val present_view_shotgun: ImageBitmap = viewRightShotgun,
)