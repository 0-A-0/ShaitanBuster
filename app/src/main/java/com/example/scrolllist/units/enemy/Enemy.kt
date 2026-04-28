package com.example.scrolllist.units.enemy

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.DrawableWithZ

abstract class Enemy(
    val speed: Float,
    startPosition: Offset = Offset.Zero,
    val spawnAnimation: List<ImageBitmap>,
) : DrawableWithZ {
    abstract val bodyType: BodyType
    abstract val bodyView: ImageBitmap
    open val enemyType: EnemyType = EnemyType.Attacking
    open val killable = true
    abstract val dstSize: IntSize
    abstract val animationSpeed: Float
    abstract val damage: Int
    abstract val animationSize: Int
    open val killWeight: Int = 1

    var animationTimeAccumulator = 0f
    var spawnAnimationTimeAccumulator = 0f
    var startTimeAccumulator = 0f

    var sleepTime: Float = 600f
    var enemyIsReady = false

    var position: Offset = startPosition
    var index: Int = 0
    var indexSpawnAnimation: Int = 0

    val spawnAnimationSize = spawnAnimation.size
    val spawnAnimationSpeed = sleepTime / spawnAnimationSize

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
    abstract fun DrawScope.onDraw()

    open suspend fun onHitEffect() {
        return
    }

    override fun DrawScope.draw() {
        if (!enemyIsReady) {
            drawSpawnEffect()
        } else onDraw()
    }

    fun DrawScope.drawSpawnEffect() {
        drawImage(
            dstSize = dstSize,
            dstOffset = position.round(),
            image = spawnAnimation[indexSpawnAnimation],
        )
    }

    fun update(deltaTime: Float, playerPos: Offset) {
        if (!enemyIsReady) {
            startTimeAccumulator += deltaTime
            spawnAnimationTimeAccumulator += deltaTime
            if (startTimeAccumulator < sleepTime) {
                if (spawnAnimationTimeAccumulator >= spawnAnimationSpeed) {
                    indexSpawnAnimation = (indexSpawnAnimation + 1) % spawnAnimationSize
                    spawnAnimationTimeAccumulator -= spawnAnimationSpeed
                }
                return
            } else {
                enemyIsReady = true
            }
        }

        animationTimeAccumulator += deltaTime
        move(playerPos, deltaTime / 1000f)

        if (animationSize != 0) {
            if (animationTimeAccumulator >= animationSpeed) {
                index = (index + 1) % animationSize
                animationTimeAccumulator -= animationSpeed
            }
        }
    }
}
