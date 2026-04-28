package com.example.scrolllist.units.weapon

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.scrolllist.units.Player
import com.example.scrolllist.units.enemy.Body
import com.example.scrolllist.units.enemy.Enemy

@Stable
sealed interface Weapon{
    val present_view: ImageBitmap
    val present_clip: String
    val trend: Boolean
    var angle: Float
    var holyModeProgress: Float
    val minHolyModeUsable: Float
    val shootable: Boolean
        get() = false
    fun DrawScope.draw(player: Player)
    fun DrawScope.drawEffects(){ return }
    fun onKill(killCount:Int){
        holyModeProgress = (holyModeProgress + Math.pow(killCount.coerceAtMost(10).toDouble(),1.5).toFloat()/20f).coerceIn(0f,1f)
    }
}