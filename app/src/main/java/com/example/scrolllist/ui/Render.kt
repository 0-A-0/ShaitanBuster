package com.example.scrolllist.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.fastCoerceAtMost
import com.example.scrolllist.domain.calcAngle
import com.example.scrolllist.domain.calcDistance
import com.example.scrolllist.domain.objects.Ammunition
import com.example.scrolllist.domain.objects.AmmunitionType
import com.example.scrolllist.domain.objects.BoxObject
import com.example.scrolllist.domain.objects.DinamicObject
import com.example.scrolllist.domain.objects.FirePoint
import com.example.scrolllist.domain.objects.Killmark.Companion.KillmarkStyle
import com.example.scrolllist.domain.objects.KillmarkController
import com.example.scrolllist.domain.objects.KillmarkView
import com.example.scrolllist.domain.objects.StaticObject
import com.example.scrolllist.domain.units.Player
import com.example.scrolllist.domain.units.PlayerTrend
import com.example.scrolllist.domain.units.enemy.Blood
import com.example.scrolllist.domain.units.enemy.Body
import com.example.scrolllist.domain.units.enemy.Crow
import com.example.scrolllist.domain.units.enemy.CrowMinion
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.FixedBody
import com.example.scrolllist.domain.units.enemy.FlyableBody
import com.example.scrolllist.domain.units.enemy.GhostBody
import com.example.scrolllist.domain.units.enemy.Scarecrow
import com.example.scrolllist.domain.units.enemy.SimpleBody
import com.example.scrolllist.domain.units.enemy.Smoke
import com.example.scrolllist.domain.units.enemy.Spawner
import com.example.scrolllist.domain.units.weapon.Axe
import com.example.scrolllist.domain.units.weapon.Revolver
import com.example.scrolllist.domain.units.weapon.Shotgun
import com.example.scrolllist.domain.units.weapon.Weapon
import com.example.scrolllist.ui.assets.EnemyAssets
import com.example.scrolllist.ui.assets.GameAssets
import com.example.scrolllist.ui.assets.PlayerAssets
import com.example.scrolllist.ui.assets.WeaponAssets
import kotlin.math.sin

fun getPlayerFrame(player: Player, playerAssets: PlayerAssets):ImageBitmap{
    return when (player.trend) {
        PlayerTrend.Left -> {
            playerAssets.animation.left[player.indexFrame]
        }

        PlayerTrend.Rigth -> {
            playerAssets.animation.right[player.indexFrame]
        }

        PlayerTrend.Front -> {
            playerAssets.animation.front[player.indexFrame]
        }

        PlayerTrend.Back -> {
            playerAssets.animation.back[player.indexFrame]
        }

        PlayerTrend.Stop -> {
            when (player.stopTrend) {
                PlayerTrend.Left -> {
                    playerAssets.viewLeft
                }

                PlayerTrend.Rigth -> {
                    playerAssets.viewRight
                }

                PlayerTrend.Front -> {
                    playerAssets.viewFront
                }

                else -> {
                    playerAssets.viewBack
                }
            }
        }
    }
}
fun DrawScope.drawPlayer(player: com.example.scrolllist.domain.units.Player, playerAssets: PlayerAssets) {
    val colorFilter = if (player.damageEffectAlpha > 0) {
        ColorFilter.tint(
            Color.Red.copy(alpha = player.damageEffectAlpha * 0.8f),
            blendMode = BlendMode.SrcAtop
        )
    } else null
    drawImage(
        topLeft = player.position,
        image = getPlayerFrame(player,playerAssets),
        colorFilter = colorFilter,
    )
}

fun DrawScope.drawSpawnEffect(enemy: Enemy, enemyAssets: EnemyAssets) {
    drawImage(
        dstSize = enemy.dstSize,
        dstOffset = enemy.position.round(),
        image = enemyAssets.spawnAnimation[enemy.indexSpawnAnimation],
    )
}

fun DrawScope.onDrawScarecrowOrSmoke(enemy: Enemy, enemyAssets: EnemyAssets) {
    if (!enemy.enemyIsReady) {
        drawSpawnEffect(enemy, enemyAssets)
    } else {
        enemyAssets.frontAnimation?.let {
            drawImage(
                dstSize = enemy.dstSize,
                dstOffset = enemy.position.round(),
                image = it[enemy.index],
                filterQuality = FilterQuality.None
            )
        }
    }
}

