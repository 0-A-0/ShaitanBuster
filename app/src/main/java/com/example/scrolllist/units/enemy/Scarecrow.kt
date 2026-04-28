package com.example.scrolllist.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.example.scrolllist.calcDistance
import kotlin.math.abs
@Stable
class Scarecrow(
    spawnAnimation: List<ImageBitmap>,
    val frontAnimation : List<ImageBitmap>,
    speed:Float = 3f,
    startPosition: Offset = Offset.Zero,
):Enemy(speed, startPosition, spawnAnimation) {
    override val bodyType: BodyType = BodyType.Simple_Body
    override val bodyView: ImageBitmap = frontAnimation[0]
    override val dstSize: IntSize = IntSize(230,200)
    override val damage: Int = 40
    override val animationSpeed = 100f
    override val animationSize: Int = frontAnimation.size
    override suspend fun onHitEffect() {

    }
    override fun move(player: Offset, deltaTime: Float) {
        if (player != Offset.Unspecified) {
            val dx = player - position
            val distance = calcDistance(position, player)
            if (abs(distance) > 0.0001f) position += dx / distance * speed * deltaTime
        }
    }
    override fun DrawScope.onDraw() {
        drawImage(
            dstSize = dstSize,
            dstOffset = position.round(),
            image = frontAnimation[index],
            filterQuality = FilterQuality.None
        )
    }
}