package com.example.scrolllist.domain.units.enemy

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.domain.units.enemy.bodies.BodyType
import com.example.scrolllist.domain.utils.calcDistance
import kotlin.math.abs

class Smoke(
    speed:Float = 3f,
    startPosition: Offset = Offset.Zero,
    spawnAnimationSize:Int,
    animationSize: Int
):Enemy(speed, startPosition, spawnAnimationSize, animationSize) {
    override val bodyType: BodyType = BodyType.Ghost_body
    override val dstSize: IntSize = IntSize(230,200)
    override val animationSpeed = 100f
    override val damage: Int = 20
    override val enemyType: EnemyType = EnemyType.Blinding
    override fun move(player: Offset, deltaTime: Float) {
        if (player != Offset.Unspecified) {
            val dx = player - position
            val distance = calcDistance(position, player)
            if (abs(distance) > 0.0001f) position += dx / distance * speed * deltaTime
        }
    }
}