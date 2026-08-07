package com.example.scrolllist.domain.units.weapon

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.domain.utils.caclAxePower
import com.example.scrolllist.domain.utils.calcAngle
import com.example.scrolllist.domain.utils.calcDistanceForComparison
import com.example.scrolllist.domain.utils.isIntersectWithLine
import com.example.scrolllist.domain.units.enemy.bodies.Body
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.bodies.GhostBody
import com.example.scrolllist.domain.utils.blazingFor
import com.example.scrolllist.domain.utils.blazingReflectFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

@Stable
class Axe(
    val scope: CoroutineScope,
) : Weapon {
    override val minHolyModeUsable: Float = 0.2f
    override var holyModeProgress by mutableStateOf(0f)
    override val present_clip: String = "∞"
    override val present_view = WeaponType.Axe
    override var angle =0f
    val dstSize = IntSize(77, 114)
    var speed = 0f
    val hitDistanceX2 = 180f*180f
    val slicePoints = mutableListOf<Offset>()
    override var trend = true
    private var currentJob: Job? = null
    fun startSlice(){
        currentJob?.cancel()
    }
    inline fun slice(
        onSuccessAction: () -> Unit,
        bodies: List<Body>,
        enemies: List<Enemy>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int, Float) -> Unit
    ){
        var totalLength = 0f
        for (i in 0 until slicePoints.size - 1) {
            totalLength += calcDistanceForComparison(slicePoints[i], slicePoints[i + 1])
        }
        if(totalLength > 10_000f){
        slicePoints.windowed(size = 2, step = 1).forEach { (start, end) ->
            enemies.blazingReflectFor { enemy ->
                if (isIntersectWithLine(enemy.collisionRect, start, end)) {
                    onHitEnemy(enemy)
                }
            }
        }
            slicePoints.windowed(size = 2, step = 1).forEach { (start, end) ->
                bodies.blazingReflectFor { body ->
                    if (isIntersectWithLine(body.collisionRect, start, end)) {
                        onHitBody(body, Random.nextFloat() * 360f,  Random.nextInt(11), 5f )
                    }
                }
            }
            onSuccessAction()
            holyModeProgress = (holyModeProgress - totalLength/400_000f).coerceIn(0f,1f)
        }

        slicePoints.clear()
    }

    fun rotationAxe(
        isHold: Boolean,
        player: com.example.scrolllist.domain.units.Player,
        enemies: List<Enemy>,
        bodies: List<Body>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int, Float) -> Unit
    ) {
        currentJob?.cancel()
        if (slicePoints.isNotEmpty()) return
        currentJob = scope.launch {
            val trendValue = if (trend) 1f else -1f
            if (!isHold) {
                while (speed > 0) {
                    TimeManager.delay(10L)
                    speed -= 0.3f
                    angle = (angle + speed * trendValue) % 360f
                    hit(player, enemies, bodies, onHitEnemy,onHitBody)
                }
                speed = 1f
                return@launch
            } else {
                while (true) {
                    TimeManager.delay(20L)
                    angle = (angle + speed * trendValue) % 360f
                    if (speed < 30f) speed += 0.1f
                    hit(player, enemies, bodies, onHitEnemy,onHitBody)
                }
            }
        }
    }

    private inline fun hit(
        player: com.example.scrolllist.domain.units.Player,
        enemies: List<Enemy>,
        bodies: List<Body>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int, Float) -> Unit
    ) {
        val stop = 2f
        val pCenter = player.center

        enemies.blazingReflectFor { enemy ->
            if (calcDistanceForComparison(pCenter, enemy.center) <= hitDistanceX2) {
                val enemyAngle = if (trend) calcAngle(enemy.center, pCenter)
                else -(360f - calcAngle(enemy.center, pCenter))

                if (speed > 25f) {
                    onHitEnemy(enemy)
                    speed -= (stop - 1f)
                    return@blazingReflectFor
                }

                val diffAngle = abs(enemyAngle - angle)
                val finalDiffAngle = if (diffAngle > 180f) 360f - diffAngle else diffAngle

                if (finalDiffAngle <= 20f) {
                    if (speed >= 10f) {
                        onHitEnemy(enemy)
                        speed -= stop
                    } else {
                        speed = max(speed - stop, 0f)
                    }
                }
            }
        }

        bodies.blazingReflectFor { body ->
            if (body !is GhostBody && calcDistanceForComparison(pCenter, body.center) <= hitDistanceX2) {
                val bodyAngle = if (trend) calcAngle(body.center, pCenter)
                else -(360f - calcAngle(body.center, pCenter))

                val hitAngle = if (trend) bodyAngle + 90f else bodyAngle - 90f
                val power = caclAxePower(speed)
                onHitBody(body, hitAngle, power, 10f)
            }
        }
    }
}