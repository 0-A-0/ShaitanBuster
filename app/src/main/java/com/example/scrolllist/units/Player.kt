package com.example.scrolllist.units

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.scrolllist.DrawableWithZ
import com.example.scrolllist.calcDistanceForСomparison
import com.example.scrolllist.checkNotCollision
import com.example.scrolllist.objects.BoxObject
import com.example.scrolllist.units.enemy.Enemy
import com.example.scrolllist.units.enemy.EnemyType

@Stable
class Player(
    val name: String,
    view: ImageBitmap,
    val viewBack: ImageBitmap,
    val viewFront: ImageBitmap,
    val viewLeft: ImageBitmap,
    val viewRight: ImageBitmap,
    val animation: Animation,
    startPosition:Offset = Offset(-1f,-1f),
):DrawableWithZ{
    var delta = Offset.Zero
    var hitPoint by mutableIntStateOf(100)
    var view by mutableStateOf(view)
    var position by mutableStateOf(startPosition)
    var trend by mutableStateOf(PlayerTrend.Stop)
        private set
    var trendCombo  = mutableListOf<PlayerTrend>()
    var lastDamageTimeAccumulator = 0f
    var lastBlindTimeAccumulator = 0f
    var isBlind by mutableStateOf(false)
    var damageEffectAlpha by mutableStateOf(0f)
    val saveDelay = 500f
    val center: Offset
        get() = position + Offset(viewBack.width / 2f, viewBack.height / 2f)
    override val indexZ: Float
        get() = center.y
    val collisionRect: Rect
        get() = Rect(
            left = position.x,
            top = position.y,
            right = position.x + viewBack.width,
            bottom = position.y + viewBack.height
        )
    fun updateTrend(newTrend: PlayerTrend){
        trend = newTrend
        if (newTrend == PlayerTrend.Stop) return
        if (trendCombo.lastOrNull() != newTrend){
            trendCombo.add(newTrend)
        }
        if (trendCombo.size > 3){
            trendCombo.removeAt(0)
        }

    }
    fun update(deltaTime:Float, objects: List<BoxObject>){
        lastDamageTimeAccumulator += deltaTime
        val step = delta * deltaTime/25f
        if(trend != PlayerTrend.Stop) {
            if (objects
                    .all {
                        checkNotCollision(
                            getNextRect(step),
                            it.collisionRect
                        )
                    }
            ) {
                position += step
            }
        }
        if(isBlind) {
            lastBlindTimeAccumulator += deltaTime
            if (lastBlindTimeAccumulator >= 15000f) isBlind = false
        }
        damageEffectAlpha = (1f - lastDamageTimeAccumulator/saveDelay).coerceIn(0f,1f)
    }
    fun checkCombo():Boolean{
        if (trendCombo.size != 3) return false
        val (first,second,last) = trendCombo
        if ( first == last && second != first){
            trendCombo.clear()
            return true
        }
        return false
    }
    fun checkDeath(DeathPlayer: (Int) -> Unit, enemies: List<Enemy>, kills: Int) {
//        return
        if(lastDamageTimeAccumulator >= saveDelay) {
            val minDistance = 50f * 50f
            for (enemy in enemies) {
                if (enemy.enemyIsReady && calcDistanceForСomparison(enemy.center, center) <= minDistance) {
                    when(enemy.enemyType) {
                        EnemyType.Attacking -> {
                            hitPoint -= enemy.damage
                        }
                        EnemyType.Blinding -> {
                            isBlind = true
                            lastBlindTimeAccumulator = 0f
                            hitPoint -= enemy.damage
                        }
                    }
                    lastDamageTimeAccumulator = 0f
                    if (hitPoint <= 0) DeathPlayer(kills)
                    break
                }
            }
        }
    }
    fun getNextRect(delta:Offset):Rect{
        val nextPosition = position + delta
        return Rect(
            left = nextPosition.x + viewBack.width/3,
            top = nextPosition.y  + viewBack.height/3,
            right = nextPosition.x + viewBack.width * 2/3,
            bottom = nextPosition.y + viewBack.height * 2/3
        )
    }
    override fun DrawScope.draw() {
        val colorFilter = if (damageEffectAlpha> 0){
            ColorFilter.tint(Color.Red.copy(alpha = damageEffectAlpha * 0.8f), blendMode = BlendMode.SrcAtop)
        } else null
        drawImage(
            topLeft = position,
            image = view,
            colorFilter = colorFilter,
        )
    }
//    var weapon by mutableStateOf()
}