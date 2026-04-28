package com.example.scrolllist.units.weapon

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.TimeManager
import com.example.scrolllist.caclAxePower
import com.example.scrolllist.calcDistanceForСomparison
import com.example.scrolllist.calcAngle
import com.example.scrolllist.isIntersectWithLine
import com.example.scrolllist.units.Player
import com.example.scrolllist.units.enemy.Body
import com.example.scrolllist.units.enemy.Enemy
import com.example.scrolllist.units.enemy.GhostBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

@Stable
class Axe(
    val scope: CoroutineScope,
    val axe: ImageBitmap,
    val axe_low_effect: ImageBitmap,
    val axe_strong_effect: ImageBitmap,
    val axe_mirror: ImageBitmap,
    val axe_low_effect_mirror: ImageBitmap,
    val axe_strong_effect_mirror: ImageBitmap,
) : Weapon {
    override val minHolyModeUsable: Float = 0.2f
    override var holyModeProgress by mutableStateOf(0f)
    override val present_clip: String = "∞"
    override val present_view = axe
    override var angle by mutableStateOf(0f)
    val dstSize = IntSize(77, 114)
    var speed by mutableStateOf(0f)
    val hitDistanceX2 = 180f*180f
    val slicePoints = mutableStateListOf<Offset>()
    override var trend by mutableStateOf(true)
    private var currentJob: Job? = null
    fun startSlice(){
        currentJob?.cancel()
    }
    inline fun slice(
        onSuccessAction: () -> Unit,
        bodies: List<Body>,
        enemies: List<Enemy>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int) -> Unit
    ){
        var totalLength = 0f
        for (i in 0 until slicePoints.size - 1) {
            totalLength += calcDistanceForСomparison(slicePoints[i], slicePoints[i + 1])
        }
        if(totalLength > 10_000f){
        slicePoints.windowed(size = 2, step = 1).forEach { (start, end) ->
            for (i in enemies.indices.reversed()) {
                if (isIntersectWithLine(enemies[i].collisionRect, start, end)) {
                    onHitEnemy(enemies[i])
                }
            }
        }
            slicePoints.windowed(size = 2, step = 1).forEach { (start, end) ->
                for (i in bodies.indices.reversed()) {
                    if (isIntersectWithLine(bodies[i].collisionRect, start, end)) {
                        onHitBody(bodies[i], Random.nextFloat() * 360f,  Random.nextInt(11) )
                    }
                }
            }
            onSuccessAction()
            holyModeProgress = (holyModeProgress - totalLength/400_000f).coerceIn(0f,1f)
        }

        slicePoints.clear()
    }
    override fun DrawScope.draw(player: Player) {
        if (slicePoints.isEmpty()) {
            val weaponPosition = player.position + Offset(
                player.view.width.toFloat(),
                player.view.height.toFloat()
            ) / 2f - Offset(dstSize.width / 2f, dstSize.height / 2f + 125f)
            if (trend) {
                drawImage(
                    topLeft = weaponPosition,
                    image = axe,
                )
                if (speed in 10f..20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_low_effect,
                    )
                }
                if (speed > 20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_strong_effect,
                    )
                }
            } else {
                drawImage(
                    topLeft = weaponPosition,
                    image = axe_mirror,
                )
                if (speed in 10f..20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_low_effect_mirror,
                    )
                }
                if (speed > 20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_strong_effect_mirror,
                    )
                }
            }
        }
    }

    override fun DrawScope.drawEffects() {
        if (slicePoints.isNotEmpty()) {
            for (i in 0 until slicePoints.size - 2) {
                drawLine(
                    start = slicePoints[i],
                    end = slicePoints[i + 1],
                    color = Color.White.copy(0.8f),
                    strokeWidth = i.toFloat().coerceAtMost(15f),
                )
            }
            if(slicePoints.size > 1) {
                rotate(
                    calcAngle(slicePoints.last(), slicePoints[slicePoints.size - 2])-90f,
                    pivot = slicePoints.last()
                ) {
                    drawImage(
                        topLeft = slicePoints.last(),
                        image = axe,
                    )
                }
            }
        }
    }

    fun rotationAxe(
        isHold: Boolean,
        player: Player,
        enemies: List<Enemy>,
        bodies: List<Body>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int) -> Unit
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
        player: Player,
        enemies: List<Enemy>,
        bodies: List<Body>,
        onHitEnemy: (Enemy) -> Unit,
        onHitBody: (Body, Float, Int) -> Unit
    ) {
        val stop = 2f
        val pCenter = player.center

        for (i in enemies.indices.reversed()) {
            val enemy = enemies[i]
            if (calcDistanceForСomparison(pCenter, enemy.center) <= hitDistanceX2) {
                val enemyAngle = if (trend) calcAngle(enemy.center, pCenter)
                else -(360f - calcAngle(enemy.center, pCenter))

                if (speed > 25f) {
                    onHitEnemy(enemy)
                    speed -= (stop - 1f)
                    continue
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

        for (i in bodies.indices.reversed()) {
            val body = bodies[i]
            if (body !is GhostBody && calcDistanceForСomparison(pCenter, body.center) <= hitDistanceX2) {
                val bodyAngle = if (trend) calcAngle(body.center, pCenter)
                else -(360f - calcAngle(body.center, pCenter))

                val hitAngle = if (trend) bodyAngle + 90f else bodyAngle - 90f
                val power = caclAxePower(speed)
                onHitBody(body, hitAngle, power)
            }
        }
    }
}