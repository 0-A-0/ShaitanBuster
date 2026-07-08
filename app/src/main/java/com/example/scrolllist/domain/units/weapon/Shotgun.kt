package com.example.scrolllist.domain.units.weapon

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.compose.ui.util.fastCoerceAtMost
import com.example.scrolllist.AudioManager
import com.example.scrolllist.TimeManager
import com.example.scrolllist.domain.calcAngle
import com.example.scrolllist.domain.calcDistance
import com.example.scrolllist.domain.calcDistanceForComparison
import com.example.scrolllist.domain.calculatePowerByDist
import com.example.scrolllist.domain.units.Player
import com.example.scrolllist.domain.units.enemy.Body
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.FixedBody
import com.example.scrolllist.domain.units.enemy.GhostBody
import com.example.scrolllist.domain.units.enemy.Spawner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

@Stable
class Shotgun(
    val scope: CoroutineScope,
//    val viewLeft: ImageBitmap,
//    val viewRight: ImageBitmap,
) : Weapon {
    override val minHolyModeUsable: Float = 0f
    override var holyModeProgress by mutableStateOf(0f)
    val dstSize = IntSize(48, 150)
    var clip by mutableStateOf(70)
    override val present_clip: String
        get() = "$clip"
    override val present_view = WeaponType.Shotgun
    override var angle  = 0f
    override val shootable: Boolean = true
    override val trend: Boolean
        get() = if (angle > 180f) false else true
    var bulletProgress = 0f
    val durationBulletAnimation = 200f
    var isBulletAimation = false
    var shotPoint: Offset? = null
    var targetMagnet: Offset? = null
    val hitDistance = 500f
    var actionJob: Job? = null

    fun update(delta:Float){
        if (!isBulletAimation) return
        bulletProgress += delta/durationBulletAnimation
        if (bulletProgress >= 1f){
            bulletProgress = 1f
            isBulletAimation = false
            shotPoint = null
        }
    }
    fun animateShotgun(currentShotPoint: Offset) {
        if (clip <= 0) return
        bulletProgress = 0f
        shotPoint = currentShotPoint
        isBulletAimation = true
    }

    fun startAction(
        enemies: List<Enemy>,
        bodies: List<Body>,
        onHitEnemy: (Enemy, Offset) -> Unit,
        onHitBody: (Body, Offset) -> Unit
    ) {
        if (actionJob?.isActive == true) return
        actionJob = scope.launch {
            var lastTime = 0L
            while (targetMagnet != null) {
                withFrameNanos {currentTime ->
                    if (lastTime == 0L) {
                        lastTime = currentTime
                        return@withFrameNanos
                    }
                    val delta = (currentTime - lastTime)/1_000_000_000f
                    lastTime = currentTime
                    targetMagnet?.let {
                        val power = 700f * holyModeProgress * delta
                        for (i in enemies.indices) {
                            if(enemies[i] is Spawner) continue
                            val distanceX2 = calcDistanceForComparison(it, enemies[i].center)
                            if (distanceX2 > hitDistance*hitDistance) continue
                            val dx = it - enemies[i].center
                            val direction = dx / calcDistance(it, enemies[i].center)
                            onHitEnemy(enemies[i], direction * power)
                        }
                        for (i in bodies.indices) {
                            if(bodies[i] is FixedBody) continue
                            val distanceX2 = calcDistanceForComparison(it, bodies[i].center)
                            if (distanceX2 > 500f * 500f) continue
                            val dx = it - bodies[i].center
                            val direction = dx / calcDistance(it, bodies[i].center)
                            onHitBody(bodies[i], direction * power)
                        }
                        holyModeProgress = (holyModeProgress - 0.2f * delta).coerceIn(0f,1f)
                    }
                }
            }
        }
    }

    fun stopAction() {
        actionJob?.cancel()
    }

    inline fun fire(
        player: Player,
        enemies: List<Enemy>,
        bodies: List<Body>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int) -> Unit
    ) {
        if (clip != 0) {
            clip -= 1
            AudioManager.play(AudioManager.SoundType.SHOTGUN_SHOT)

            val minDistance = hitDistance * hitDistance
            val fixedPlayerCenter = player.center
            for (i in enemies.indices.reversed()) {
                val distance = calcDistanceForComparison(fixedPlayerCenter, enemies[i].center)
                if (distance > minDistance) continue

                val enemyAngle = calcAngle(enemies[i].center, fixedPlayerCenter)
                val diffAngle = abs(enemyAngle - angle)
                val finalDiffAngle = if (diffAngle > 180f) 360f - diffAngle else diffAngle

                if (finalDiffAngle <= 45f) {
                    onHitEnemy(enemies[i])
                }
            }

            for (i in bodies.indices.reversed()) {
                val distance = calcDistanceForComparison(fixedPlayerCenter, bodies[i].center)
                if (bodies[i] is GhostBody || calcDistanceForComparison(
                        fixedPlayerCenter,
                        bodies[i].center
                    ) > minDistance
                ) continue

                val bodyAngle = calcAngle(bodies[i].center, fixedPlayerCenter)
                val diffAngle = abs(bodyAngle - angle)
                val finalDiffAngle = if (diffAngle > 180f) 360f - diffAngle else diffAngle

                if (finalDiffAngle <= 45f) {
                    onHitBody(bodies[i], bodyAngle, calculatePowerByDist(distance, minDistance))
                }
            }
        }
    }

}