package com.example.scrolllist.domain.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.domain.units.enemy.bodies.BodyType
import com.example.scrolllist.domain.utils.calcAngle
import com.example.scrolllist.domain.utils.calcDistance
import kotlin.math.abs

@Stable
class CrowMinion(
    speed: Float = 3f,
    val startPosition: Offset = Offset.Zero,
    spawnAnimationSize:Int,
    animationSize: Int
) : Enemy(speed, startPosition, spawnAnimationSize,animationSize) {
    override val bodyType: BodyType = BodyType.Flyable_Body
    override val dstSize: IntSize = IntSize(150, 150)
    override val animationSpeed = 20f
    override val damage: Int = 50
    override val indexZ: Float = Float.MAX_VALUE
    override val killWeight: Int = 0
    var angle: Float = 0f
    var direction = Offset.Unspecified

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
}