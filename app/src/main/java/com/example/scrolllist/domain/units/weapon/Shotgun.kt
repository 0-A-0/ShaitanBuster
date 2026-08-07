package com.example.scrolllist.domain.units.weapon

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.managers.AudioManager
import com.example.scrolllist.domain.utils.calcAngle
import com.example.scrolllist.domain.utils.calcDistance
import com.example.scrolllist.domain.utils.calcDistanceForComparison
import com.example.scrolllist.domain.utils.calculatePowerByDist
import com.example.scrolllist.domain.units.Player
import com.example.scrolllist.domain.units.enemy.bodies.Body
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.bodies.FixedBody
import com.example.scrolllist.domain.units.enemy.bodies.GhostBody
import com.example.scrolllist.domain.units.enemy.Spawner
import com.example.scrolllist.domain.utils.blazingFor
import com.example.scrolllist.domain.utils.blazingReflectFor
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
    val weaponPower = 100f
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
                        enemies.blazingFor { enemy ->
                            if(enemy is Spawner) return@blazingFor
                            val distanceX2 = calcDistanceForComparison(it, enemy.center)
                            if (distanceX2 > hitDistance*hitDistance) return@blazingFor
                            val dx = it - enemy.center
                            val direction = dx / calcDistance(it, enemy.center)
                            onHitEnemy(enemy, direction * power)
                        }
                        bodies.blazingFor { body ->
                            if(body is FixedBody) return@blazingFor
                            val distanceX2 = calcDistanceForComparison(it, body.center)
                            if (distanceX2 > 500f * 500f) return@blazingFor
                            val dx = it - body.center
                            val direction = dx / calcDistance(it, body.center)
                            onHitBody(body, direction * power)
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
        onHitBody: (Body, Float, Int, Float) -> Unit
    ) {
        if (clip != 0) {
            clip -= 1
            AudioManager.play(AudioManager.SoundType.SHOTGUN_SHOT)

            val minDistance = hitDistance * hitDistance
            val fixedPlayerCenter = player.center
            enemies.blazingReflectFor { enemy ->
                val distance = calcDistanceForComparison(fixedPlayerCenter, enemy.center)
                if (distance > minDistance) return@blazingReflectFor

                val enemyAngle = calcAngle(enemy.center, fixedPlayerCenter)
                val diffAngle = abs(enemyAngle - angle)
                val finalDiffAngle = if (diffAngle > 180f) 360f - diffAngle else diffAngle

                if (finalDiffAngle <= 45f) {
                    onHitEnemy(enemy)
                }
            }

            bodies.blazingReflectFor { body ->
                val distance = calcDistanceForComparison(fixedPlayerCenter, body.center)
                if (body is GhostBody || calcDistanceForComparison(
                        fixedPlayerCenter,
                        body.center
                    ) > minDistance
                ) return@blazingReflectFor

                val bodyAngle = calcAngle(body.center, fixedPlayerCenter)
                val diffAngle = abs(bodyAngle - angle)
                val finalDiffAngle = if (diffAngle > 180f) 360f - diffAngle else diffAngle

                if (finalDiffAngle <= 45f) {
                    onHitBody(body, bodyAngle, calculatePowerByDist(distance, minDistance), weaponPower)
                }
            }
        }
    }

}