package com.example.scrolllist.ui.screens.menu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.scrolllist.ui.theme.MyButton
import kotlinx.coroutines.delay

@Composable
fun StartScreen(
    StartGame: () -> Unit,
    StartInfinityGame: () -> Unit,
    modifier: Modifier = Modifier
) {
//    Image(
//        modifier = Modifier.fillMaxSize(),
//        painter = painterResource(R.drawable.main_back),
//        contentDescription = null,
//        contentScale = ContentScale.Crop
//    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Spacer(Modifier.fillMaxHeight(0.4f))
        MyButton(
            onClick = StartGame,
        ) {
            Text(
                "Начать игру",
                fontSize = 20.sp,
            )
        }
        MyButton(
            onClick = StartInfinityGame,
        ) {
            Text(
                "Бесконечный режим",
                fontSize = 20.sp
            )
        }
        Spacer(Modifier.fillMaxHeight(0.4f))
        AnimatedHelpText()
    }
}

@Composable
fun AnimatedHelpText() {
    val alpha = remember { Animatable(0f) }
    val helpTexts = remember {
        listOf(
            "Револьвер: удерживайте для особой атаки",
            "Топор имеет повышеный шанс выпадения патронов",
            "Дробовик: сила особой атаки уменьшается постепенно",
            "Убийства дают вам силу для особой атаки",
            "Топор: проведите пальцем для особой атаки",
            "Можно убрать кровь или трупы для энергосбережения",
            "Убийства с револьвера дают патроны только на дробовик и наоборот",
            "Дикие вороны неуязвимы",
            "Мультиубийства имеют экспоненциальную ценность",
            "Сторона вращения топора определяет тип выпадающих патронов. Используйте",
            "Дробовик: тяните пальцем для особой атаки",
            "Лёгкий уровень? - Измените скорость игры в настройках",
            "Слабо раскрученый топор бесполезен",
            "Миньоны не влияют на прогресс уровня",
            "Револьвер: особая атака распространяется за пределы экрана",
        )
    }
    var helpText by remember { mutableStateOf(helpTexts[0]) }
    LaunchedEffect(Unit) {
        var index = 0
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
        modifier = Modifier.graphicsLayer { this.alpha = alpha.value }
    )
}