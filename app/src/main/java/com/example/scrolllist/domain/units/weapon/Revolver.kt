package com.example.scrolllist.domain.units.weapon

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.managers.AudioManager
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.domain.utils.Accumulator
import com.example.scrolllist.domain.utils.isIntersectWithLine
import com.example.scrolllist.domain.units.enemy.bodies.Body
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.bodies.GhostBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
class Revolver(
    val scope: CoroutineScope,
    val animationSize: Int,
//    val viewRightAnimation: List<ImageBitmap>,
//    val viewLeftAnimation: List<ImageBitmap>,
) : Weapon {
    override val minHolyModeUsable: Float = 0.7f
    override var holyModeProgress by mutableStateOf(0f)
    override val present_clip: String
        get() = "$clip"
    override val present_view = WeaponType.Revolver
    override var angle = 0f
    override val shootable: Boolean = true
    var seriesCount = 0
    var animationIndex = 0
    val animationAccumulator = Accumulator(200f)
    var bulletProgress = 0f
    val durationBulletAnimation = 200f
    var isBulletAimation = false
    var shotPoint: Offset? = null
    var isHolyShot = false
    var job: Job? = null
    var startBullet: Offset = Offset.Zero

    val dstSize = IntSize(60, 80)
    var clip by mutableStateOf(100)
    override val trend: Boolean
        get() = if (angle > 180f) false else true
    fun update(delta:Float){
        animationAccumulator.update(delta){
            if (isBulletAimation) animationIndex = ++animationIndex % animationSize
        }
        if (!isBulletAimation) return
        bulletProgress += delta/durationBulletAnimation
        if (bulletProgress >= 1f){
            bulletProgress = 1f
            isBulletAimation = false
            shotPoint = null
            animationIndex = 0
        }
    }
    fun animateRevolver(currentShotPoint: Offset, holyMode: Boolean) {
        if (clip <= 0) return
        bulletProgress = 0f
        shotPoint = currentShotPoint
        isHolyShot = holyMode
        isBulletAimation = true
    }

    fun checkSeries(createKillmark: (Int) -> Unit) {
        job?.cancel()
        seriesCount++
        if (seriesCount > 1) {
            createKillmark(seriesCount)
        }
        job = scope.launch {
            TimeManager.delay(1000L)
            seriesCount = 0
        }
    }
    inline fun fire(
        onSuccessAction:() -> Unit,
        player: com.example.scrolllist.domain.units.Player,
        position: Offset,
        enemies: List<Enemy>,
        bodies: List<Body>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int) -> Unit,
        holyMode: Boolean
    ){
        if (clip != 0) {
            if (holyMode && holyModeProgress < minHolyModeUsable) return
            if(holyMode) onSuccessAction()
            clip -= 1
            startBullet = player.center
            animateRevolver(position,holyMode)
            AudioManager.play(AudioManager.SoundType.REVOLVER_SHOT)
                val bulletRect = if(!holyMode){
                    Rect(
                        left = minOf(player.position.x, position.x) - 0.1f,
                        top = minOf(player.position.y, position.y) - 0.1f,
                        right = maxOf(player.position.x, position.x) + 0.1f,
                        bottom = maxOf(player.position.y, position.y) + 0.1f
                    )
                } else null
//            var hitCount = 0
            val fixedPlayerCenter = player.center
            for (i in enemies.indices.reversed()) {
                if(!holyMode) {
                    if (/*hitCount >= 3 ||*/ !bulletRect!!.overlaps(enemies[i].collisionRect)) continue
                }
                    if (isIntersectWithLine(enemies[i].collisionRect, fixedPlayerCenter, position, holyMode)) {
                    onHitEnemy(enemies[i])
//                    hitCount++
                }
            }
            for (i in bodies.indices.reversed()) {
                if (bodies[i] is GhostBody) continue
                if ((holyMode || bulletRect!!.overlaps(bodies[i].collisionRect)) && isIntersectWithLine(bodies[i].collisionRect, fixedPlayerCenter, position, holyMode)) {
                    onHitBody(bodies[i],angle, if(!holyMode)7 else 10)
                }
            }
            if(holyMode) holyModeProgress = (holyModeProgress - minHolyModeUsable).coerceIn(0f,1f)
        }
    }
}