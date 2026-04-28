package com.example.scrolllist.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import kotlin.math.sin

@Stable
class Spawner(
    val view: ImageBitmap,
    val action:((Enemy) -> Unit),
    spawnAnimation: List<ImageBitmap>,
    speed:Float = 3f,
    startPosition: Offset = Offset.Zero,
):Enemy(speed, startPosition, spawnAnimation) {
    override val bodyType: BodyType = BodyType.Fixed_Body
    override val bodyView: ImageBitmap = view
    override val dstSize: IntSize = IntSize(300,300)
    override val animationSpeed = 200f
    override val damage: Int = 90
    override val animationSize: Int = 10
    override val enemyType: EnemyType = EnemyType.Attacking
    val actionSpeed = 1f // 1 секунда
    var actionTimeAccumulator = 0f
    val centerPoint = this@Spawner.center
    val radius = dstSize.width/2f
    override fun move(player: Offset, deltaTime: Float) {
        actionTimeAccumulator += deltaTime
        if (actionTimeAccumulator >= actionSpeed ) {
            action(this)
            actionTimeAccumulator -= actionSpeed
        }
    }
    override fun DrawScope.onDraw() {
            drawCircle(
                brush = Brush.radialGradient(
                    0f to Color(0xFF2E1946),
                    0.2f to Color(0xFF2E1946),
                    0.8f to Color.Black,
                    1f to Color.Transparent,
                    center = centerPoint,
                    radius = radius *  (1f + (sin(index * 0.2f) * 0.1f))
                ),
                center = centerPoint,
                radius = radius,
                alpha = 0.7f
            )
        drawImage(
            dstSize = dstSize,
            dstOffset = position.round(),
            image = view,
            filterQuality = FilterQuality.None
        )
    }
}