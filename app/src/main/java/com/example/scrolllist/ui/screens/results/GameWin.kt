package com.example.scrolllist.ui.screens.results

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrolllist.ui.screens.game.AxeButtons
import com.example.scrolllist.ui.theme.MyButton
import kotlinx.coroutines.delay

@Composable
fun GameWin(RestartGame: () -> Unit, kills: Int, modifier: Modifier = Modifier /*time:Time*/) {
    val scaleAnim = remember{ Animatable(1f) }
    LaunchedEffect(Unit) {
        delay(11000L)
        scaleAnim.animateTo(
            targetValue = 4f,
            animationSpec = tween(1000, easing = LinearEasing)
        )
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            ":)",
            fontSize = 50.sp,
            modifier = Modifier.graphicsLayer{
                rotationZ = 90f
                scaleY = scaleAnim.value
            }
        )
        Text(
            "Вы разогнали всю шпану",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "Назойливые детишки больше не пристают к вам",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        MyButton(onClick = RestartGame) {
            Text(
                "Выйти в меню",
                fontSize = 20.sp
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun GameWinScreen(){
    GameWin(
        modifier = Modifier.fillMaxSize(),
        RestartGame = {},
        kills = 0
    )
}