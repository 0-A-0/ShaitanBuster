package com.example.scrolllist.ui.screens.menu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
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
import com.example.scrolllist.ui.theme.ExpandableCard
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
    val heartRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val triangleTranslate by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val heart = ImageBitmap.imageResource(R.drawable.heart)
    val hat = ImageBitmap.imageResource(R.drawable.player)
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    BoxWithConstraints(modifier = modifier) {
        val viewportHeight = this.maxHeight
        Column(
            modifier = Modifier
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
                }
                .verticalScroll(scrollState),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(viewportHeight)
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
                                quadraticTo(
                                    size.width * 0.15f,
                                    size.height * 0.9f,
                                    grassStep,
                                    size.height
                                )
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
                Box(
                    contentAlignment = Alignment.Center,
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
                        ).drawWithCache{
                            val width = size.width*0.05f
                            val height = size.height*0.05f
                            val triangle = Path().apply {
                                moveTo(width/-2f, 0f)
                                lineTo(width/2f, 0f)
                                lineTo(0f, height)
                                close()
                            }
                            val color = Color(0xFFFFF3E0)
                            onDrawBehind {
                                if (scrollState.value == 0) {
                                    translate(size.width/2f,(size.height-height)*triangleTranslate) {
                                        drawPath(triangle, color = color)
                                    }
                                }
                            }
                        }
                ) {
                    AnimatedHelpText(
                        modifier = Modifier.heightIn(min = 56.dp)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2B4B06),
                                Color(0xFF543B1B),
                                Color(0xFF342406),
                            ),
                            endY = 400f
                        )
                    )
            ) {
                Image(
                    bitmap = hat,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    filterQuality = FilterQuality.None
                )
                Text("Тут похоронен безработный автор",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
//                    modifier = Modifier.height(40.dp)
                )
                Text("Если вам понравилась игра, вы можете почтить его память безвозмездным денежным переводом ",
                        textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 10.dp)
                    )
                Button(
                    onClick = {
                        uriHandler.openUri("https://pay.cloudtips.ru/p/0a016f6a")
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDFF0F0)
                    ),
                    modifier = Modifier.border(4.dp, color = colorResource(R.color.content)).padding(10.dp)
                ) {
                    Text("Поддержать автора")
                    Image(
                        bitmap = heart,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                            .graphicsLayer{
                                rotationY = heartRotate
                                cameraDistance = 8f * density
                        },
                        filterQuality = FilterQuality.None
                    )
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                        .height(44.dp)
                        .drawWithCache {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val halfWidth = size.width * 0.36f
                            val diamondHalfWidth = size.height * 0.28f
                            val diamondHalfHeight = size.height * 0.42f

                            val color = Color(0xFFFFF3E0)
                            val stroke = Stroke(width = 2.dp.toPx())

                            val ornament = Path().apply {
                                moveTo(cx, cy - diamondHalfHeight)
                                lineTo(cx + diamondHalfWidth, cy)
                                lineTo(cx, cy + diamondHalfHeight)
                                lineTo(cx - diamondHalfWidth, cy)
                                close()

                                moveTo(cx, cy - diamondHalfHeight * 0.48f)
                                lineTo(cx + diamondHalfWidth * 0.48f, cy)
                                lineTo(cx, cy + diamondHalfHeight * 0.48f)
                                lineTo(cx - diamondHalfWidth * 0.48f, cy)
                                close()

                                moveTo(cx - diamondHalfWidth, cy)
                                lineTo(cx - halfWidth * 0.72f, cy)
                                lineTo(cx - halfWidth, cy - size.height * 0.28f)
                                lineTo(cx - halfWidth * 0.88f, cy)
                                lineTo(cx - halfWidth, cy + size.height * 0.28f)
                                lineTo(cx - halfWidth * 0.72f, cy)
                                lineTo(cx - diamondHalfWidth, cy)

                                moveTo(cx + diamondHalfWidth, cy)
                                lineTo(cx + halfWidth * 0.72f, cy)
                                lineTo(cx + halfWidth, cy - size.height * 0.28f)
                                lineTo(cx + halfWidth * 0.88f, cy)
                                lineTo(cx + halfWidth, cy + size.height * 0.28f)
                                lineTo(cx + halfWidth * 0.72f, cy)
                                lineTo(cx + diamondHalfWidth, cy)
                            }

                            onDrawBehind {
                                drawPath(
                                    path = ornament,
                                    color = color,
                                    style = stroke
                                )

                                val dotRadius = 2.dp.toPx()
                                drawCircle(
                                    color = color,
                                    radius = dotRadius,
                                    center = Offset(cx - halfWidth * 0.58f, cy)
                                )
                                drawCircle(
                                    color = color,
                                    radius = dotRadius,
                                    center = Offset(cx + halfWidth * 0.58f, cy)
                                )
                            }
                        }
                )

                Text("Интересное об игре", fontSize = 25.sp, )
                ExpandableCard(
                    text = "Что под капотом?",
                    expandedText = "Игра написана без игровых движков, что обуславливает малый размер",
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 15.dp)
                )
                ExpandableCard(
                    text = "120+",
                    expandedText = "Все спрайты (около 120 штук) нарисованы автором вручную",
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 15.dp)
                )
                ExpandableCard(
                    text = "музыка",
                    expandedText = "Все звуковые эффекты и один саундтрек взяты с https://freesound.org/ по лицензии CC0, использованы в модифицированном виде (обрезка, сжатие формата)",
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 15.dp)
                )
                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun AnimatedHelpText(modifier: Modifier = Modifier) {
    val alpha = remember { Animatable(0f) }
    val helpTexts = remember {
        listOf(
            "Револьвер: быстро проведите пальцем по дуге или вокруг персонажа для особой атаки",
            "Топор имеет повышеный шанс выпадения патронов",
            "Можно поставить свой саундтрек",
            "Убийства дают вам силу для особой атаки",
            "Топор: рисуйте пальцем для особой атаки",
            "Можно убрать кровь или трупы в настройках",
            "Убийства с револьвера дают патроны только на дробовик и наоборот",
            "Вороны неуязвимы",
            "Переходы между уровнями восстанавливают здоровье",
            "Сторона вращения топора определяет тип выпадающих патронов. Используйте",
            "Дробовик: тяните пальцем для особой атаки",
            "Регулируйте скорость игры под темп музыки",
            "Раскурчивайте топор короткими движениями пальца",
            "Особая атака доступна, когда крест мигает",
            "Револьвер: особая атака распространяется за пределы экрана",
        )
    }
    val randomIndex = remember { Random.nextInt(helpTexts.size - 1) }
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