fun DrawScope.onDrawSpawner(enemy: Spawner, enemyAssets: EnemyAssets) {
    if (!enemy.enemyIsReady) {
        drawSpawnEffect(enemy, enemyAssets)
    } else {
        enemyAssets.view?.let {
            drawCircle(
                brush = Brush.radialGradient(
                    0f to Color(0xFF2E1946),
                    0.2f to Color(0xFF2E1946),
                    0.8f to Color.Black,
                    1f to Color.Transparent,
                    center = enemy.centerPoint,
                    radius = enemy.radius * (1f + (sin(enemy.index * 0.2f) * 0.1f))
                ),
                center = enemy.centerPoint,
                radius = enemy.radius,
                alpha = 0.7f
            )
            drawImage(
                dstSize = enemy.dstSize,
                dstOffset = enemy.position.round(),
                image = it,
                filterQuality = FilterQuality.None
            )
        }
    }
}

fun DrawScope.onDrawCrow(enemy: Crow, enemyAssets: EnemyAssets) {
    if (!enemy.enemyIsReady) {
        drawSpawnEffect(enemy, enemyAssets)
    } else {
    val currentAnimation =
        if (enemy.angle < 180f) enemyAssets.frontAnimationMirrored else enemyAssets.frontAnimation
    currentAnimation?.let {
        rotate(enemy.angle, pivot = enemy.center) {
            drawImage(
                dstSize = enemy.dstSize,
                dstOffset = enemy.position.round(),
                image = it[enemy.index],
                alpha = enemy.alpha.value,
                filterQuality = FilterQuality.None
            )
        }
    }
    }
}

