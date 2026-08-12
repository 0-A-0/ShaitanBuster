package com.example.scrolllist.ui.screens.menu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrolllist.R
import com.example.scrolllist.ui.theme.MyButton
import com.example.scrolllist.ui.theme.burning
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun StartScreen(
    StartGame: () -> Unit,
    StartInfinityGame: () -> Unit,
    EnterAudioLobby: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val timesOfDay by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 15_000
                0f at 0
                0f at 3_000
                1f at 6_000 using FastOutSlowInEasing
                1f at 15_000
            },
            repeatMode = RepeatMode.Reverse,
        )
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .drawWithContent {
                drawRect(
//                color = Color(0xFF3B0B0B),
//                color = Color(0xFF5A0D0D),
//                Color(0xFF6B1124)
                    color = Color(0xFFD5DCD2),
                    topLeft = Offset(0f, 0f)
                )
                drawContent()
                drawRect(
                    color = Color(0xFF411F1F),
                    blendMode = BlendMode.ColorBurn,
                    alpha = timesOfDay
                )
            },
    ) {

        Spacer(Modifier.weight(1f))
        Text(
            text = "ShaitanBuster",
            style = TextStyle(
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily(Font(R.font.kelly_slab)),
                color = Color.White,
            ),
            modifier = Modifier.burning()
        )
        Spacer(Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
                .graphicsLayer()
                .drawWithCache {
                    val widthRect = size.width * 0.08f
                    val heightRect = size.height
                    val x = (size.width - widthRect) / 2f
                    val y = 0f
                    val grassStep = size.width * 0.15f
                    val path = Path().apply {
                        moveTo(0f, size.height)
                        quadraticTo(
                            size.width * 0.05f,
                            size.height * 0.9f,
                            size.width * 0.2f,
                            size.height * 0.8f
                        )
                        quadraticTo(size.width * 0.15f, size.height * 0.9f, grassStep, size.height)
                        close()
                    }
                    val colorsGrass = listOf(
                        Color(0xFF2B4B06),
                        Color(0xFF147A18),
                        Color(0xFF8BB956),
                        Color(0xFF7FBD37),
                        Color(0xFF7FBD37),
                    )
                    onDrawWithContent {
                        drawRect(
                            color = Color(0xFF000000),
                            size = Size(widthRect, heightRect),
                            topLeft = Offset(x, y)
                        )
                        drawContent()
//                        drawImage(
//                            image = hat,
//                            topLeft = Offset((size.width - hat.width)/2f,-size.height*0.05f)
//                        )
                        drawPath(
                            path = path,
                            color = Color(0xFF7FBD37)
                        )
                        var currentFill = 0f
                        while (currentFill < size.width) {
                            withTransform({
                                translate(left = currentFill, top = 0f)
                                scale(
                                    0.7f + Random.nextFloat() * 0.6f,
                                    0.7f + Random.nextFloat() * 0.6f,
                                    Offset(0f, size.height)
                                )
                            }) {
                                drawPath(
                                    path = path,
                                    color = colorsGrass.random()
                                )
                            }
                            currentFill += grassStep
                        }
                        currentFill = grassStep * 0.8f
                        while (currentFill < size.width) {
                            val reflectBoolean = Random.nextBoolean()
                            withTransform({
                                translate(left = currentFill, top = 0f)
                                if (reflectBoolean) scale(
                                    -(0.7f + Random.nextFloat() * 0.6f),
                                    0.7f + Random.nextFloat() * 0.6f,
                                    Offset(0f, size.height)
                                )
                            }) {
                                drawPath(
                                    path = path,
                                    color = colorsGrass.random()
                                )
                            }
                            currentFill += grassStep
                        }
//                        drawContent()
                    }
                },
        ) {
            Spacer(Modifier.fillMaxSize(0.1f))
            MyButton(
                onClick = StartGame,
                //            modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    "Начать игру",
                    fontSize = 20.sp,
                )
            }
            MyButton(
                onClick = StartInfinityGame,
                //            modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    "Бесконечный режим",
                    fontSize = 20.sp
                )
            }
            Spacer(Modifier.fillMaxHeight(0.45f))
            MyButton(
                onClick = EnterAudioLobby,
                modifier = Modifier.rotate(30f)
            ) {
                Text(
                    "Аудио",
                    fontSize = 14.sp
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7FBD37),
                            Color(0xFF2B4B06)
                        ),
                        endY = 100f
                    )
                )
        ) {
            AnimatedHelpText(
                modifier = Modifier.heightIn(min = 56.dp)
            )
        }
    }
}

@Composable
fun AnimatedHelpText(modifier: Modifier = Modifier) {
    val alpha = remember { Animatable(0f) }
    val helpTexts = remember {
        listOf(
            "Револьвер: быстро проведите пальцем для особой атаки",
            "Топор имеет повышеный шанс выпадения патронов",
            "Можно поставить свой саундтрек",
            "Убийства дают вам силу для особой атаки",
            "Топор: рисуйте пальцем для особой атаки",
            "Можно убрать кровь или трупы в настройках",
            "Убийства с револьвера дают патроны только на дробовик и наоборот",
            "Дикие вороны неуязвимы",
            "Переходы между уровнями восстанавливают здоровье",
            "Сторона вращения топора определяет тип выпадающих патронов. Используйте",
            "Дробовик: тяните пальцем для особой атаки",
            "Лёгкий уровень? - Измените скорость игры в настройках",
            "Раскурчивайте топор короткими движениями пальца",
            "Миньоны не влияют на прогресс уровня",
            "Крест показывает накопленную силу",
            "Револьвер: особая атака распространяется за пределы экрана",
        )
    }
    val randomIndex = Random.nextInt(helpTexts.size - 1)
    var helpText by remember { mutableStateOf(helpTexts[randomIndex]) }
    LaunchedEffect(Unit) {
        var index = randomIndex
        while (true) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(500)
            )
            delay(3500L)
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(500)
            )
            helpText = helpTexts[++index % helpTexts.size]
        }
    }
    Text(
        text = helpText,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        color = colorResource(R.color.content),
        modifier = modifier.graphicsLayer { this.alpha = alpha.value }
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF00ffff
)
@Composable
fun StartScreenPreview() {
    StartScreen(StartGame = {}, StartInfinityGame = {}, EnterAudioLobby = {}, modifier = Modifier.fillMaxSize())
}