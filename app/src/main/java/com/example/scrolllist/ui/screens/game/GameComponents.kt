package com.example.scrolllist.ui.screens.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.example.scrolllist.R
import com.example.scrolllist.domain.units.weapon.Weapon
import com.example.scrolllist.domain.units.weapon.WeaponType
import com.example.scrolllist.domain.utils.calcDistance
import com.example.scrolllist.ui.assets.WeaponAssets

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    StartTrendAnimation: (Offset) -> Unit,
//    SetMovePosition: (Offset) -> Unit,
    StopTrendAnimation: () -> Unit
) {
    var joystick by remember { mutableStateOf(Offset(0f, 0f)) }
    val moveModifier = remember {
        Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event: PointerEvent = awaitPointerEvent()
                    val changes: List<PointerInputChange> = event.changes
                    for (change in changes) {
                        val position = change.position - Offset(
                            size.width / 2f,
                            size.height / 2f
                        )
                        val r = size.width / 3f
                        val distance = calcDistance(Offset(0f, 0f), position)
                        if (distance <= r) {
                            joystick = size.center.toOffset() + position
                            StartTrendAnimation(position)
                        } else {
                            var normPosition = position / distance
                            normPosition *= r
                            joystick = size.center.toOffset() + normPosition
                            StartTrendAnimation(normPosition)
                        }
                        if (!change.pressed) {
                            StopTrendAnimation()
//                            SetMovePosition(position)
                            joystick = size.center.toOffset()
                        }
                    }
                }
            }
        }
    }
    Canvas(
        modifier = modifier
            .padding(bottom = 50.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .then(moveModifier)
    ) {
        if (joystick == Offset.Zero) joystick = center
        drawCircle(
            center = joystick,
            radius = 100f,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ChandeWeaponButton(
    ChangeWeapon: (Int) -> Unit,
    modifier: Modifier = Modifier,
    weapon: Weapon,
    weaponAssets: WeaponAssets
) {
    var indexWeapon by remember { mutableStateOf(0) }
    Row(
        modifier = modifier
            .padding(start = 20.dp, bottom = 20.dp)
            .aspectRatio(1f),
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            ModeIndicator(
                minActionProgress = weapon.minHolyModeUsable,
                progress = weapon.holyModeProgress,
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .aspectRatio(1f)
            )
            Image(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .aspectRatio(1f)
                    .padding(top = 5.dp, end = 5.dp)
                    .rotate(90f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = Color(0x9C151A15),
                    )
                    .border(
                        width = 2.dp,
                        color = Color(0xFF11110E),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        indexWeapon = ++indexWeapon % 3
                        ChangeWeapon(indexWeapon)
                    },
                bitmap = when (weapon.present_view) {
                    WeaponType.Axe -> weaponAssets.present_view_axe
                    WeaponType.Revolver -> weaponAssets.present_view_revolver
                    WeaponType.Shotgun -> weaponAssets.present_view_shotgun
                },
                contentDescription = null,
                filterQuality = FilterQuality.None
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ShootableIcon(weapon.shootable)
            Text(
                text = weapon.present_clip
            )
        }
    }
}

@Composable
fun AxeButtons(
    changeAxeTrend: (Boolean) -> Unit,
    trend: Boolean,
    modifier: Modifier = Modifier,
) {
    val colorMatrix = remember {
        ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                1f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .scale( if(trend) -1f else 1f, 1f)
                .pointerInput(trend) {
                    detectTapGestures(
                        onTap = {
                            changeAxeTrend(!trend)
                        }
                    )
                },
            alpha = 0.8f,
            painter = painterResource(R.drawable.left_attack),
            colorFilter = if(trend) ColorFilter.colorMatrix( colorMatrix = colorMatrix) else null,
            contentDescription = null
        )
    }
}

@Composable
fun HitPointIndicator(
    modifier: Modifier = Modifier,
    hitpoint: Int,
) {
    // сделать индикатор в виде топора рубящего сердце
    val rotation by animateFloatAsState(
        targetValue = 100f - hitpoint,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )


    val axe = ImageBitmap.imageResource(R.drawable.dark_axe)
    val heart = ImageBitmap.imageResource(R.drawable.heart)

    Box(
        modifier = modifier
            .drawWithCache {
                val axeRation = axe.width.toFloat() / axe.height.toFloat()
                val axeHeight = size.height * 0.6f
                val axeWidth = axeHeight * axeRation
                val axeSize = IntSize(axeWidth.toInt(), axeHeight.toInt())

                val heartRation = heart.width.toFloat() / heart.height.toFloat()
                val heartHeight = size.height / 5f
                val heartWidth = heartHeight * heartRation
                val heartSize = IntSize(heartWidth.toInt(), heartWidth.toInt())
                val heartPosition = IntOffset(
                    ((axeWidth * 1.5f - heartWidth / 2f)).toInt(),
                    (size.height * 0.75f).toInt()
                )
                val hpPersent = hitpoint / 100f
                val redAxePosition = IntOffset(0, (axeHeight - axeHeight * hpPersent).toInt())
                val redAxeSize = IntSize(axeWidth.toInt(), (axeHeight * hpPersent).toInt())
                val clipAxePositionWithPadding =
                    IntOffset(0, (axe.height - axe.height * hpPersent).toInt())
                val clipAxeSize = IntSize(axe.width, (axe.height * hpPersent).toInt())
                val pivot = Offset(axeWidth / 4f, axeHeight)
                onDrawWithContent {
                    rotate(rotation, pivot = pivot) {
                        drawImage(
                            image = axe,
                            filterQuality = FilterQuality.None,
                            dstSize = axeSize,
                            dstOffset = IntOffset.Zero,
                        )
                        drawImage(
                            image = axe,
                            colorFilter = ColorFilter.tint(
                                Color.Red.copy(0.4f),
                                blendMode = BlendMode.SrcAtop
                            ),
                            filterQuality = FilterQuality.None,
                            srcSize = clipAxeSize,
                            srcOffset = clipAxePositionWithPadding,
                            dstSize = redAxeSize,
                            dstOffset = redAxePosition,

                            )
                    }
                    drawImage(
                        dstSize = heartSize,
                        dstOffset = heartPosition,
                        image = heart,
                        filterQuality = FilterQuality.None
                    )
                }
            }
    ) {}

}

@Composable
fun ShootableIcon(shootable: Boolean) {
    if (shootable) {
        Image(
            painter = painterResource(R.drawable.target),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ModeIndicator(
    minActionProgress: Float,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val progressAnim by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(200)
    )
    val infiniteTransition = rememberInfiniteTransition()
    val alphaActive by if (progress >= minActionProgress) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        )
    } else {
        remember { mutableStateOf(1f) }
    }
    val modeIcon = ImageBitmap.imageResource(R.drawable.holy_icon_v2)
    Box(
        modifier = modifier.drawWithCache {
            val ratio = modeIcon.width / modeIcon.height.toFloat()
            val dstSize = IntSize((size.width * ratio).toInt(), size.height.toInt())
            onDrawBehind {
                drawImage(
                    image = modeIcon,
                    dstSize = dstSize,
                    filterQuality = FilterQuality.None,
                    colorFilter = ColorFilter.tint(Color.Black.copy(0.8f), BlendMode.SrcAtop)
                )
                clipRect(
                    top = size.height * (1f - progressAnim)
                ) {
                    drawImage(
                        image = modeIcon,
                        dstSize = dstSize,
                        filterQuality = FilterQuality.None,
                        alpha = alphaActive
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun axeButton(){
    AxeButtons(
        modifier = Modifier.fillMaxSize(),
        trend = true,
        changeAxeTrend = {}
    )
}
@Preview
@Composable
fun axeButtonFalse(){
    AxeButtons(
        modifier = Modifier.fillMaxSize(),
        trend = false,
        changeAxeTrend = {}
    )
}