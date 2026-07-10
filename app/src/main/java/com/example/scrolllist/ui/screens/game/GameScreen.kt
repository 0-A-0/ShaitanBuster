package com.example.scrolllist.ui.screens.game

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrolllist.data.GlobalGameSettings
import com.example.scrolllist.R
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.domain.units.PlayerTrend
import com.example.scrolllist.domain.units.weapon.Axe
import com.example.scrolllist.domain.units.weapon.Revolver
import com.example.scrolllist.domain.units.weapon.Shotgun
import com.example.scrolllist.ui.assets.drawBlood
import com.example.scrolllist.ui.assets.drawKillmark
import com.example.scrolllist.ui.assets.drawWeapon
import com.example.scrolllist.ui.assets.drawWithZ
import com.example.scrolllist.ui.screens.settings.Settings
import com.example.scrolllist.ui.theme.LocalShakeTrigger
import com.example.scrolllist.ui.theme.MyButton
import com.example.scrolllist.ui.theme.jiggle
import com.example.scrolllist.ui.theme.shaking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GameScreenController(
    Exit: () -> Unit,
    DeathPlayer: (Int) -> Unit,
    WinPlayer: () -> Unit,
    mode: Int
) {
    val display = LocalContext.current.resources.displayMetrics
    val width = remember { display.widthPixels.toFloat() }
    val height = remember { display.heightPixels.toFloat() }
    val textMeasurer = rememberTextMeasurer()
    val viewModel: GameViewModel = viewModel(
        initializer = {
            val contex = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                ?: throw IllegalArgumentException("Contex missing")
            GameViewModel(
                application = contex,
                canvasWidth = width,
                canvasHeight = height,
                mode = mode,
                DeathPlayer = DeathPlayer,
                WinPlayer = WinPlayer,
            )
        }
    )
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetGame()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }

