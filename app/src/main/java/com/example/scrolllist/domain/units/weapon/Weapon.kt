package com.example.scrolllist.domain.units.weapon

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope

@Stable
sealed interface Weapon{
    val present_view: WeaponType
    val present_clip: String
    val trend: Boolean
    var angle: Float
    var holyModeProgress: Float
    val minHolyModeUsable: Float
    val shootable: Boolean
        get() = false
    fun onKill(killCount:Int){
        holyModeProgress = (holyModeProgress + Math.pow(killCount.coerceAtMost(10).toDouble(),1.5).toFloat()/20f).coerceIn(0f,1f)
    }
}