fun DrawScope.onDrawCrowMinion(enemy: CrowMinion, enemyAssets: EnemyAssets) {
    with(enemy) {
        if (!enemyIsReady) {
            drawSpawnEffect(enemy, enemyAssets)
        } else {
            val currentAnimation =
                if (angle < 180f) enemyAssets.frontAnimationMirrored else enemyAssets.frontAnimation
            currentAnimation?.let {
                val currentCenter = this.center
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
                        image = it[index],
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
    }
}

fun DrawScope.drawBlood(bloodList: List<Blood>, animation:List<ImageBitmap>) {
    for (blood in bloodList) {
            rotate(blood.angle, blood.pivot) {
                drawImage(
                    topLeft = blood.position,
                    image = animation[blood.index],
//                    colorFilter = ColorFilter.tint(Color(0x54FF5722))
                )
        }
    }
}
val bodyColorFilter = ColorFilter.tint(
    Color.Black.copy(alpha = 0.7f),
    blendMode = BlendMode.SrcAtop
)

fun DrawScope.drawBody(body: Body, enemyAssets: EnemyAssets) {
    scale(0.9f,0.9f, pivot = body.center) {
        drawImage(
            dstSize = body.dstSize,
            dstOffset = body.position.round(),
            image = enemyAssets.bodyView,
            filterQuality = FilterQuality.None,
            colorFilter = bodyColorFilter
        )
    }
}
fun DrawScope.drawGhostBody(body: GhostBody, enemyAssets: EnemyAssets) {
    with(body) {
        scale(0.9f, 0.9f, pivot = body.center) {
            drawImage(
                dstSize = dstSize,
                dstOffset = position.round(),
                image = enemyAssets.bodyView,
                filterQuality = FilterQuality.None,
                colorFilter = bodyColorFilter,
                alpha = alpha.coerceAtLeast(0f)
            )
        }
    }
}

fun DrawScope.drawStaticObject(_object: StaticObject, view:ImageBitmap){
    drawImage(
        image = view,
        dstSize = _object.dstSize,
        dstOffset = _object.position
    )
}
fun DrawScope.drawDinamicObject(_object: DinamicObject, animation:List<ImageBitmap>){
    drawImage(
        image = animation[_object.indexFrame],
        dstSize = _object.dstSize,
        dstOffset = _object.position
    )
}
fun DrawScope.drawWithZ(listOfDrawableWithZ: List<DrawableWithZ>, assets: GameAssets) {
    for (i in listOfDrawableWithZ.indices) {
        val entity = listOfDrawableWithZ[i]
        when(entity){
            is Player -> drawPlayer(entity,assets.playerAssets)
            is Spawner -> onDrawSpawner(entity,assets.spawnerAssets)
            is Crow -> onDrawCrow(entity,assets.crowAssets)
            is CrowMinion -> onDrawCrowMinion(entity,assets.crowAssets)
            is Ammunition -> drawStaticObject(entity, view = when(entity.type){
                AmmunitionType.Pellets -> assets.pellets
                AmmunitionType.Cartridges -> assets.cartridges
            })
            is BoxObject -> drawStaticObject(entity, assets.box_)
            is FirePoint -> drawDinamicObject(entity, assets.fireAnimation)
            is Scarecrow -> onDrawScarecrowOrSmoke(entity,assets.scarecrowAssets)
            is Smoke -> onDrawScarecrowOrSmoke(entity,assets.smokeAssets)
            is GhostBody -> drawGhostBody(entity,assets.smokeAssets)
            is SimpleBody -> drawBody(entity,assets.scarecrowAssets)
            is FixedBody -> drawBody(entity,assets.spawnerAssets)
            is FlyableBody -> drawBody(entity, assets.crowAssets)
        }
    }
}
fun DrawScope.drawKillmark(killmarkController: KillmarkController, textMeasurer: TextMeasurer, gameAssets: GameAssets) {
    killmarkController.killmark?.let { killmark ->
        val killmark_position = IntOffset(
            x = (size.width.toInt() - killmark.dstSize.width) / 2,
            y = (size.height.toInt() * 2 / 3 - killmark.dstSize.height / 2)
        )
        with(killmark) {
            val scale = killmarkController.scale.value
            val alpha = killmarkController.alpha.value
            val position = killmark_position
            view?.let {
                scale(scale) {
                    drawImage(
                        dstOffset = position,
                        image = when(view){
                            KillmarkView.axe_killmark -> gameAssets.axe_killmark
                            KillmarkView.revolver_killmark -> gameAssets.revolver_killmark
                            KillmarkView.killmark -> gameAssets.killmark
                        },
                        dstSize = dstSize,
                        alpha = alpha,
                        filterQuality = FilterQuality.None
                    )
                }
            }
            text?.let {
                val text = textMeasurer.measure(
                    text = it,
                    style = KillmarkStyle
                )
                drawText(
                    textLayoutResult = text,
                    topLeft = (position + IntOffset(dstSize.width/2 - text.size.width/2, 0)).toOffset(),
                )
            }
        }
    }
}
fun DrawScope.drawAxe(player: Player, axe: Axe,weaponAssets: WeaponAssets) = with(axe) {
    with(weaponAssets) {
        if (slicePoints.isEmpty()) {
            val weaponPosition = player.position + Offset(
                player.dstSize.width.toFloat(),
                player.dstSize.height.toFloat()
            ) / 2f - Offset(dstSize.width / 2f, dstSize.height / 2f + 125f)
            if (trend) {
                drawImage(
                    topLeft = weaponPosition,
                    image = weaponAssets.axe,
                )
                if (speed in 10f..20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_low_effect,
                    )
                }
                if (speed > 20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_strong_effect,
                    )
                }
            } else {
                drawImage(
                    topLeft = weaponPosition,
                    image = axe_mirror,
                )
                if (speed in 10f..20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_low_effect_mirror,
                    )
                }
                if (speed > 20f) {
                    drawImage(
                        topLeft = weaponPosition,
                        image = axe_strong_effect_mirror,
                    )
                }
            }
        }
    }
}
fun DrawScope.drawEffectsAxe(axe: Axe, weaponAssets: WeaponAssets) = with(axe) {
    if (slicePoints.isNotEmpty()) {
        for (i in 0 until slicePoints.size - 2) {
            drawLine(
                start = slicePoints[i],
                end = slicePoints[i + 1],
                color = Color.White.copy(0.8f),
                strokeWidth = i.toFloat().coerceAtMost(15f),
            )
        }
        if(slicePoints.size > 1) {
            rotate(
                calcAngle(slicePoints.last(), slicePoints[slicePoints.size - 2]) -90f,
                pivot = slicePoints.last()
            ) {
                drawImage(
                    topLeft = slicePoints.last(),
                    image = weaponAssets.axe,
                )
            }
        }
    }
}
fun DrawScope.drawRevolver(player: Player,revolver: Revolver, weaponAssets: WeaponAssets) = with(revolver) {
    val weaponPosition = player.center + Offset(-dstSize.width / 2f, -dstSize.height * 1.8f)
    drawImage(
        dstOffset = weaponPosition.round(),
        dstSize = dstSize,
        filterQuality = FilterQuality.None,
        image = when (trend) {
            true -> weaponAssets.viewRightAnimation[animationIndex]
            false -> weaponAssets.viewLeftAnimation[animationIndex]
        }
    )
}
fun DrawScope.drawEffectsRevolver(revolver: Revolver)= with(revolver) {
    shotPoint?.let {
        val currentBulletProgress = bulletProgress
        val strokeWith = if (isHolyShot) 20f else 5f
        val bulletLength = if (isHolyShot) 0.3f else 0.1f
        val bulletColor = if (isHolyShot) Color.Yellow else Color.White
        val end = if (!isHolyShot) it else run {
            startBullet + (it - startBullet)/ calcDistance(it,startBullet) * 5000f
        }
        val start = startBullet
        if (isHolyShot){
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent,Color.White.copy(0.5f)),
                    start = start,
                    end = end
                ),
                start = start,
                end = end,
                strokeWidth = strokeWith,
                alpha = bulletProgress,
                cap = StrokeCap.Round
            )
        }
        drawLine(
            brush = Brush.linearGradient(
                (currentBulletProgress - 0.01f).coerceAtLeast(0f) to Color.Transparent,
                currentBulletProgress to bulletColor,
                (currentBulletProgress + bulletLength).fastCoerceAtMost(1f) to bulletColor,
                (currentBulletProgress + bulletLength + 0.01f).fastCoerceAtMost(1f) to Color.Transparent,
                start = start,
                end = end,
            ),
            start = start,
            end = end,
            strokeWidth = strokeWith,
            alpha = bulletProgress,
            cap = StrokeCap.Round
        )
    }
}
fun DrawScope.drawShotgun(player: Player,shotgun: Shotgun, weaponAssets: WeaponAssets)= with(shotgun) {
    val weaponPosition = player.center + Offset(-dstSize.width / 2f, -dstSize.height * 1.4f)
    drawImage(
        dstOffset = weaponPosition.round(),
        dstSize = dstSize,
        filterQuality = FilterQuality.None,
        image = when (trend) {
            true -> weaponAssets.viewRightShotgun
            false -> weaponAssets.viewLeftShotgun
        }
    )
    shotPoint?.let {
        val currentBulletProgress = bulletProgress
        drawArc(
            brush = Brush.radialGradient(
                (currentBulletProgress + 0.30f).fastCoerceAtMost(1f) to Color.Transparent,
                (currentBulletProgress + 0.35f).fastCoerceAtMost(1f) to Color.White.copy(alpha = 0.3f),
                (currentBulletProgress + 0.40f).fastCoerceAtMost(1f) to Color.DarkGray,
                (currentBulletProgress + 0.45f).fastCoerceAtMost(1f) to Color.Transparent,
                radius = hitDistance,
                center = player.center
            ),
            startAngle = -45 - 90f,
            sweepAngle = 90f,
            useCenter = true,
            size = Size(hitDistance * 2, hitDistance * 2),
            topLeft = player.center - Offset(hitDistance, hitDistance)
        )
    }
}
fun DrawScope.drawEffectsShotgun(shotgun: Shotgun)= with(shotgun) {
    targetMagnet?.let {
        drawCircle(
            brush = Brush.radialGradient(
                0f to Color.Yellow.copy(0.05f),
                1f to Color.Transparent,
                center = it,
                radius = hitDistance,
            ),
            center = it,
            radius = hitDistance
        )
    }
}

fun DrawScope.drawWeapon(player: Player, weapon: Weapon,weaponAssets: WeaponAssets){
    when(weapon){
        is Axe -> {
            rotate(weapon.angle, pivot = player.center) {
                drawAxe(player,weapon,weaponAssets)
            }
            drawEffectsAxe(weapon,weaponAssets)
        }
        is Revolver -> {
            rotate(weapon.angle, pivot = player.center) {
                drawRevolver(player, weapon, weaponAssets)
            }
            drawEffectsRevolver(weapon)
        }
        is Shotgun -> rotate(weapon.angle, pivot = player.center) {
            drawShotgun(player,weapon,weaponAssets)
            drawEffectsShotgun(weapon)
        }
    }
}