//    fun unsucsessAction(){
//        if (snackbarHostState.currentSnackbarData == null) {
//                scope.launch {
//                    snackbarHostState.showSnackbar(
//                        message = "⚠️ Выход за границы",
//                        duration = SnackbarDuration.Short
//                    )
//                }
//            }
//    }
//    val ChangeZoom: (Float) -> Unit = { newZoom: Float ->
//        if (canvasWidth / newZoom <= mapSize.width && canvasHeight / newZoom <= mapSize.height) {
//            zoom = newZoom
//            checkScroll()
//        } else {
//            if (snackbarHostState.currentSnackbarData == null) {
//                scope.launch {
//                    snackbarHostState.showSnackbar(
//                        message = "⚠️ Выход за границы",
//                        duration = SnackbarDuration.Short
//                    )
//                }
//            }
//        }
//    }
    val grayScaleFilter = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.5f) })
    }
    var timePause by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var frame by remember { mutableStateOf(0L) }
    val scrollX = {
        viewModel.engine.scrollX
    }
    val scrollY = { viewModel.engine.scrollY }
    val zoom = { viewModel.engine.zoom }
    val trigger = { viewModel.engine.trigger }
    LaunchedEffect(Unit) {
        viewModel.startGame(newMode = mode)
        while (true) {
            TimeManager.isTimePaused.first { isPaused -> !isPaused }
            withFrameNanos { currentTime ->
//                viewModel.onTick(currentTime)
                viewModel.engine.update(currentTime)
                frame++
            }
        }
    }
    LaunchedEffect(timePause) {
        if (timePause) TimeManager.setPaused(true)
        else {
            delay(500L)
            TimeManager.setPaused(false)
        }
    }
    if (viewModel.isActualGame) {
        val weapon = viewModel.engine.weapon
        val player = viewModel.engine.player
        val mapSize = viewModel.engine.mapSize
        val tapModifier = remember(weapon) {
            when (weapon) {
                is Revolver ->
                    Modifier
                        .fillMaxSize()
                        .pointerInput(weapon) {
                            detectTapGestures(
                                onTap = { offset ->
                                    viewModel.engine.revolverTap(offset)
                                },
                                onLongPress = { offset ->
                                    viewModel.engine.revolverOnLongPress(offset)
                                }
                            )
                        }

                is Shotgun ->
                    Modifier
                        .fillMaxSize()
                        .pointerInput(weapon) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    viewModel.engine.shotgunOnDragStart(offset)
                                },
                                onDrag = { change, dragAmount ->
                                    viewModel.engine.shotgunOnDrag(position = change.position)
                                },
                                onDragEnd = {
                                    viewModel.engine.shotgunOnDragEnd()
                                },
                                onDragCancel = {
                                    viewModel.engine.shotgunOnDragCancel()
                                }
                            )
                        }
                        .pointerInput(weapon) {
                            detectTapGestures(
                                onTap = { offset ->
                                    viewModel.engine.shotgunOnTap(offset)
                                }
                            )
                        }

                is Axe -> Modifier
                    .fillMaxSize()
                    .pointerInput(weapon) {
//                    var startTime = 0L
                        detectDragGestures(
                            onDragStart = { offset ->
                                viewModel.engine.axeOnDragStart()
//                            startTime = System.currentTimeMillis()
                            },
                            onDrag = { change, dragAmount ->
                                if (dragAmount.getDistance() > 5f) {
                                    viewModel.engine.axeOnDrag(position = change.position)
                                }
                            },
                            onDragEnd = {
                                viewModel.engine.axeOnDragEnd()
                            },
                            onDragCancel = {
                                viewModel.engine.axeOnDragCancel()
                            }
                        )
                    }
            }
        }
        CompositionLocalProvider(LocalShakeTrigger provides trigger()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size ->
                        viewModel.engine.updateGameScreenSize(
                            width = size.width.toFloat(),
                            height = size.height.toFloat()
                        )
                    }
            ) {
                Box(
                    modifier = tapModifier
                        .drawWithContent {
                            drawContent()
                            if (player.hitPoint <= 50) {
                                drawImage(
                                    image = viewModel.gameAssets.hurt_frame,
                                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                                )
                            }
                            drawKillmark(
                                viewModel.engine.killmarkController,
                                textMeasurer,
                                viewModel.gameAssets
                            )
                        }
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = zoom()
                                scaleY = zoom()
                                transformOrigin = TransformOrigin(0f, 0f)
                                translationX = scrollX() * zoom()
                                translationY = scrollY() * zoom()
                            }
                            .jiggle { viewModel.engine.trigger2 }
                            .drawWithContent {
                                drawContent()
                                if (player.isBlind) {
                                    drawRect(
                                        brush = Brush.radialGradient(
                                            0.0f to Color.Transparent,
                                            0.1f to Color.Black.copy(0.1f),
                                            0.2f to Color.Black.copy(0.3f),
                                            0.3f to Color.Black.copy(0.7f),
                                            1f to Color.Black,
                                            radius = 500f,
                                            center = player.center,
                                        ),
                                        topLeft = Offset(-scrollX(), -scrollY()),
                                        size = size,
                                    )
                                }
                            }
                    ) {
                        frame
                        drawImage(
                            dstSize = mapSize,
                            image = viewModel.gameAssets.back,
                        )
                        drawBlood(viewModel.engine.bloods, viewModel.gameAssets.bloodAnimation)
                        drawWithZ(viewModel.engine.listOfDrawableWithZ, viewModel.gameAssets)
                        drawWeapon(
                            player = viewModel.engine.player,
                            weapon = weapon,
                            weaponAssets = viewModel.gameAssets.weaponAssets
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        HitPointIndicator(
                            hitpoint = player.hitPoint,
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxHeight(0.1f)
                                .aspectRatio(1f)
                                .shaking()
                                .background(
                                    Color.Black.copy(0.9f),
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        bottomStart = 8.dp,
                                        topEnd = 8.dp,
                                        bottomEnd = 30.dp
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        ChandeWeaponButton(
                            modifier = Modifier.shaking(),
                            ChangeWeapon = { index ->
                                viewModel.engine.changeWeapon(index)
                            },
                            weapon = weapon,
                            weaponAssets = viewModel.gameAssets.weaponAssets
                        )
                        Joystick(
                            modifier = Modifier.shaking(),
                            StartTrendAnimation = { position ->
                                viewModel.engine.player.startPayerMoveAnimation(position)
                            },
                            StopTrendAnimation = {
                                viewModel.engine.player.updateTrend(PlayerTrend.Stop)
                            }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        when (weapon) {
                            is Axe -> AttackAxeButtons(
                                modifier = Modifier,
                                rotationAxe = { _isHold ->
                                    viewModel.engine.axeOnRotate(_isHold)
                                },
                                axe = viewModel.engine.axe
                            )

                            else -> {}
                        }
                    }
                }
                MyButton(
                    onClick = {
                        timePause = true
                        showSettings = true
                    },
                    modifier = Modifier
                        .padding(30.dp)
                        .align(Alignment.TopEnd)
                        .shaking()
                ) {
                    Text("Настройки", fontSize = 10.sp)
                }
                AnimatedVisibility(
                    visible = showSettings,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None
                ) {
                    Settings(
                        zoom = zoom(),
                        ChangeZoom = { newZoom ->
                            viewModel.ChangeZoom(
                                newZoom,
                                unsucsessAction = {})
                        },
                        Exit = Exit,
                        modifier = Modifier.fillMaxSize(),
                        brightness = GlobalGameSettings.brightness,
                        CloseSettings = {
                            timePause = false
                            showSettings = false
                        },
                    )
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    Snackbar(
                        containerColor = colorResource(R.color.back_content),
                        contentColor = colorResource(R.color.content),
                        snackbarData = it
                    )
                }
            }
        }
    }
}