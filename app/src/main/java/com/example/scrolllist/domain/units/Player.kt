package com.example.scrolllist.domain.units

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.domain.DrawableWithZ
import com.example.scrolllist.domain.utils.Accumulator
import com.example.scrolllist.domain.utils.calcDistanceForComparison
import com.example.scrolllist.domain.utils.checkNotCollision
import com.example.scrolllist.domain.objects.BoxObject
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.EnemyType
import kotlin.math.abs

@Stable
class Player(
    var position: Offset = Offset(-1f, -1f),
    val animationLeftSize: Int,
    val animationRightSize: Int,
    val animationFrontSize: Int,
    val animationBackSize: Int,
) : DrawableWithZ {
    val dstSize = IntSize(150, 150)
    init{
        position -= Offset(dstSize.width.toFloat(),dstSize.height.toFloat())/2f
    }
    var indexFrame = 0
    private var delta = Offset.Zero
    var hitPoint by mutableIntStateOf(100)
    var stopTrend = PlayerTrend.Back
    var trend = PlayerTrend.Stop
        private set
//    private var lastTrend = PlayerTrend.Stop

    //    var trendCombo  = mutableListOf<PlayerTrend>()
    private var lastDamageTimeAccumulator = 0f
    private var lastBlindTimeAccumulator = 0f
    private var animationTime = Accumulator(100f)
    var isBlind by mutableStateOf(false)
    var damageEffectAlpha = 0f
    val saveDelay = 500f
    val center: Offset
        get() = position + Offset(dstSize.width / 2f, dstSize.height / 2f)
    override val indexZ: Float
        get() = center.y
    val collisionRect: Rect
        get() = Rect(
            left = position.x,
            top = position.y,
            right = position.x + dstSize.width,
            bottom = position.y + dstSize.height
        )

    fun updateTrend(newTrend: PlayerTrend) {
        if (trend != newTrend) {
            if (newTrend == PlayerTrend.Stop) {
                stopTrend = trend
            }
            trend = newTrend
            indexFrame = 0
            animationTime.reset()
        }
//        if (newTrend == PlayerTrend.Stop) return
//        if (trendCombo.lastOrNull() != newTrend){
//            trendCombo.add(newTrend)
//        }
//        if (trendCombo.size > 3){
//            trendCombo.removeAt(0)
//        }

    }
    fun startPayerMoveAnimation(position: Offset){
        delta = position / position.getDistance() * 10f
        if (abs(position.x) >= abs(position.y)) {
            if (position.x > 0) {
                updateTrend(PlayerTrend.Rigth)
            } else {
                updateTrend(PlayerTrend.Left)
            }
        } else {
            if (position.y < 0) {
                updateTrend(PlayerTrend.Back)
            } else {
                updateTrend(PlayerTrend.Front)
            }
        }
    }
    fun update(deltaTime: Float, objects: List<BoxObject>) {
        lastDamageTimeAccumulator += deltaTime
        val step = delta * deltaTime / 25f
//        if (lastTrend != trend) {
//            indexFrame = 0
//            animationTime.reset()
//        }
        if (trend != PlayerTrend.Stop) {
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
            when (trend) {
                PlayerTrend.Left -> {
                    animationTime.update(deltaTime) {
                        indexFrame = (indexFrame + 1) % animationLeftSize
                    }
                }

                PlayerTrend.Rigth -> {
                    animationTime.update(deltaTime) {
                        indexFrame = (indexFrame + 1) % animationRightSize
                    }
                }

                PlayerTrend.Front -> {
                    animationTime.update(deltaTime) {
                        indexFrame = (indexFrame + 1) % animationFrontSize
                    }
                }

                PlayerTrend.Back -> {
                    animationTime.update(deltaTime) {
                        indexFrame = (indexFrame + 1) % animationBackSize
                    }
                }
                PlayerTrend.Stop -> {}
            }
        }
        if (isBlind) {
            lastBlindTimeAccumulator += deltaTime
            if (lastBlindTimeAccumulator >= 15000f) isBlind = false
        }
        damageEffectAlpha = (1f - lastDamageTimeAccumulator / saveDelay).coerceIn(0f, 1f)
    }

    //    fun checkCombo():Boolean{
//        if (trendCombo.size != 3) return false
//        val (first,second,last) = trendCombo
//        if ( first == last && second != first){
//            trendCombo.clear()
//            return true
//        }
//        return false
//    }
    fun checkDeath(DeathPlayer: (Int) -> Unit, enemies: List<Enemy>, kills: Int) {
//        return
        if (lastDamageTimeAccumulator >= saveDelay) {
            val minDistance = 50f * 50f
            for (enemy in enemies) {
                if (enemy.enemyIsReady && calcDistanceForComparison(
                        enemy.center,
                        center
                    ) <= minDistance
                ) {
                    when (enemy.enemyType) {
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

    fun getNextRect(delta: Offset): Rect {
        val nextPosition = position + delta
        return Rect(
            left = nextPosition.x + dstSize.width / 3,
            top = nextPosition.y + dstSize.height / 3,
            right = nextPosition.x + dstSize.width * 2 / 3,
            bottom = nextPosition.y + dstSize.height * 2 / 3
        )
    }
}