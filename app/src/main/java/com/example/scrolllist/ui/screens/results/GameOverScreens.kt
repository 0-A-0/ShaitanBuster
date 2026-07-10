package com.example.scrolllist.ui.screens.results

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.example.scrolllist.R
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.ui.theme.MyButton

@Composable
fun GameOver(RestartGame: () -> Unit, modifier: Modifier = Modifier /*time:Time*/) {
    val player0 = ImageBitmap.imageResource(R.drawable.player_left)
    val player1 = ImageBitmap.imageResource(R.drawable.player_left_6)
    var player by remember { mutableStateOf(player1) }
    val enemy = ImageBitmap.imageResource(R.drawable.scarecrow_left)
    var prop by remember { mutableFloatStateOf(10f) }
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.9f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    LaunchedEffect(Unit) {
        while (true) {
            TimeManager.delay(100L)
            prop = 5f
            TimeManager.delay(100L)
            prop = 10f
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            TimeManager.delay(700L)
            player = player0
            TimeManager.delay(200L)
            player = player1
        }
    }
    Column(
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Black,
                    0.2f to Color.Transparent,
                    0.5f to Color.Red.copy(0.1f),
                    0.8f to Color.Transparent,
                    1f to Color.Black,
                )
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to Color.Black,
                    0.2f to Color.Transparent,
                    0.5f to Color.Red.copy(0.1f),
                    0.8f to Color.Transparent,
                    1f to Color.Black,
                )
            )
        },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(Modifier.fillMaxHeight(0.3f))
        Canvas(
            modifier = Modifier
                .size(150.dp)
        ) {
            scale(size.height * 0.6f / player0.height) {
                drawImage(
                    dstOffset = (center - Offset(player.width / 2f, player.height / 2f)).round(),
                    image = player,
                    filterQuality = FilterQuality.None,
                )
                drawImage(
                    dstOffset = (center + Offset(200f + prop, -20f) - Offset(
                        enemy.width / 2f,
                        enemy.height / 2f
                    )).round(),
                    filterQuality = FilterQuality.None,
                    image = enemy
                )
            }
        }
        Text(
            ":(",
            fontSize = 50.sp,
        )
        MyButton(
            onClick = RestartGame,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        ) {
            Text(
                "Выйти в меню",
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun GameOverInfinity(
    RestartGame: () -> Unit,
    kills: Int,
    modifier: Modifier = Modifier /*time:Time*/
) {
    val player0 = ImageBitmap.imageResource(R.drawable.player_left)
    val player1 = ImageBitmap.imageResource(R.drawable.player_left_6)
    var player by remember { mutableStateOf(player1) }
    val enemy = ImageBitmap.imageResource(R.drawable.scarecrow_left)
    var prop by remember { mutableFloatStateOf(10f) }
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.9f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    LaunchedEffect(Unit) {
        while (true) {
            TimeManager.delay(100L)
            prop = 5f
            TimeManager.delay(100L)
            prop = 10f
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            TimeManager.delay(700L)
            player = player0
            TimeManager.delay(200L)
            player = player1
        }
    }
    Column(
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Black,
                    0.2f to Color.Transparent,
                    0.5f to Color.Red.copy(0.1f),
                    0.8f to Color.Transparent,
                    1f to Color.Black,
                )
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to Color.Black,
                    0.2f to Color.Transparent,
                    0.5f to Color.Red.copy(0.1f),
                    0.8f to Color.Transparent,
                    1f to Color.Black,
                )
            )
        },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(Modifier.fillMaxHeight(0.3f))
        Canvas(
            modifier = Modifier
                .size(150.dp)
        ) {
            scale(size.height * 0.6f / player0.height) {
                drawImage(
                    dstOffset = (center - Offset(player.width / 2f, player.height / 2f)).round(),
                    image = player,
                    filterQuality = FilterQuality.None,
                )
                drawImage(
                    dstOffset = (center + Offset(50f + prop, -20f) - Offset(
                        enemy.width / 2f,
                        enemy.height / 2f
                    )).round(),
                    filterQuality = FilterQuality.None,
                    image = enemy
                )
            }
        }
        Text(
            ":(",
            fontSize = 50.sp,
        )
        Text(
            text = "Убито всего $kills шайтанов во время Хеллоуина",
            fontSize = 16.sp,
        )
        Text(
            text = "Вы способны на большее",
            fontSize = 16.sp,
        )
        MyButton(
            onClick = RestartGame,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        ) {
            Text(
                "Выйти в меню",
                fontSize = 20.sp,
            )
        }
    }
}