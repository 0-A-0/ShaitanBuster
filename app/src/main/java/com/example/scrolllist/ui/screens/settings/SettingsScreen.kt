package com.example.scrolllist.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrolllist.managers.AudioManager
import com.example.scrolllist.data.GlobalGameSettings
import com.example.scrolllist.R
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.ui.theme.MyButton
import com.example.scrolllist.ui.theme.MyCheckbox
import com.example.scrolllist.ui.theme.MySlider
import com.example.scrolllist.ui.theme.ScrollListTheme
import com.example.scrolllist.ui.theme.VerticalSlider

@Composable
fun AnimatedVisibilityScope.Settings(
    zoom: Float,
    ChangeZoom: (Float) -> Unit,
    Exit: () -> Unit,
    brightness: Float,
    modifier: Modifier = Modifier,
    CloseSettings: () -> Unit,
) {
    val contex = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            GlobalGameSettings.save(contex)
        }
    }
    val topShape = remember {
        GenericShape { size, _ ->
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.75f)
            quadraticTo(size.width * 0.8f, size.height * 0.8f, size.width * 0.75f, size.height)
            quadraticTo(size.width / 2f, size.height * 0.8f, size.width * 0.25f, size.height)
            quadraticTo(size.width * 0.2f, size.height * 0.8f, 0f, size.height * 0.75f)
            close()
        }
    }
    val fakelPainter = painterResource(R.drawable.fakel)
    val bottomShape = remember {
        GenericShape { size, _ ->
            moveTo(0f, size.height)
            lineTo(size.width, size.height)
            lineTo(size.width, size.height * 0.5f)
            quadraticTo(size.width * 0.8f, size.height * 0.8f, size.width * 0.75f, 0f)
            quadraticTo(size.width / 2f, size.height / 2f, size.width * 0.25f, 0f)
            quadraticTo(size.width * 0.2f, size.height * 0.8f, 0f, size.height * 0.5f)
            close()
        }
    }
    val value = TimeManager.timeScale.collectAsState().value
    val checkedBody = GlobalGameSettings.useBody.collectAsState().value
    val checkedBlood = GlobalGameSettings.useBlood.collectAsState().value
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(color = colorResource(R.color.back_content)),
            onClick = {}
        )
    ) {
        Surface(
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .animateEnterExit(
                    enter = slideInVertically { -it },
                    exit = slideOutVertically { -it }
                ),
            /*.border(width = 2.dp, color =  Color(0xFFC03B11), shape = topShape)*/
            shape = topShape,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VerticalSlider(
                        text = "Музыка:",
                        value = AudioManager._musicVolume,
                        range = 0.0f..0.6f,
                        onChangeValue = { AudioManager.setMusicVolume(it) },
                        modifier = Modifier
                            .fillMaxWidth(0.2f)
                            .fillMaxHeight(0.3f)
                    )
                    Text("Кровь")
                    MyCheckbox(
                        checked = checkedBlood,
                        onCheckedChange = { GlobalGameSettings.setUseBlood(it) },
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Настройки игры",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )
                    Text("Яркость:")
                    MySlider(
                        value = brightness,
                        onValueChange = { GlobalGameSettings.changeBrightness(it) },
                        valueRange = 0f..0.2f,
                    ) {
                        val infiniteTransition = rememberInfiniteTransition()
                        val firePulse by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1f,
                            animationSpec = InfiniteRepeatableSpec(
                                animation = tween(
                                    durationMillis = 1000,
                                    easing = FastOutSlowInEasing
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                        )
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(40.dp)) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.Yellow.copy(alpha = 0.4f),
                                            Color.Transparent
                                        ),
                                        center = center,
                                        radius = size.width * firePulse
                                    ),
                                    radius = size.width
                                )
                            }
                            Image(
                                painter = fakelPainter,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Text("Скорость игры: x%.1f".format(value))
                    MySlider(
                        value = value,
                        onValueChange = { TimeManager.setTimeScale(it) },
                        valueRange = 0.1f..2f,
                        steps = 18,
                    ) {
                        val timeScale by TimeManager.timeScale.collectAsState()
                        key(timeScale) {
                            val infiniteTransition = rememberInfiniteTransition()
                            val minuteAngle by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 359f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = (1000 / timeScale).toInt(),
                                        easing = LinearEasing
                                    ),
                                    repeatMode = RepeatMode.Restart
                                )
                            )
                            val hourAngle by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 359f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = (5000 / timeScale).toInt(),
                                        easing = LinearEasing
                                    ),
                                    repeatMode = RepeatMode.Restart
                                )
                            )
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            )
                            {
                                Canvas(
                                    modifier = Modifier.size(10.dp),
                                ) {
                                    drawRect(
                                        color = Color.Gray,
                                        topLeft = Offset(size.width * 0.3f, -size.height),
                                        size = Size(
                                            width = size.width * 0.4f,
                                            height = size.height * 0.3f
                                        )
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = size.width,
                                        center = center,
                                    )
                                    drawCircle(
                                        color = Color.Black,
                                        radius = size.width * 0.8f,
                                        center = center,
                                        style = Stroke(
                                            width = size.width * 0.2f,
                                            pathEffect = PathEffect.dashPathEffect(
                                                floatArrayOf(2f, 20f)
                                            )
                                        )
                                    )
                                    rotate(minuteAngle) {
                                        drawLine(
                                            color = Color.Black,
                                            start = center,
                                            end = Offset(0f, size.height * 0.9f),
                                            strokeWidth = size.width * 0.1f,
                                        )
                                    }
                                    rotate(hourAngle) {
                                        drawLine(
                                            color = Color.Black,
                                            start = center,
                                            end = Offset(0f, size.height * 0.5f),
                                            strokeWidth = size.width * 0.1f,
                                        )
                                    }
                                }
                            }
                        }
