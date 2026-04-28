package com.example.scrolllist.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.calcDistance
import com.example.scrolllist.calcAngle
import kotlin.math.abs
@Stable
class CrowMinion(
    spawnAnimation: List<ImageBitmap>,
    val frontAnimation: List<ImageBitmap>,
    val frontAnimationMirrored: List<ImageBitmap>,
    speed: Float = 3f,
    val startPosition: Offset = Offset.Zero,
) : Enemy(speed, startPosition, spawnAnimation) {
    override val bodyType: BodyType = BodyType.Flyable_Body
    override val bodyView: ImageBitmap = frontAnimation[0]
    override val dstSize: IntSize = IntSize(150, 150)
    override val animationSpeed = 20f
    override val damage: Int = 50
    override val animationSize: Int = frontAnimation.size
    override val indexZ: Float = Float.MAX_VALUE
    override val killWeight: Int = 0
    var angle: Float = 0f
    var direction = Offset.Unspecified
    val currentAnimation
        get() = if (angle < 180f) frontAnimationMirrored else frontAnimation

    override fun move(player: Offset, deltaTime: Float) {
        if (player != Offset.Unspecified) {
            angle = calcAngle(player, position)
            val dx = player - position
            val distance = calcDistance(position, player)
            if (abs(distance) > 0.0001f) {
                direction = dx / distance
                position += direction * speed * deltaTime
            }
        }
    }

    override fun DrawScope.onDraw() {
        val currentCenter = this@CrowMinion.center
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Black, Color(0xC9AD5F1D)),
                start = startPosition,
                end = startPosition + direction * 10f,
                tileMode = TileMode.Repeated
            ),
            start = startPosition,
            end = currentCenter,
            strokeWidth = 8f
        )
        rotate(angle, pivot = currentCenter) {
            drawImage(
                dstSize = dstSize,
                dstOffset = position.round(),
                image = currentAnimation[index],
                filterQuality = FilterQuality.None
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                0f to Color(0xFF2E1946),
                0.2f to Color(0xFF2E1946),
                0.8f to Color.Black,
                1f to Color.Transparent,
                center = currentCenter,
                radius = dstSize.width / 2f
            ),
            center = currentCenter,
            radius = dstSize.width / 2f,
            alpha = 0.5f
        )
    }
}