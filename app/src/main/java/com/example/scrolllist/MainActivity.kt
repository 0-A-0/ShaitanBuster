package com.example.scrolllist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrolllist.domain.calcDistance
import com.example.scrolllist.domain.units.PlayerTrend
import com.example.scrolllist.domain.units.weapon.Axe
import com.example.scrolllist.domain.units.weapon.Revolver
import com.example.scrolllist.domain.units.weapon.Shotgun
import com.example.scrolllist.domain.units.weapon.Weapon
import com.example.scrolllist.domain.units.weapon.WeaponType
import com.example.scrolllist.ui.GameViewModel
import com.example.scrolllist.ui.assets.WeaponAssets
import com.example.scrolllist.ui.drawBlood
import com.example.scrolllist.ui.drawKillmark
import com.example.scrolllist.ui.drawWeapon
import com.example.scrolllist.ui.drawWithZ
import com.example.scrolllist.ui.theme.LocalShakeTrigger
import com.example.scrolllist.ui.theme.ScrollListTheme
import com.example.scrolllist.ui.theme.jiggle
import com.example.scrolllist.ui.theme.shaking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        setContent {
            ScrollListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Menu()
                }
            }
        }
    }
}

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

@Composable
fun GameWin(RestartGame: () -> Unit, kills: Int, modifier: Modifier = Modifier /*time:Time*/) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            ":)",
            fontSize = 50.sp
        )
        Text(
            "Вы разогнали всю шпану",
            fontSize = 16.sp
        )
        Text(
            "Назойливые детишки больше не пристают к вам",
            fontSize = 16.sp,
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
fun AttackAxeButtons(
    rotationAxe: (Boolean) -> Unit,
    axe: Axe,
    modifier: Modifier = Modifier,
) {
    var isHoldL by remember { mutableStateOf(false) }
    var isHoldR by remember { mutableStateOf(false) }
    val animatedRotationL by animateFloatAsState(
        targetValue = if (isHoldL) -360f else 0f,
        animationSpec = tween(
            durationMillis = 5000,
            easing = LinearEasing
        ),
        label = "press_rotation"
    )
    val animatedRotationR by animateFloatAsState(
        targetValue = if (isHoldR) -360f else 0f,
        animationSpec = tween(
            durationMillis = 5000,
            easing = LinearEasing
        ),
        label = "press_rotation"
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
    ) {
        Image(
            modifier = Modifier
                .padding(bottom = 100.dp)
                .weight(1f)
                .aspectRatio(1f)
                .scale(1f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            axe.trend = false
                            isHoldL = true
                            rotationAxe(isHoldL)
                            tryAwaitRelease()
                            isHoldL = false
                            rotationAxe(isHoldL)
                        }
                    )
                }
                .graphicsLayer {
                    rotationZ = animatedRotationL
                },
            alpha = 0.8f,
            painter = painterResource(R.drawable.left_attack),
            contentDescription = null
        )
        Spacer(Modifier.width(10.dp))
        Image(
            modifier = Modifier
                .padding(bottom = 100.dp, end = 5.dp)
                .weight(1f)
                .aspectRatio(1f)
                .scale(-1f, 1f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            axe.trend = true
                            isHoldR = true
                            rotationAxe(isHoldR)
                            tryAwaitRelease()
                            isHoldR = false
                            rotationAxe(isHoldR)
                        }
                    )
                }
                .graphicsLayer {
                    rotationZ = animatedRotationR
                },
            alpha = 0.8f,
            painter = painterResource(R.drawable.left_attack),
            contentDescription = null
        )
    }
}