//                        Text(
//                            "⏱️",
//                            fontSize = 20.sp,
//                            modifier = Modifier
//                        )
                    }
                    Text("Размер окна: x%.1f".format(zoom))
                    MySlider(
                        value = zoom,
                        onValueChange = { ChangeZoom(it) },
                        valueRange = 0.1f..2f,
                        steps = 18,
                    ) {
                        Text(
                            "🔎",
                            fontSize = 20.sp,
                            modifier = Modifier
                        )
                    }
                    MyButton(onClick = { Exit() }) {
                        Text("Выйти в меню")
                    }
                    Spacer(Modifier.fillMaxHeight(0.2f))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VerticalSlider(
                        text = "Звуки:",
                        value = AudioManager._soundVolume,
                        range = 0.0f..1.0f,
                        onChangeValue = { AudioManager.setSoundVolume(it) },
                        modifier = Modifier
                            .fillMaxWidth(0.2f)
                            .fillMaxHeight(0.3f)
                    )
                    Text("Трупы")
                    MyCheckbox(
                        checked = checkedBody,
                        onCheckedChange = { GlobalGameSettings.setUseBody(it) },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Surface(
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.BottomCenter)
                .animateEnterExit(
                    enter = slideInVertically(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutLinearInEasing
                        )
                    ) { +it },
                    exit = slideOutVertically { +it }
                )
                .border(width = 10.dp, color = Color.Black, shape = bottomShape),
            shape = bottomShape,
        ) {
            MyButton(onClick = { CloseSettings() }) {
                Text("Закрыть")
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2400px,dpi=431"
)
@Composable
fun GreetingPreview() {
    ScrollListTheme {
        AnimatedVisibility(visible = true) {
            Settings(
                zoom = 1f,
                ChangeZoom = {},
                Exit = {},
                CloseSettings = {},
                brightness = 1f,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=720px,height=1560px,dpi=282"
)
@Composable
fun GreetingPreview2() {
    ScrollListTheme {
        AnimatedVisibility(visible = true) {
            Settings(
                zoom = 1f,
                ChangeZoom = {},
                Exit = {},
                CloseSettings = {},
                brightness = 1f,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}