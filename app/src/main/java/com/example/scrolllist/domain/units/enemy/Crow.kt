package com.example.scrolllist.domain.units.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.domain.calcAngle
import com.example.scrolllist.domain.calcDistance
import kotlin.math.abs

@Stable
class Crow(
    speed:Float = 3f,
    startPosition: Offset = Offset.Zero,
    fixedPlayerPosition:Offset,
    spawnAnimationSize:Int,
    animationSize: Int
):Enemy(speed, startPosition, spawnAnimationSize,animationSize ) {
    override val bodyType: BodyType = BodyType.Flyable_Body
    val alpha = Animatable(1f)
    val angle:Float = calcAngle(fixedPlayerPosition, center)
    override val dstSize: IntSize = IntSize(150,150)
    override val animationSpeed = 20f
    override val damage: Int = 50
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
}