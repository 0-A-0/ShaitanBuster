package com.example.scrolllist.ui.screens.menu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import com.example.scrolllist.data.GlobalGameSettings
import com.example.scrolllist.ui.screens.game.GameScreenController
import com.example.scrolllist.ui.screens.results.GameOver
import com.example.scrolllist.ui.screens.results.GameOverInfinity
import com.example.scrolllist.ui.screens.results.GameWin

@Composable
fun Menu(
) {
    var gameState by remember { mutableStateOf(0) }
    var kills by remember { mutableStateOf(0) }
    val defaultModifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
//    var time by remember { mutableStateOf(0) }
//    val mask = ImageBitmap.imageResource(R.drawable.mask)

    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .background(if (gameState == 3) Color.Red else Color.Black)
            .drawWithContent {
                drawContent()
                drawRect(
                    color = Color.White,
                    alpha = GlobalGameSettings.brightness,
                    blendMode = BlendMode.Screen,
                )
//                drawImage(
//                    image = mask,
//                    dstSize = IntSize(size.width.toInt(),size.height.toInt()),
//                    alpha = GlobalGameSettings.brightness,
//                    blendMode = BlendMode.Screen,
//                )
            },
        targetState = gameState,
        transitionSpec = {
            when (gameState) {
                3, 4 -> {
                    fadeIn(tween(200, delayMillis = 50)) togetherWith
                            (fadeOut(tween(50)))
                }

                5 -> {
                    fadeIn(tween(200, delayMillis = 10000)) togetherWith
                            (fadeOut(tween(10000)))
                }

                else -> {
                    fadeIn(animationSpec = tween(400, delayMillis = 400)) togetherWith
                            fadeOut(animationSpec = tween(400))
                }
            }
        },
    ) {
        when (it) {
            0 -> StartScreen(
                StartGame = { gameState = 1 },
                StartInfinityGame = { gameState = 2 },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )

            1 -> GameScreenController(
                Exit = { gameState = 0 },
                DeathPlayer = { finalKills ->
                    gameState = 3
                },
                WinPlayer = {
                    gameState = 5
                },
                mode = 0
            )

            2 -> GameScreenController(
                Exit = { gameState = 0 },
                DeathPlayer = { finalKills ->
                    kills = finalKills
                    gameState = 4
                },
                WinPlayer = {
                    gameState = 5
                },
                mode = 1
            )

            3 -> GameOver(RestartGame = { gameState = 0 }, modifier = defaultModifier /*time*/)
            4 -> GameOverInfinity(
                RestartGame = { gameState = 0 },
                kills,
                modifier = defaultModifier /*time*/
            )

            else -> GameWin(
                RestartGame = { gameState = 0 },
                kills,
                modifier = defaultModifier /*time*/
            )
        }
    }
}