@Composable
fun AnimatedVisibilityScope.Settings(
    zoom: Float,
    ChangeZoom: (Float) -> Unit,
    Exit: () -> Unit,
    brightness: Float,
    modifier: Modifier = Modifier,
    CloseSettings: () -> Unit,
) {
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

@Composable
fun MyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val buttonShape = remember {
        GenericShape { size, _ ->
            val propW = 0.2f
            val firstHalf = Path().apply {
                val spikeHeight = 0f
                val topLineY = 15f
                moveTo(0f, size.height / 2f)
                quadraticTo(
                    size.width * 0.05f,
                    size.height * 0.3f,
                    size.width * 0.08f,
                    spikeHeight
                )
                quadraticTo(size.width * 0.12f, topLineY, size.width * propW, topLineY)
                lineTo(size.width * (1f - propW), topLineY)
                quadraticTo(
                    size.width * (1f - 0.12f),
                    topLineY,
                    size.width * (1f - 0.08f),
                    spikeHeight
                )
                quadraticTo(
                    size.width * (1f - 0.05f),
                    size.height * 0.3f,
                    size.width,
                    size.height / 2f
                )
                close()
            }
            val matrix = Matrix().apply {
                translate(0f, size.height / 2f)
                scale(1f, -1f)
                translate(0f, -size.height / 2f)
            }
            val secondHalf = Path().apply {
                addPath(firstHalf)
                transform(matrix)
            }
            addPath(firstHalf)
            addPath(secondHalf)
        }
    }
    Button(
        shape = buttonShape,
        onClick = onClick,
        modifier = modifier
            .sizeIn(
                minWidth = 100.dp,
                minHeight = 48.dp,
                maxWidth = 300.dp,
                maxHeight = 100.dp
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(R.color.back_content),
//                        colorResource(R.color.back_content),
                        Color(0xFF67210A)
                    )
                ),
                shape = buttonShape
            ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
//            containerColor = colorResource(R.color.back_content),
            containerColor = Color.Transparent,
            contentColor = colorResource(R.color.content)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySlider(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    colors: SliderColors = SliderDefaults.colors(
        thumbColor = Color.Transparent,
        activeTrackColor = Color.Transparent
    ),
    thumbContent: @Composable () -> Unit
) {
    val clipShape = remember {
        GenericShape { size, _ ->
            moveTo(0f, size.height / 2f)
            quadraticTo(size.width / 2f, 0f, size.width, size.height / 2f)
            quadraticTo(size.width / 2f, size.height, 0f, size.height / 2f)
            close()
        }
    }
    var targetRotation by remember { mutableStateOf(0f) }
    LaunchedEffect(value) {
        delay(100)
        targetRotation = 0f
    }
    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = {
            targetRotation = when {
                (it - value) > 0.001f -> -20f
                (it - value) < -0.001f -> 20f
                else -> 0f
            }
            onValueChange(it)
        },
        valueRange = valueRange,
        steps = steps,
        colors = colors,
        thumb = {
            Box(modifier = Modifier.graphicsLayer(rotationZ = rotation)) {
                thumbContent()
            }
        },
        track = {
            val brush = Brush.linearGradient(
                colors = listOf(
                    colorResource(R.color.back_content),
                    colorResource(R.color.content)
                )
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(clipShape)
            ) {
                drawRect(
                    color = Color.DarkGray,
                )
                drawRect(
                    brush = brush,
                    size = size.copy(width = size.width * ((it.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)))
                )
            }
        }
    )
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
fun MyCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val QuatrefoilShape = remember {
        GenericShape { size, _ ->
            val r = size.minDimension / 4f
            val cx = size.width / 2f
            val cy = size.height / 2f

            addOval(Rect(cx - r, cy - 2 * r, cx + r, cy))
            addOval(Rect(cx - r, cy, cx + r, cy + 2 * r))
            addOval(Rect(cx - 2 * r, cy - r, cx, cy + r))
            addOval(Rect(cx, cy - r, cx + 2 * r, cy + r))
        }
    }
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkedColor = colorResource(R.color.back_content),
            checkmarkColor = colorResource(R.color.content),
            uncheckedColor = colorResource(R.color.back_content)
        )
    )
}

@Composable
fun VerticalSlider(
    text: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    onChangeValue: (Float) -> Unit,
) {
    val currentValue by rememberUpdatedState(value)
    val color = colorResource(R.color.back_content)
    val rangeLength = remember { range.endInclusive - range.start }
    val previewValue = ((value - range.start) / (rangeLength) * 100f).toInt().coerceIn(0, 100)
    Text(
        text = text.plus(" $previewValue")
    )
    Canvas(
        modifier = modifier
            .drawWithCache {
                val path = Path()
                path.moveTo(size.width / 2f, 0f)
                path.quadraticTo(size.width * 3f, size.height / 2f, size.width / 2f, size.height)
                path.quadraticTo(-size.width * 2f, size.height / 2f, size.width / 2f, 0f)
                path.close()
                onDrawBehind {
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 6f)
                    )
                    clipRect(
                        left = 0f,
                        top = size.height - (size.height * (currentValue / rangeLength)),
                        right = size.width,
                        bottom = size.height,
                    ) {
                        drawPath(
                            path = path,
                            brush = Brush.radialGradient(
                                0f to Color.Red,
                                0.1f to Color.Red.copy(0.7f),
                                0.4f to color,
                                1f to color,
                                center = size.center,
                                radius = size.height,
                            )
                        )
                    }
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val valuePixel = rangeLength / size.height
                    val newValue = currentValue - (dragAmount * valuePixel)
                    onChangeValue(newValue.coerceIn(range))
                }
            }
    ) {

    }
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
//@Preview(
//    showBackground = true,
//    widthDp = 300,
//    heightDp = 800
//)
//@Composable
//fun one() {
//    HitPointIndicator(
//        hitpoint = 50,
//        modifier = Modifier
//            .padding(30.dp)
//            .fillMaxSize()
//            .background(
//                Color.Black.copy(0.9f),
//                shape = RoundedCornerShape(
//                    topStart = 8.dp,
//                    bottomStart = 8.dp,
//                    topEnd = 8.dp,
//                    bottomEnd = 30.dp
//                )
//            )
//    )
//}

//@Preview(
//    showBackground = true,
//    device = "spec:width=720px,height=1560px,dpi=282"
//)
//@Composable
//fun GameOverScreen() {
//    GameOver(RestartGame = {})
//}
//
//@Preview(
//    showBackground = true,
//    device = "spec:width=1080px,height=2400px,dpi=431"
//)
//@Composable
//fun GameOverScreen2() {
//    GameOver(RestartGame = {})
//}
