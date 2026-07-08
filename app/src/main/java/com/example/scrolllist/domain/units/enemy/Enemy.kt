package com.example.scrolllist.domain.units.enemy

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.ui.DrawableWithZ
import com.example.scrolllist.domain.Accumulator

abstract class Enemy(
    val speed: Float,
    var position: Offset = Offset.Zero,
    val spawnAnimationSize:Int,
    val animationSize: Int
) : DrawableWithZ {
    abstract val bodyType: BodyType
    open val enemyType: EnemyType = EnemyType.Attacking
    open val killable = true
    abstract val dstSize: IntSize
    abstract val animationSpeed: Float
    abstract val damage: Int
    open val killWeight: Int = 1
    var sleepTime: Float = 600f
    val spawnAnimationSpeed = sleepTime / spawnAnimationSize

    val animationTimeAccumulator by lazy { Accumulator(animationSpeed) }
    val spawnAnimationTimeAccumulator by lazy { Accumulator(spawnAnimationSpeed) }
    var startTimeAccumulator = 0f

    var enemyIsReady = false

    var index: Int = 0
    var indexSpawnAnimation: Int = 0

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

    abstract fun move(player: Offset, deltaTime: Float)

    open suspend fun onHitEffect() {
        return
    }

    fun update(deltaTime: Float, playerPos: Offset) {
        if (!enemyIsReady) {
            startTimeAccumulator += deltaTime
            if (startTimeAccumulator < sleepTime) {
                spawnAnimationTimeAccumulator.update(deltaTime){
                    indexSpawnAnimation = (indexSpawnAnimation + 1) % spawnAnimationSize
                }
                return
            } else {
                enemyIsReady = true
            }
        }

        move(playerPos, deltaTime / 1000f)

        if (animationSize != 0) {
            animationTimeAccumulator.update(deltaTime) {
                index = (index + 1) % animationSize
            }
        }
    }
}
