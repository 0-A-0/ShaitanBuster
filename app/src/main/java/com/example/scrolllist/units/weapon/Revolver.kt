package com.example.scrolllist.units.weapon

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.compose.ui.util.fastCoerceAtMost
import com.example.scrolllist.AudioManager
import com.example.scrolllist.TimeManager
import com.example.scrolllist.calcDistance
import com.example.scrolllist.isIntersectWithLine
import com.example.scrolllist.units.Player
import com.example.scrolllist.units.enemy.Body
import com.example.scrolllist.units.enemy.Enemy
import com.example.scrolllist.units.enemy.GhostBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Stable
class Revolver(
    val scope: CoroutineScope,
    val viewRight: ImageBitmap,
    val viewRightAnimation: List<ImageBitmap>,
    val viewLeftAnimation: List<ImageBitmap>,
) : Weapon {
    override val minHolyModeUsable: Float = 0.7f
    override var holyModeProgress by mutableStateOf(0f)
    override val present_clip: String
        get() = "$clip"
    override val present_view = viewRight
    override var angle by mutableStateOf(0f)
    override val shootable: Boolean = true
    val animationIndexTarget = (viewRightAnimation.size - 1).toFloat()
    var seriesCount = 0
    val animationIndex = Animatable(0f)
    val bulletProgress = Animatable(0f)
    var shotPoint: Offset? = null
    var isHolyShot = false
    var job: Job? = null
    var animationJob: Job? = null
    var startBullet: Offset = Offset.Zero

    //    var seriesCount = 0
    val dstSize = IntSize(60, 80)
    var clip by mutableStateOf(100)
    override val trend: Boolean
        get() = if (angle > 180f) false else true

    override fun DrawScope.draw(player: Player) {
        val weaponPosition = player.center + Offset(-dstSize.width / 2f, -dstSize.height * 1.8f)
        drawImage(
            dstOffset = weaponPosition.round(),
            dstSize = dstSize,
            filterQuality = FilterQuality.None,
            image = when (trend) {
                true -> viewRightAnimation[animationIndex.value.roundToInt()]
                false -> viewLeftAnimation[animationIndex.value.roundToInt()]
            }
        )

    }

    override fun DrawScope.drawEffects() {
        shotPoint?.let {
            val currentBulletProgress = bulletProgress.value
            val strokeWith = if (isHolyShot) 20f else 5f
            val bulletLength = if (isHolyShot) 0.3f else 0.1f
            val bulletColor = if (isHolyShot) Color.Yellow else Color.White
            val end = if (!isHolyShot) it else run {
                startBullet + (it - startBullet)/calcDistance(it,startBullet) * 5000f
            }
            val start = startBullet
            if (isHolyShot){
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent,Color.White.copy(0.5f)),
                        start = start,
                        end = end
                    ),
                    start = start,
                    end = end,
                    strokeWidth = strokeWith,
                    alpha = bulletProgress.value,
                    cap = StrokeCap.Round
                )
            }
            drawLine(
                brush = Brush.linearGradient(
                    (currentBulletProgress - 0.01f).coerceAtLeast(0f) to Color.Transparent,
                    currentBulletProgress to bulletColor,
                    (currentBulletProgress + bulletLength).fastCoerceAtMost(1f) to bulletColor,
                    (currentBulletProgress + bulletLength + 0.01f).fastCoerceAtMost(1f) to Color.Transparent,
                    start = start,
                    end = end,
                ),
                start = start,
                end = end,
                strokeWidth = strokeWith,
                alpha = bulletProgress.value,
                cap = StrokeCap.Round
            )
        }
    }

    fun animateRevolver(currentShotPoint: Offset, holyMode: Boolean) {
        if (clip <= 0) return

        animationJob?.cancel()
        animationJob = scope.launch {
            try {
                isHolyShot = holyMode
                shotPoint = currentShotPoint
                animationIndex.snapTo(0f)
                bulletProgress.snapTo(0f)
                coroutineScope {
                    launch {
                        animationIndex.animateTo(
                            targetValue = animationIndexTarget,
                            animationSpec = tween(
                                durationMillis = TimeManager.getScaledTime(200).toInt(),
                                easing = { it })
                        )
                    }
                    launch {
                        bulletProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(TimeManager.getScaledTime(200).toInt())
                        )
                    }
                }
            } finally {
                animationIndex.snapTo(0f)
                shotPoint = null
            }
        }
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
        player: Player,
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