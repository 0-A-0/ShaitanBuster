package com.example.scrolllist.units.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.calcDistance
import com.example.scrolllist.calcAngle
import kotlin.math.abs
@Stable
class Crow(
    spawnAnimation: List<ImageBitmap>,
    frontAnimation : List<ImageBitmap>,
    frontAnimationMirrored : List<ImageBitmap>,
    speed:Float = 3f,
    startPosition: Offset = Offset.Zero,
    fixedPlayerPosition:Offset,
):Enemy(speed, startPosition, spawnAnimation ) {
    override val bodyType: BodyType = BodyType.Flyable_Body
    override val bodyView: ImageBitmap = frontAnimation[0]
    val alpha = Animatable(1f)
    val angle:Float = calcAngle(fixedPlayerPosition, center)
    val currentAnimation = if (angle < 180f) frontAnimationMirrored else frontAnimation
    override val dstSize: IntSize = IntSize(150,150)
    override val animationSpeed = 20f
    override val damage: Int = 50
    override val animationSize: Int = currentAnimation.size
    override val killable: Boolean = false
    override val indexZ: Float = Float.MAX_VALUE
    val vector = run {
        val dx = fixedPlayerPosition - center
        val distance = calcDistance(center, fixedPlayerPosition)
        if (abs(distance) > 0.0001f) dx / distance else Offset(1f,0f)
    }

    override suspend fun onHitEffect() {
        alpha.animateTo(
            targetValue = 0.2f,
            animationSpec = tween(durationMillis = 200)
        )
        alpha.snapTo(1f)
    }

    override fun move(player: Offset, deltaTime: Float) {
        position += vector * speed * deltaTime
    }
    override fun DrawScope.onDraw() {
        rotate(angle, pivot = this@Crow.center) {
            drawImage(
                dstSize = dstSize,
                dstOffset = position.round(),
                image = currentAnimation[index],
                alpha = alpha.value,
                filterQuality = FilterQuality.None
            )
        }
    }
}