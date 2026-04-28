package com.example.scrolllist

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.RectF
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
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
import com.example.scrolllist.objects.Ammunition
import com.example.scrolllist.objects.AmmunitionType
import com.example.scrolllist.objects.BoxObject
import com.example.scrolllist.objects.DinamicObject
import com.example.scrolllist.objects.FirePoint
import com.example.scrolllist.objects.Killmark
import com.example.scrolllist.objects.KillmarkController
import com.example.scrolllist.ui.theme.LocalShakeTrigger
import com.example.scrolllist.ui.theme.ScrollListTheme
import com.example.scrolllist.ui.theme.jiggle
import com.example.scrolllist.ui.theme.shaking
import com.example.scrolllist.units.Animation
import com.example.scrolllist.units.Player
import com.example.scrolllist.units.PlayerTrend
import com.example.scrolllist.units.enemy.Blood
import com.example.scrolllist.units.enemy.Body
import com.example.scrolllist.units.enemy.Crow
import com.example.scrolllist.units.enemy.CrowMinion
import com.example.scrolllist.units.enemy.EnemiesList
import com.example.scrolllist.units.enemy.Scarecrow
import com.example.scrolllist.units.enemy.Smoke
import com.example.scrolllist.units.enemy.Spawner
import com.example.scrolllist.units.weapon.Axe
import com.example.scrolllist.units.weapon.Revolver
import com.example.scrolllist.units.weapon.Shotgun
import com.example.scrolllist.units.weapon.Weapon
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

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
            text = "Убито всего $kills детей во время Хеллоуина",
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
    val textMeasurer = rememberTextMeasurer()
    val resources = LocalContext.current.resources
    val context = LocalContext.current
    AudioManager.init(context)
    DisposableEffect(Unit) {
        onDispose {
            AudioManager.release()
            TimeManager.setPaused(false)
        }
    }
    var isLevelCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(isLevelCompleted) {
        if (!isLevelCompleted) {
            combine(TimeManager.timeScale, TimeManager.isTimePaused) { scale, paused ->
                scale to paused
            }.collect { (currentScale, currentPaused) ->
                delay(30L)
                if (!currentPaused) AudioManager.setMusicSpeed(currentScale)
                AudioManager.playMusic(currentPaused)
            }
        } else AudioManager.stopMusic()
    }

    fun loadListOfFrames(ids: List<Int>): List<ImageBitmap> {
        val cacheIds = ids.distinct().associateWith { id ->
            BitmapFactory.decodeResource(resources, id).asImageBitmap()
        }
        return ids.map { id -> cacheIds[id]!! }
    }

    val box_ = ImageBitmap.imageResource(R.drawable.box)
    val killmark = ImageBitmap.imageResource(R.drawable.killmark)
    val axe_killmark = ImageBitmap.imageResource(R.drawable.axe_killmark)
    val revolver_killmark = ImageBitmap.imageResource(R.drawable.revolver_killmark)

    val scarecrowIds = remember {
        listOf(
            R.drawable.scarecrow_front_1,
            R.drawable.scarecrow_front_2,
            R.drawable.scarecrow_front_3,
            R.drawable.scarecrow_front_4,
            R.drawable.scarecrow_front_5,
            R.drawable.scarecrow_front_6
        )
    }
    val scarecrow = remember { loadListOfFrames(scarecrowIds) }

    val playerBack = ImageBitmap.imageResource(R.drawable.player_back)
    val playerBackAnimationIds = remember {
        listOf(
            R.drawable.player_back_1,
            R.drawable.player_back_2,
            R.drawable.player_back_3,
            R.drawable.player_back_4,
            R.drawable.player_back_5
        )
    }
    val playerBackAnimation = remember { loadListOfFrames(playerBackAnimationIds) }

    val playerFront = ImageBitmap.imageResource(R.drawable.player_front)
    val playerFrontAnimationIds = remember {
        listOf(
            R.drawable.player_front_1,
            R.drawable.player_front_2,
            R.drawable.player_front_3,
            R.drawable.player_front_4,
            R.drawable.player_front_5,
            R.drawable.player_front_6
        )
    }
    val playerFrontAnimation = remember { loadListOfFrames(playerFrontAnimationIds) }

    val playerRight = ImageBitmap.imageResource(R.drawable.player_right)
    val playerRightAnimationIds = remember {
        listOf(
            R.drawable.player_right_1, R.drawable.player_right_2, R.drawable.player_right_3,
            R.drawable.player_right_4, R.drawable.player_right_5, R.drawable.player_right_6
        )
    }
    val playerRightAnimation = remember { loadListOfFrames(playerRightAnimationIds) }

    val playerLeft = ImageBitmap.imageResource(R.drawable.player_left)
    val playerLeftAnimationIds = remember {
        listOf(
            R.drawable.player_left_1, R.drawable.player_left_2, R.drawable.player_left_3,
            R.drawable.player_left_4, R.drawable.player_left_5, R.drawable.player_left_6
        )
    }
    val playerLeftAnimation = remember { loadListOfFrames(playerLeftAnimationIds) }

    val bloodIds = remember {
        listOf(
            R.drawable.blood_1, R.drawable.blood_2, R.drawable.blood_3,
            R.drawable.blood_4, R.drawable.blood_5, R.drawable.blood_6,
            R.drawable.blood_7, R.drawable.blood_8, R.drawable.blood_9,
            R.drawable.blood_10
        )

    }
    val fireIds = remember {
        listOf(
            R.drawable.fire_0, R.drawable.fire_1, R.drawable.fire_2,
            R.drawable.fire_3, R.drawable.fire_4, R.drawable.fire_5,
            R.drawable.fire_6, R.drawable.fire_7, R.drawable.fire_8,
            R.drawable.fire_9, R.drawable.fire_10,
            R.drawable.fire_9, R.drawable.fire_8, R.drawable.fire_7,
            R.drawable.fire_6, R.drawable.fire_5, R.drawable.fire_4,
            R.drawable.fire_3, R.drawable.fire_2, R.drawable.fire_1
        )
    }
    val bloodAnimation = remember {
        loadListOfFrames(bloodIds)
    }
    val fireAnimation = remember { loadListOfFrames(fireIds) }

    val back = ImageBitmap.imageResource(R.drawable.map)
    val cartridges = ImageBitmap.imageResource(R.drawable.cartridges)
    val pellets = ImageBitmap.imageResource(R.drawable.pellets)
    val mapSize = IntSize(2000, 2500)
    val hurt_frame = ImageBitmap.imageResource(R.drawable.hurt_frame)
    val axe_image = ImageBitmap.imageResource(R.drawable.axe)
    val axe_image_mirror = ImageBitmap.imageResource(R.drawable.axe_mirror)
    val axe_low_effect = ImageBitmap.imageResource(R.drawable.axe_low_effect)
    val axe_low_effect_mirror = ImageBitmap.imageResource(R.drawable.axe_low_effect_mirror)
    val axe_strong_effect_mirror = ImageBitmap.imageResource(R.drawable.axe_strong_effect_mirror)
    val axe_strong_effect = ImageBitmap.imageResource(R.drawable.axe_strong_effect)
    val revolver_left = ImageBitmap.imageResource(R.drawable.revolver_left)
    val revolver_left_1 = ImageBitmap.imageResource(R.drawable.revolver_left_1)
    val revolver_right = ImageBitmap.imageResource(R.drawable.revolver_right)
    val revolver_right_1 = ImageBitmap.imageResource(R.drawable.revolver_right_1)
    val revolverRightAnimation = listOf(revolver_right, revolver_right_1)
    val revolverLeftAnimation = listOf(revolver_left, revolver_left_1)
    val shotgun_left = ImageBitmap.imageResource(R.drawable.shotgun_left)
    val shotgun_right = ImageBitmap.imageResource(R.drawable.shotgun_right)
    val scope = rememberCoroutineScope()
    val enemiesList = remember { EnemiesList(scope) }
    val killmarkController = remember { KillmarkController(scope) }
    val firePoint = remember {
        FirePoint(
            fireAnimation = fireAnimation,
            position = Offset(mapSize.width / 2f, mapSize.height / 2f)
        )
    }
    val player = remember {
        Player(
            name = "player",
            view = playerBack,
            viewLeft = playerLeft,
            viewBack = playerBack,
            viewRight = playerRight,
            viewFront = playerFront,
            animation = Animation(
                back = playerBackAnimation,
                left = playerLeftAnimation,
                right = playerRightAnimation,
                front = playerFrontAnimation
            ),
            startPosition = Offset(mapSize.width / 2f, mapSize.height / 2f) - Offset(
                playerBack.width / 2f,
                playerBack.height / 2f
            )
        )
    }

    val axe = remember {
        Axe(
            scope = scope,
            axe = axe_image,
            axe_low_effect = axe_low_effect,
            axe_strong_effect = axe_strong_effect,
            axe_mirror = axe_image_mirror,
            axe_low_effect_mirror = axe_low_effect_mirror,
            axe_strong_effect_mirror = axe_strong_effect_mirror
        )
    }
    val revolver = remember {
        Revolver(
            scope = scope,
            viewRight = revolver_right,
            viewRightAnimation = revolverRightAnimation,
            viewLeftAnimation = revolverLeftAnimation,
        )
    }

    val shotgun = remember {
        Shotgun(
            scope = scope,
            viewLeft = shotgun_left,
            viewRight = shotgun_right
        )
    }

    var weapon: Weapon by remember { mutableStateOf(axe) }
    var position_index = remember { 0 }
    var scrollX by remember { mutableStateOf(0f) }
    var scrollY by remember { mutableStateOf(0f) }
    val right_wall = (mapSize.width - player.viewBack.width).toFloat()
    val bottom_wall = (mapSize.height - player.viewBack.height).toFloat()
    val left_wall = 0f
    val top_wall = 0f
    val mapBox = RectF(-100f, -100f, mapSize.width + 100f, mapSize.height + 100f)
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }
    val canvasWidth = canvasSize.width.toFloat()
    val canvasHeight = canvasSize.height.toFloat()
    var zoom by remember { mutableStateOf(1f) }
    val left = canvasWidth / 2f / zoom
    val right = (canvasWidth - canvasWidth / 2f) / zoom
    val top = canvasHeight / 2f / zoom
    val bottom = (canvasHeight - canvasHeight / 2f) / zoom

    val snackbarHostState = remember { SnackbarHostState() }
    fun checkScroll() {
        val playerOnScreenX = player.position.x + scrollX
        val playerOnScreenY = player.position.y + scrollY
        if (playerOnScreenX < left) {
            scrollX += (left - playerOnScreenX)
        } else if (playerOnScreenX > right) {
            scrollX += (right - playerOnScreenX)
        }
        if (playerOnScreenY < top) {
            scrollY += (top - playerOnScreenY)
        } else if(playerOnScreenY > bottom) {
            scrollY += (bottom - playerOnScreenY)
        }
        scrollX = max(scrollX, canvasWidth / zoom - mapSize.width)
        scrollY = max(scrollY, canvasHeight / zoom - mapSize.height)
        scrollX = min(scrollX, 0f)
        scrollY = min(scrollY, 0f)
//        println(canvasWidth)
    }

    val ChangeZoom: (Float) -> Unit = { newZoom: Float ->
        if (canvasWidth / newZoom <= mapSize.width && canvasHeight / newZoom <= mapSize.height) {
            zoom = newZoom
            checkScroll()
        } else {
            if (snackbarHostState.currentSnackbarData == null) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "⚠️ Выход за границы",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    val dinamicObjects: MutableList<DinamicObject> = remember {
        run {
            val list = List<DinamicObject>(10) { index ->
                BoxObject(
                    view = box_,
                    startPosition = Offset(
                        (0..mapSize.width).random().toFloat(),
                        (0..mapSize.height).random().toFloat()
                    )
                )
            }.toMutableList()
            for (i in list.indices.reversed()) {
                if (list[i].collisionRect.overlaps(player.collisionRect) || list[i].collisionRect.overlaps(
                        firePoint.collisionRect
                    )
                ) list.removeAt(i)
            }
            list
        }
    }
    val listOfDrawableWithZ = remember { ArrayList<DrawableWithZ>() }
//        derivedStateOf {
//            buildList<DrawableWithZ> {
//                addAll(enemiesList.enemiesSnapshot)
//                addAll(enemiesList.bodiesSnapshot)
//                addAll(dinamicObjects)
//                add(player)
//                if (isLevelCompleted) add(firePoint)
//                sortBy { it.indexZ }
//            }
//        }
//    }
    val crowIds = remember {
        listOf(
            R.drawable.crow_0, R.drawable.crow_1, R.drawable.crow_2,
            R.drawable.crow_3, R.drawable.crow_4, R.drawable.crow_5,
            R.drawable.crow_6, R.drawable.crow_7, R.drawable.crow_8,
            R.drawable.crow_7, R.drawable.crow_6, R.drawable.crow_5,
            R.drawable.crow_4, R.drawable.crow_3, R.drawable.crow_2, R.drawable.crow_1
        )
    }
    val crowMirrorIds = remember {
        listOf(
            R.drawable.crow_mirror_0, R.drawable.crow_mirror_1, R.drawable.crow_mirror_2,
            R.drawable.crow_mirror_3, R.drawable.crow_mirror_4, R.drawable.crow_mirror_5,
            R.drawable.crow_mirror_6, R.drawable.crow_mirror_7, R.drawable.crow_mirror_8,
            R.drawable.crow_mirror_7, R.drawable.crow_mirror_6, R.drawable.crow_mirror_5,
            R.drawable.crow_mirror_4, R.drawable.crow_mirror_3, R.drawable.crow_mirror_2,
            R.drawable.crow_mirror_1
        )
    }
    val smokeIds = remember {
        listOf(
            R.drawable.smoke,
            R.drawable.smoke_1, R.drawable.smoke_2, R.drawable.smoke_3, R.drawable.smoke_4,
            R.drawable.smoke_3, R.drawable.smoke_2, R.drawable.smoke_1
        )
    }

    val crowAnimation = remember { loadListOfFrames(crowIds) }
    val crowMirrorAnimation = remember { loadListOfFrames(crowMirrorIds) }
    val smokeAnimation = remember { loadListOfFrames(smokeIds) }

    val totem = ImageBitmap.imageResource(R.drawable.totem)
    val spawnIds = remember {
        listOf(
            R.drawable.spawnanim_0, R.drawable.spawnanim_1, R.drawable.spawnanim_2,
            R.drawable.spawnanim_3, R.drawable.spawnanim_4, R.drawable.spawnanim_5,
            R.drawable.spawnanim_6
        )
    }
    val spawnEffect = remember { loadListOfFrames(spawnIds) }


    var kills by remember { mutableStateOf(0) }
    var timePause by remember { mutableStateOf(false) }
    var level by remember { mutableStateOf(1) }

//    val levelStates = when (level) {
//        1 -> LevelStates(1000L, 20, 0, 0, 0, (5..10), (0..1), (0..1), (5..10))
//        2 -> LevelStates(700L, 40, 1, 0, 0, (10..15), (10..15), (0..1))
//        3 -> LevelStates(500L, 25, 10, 0, 0, (10..15), (13..20), (0..1))
//        4 -> LevelStates(500L, 40, 5, 1, 0, (10..15), (13..20), (90..100))
//        5 -> LevelStates(300L, 1, 20, 20, 0, (5..10), (13..20), (30..50))
//        6 -> LevelStates(300L, 20, 2, 5, 0, (15..20), (13..20), (20..30))
//        else -> LevelStates(200L, 30, 5, 0, 0, (15..20), (13..20), (0..1))
//    }
//    val levelStates = when (level) {
//        // 1: Вступление. Скорость 300-500 пикс/сек.
//        1 -> LevelStates(0L, 15, 0, 0, 0, (300..500))
//
//        // 2: Плотность выше.
//        2 -> LevelStates(1000L, 20, 1, 0, 0, (300..550), (400..600))
//
//        // 3: Первая ворона. Она быстрее пугала.
//        3 -> LevelStates(800L, 15, 5, 0, 0, (400..600), (700..900))
//
//        // 4: Толпа пугал. Отдыхаем от ворон, но темп выше.
//        4 -> LevelStates(0L, 20, 0, 0, 0, (450..650))
//
//        // 5: Теневой заслон. Дым (Smoke) летает ОЧЕНЬ быстро.
//        5 -> LevelStates(800L, 35, 0, 1, 0, (500..700), (0..0), (1200..1500))
//
//        // 6: Гнездо и дым.
//        6 -> LevelStates(1000L, 15, 0, 15, 0, (550..750), (0..0), (600..900))
//
//        // 7: Спаунер вносит суету.
//        7 -> LevelStates(900L, 10, 5, 5, 1, (600..800), (800..1000), (600..850), (700..900))
//
//        // 8: Охота. Вороны повсюду.
//        8 -> LevelStates(1200L, 5, 15, 0, 0, (650..850), (900..1100))
//
//        // 9: Осада. Высокий темп и дым.
//        9 -> LevelStates(800L, 25, 0, 20, 3, (700..900), (0..0), (800..1000), (850..1050))
//
//        // 10: Финальный темп. Почти предел реакции.
//        10 -> LevelStates(700L, 30, 10, 10, 1, (750..950), (1000..1200), (900..1100), (950..1150))
//
//        // 11: Испытание. Максимальная сложность.
//        11 -> LevelStates(750L, 40, 20, 20, 2, (800..1000), (1100..1300), (1000..1200), (1050..1250))
//
//        else -> LevelStates(600L, 50, 30, 30, 3, (850..1050), (1150..1350), (1100..1300), (1100..1300))
//    }
    val levelStates = when (level) {
        // 1: Вступление. Скорость 300-500 пикс/сек.
        1 -> LevelStates(10L, 350, 0, 0, 0, (300..500))

        // 2: Плотность выше.
        2 -> LevelStates(100L, 100, 1, 0, 0, (300..550), (400..600))

        // 3: Первая ворона. Она быстрее пугала.
        3 -> LevelStates(800L, 15, 5, 0, 0, (400..600), (700..900))

        // 4: Толпа пугал. Отдыхаем от ворон, но темп выше.
        4 -> LevelStates(0L, 20, 0, 0, 0, (450..650))

        // 5: Теневой заслон. Дым (Smoke) летает ОЧЕНЬ быстро.
        5 -> LevelStates(300L, 350, 0, 1, 0, (500..700), (0..0), (1200..1500))

        // 6: Гнездо и дым.
        6 -> LevelStates(1000L, 15, 0, 15, 0, (550..750), (0..0), (600..900))

        // 7: Спаунер вносит суету.
        7 -> LevelStates(900L, 10, 5, 5, 1, (600..800), (800..1000), (600..850), (700..900))

        // 8: Охота. Вороны повсюду.
        8 -> LevelStates(1200L, 5, 15, 0, 0, (650..850), (900..1100))

        // 9: Осада. Высокий темп и дым.
        9 -> LevelStates(800L, 25, 0, 20, 3, (700..900), (0..0), (800..1000), (850..1050))

        // 10: Финальный темп. Почти предел реакции.
        10 -> LevelStates(700L, 30, 10, 10, 1, (750..950), (1000..1200), (900..1100), (950..1150))

        // 11: Испытание. Максимальная сложность.
        11 -> LevelStates(750L, 40, 20, 20, 2, (800..1000), (1100..1300), (1000..1200), (1050..1250))

        else -> LevelStates(600L, 50, 30, 30, 3, (850..1050), (1150..1350), (1100..1300), (1100..1300))
    }

    var showSettings by remember { mutableStateOf(false) }
    var trigger by remember { mutableStateOf(0) }
    var trigger2 by remember { mutableStateOf(0) }
    fun onHitBody(body: Body, angle: Float, power: Int, weaponPower: Float? = null) {
        body.addBias(angle = angle, power = power)
        enemiesList.addBlood(
            Blood(
                power = power,
                view = null,
                position = body.position + Offset(0f, -body.dstSize.height.toFloat()),
                pivot = body.center,
                angle = angle
            )
        )
    }


    LaunchedEffect(player.trend) {
        if (player.trend != PlayerTrend.Stop) {
            when (player.trend) {
                PlayerTrend.Left -> {
                    while (true) {
                        TimeManager.delay(100L)
                        position_index = (position_index + 1) % player.animation.left.size
                        player.view = player.animation.left[position_index]
                    }
                }

                PlayerTrend.Rigth -> {
                    while (true) {
                        TimeManager.delay(100L)
                        position_index = (position_index + 1) % player.animation.right.size
                        player.view = player.animation.right[position_index]
                    }
                }

                PlayerTrend.Front -> {
                    while (true) {
                        TimeManager.delay(120L)
                        position_index = (position_index + 1) % player.animation.front.size
                        player.view = player.animation.front[position_index]
                    }
                }

                else -> {
                    while (true) {
                        TimeManager.delay(120L)
                        position_index = (position_index + 1) % player.animation.back.size
                        player.view = player.animation.back[position_index]
                    }
                }
            }
        }
    }
//
//    fun movePlayer(delta: Offset) {
//        if (dinamicObjects
//                .filterIsInstance<BoxObject>()
//                .all {
//                    checkNotCollision(
//                        player.getNextRect(delta),
//                        it.collisionRect
//                    )
//                }
//        ) {
//            val newPlayerPosition = player.position + delta
//            val x = newPlayerPosition.x.coerceIn(left_wall, right_wall)
//            val y = newPlayerPosition.y.coerceIn(top_wall, bottom_wall)
//            player.position = Offset(x, y)
//            checkScroll()
//        }
//    }
    LaunchedEffect(player.trend) {
        while (player.trend != PlayerTrend.Stop) {
            TimeManager.delay(35L) // было 50
            checkScroll()
        }
    }

    LaunchedEffect(level) {
        if (mode == 0) {
            player.hitPoint = 100
            launch {
                for (i in 0 until levelStates.enemyScarecrowCounts) {
                    TimeManager.delay(levelStates.spawnTime)
                    enemiesList.add(
                        enemy =
                        Scarecrow(
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = levelStates.speedScarecrowRange.random().toFloat(),
                            frontAnimation = scarecrow
                        )
                    )
                }
            }
            launch {
                for (i in 0 until levelStates.enemyCrowCounts) {
                    TimeManager.delay(levelStates.spawnTime)
                    enemiesList.add(
                        enemy =
                        Crow(
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = levelStates.speedCrowRange.random().toFloat(),
                            frontAnimation = crowAnimation,
                            frontAnimationMirrored = crowMirrorAnimation,
                            fixedPlayerPosition = player.center
                        )
                    )
                }
            }
            launch {
                for (i in 0 until levelStates.enemySpawnerCounts) {
                    TimeManager.delay(levelStates.spawnTime)
                    enemiesList.add(
                        enemy =
                        Spawner(
                            view = totem,
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            action = { self ->
                                enemiesList.add(
                                    enemy = CrowMinion(
                                        spawnAnimation = spawnEffect,
                                        startPosition = self.center,
                                        speed = levelStates.speedMinionRange.random().toFloat(),
                                        frontAnimation = crowAnimation,
                                        frontAnimationMirrored = crowMirrorAnimation,
                                    )
                                )
                            }
                        )
                    )
                }
            }
            launch {
                for (i in 0 until levelStates.enemySmokeCounts) {
                    TimeManager.delay(levelStates.spawnTime)
                    enemiesList.add(
                        enemy = Smoke(
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = levelStates.speedSmokeRange.random().toFloat(),
                            frontAnimation = smokeAnimation
                        )
                    )
                }
            }
        }
        if (mode == 1) {
            val speedRange = (600..900)
            while (true) {
                val random = Random.nextFloat()
                TimeManager.delay(1000L)
                if (random < 0.8f) {
                    enemiesList.add(
                        enemy =
                        Scarecrow(
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = speedRange.random().toFloat(),
                            frontAnimation = scarecrow
                        )
                    )
                }
                if (random < 0.4f) {
                    enemiesList.add(
                        enemy =
                        Crow(
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = speedRange.random().toFloat(),
                            frontAnimation = crowAnimation,
                            frontAnimationMirrored = crowMirrorAnimation,
                            fixedPlayerPosition = player.center
                        )
                    )
                }
                if (random < 0.3f) {
                    enemiesList.add(
                        enemy = Smoke(
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = speedRange.random().toFloat(),
                            frontAnimation = smokeAnimation
                        )
                    )
                }
                if (random < 0.1f) {
                    enemiesList.add(
                        enemy =
                        Spawner(
                            view = totem,
                            spawnAnimation = spawnEffect,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            action = { self ->
                                enemiesList.add(
                                    enemy = CrowMinion(
                                        spawnAnimation = spawnEffect,
                                        startPosition = self.center,
                                        speed = speedRange.random().toFloat(),
                                        frontAnimation = crowAnimation,
                                        frontAnimationMirrored = crowMirrorAnimation,
                                    )
                                )
                            }
                        )
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        var lastNanosTime: Long = 0L
        while (true) {
            TimeManager.isTimePaused.first { isPaused -> !isPaused }
            withFrameNanos { currentTime ->
                if (canvasSize == IntSize.Zero) return@withFrameNanos
                if (!TimeManager.isTimePaused.value) {
                    if (lastNanosTime == 0L) {
                        lastNanosTime = currentTime
                        return@withFrameNanos
                    }
                    val deltaTime = (currentTime - lastNanosTime) / 1_000_000f * TimeManager.timeScale.value
                    lastNanosTime = currentTime
                    player.update(deltaTime,dinamicObjects.filterIsInstance<BoxObject>())
                    player.position = player.position.copy(x = player.position.x.coerceIn(left_wall, right_wall))
                    player.position = player.position.copy(y = player.position.y.coerceIn(top_wall, bottom_wall))
//                    checkScroll()
                    for (ammunition in dinamicObjects.filterIsInstance<Ammunition>()) {
                        if (!checkNotCollision(player.collisionRect, ammunition.collisionRect)) {
                            when (ammunition.type) {
                                AmmunitionType.Cartridges -> {
                                    AudioManager.play(AudioManager.SoundType.CARTRIDGES_CRIBE)
                                    revolver.clip += ammunition.value
                                }

                                AmmunitionType.Pellets -> {
                                    AudioManager.play(AudioManager.SoundType.PELLETS_CRIBE)
                                    shotgun.clip += ammunition.value
                                }
                            }
                            dinamicObjects.remove(ammunition)
                        }
                    }
                    player.checkDeath(DeathPlayer, enemiesList.enemies, kills)
                    for (i in enemiesList.enemies.indices.reversed()) {
                        val enemy = enemiesList.enemies[i]
                        enemy.update(deltaTime, player.position)
                        if (!mapBox.contains(enemy.position.x, enemy.position.y)) {
                            enemiesList.removeEnemy(enemy)
                            kills += enemy.killWeight
                        }
                    }
                    for (i in enemiesList.bodies.indices) {
                        enemiesList.bodies[i].update()
                    }
                    listOfDrawableWithZ.apply {
                        clear()
                        addAll(enemiesList.enemies)
                        addAll(enemiesList.bodies)
                        addAll(dinamicObjects)
                        add(player)
                        if (isLevelCompleted) add(firePoint)
                        sortBy { it.indexZ }
                    }
                } else {
                    lastNanosTime = 0L
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            TimeManager.delay(10L)
            for (blood in enemiesList.bloods) {
                if (blood.index != blood.power - 1) {
                    blood.index = (blood.index + 1) % blood.power
                    blood.view = bloodAnimation[blood.index]
                }
            }
        }
    }

    LaunchedEffect(isLevelCompleted) {
        if (isLevelCompleted) {
            while (true) {
                TimeManager.delay(150L)
                firePoint.index = (firePoint.index + 1) % firePoint.fireAnimation.size
                if (calcDistanceForСomparison(
                        player.center,
                        firePoint.center
                    ) <= 50f * 50f
                ) {
                    isLevelCompleted = false
                    level++
                    killmarkController.startAnimation(Killmark(text = "Уровень $level"))
                }
            }
        }
    }
    if (mode == 0) {
        LaunchedEffect(kills) {
            if (kills == levelStates.killableCount) {
                AudioManager.play(AudioManager.SoundType.HOLY_MOMENT)
                kills = 0
                if (level == 12) WinPlayer() else isLevelCompleted = true
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
    fun Revolver.doFire(offset: Offset, isHoly: Boolean) {
        val position = offset / zoom - Offset(scrollX, scrollY)
        this.angle = calcAngle(position, player.center)
        var killCount = 0
        this.fire(
            onSuccessAction = { trigger++ },
            position = position,
            player = player,
            enemies = enemiesList.enemies,
            bodies = enemiesList.bodies,
            holyMode = isHoly,
            onHitEnemy = { enemy ->
                if (enemy.killable) {
                    enemiesList.killEnemy(enemy)
                    kills += enemy.killWeight
                    killCount++
                    this.checkSeries(
                        createKillmark = {
                            val newKillmark = Killmark(
                                view = revolver_killmark,
                                text = "$it подряд"
                            )
                            killmarkController.startAnimation(newKillmark)
                        }
                    )

                    if (Random.nextFloat() < 0.1f) {
                        val ammunition = Ammunition(
                            value = 5,
                            type = AmmunitionType.Pellets,
                            view = pellets,
                            startPosition = enemy.center
                        )
                        dinamicObjects.add(ammunition)
                    }
                } else {
                    scope.launch { enemy.onHitEffect() }
                }
            },
            onHitBody = ::onHitBody
        )
        onKill(killCount)
    }

    fun Shotgun.doFire(position: Offset, powerWeapon: Float = 30f) {
        this.angle = calcAngle(position, player.center)
        this.animateShotgun(position)
        var killCount = 0
        this.fire(
            player = player,
            enemies = enemiesList.enemies,
            bodies = enemiesList.bodies,
            onHitEnemy = { enemy ->
                if (enemy.killable) {
                    enemiesList.killEnemy(enemy)
                    kills += enemy.killWeight
                    killCount++
                    if (Random.nextFloat() < 0.1f) {
                        val ammunition = Ammunition(
                            value = 12,
                            type = AmmunitionType.Cartridges,
                            view = cartridges,
                            startPosition = enemy.center
                        )
                        dinamicObjects.add(ammunition)
                    }
                } else {
                    scope.launch { enemy.onHitEffect() }
                }
            },
            onHitBody = { body, angle, power ->
                body.addBias(
                    angle = angle,
                    power = power,
                    powerWeapon = powerWeapon
                )
                enemiesList.addBlood(
                    Blood(
                        power = power,
                        view = null,
                        position = body.position + Offset(
                            0f,
                            -body.dstSize.height.toFloat()
                        ),
                        pivot = body.center,
                        angle = angle
                    )
                )
            }
        )
        onKill(killCount)
        if (killCount > 1) {
            val newKillmark = Killmark(
                view = killmark,
                text = "X$killCount",
            )
            killmarkController.startAnimation(newKillmark)
        }
    }

    val tapModifier = remember(weapon) {
        when (weapon) {
            is Revolver ->
                Modifier
                    .fillMaxSize()
                    .pointerInput(weapon) {
                        detectTapGestures(
                            onTap = { offset ->
                                (weapon as? Revolver)?.let { it.doFire(offset, isHoly = false) }
                            },
                            onLongPress = { offset ->
                                (weapon as? Revolver)?.let {
                                    it.doFire(offset, isHoly = true)
                                }
                            }
                        )
                    }

            is Shotgun ->
                Modifier
                    .fillMaxSize()
                    .pointerInput(weapon) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                (weapon as? Shotgun)?.let { shotgun ->
                                    shotgun.targetMagnet = offset / zoom - Offset(scrollX, scrollY)
                                    shotgun.startAction(
                                        enemies = enemiesList.enemies,
                                        bodies = enemiesList.bodies,
                                        onHitEnemy = { enemy, offset ->
                                            enemy.position += offset
                                        },
                                        onHitBody = { body, offset ->
                                            body.position += offset
                                        }
                                    )
                                }
                            },
                            onDrag = { change, dragAmount ->
                                (weapon as? Shotgun)?.let { shotgun ->
                                    shotgun.targetMagnet =
                                        change.position / zoom - Offset(scrollX, scrollY)
                                }
                            },
                            onDragEnd = {
                                (weapon as? Shotgun)?.let { shotgun ->
                                    shotgun.stopAction()
                                    shotgun.doFire(
                                        position = shotgun.targetMagnet ?: Offset.Zero,
                                        powerWeapon = 50f
                                    )
                                    shotgun.targetMagnet = null
                                }
                            },
                            onDragCancel = {
                                (weapon as? Shotgun)?.let { shotgun ->
                                    shotgun.stopAction()
                                    shotgun.targetMagnet = null
                                }
                            }
                        )
                    }
                    .pointerInput(weapon) {
                        detectTapGestures(
                            onTap = { offset ->
                                (weapon as? Shotgun)?.let { shotgun ->
                                    shotgun.doFire(
                                        position = offset / zoom - Offset(scrollX, scrollY)
                                    )
                                }
                            }
                        )
                    }

            is Axe -> Modifier
                .fillMaxSize()
                .pointerInput(weapon) {
//                    var startTime = 0L
                    detectDragGestures(
                        onDragStart = { offset ->
                            (weapon as? Axe)?.let { axe ->
                                axe.startSlice()
                            }
//                            startTime = System.currentTimeMillis()
                        },
                        onDrag = { change, dragAmount ->
                            if (dragAmount.getDistance() > 5f) {
                                (weapon as? Axe)?.let { axe ->
                                    if (axe.holyModeProgress >= axe.minHolyModeUsable) {
                                        axe.slicePoints.add(
                                            change.position / zoom - Offset(
                                                scrollX,
                                                scrollY
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            (weapon as? Axe)?.let { axe ->
//                                if(System.currentTimeMillis() - startTime > 250L) {
                                    axe.slice(
                                        onSuccessAction = { trigger2++ },
                                        bodies = enemiesList.bodies,
                                        enemies = enemiesList.enemies,
                                        onHitEnemy = { enemy ->
                                            if (enemy.killable) {
                                                AudioManager.play(AudioManager.SoundType.AXE_HIT)
                                                enemiesList.killEnemy(enemy)
                                                kills += enemy.killWeight
                                                axe.onKill(1)
                                                if (Random.nextFloat() < 0.3f) {
                                                    val newKillmark = Killmark(
                                                        view = axe_killmark,
                                                    )
                                                    killmarkController.startAnimation(newKillmark)
                                                    val ammunition =
                                                        when (Random.nextFloat() > 0.5f) {
                                                            true -> Ammunition(
                                                                value = 12,
                                                                type = AmmunitionType.Cartridges,
                                                                view = cartridges,
                                                                startPosition = enemy.center
                                                            )

                                                            false -> Ammunition(
                                                                value = 5,
                                                                type = AmmunitionType.Pellets,
                                                                view = pellets,
                                                                startPosition = enemy.center
                                                            )
                                                        }
                                                    dinamicObjects.add(ammunition)
                                                }
                                            } else scope.launch { enemy.onHitEffect() }
                                        },
                                        onHitBody = ::onHitBody
                                    )
//                                }
                                axe.speed = 30f
                                axe.rotationAxe(
                                    isHold = false,
                                    player = player,
                                    enemies = enemiesList.enemies,
                                    bodies = enemiesList.bodies,
                                    onHitEnemy = { enemy ->
                                        if (enemy.killable) {
                                            AudioManager.play(AudioManager.SoundType.AXE_HIT)
                                            enemiesList.killEnemy(enemy)
                                            kills += enemy.killWeight
                                            axe.onKill(1)
                                            if (Random.nextFloat() < 0.3f) {
                                                val newKillmark = Killmark(
                                                    view = axe_killmark,
                                                )
                                                killmarkController.startAnimation(newKillmark)
                                                val ammunition = when (axe.trend) {
                                                    true -> Ammunition(
                                                        value = 12,
                                                        type = AmmunitionType.Cartridges,
                                                        view = cartridges,
                                                        startPosition = enemy.center
                                                    )

                                                    false -> Ammunition(
                                                        value = 5,
                                                        type = AmmunitionType.Pellets,
                                                        view = pellets,
                                                        startPosition = enemy.center
                                                    )
                                                }
                                                dinamicObjects.add(ammunition)
                                            }
                                        } else scope.launch { enemy.onHitEffect() }
                                    },
                                    onHitBody = ::onHitBody,
                                )
                            }
                        },
                        onDragCancel = {
                            axe.slicePoints.clear()
                        }
                    )
                }
        }
    }
    CompositionLocalProvider(LocalShakeTrigger provides trigger) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    canvasSize = size
                }
        ) {
            Box(
                modifier = tapModifier
                    .drawWithContent {
                        drawContent()
                        if (player.hitPoint <= 50) {
                            drawImage(
                                image = hurt_frame,
                                dstSize = IntSize(size.width.toInt(), size.height.toInt())
                            )
                        }
                        drawKillmark(killmarkController, textMeasurer)
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = zoom
                            scaleY = zoom
                            transformOrigin = TransformOrigin(0f, 0f)
                            translationX = scrollX * zoom
                            translationY = scrollY * zoom
                        }
                        .jiggle(trigger2)
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
                                    topLeft = Offset(-scrollX, -scrollY),
                                    size = size,
                                )
                            }
                        }
                ) {
                    drawImage(
                        dstSize = mapSize,
                        image = back,
                    )
                    drawBodies(enemiesList.bloods)
                    drawWithZ(listOfDrawableWithZ)
                    rotate(weapon.angle, pivot = player.center) {
                        with(weapon) {
                            draw(player)
                        }
                    }
                    with(weapon) {
                        drawEffects()
                    }
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
                            weapon = when (index) {
                                1 -> revolver
                                2 -> shotgun
                                else -> axe
                            }
                        },
                        weapon = weapon
                    )
                    Joystick(
                        modifier = Modifier.shaking(),
                        StartTrendAnimation = { position ->
                            position_index = 0
                            player.delta = position / position.getDistance() * 10f
                            if (abs(position.x) >= abs(position.y)) {
                                if (position.x > 0) {
                                    player.updateTrend(PlayerTrend.Rigth)
                                } else {
                                    player.updateTrend(PlayerTrend.Left)
                                }
                            } else {
                                if (position.y < 0) {
                                    player.updateTrend(PlayerTrend.Back)
                                } else {
                                    player.updateTrend(PlayerTrend.Front)
                                }
                            }
                        },
                        SetMovePosition = { position ->
                            if (abs(position.x) >= abs(position.y)) {
                                if (position.x > 0) {
                                    player.view = player.viewRight
                                } else {
                                    player.view = player.viewLeft
                                }
                            } else {
                                if (position.y < 0) {
                                    player.view = player.viewBack
                                } else {
                                    player.view = player.viewFront
                                }
                            }
                        },
                        StopTrendAnimation = {
                            player.updateTrend(PlayerTrend.Stop)
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
                                axe.rotationAxe(
                                    isHold = _isHold,
                                    player = player,
                                    enemies = enemiesList.enemies,
                                    bodies = enemiesList.bodies,
                                    onHitEnemy = { enemy ->
                                        if (enemy.killable) {
                                            AudioManager.play(AudioManager.SoundType.AXE_HIT)
                                            enemiesList.killEnemy(enemy)
                                            kills += enemy.killWeight
                                            axe.onKill(1)
                                            if (Random.nextFloat() < 0.3f) {
                                                val newKillmark = Killmark(
                                                    view = axe_killmark,
                                                )
                                                killmarkController.startAnimation(newKillmark)
                                                val ammunition = when (axe.trend) {
                                                    true -> Ammunition(
                                                        value = 12,
                                                        type = AmmunitionType.Cartridges,
                                                        view = cartridges,
                                                        startPosition = enemy.center
                                                    )

                                                    false -> Ammunition(
                                                        value = 5,
                                                        type = AmmunitionType.Pellets,
                                                        view = pellets,
                                                        startPosition = enemy.center
                                                    )
                                                }
                                                dinamicObjects.add(ammunition)
                                            }
                                        } else scope.launch { enemy.onHitEffect() }
                                    },
                                    onHitBody = ::onHitBody,
                                )
                            },
                            axe = axe
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
                    zoom = zoom,
                    ChangeZoom = ChangeZoom,
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

fun DrawScope.drawWithZ(listOfDrawableWithZ: List<DrawableWithZ>) {
    for (i in listOfDrawableWithZ.indices) {
        with(listOfDrawableWithZ[i]) {
            draw()
        }
    }
}

fun DrawScope.drawKillmark(killmarkController: KillmarkController, textMeasurer: TextMeasurer) {
    killmarkController.killmark?.let { killmark ->
        val killmark_position = IntOffset(
            x = (size.width.toInt() - killmark.dstSize.width) / 2,
            y = (size.height.toInt() * 2 / 3 - killmark.dstSize.height / 2)
        )
        with(killmark) {
            draw(
                textMeasurer,
                killmark_position,
                killmarkController.alpha.value,
                killmarkController.scale.value
            )
        }
    }
}

fun DrawScope.drawBodies(bodies: List<Blood>) {
    for (body in bodies) {
        body.view?.let {
            rotate(body.angle, body.pivot) {
                drawImage(
                    topLeft = body.position,
                    image = it,
//                    colorFilter = ColorFilter.tint(Color(0x54FF5722))
                )
            }
        }
    }
}

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    StartTrendAnimation: (Offset) -> Unit,
    SetMovePosition: (Offset) -> Unit,
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
                            SetMovePosition(position)
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
    weapon: Weapon
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
                bitmap = weapon.present_view,
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


    //            .background(
//            brush = Brush.linearGradient(
//                colors = listOf(colorResource(R.color.back_content),colorResource(R.color.back_content),Color(
//                    0x994D4949
//                )
//                )
//            ),
//                shape = buttonShape
//        )
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
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp,
            pressedElevation = 1.dp,
            disabledElevation = 0.dp
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
