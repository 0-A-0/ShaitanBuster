package com.example.scrolllist.domain.units.enemy

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.domain.units.enemy.bodies.BodyType

class Spawner(
    speed:Float = 3f,
    startPosition: Offset = Offset.Zero,
    spawnAnimationSize:Int,
    speedMinionRange: IntRange, // Передаем рендж скоростей внутрь
    crowAnimationSize: Int,
):Enemy(speed, startPosition, spawnAnimationSize,animationSize = 10) {
    override val bodyType: BodyType = BodyType.Fixed_Body
    override val dstSize: IntSize = IntSize(300,300)
    override val animationSpeed = 200f
    override val damage: Int = 90
    override val enemyType: EnemyType = EnemyType.Attacking
    val centerPoint = this@Spawner.center
    val radius = dstSize.width/2f
    val minion: CrowMinion = CrowMinion(
        spawnAnimationSize = spawnAnimationSize,
        startPosition = this.center,
        speed = speedMinionRange.random().toFloat(),
        animationSize = crowAnimationSize
    )
    override fun move(player: Offset, deltaTime: Float) {
        return
    }
}