package com.example.scrolllist.domain.units.enemy

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

class Spawner(
    val action:((Enemy) -> Unit),
    speed:Float = 3f,
    startPosition: Offset = Offset.Zero,
    spawnAnimationSize:Int,
):Enemy(speed, startPosition, spawnAnimationSize,animationSize = 10) {
    override val bodyType: BodyType = BodyType.Fixed_Body
    override val dstSize: IntSize = IntSize(300,300)
    override val animationSpeed = 200f
    override val damage: Int = 90
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
}