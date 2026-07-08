package com.example.scrolllist.ui

import android.app.Application
import android.graphics.RectF
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.AndroidViewModel
import com.example.scrolllist.AudioManager
import com.example.scrolllist.LevelStates
import com.example.scrolllist.TimeManager
import com.example.scrolllist.domain.badBlazingRemove
import com.example.scrolllist.domain.calcAngle
import com.example.scrolllist.domain.calcDistanceForComparison
import com.example.scrolllist.domain.checkNotCollision
import com.example.scrolllist.domain.objects.Ammunition
import com.example.scrolllist.domain.objects.AmmunitionType
import com.example.scrolllist.domain.objects.BoxObject
import com.example.scrolllist.domain.objects.FirePoint
import com.example.scrolllist.domain.objects.Killmark
import com.example.scrolllist.domain.objects.KillmarkController
import com.example.scrolllist.domain.units.Player
import com.example.scrolllist.domain.units.enemy.Blood
import com.example.scrolllist.domain.units.enemy.Body
import com.example.scrolllist.domain.units.enemy.Crow
import com.example.scrolllist.domain.units.enemy.CrowMinion
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.game.GameEngine
import com.example.scrolllist.domain.units.enemy.Scarecrow
import com.example.scrolllist.domain.units.enemy.Smoke
import com.example.scrolllist.domain.units.enemy.Spawner
import com.example.scrolllist.domain.units.weapon.Axe
import com.example.scrolllist.domain.units.weapon.Revolver
import com.example.scrolllist.domain.units.weapon.Shotgun
import com.example.scrolllist.domain.units.weapon.Weapon
import com.example.scrolllist.ui.assets.GameAssets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

class GameViewModel(
    application: Application,
    val DeathPlayer: (Int) -> Unit,
    val WinPlayer: () -> Unit,
    var canvasWidth: Float,
    var canvasHeight: Float,
    var mode: Int,
) : AndroidViewModel(application) {

//    var zoom by mutableStateOf(1f)
////    var zoom = 1f
//        private set
//    var isLevelCompleted by mutableStateOf(false)
//        private set
//    var scrollX by mutableStateOf(0f)
////    var scrollX = 0f
//        private set
//    var scrollY by mutableStateOf(0f)
////    var scrollY = 0f
//        private set
//    var level by mutableStateOf(1)
//        private set
//    var trigger by mutableStateOf(0)
//    var trigger2 by mutableStateOf(0)
//    val levelStates: LevelStates
//        get() = when (level) {
//            // 1: Вступление. Скорость 300-500 пикс/сек.
//            1 -> LevelStates(400L, 15, 0, 0, 0, (300..500))
//
//            // 2: Плотность выше.
//            2 -> LevelStates(100L, 30, 1, 0, 0, (300..550), (400..600))
//
//            // 3: Первая ворона. Она быстрее пугала.
//            3 -> LevelStates(800L, 15, 5, 0, 0, (400..600), (700..900))
//
//            // 4: Толпа пугал. Отдыхаем от ворон, но темп выше.
//            4 -> LevelStates(0L, 20, 0, 0, 0, (450..650))
//
//            // 5: Теневой заслон. Дым (Smoke) летает ОЧЕНЬ быстро.
//            5 -> LevelStates(300L, 350, 0, 1, 0, (500..700), (0..0), (1200..1500))
//
//            // 6: Гнездо и дым.
//            6 -> LevelStates(1000L, 15, 0, 15, 0, (550..750), (0..0), (600..900))
//
//            // 7: Спаунер вносит суету.
//            7 -> LevelStates(900L, 10, 5, 5, 1, (600..800), (800..1000), (600..850), (700..900))
//
//            // 8: Охота. Вороны повсюду.
//            8 -> LevelStates(1200L, 5, 15, 0, 0, (650..850), (900..1100))
//
//            // 9: Осада. Высокий темп и дым.
//            9 -> LevelStates(800L, 25, 0, 20, 3, (700..900), (0..0), (800..1000), (850..1050))
//
//            // 10: Финальный темп. Почти предел реакции.
//            10 -> LevelStates(
//                700L,
//                30,
//                10,
//                10,
//                1,
//                (750..950),
//                (1000..1200),
//                (900..1100),
//                (950..1150)
//            )
//
//            // 11: Испытание. Максимальная сложность.
//            11 -> LevelStates(
//                750L,
//                40,
//                20,
//                20,
//                2,
//                (800..1000),
//                (1100..1300),
//                (1000..1200),
//                (1050..1250)
//            )
//
//            else -> LevelStates(
//                600L,
//                50,
//                30,
//                30,
//                3,
//                (850..1050),
//                (1150..1350),
//                (1100..1300),
//                (1100..1300)
//            )
//        }
//    var kills = 0
//    var timePause by mutableStateOf(false)
    val gameAssets = GameAssets()


//    val mapSize = IntSize(2000, 2500)
//    val epsilon = 15f
//    var left = canvasWidth / 2f / zoom - epsilon
//    var right = (canvasWidth - canvasWidth / 2f) / zoom + epsilon
//    var top = canvasHeight / 2f / zoom - epsilon
//    var bottom = (canvasHeight - canvasHeight / 2f) / zoom + epsilon

    //    val scope = viewModelScope
    val scope = CoroutineScope(AndroidUiDispatcher.Main + SupervisorJob())

    //    val scope = CoroutineScope(Dispatchers.Main)
    var isActualGame by mutableStateOf(false)
    lateinit var engine: GameEngine
//    var player: Player

    init {
        gameAssets.loadAll(application)
        AudioManager.init(application)
//        player = Player(
//            position = Offset(mapSize.width / 2f, mapSize.height / 2f),
//            animationFrontSize = gameAssets.playerFrontAnimation.size,
//            animationBackSize = gameAssets.playerBackAnimation.size,
//            animationRightSize = gameAssets.playerRightAnimation.size,
//            animationLeftSize = gameAssets.playerLeftAnimation.size
//        )
//        engine = GameEngine(player)
//        initMusicController()
//        initSpawnLogic()
    }
//    val right_wall = (mapSize.width - player.dstSize.width).toFloat()
//    val bottom_wall = (mapSize.height - player.dstSize.height).toFloat()
//    val left_wall = 0f
//    val top_wall = 0f
//    var lastNanosTime = 0L
//    val killmarkController = KillmarkController(scope)
//    val firePoint = FirePoint(
//        position = Offset(mapSize.width / 2f, mapSize.height / 2f),
//        animationSize = gameAssets.fireAnimation.size
//    )
//    val axe =
//        Axe(
//            scope = scope,
//            axe = gameAssets.axe_image,
//            axe_low_effect = gameAssets.axe_low_effect,
//            axe_strong_effect = gameAssets.axe_strong_effect,
//            axe_mirror = gameAssets.axe_image_mirror,
//            axe_low_effect_mirror = gameAssets.axe_low_effect_mirror,
//            axe_strong_effect_mirror = gameAssets.axe_strong_effect_mirror
//        )
//    val revolver =
//        Revolver(
//            scope = scope,
//            viewRight = gameAssets.revolver_right,
//            viewRightAnimation = gameAssets.revolverRightAnimation,
//            viewLeftAnimation = gameAssets.revolverLeftAnimation,
//        )
//    val shotgun =
//        Shotgun(
//            scope = scope,
//            viewLeft = gameAssets.shotgun_left,
//            viewRight = gameAssets.shotgun_right
//        )
//    var weapon: Weapon by mutableStateOf(axe)
//    val mapBox = RectF(-100f, -100f, mapSize.width + 100f, mapSize.height + 100f)
//
//    val boxes: MutableList<BoxObject> =
//        run {
//            val list = List<BoxObject>(10) { index ->
//                BoxObject(
//                    startPosition = Offset(
//                        (0..mapSize.width).random().toFloat(),
//                        (0..mapSize.height).random().toFloat()
//                    )
//                )
//            }.toMutableList()
//            // удаление ящиков в важных точках
//            for (i in list.indices.reversed()) {
//                if (list[i].collisionRect.overlaps(player.collisionRect) || list[i].collisionRect.overlaps(
//                        firePoint.collisionRect
//                    )
//                ) list.removeAt(i)
//            }
//            list
//        }
//    val listOfDrawableWithZ = ArrayList<DrawableWithZ>()
    fun ChangeZoom(newZoom: Float, unsucsessAction: () -> Unit) {
//        if (canvasWidth / newZoom <= mapSize.width && canvasHeight / newZoom <= mapSize.height) {
//            zoom = newZoom
//            checkScroll()
//        } else {
//            unsucsessAction()
//        }
    }
//    fun checkScroll() {
//        val playerOnScreenX = player.position.x + scrollX
//        val playerOnScreenY = player.position.y + scrollY
//        var newScrollX = scrollX
//        var newScrollY = scrollY
//        if (playerOnScreenX < left) {
//            newScrollX += (left - playerOnScreenX)
//        } else if (playerOnScreenX > right) {
//            newScrollX += (right - playerOnScreenX)
//        }
//        if (playerOnScreenY < top) {
//            newScrollY += (top - playerOnScreenY)
//        } else if (playerOnScreenY > bottom) {
//            newScrollY += (bottom - playerOnScreenY)
//        }
//        newScrollX = newScrollX.coerceIn(canvasWidth / zoom - mapSize.width,0f)
//        newScrollY = newScrollY.coerceIn(canvasHeight / zoom - mapSize.height,0f)
//        if (abs(scrollX - newScrollX) > 0.001f){
//            scrollX = newScrollX
//        }
//        if (abs(scrollY - newScrollY) > 0.001f){
//            scrollY = newScrollY
//        }
//    }
//
//    fun updateGameScreenSize(width: Float, height: Float) {
//        if (width == canvasWidth && height == canvasHeight) return
//        canvasWidth = width
//        canvasHeight = height
//        left = canvasWidth / 2f / zoom
//        right = (canvasWidth - canvasWidth / 2f) / zoom
//        top = canvasHeight / 2f / zoom
//        bottom = (canvasHeight - canvasHeight / 2f) / zoom
//    }

//    fun onTick(currentTime: Long) {
//        if (canvasWidth == 0f || canvasHeight == 0f) return
//        if (!TimeManager.isTimePaused.value) {
//            if (lastNanosTime == 0L) {
//                lastNanosTime = currentTime
//                return
//            }
//            val deltaTime = (currentTime - lastNanosTime) / 1_000_000f * TimeManager.timeScale.value
//            lastNanosTime = currentTime
//            player.update(deltaTime, boxes)
//            player.position =
//                player.position.copy(x = player.position.x.coerceIn(left_wall, right_wall))
//            player.position =
//                player.position.copy(y = player.position.y.coerceIn(top_wall, bottom_wall))
//            checkScroll()
//            engine.ammunition.badBlazingRemove { ammunition ->
//                if (!checkNotCollision(player.collisionRect, ammunition.collisionRect)) {
//                    when (ammunition.type) {
//                        AmmunitionType.Cartridges -> {
//                            AudioManager.play(AudioManager.SoundType.CARTRIDGES_CRIBE)
//                            revolver.clip += ammunition.value
//                        }
//
//                        AmmunitionType.Pellets -> {
//                            AudioManager.play(AudioManager.SoundType.PELLETS_CRIBE)
//                            shotgun.clip += ammunition.value
//                        }
//                    }
//                    true
//                } else false
//            }
//            player.checkDeath(DeathPlayer, engine.enemies, kills)
//            for (i in engine.enemies.indices.reversed()) {
//                val enemy = engine.enemies[i]
//                enemy.update(deltaTime, player.position)
//                if (!mapBox.contains(enemy.position.x, enemy.position.y)) {
//                    engine.removeEnemy(enemy)
//                    kills += enemy.killWeight
//                }
//            }
//            engine.update(deltaTime)
//            shotgun.update(deltaTime)
//            revolver.update(deltaTime)
//            checkLevelProgress()
//            checkIsLevelComplete(deltaTime)
//            listOfDrawableWithZ.apply {
//                clear()
//                addAll(engine.enemies)
//                addAll(engine.bodies)
//                addAll(engine.ammunition)
//                addAll(boxes)
//                add(player)
//                if (isLevelCompleted) add(firePoint)
//                sortBy { it.indexZ }
//            }
//        } else {
//            lastNanosTime = 0L
//        }
//    }

//    private fun checkIsLevelComplete(delta: Float) {
//        if (isLevelCompleted) {
//            firePoint.update(delta)
//            if (calcDistanceForComparison(
//                    player.center,
//                    firePoint.center
//                ) <= 50f * 50f
//            ) {
//                isLevelCompleted = false
//                level++
//                killmarkController.startAnimation(Killmark(text = "Уровень $level"))
//            }
//        }
//    }

//    private fun checkLevelProgress() {
//        if (kills == levelStates.killableCount) {
//            AudioManager.play(AudioManager.SoundType.HOLY_MOMENT)
//            kills = 0
//            if (level == 12) WinPlayer() else isLevelCompleted = true
//        }
//    }

//    fun onHitBody(body: Body, angle: Float, power: Int, weaponPower: Float? = null) {
//        body.addBias(angle = angle, power = power)
//        engine.addBlood(
//            Blood(
//                power = power,
//                position = body.position + Offset(0f, -body.dstSize.height.toFloat()),
//                pivot = body.center,
//                angle = angle
//            )
//        )
//    }

//    fun Revolver.doFire(offset: Offset, isHoly: Boolean) {
//        val position = offset / zoom - Offset(scrollX, scrollY)
//        this.angle = calcAngle(position, player.center)
//        var killCount = 0
//        this.fire(
//            onSuccessAction = { trigger++ },
//            position = position,
//            player = player,
//            enemies = engine.enemies,
//            bodies = engine.bodies,
//            holyMode = isHoly,
//            onHitEnemy = { enemy: Enemy ->
//                if (enemy.killable) {
//                    engine.killEnemy(enemy)
//                    kills += enemy.killWeight
//                    killCount++
//                    this.checkSeries(
//                        createKillmark = {
//                            val newKillmark = Killmark(
//                                view = gameAssets.revolver_killmark,
//                                text = "$it подряд"
//                            )
//                            killmarkController.startAnimation(newKillmark)
//                        }
//                    )
//
//                    if (Random.nextFloat() < 0.1f) {
//                        val ammunition = Ammunition(
//                            value = 5,
//                            type = AmmunitionType.Pellets,
//                            startPosition = enemy.center
//                        )
//                        engine.addAmmunition(ammunition)
//                    }
//                } else {
//                    scope.launch { enemy.onHitEffect() }
//                }
//            },
//            onHitBody = ::onHitBody
//        )
//        onKill(killCount)
//    }

//    fun Shotgun.doFire(position: Offset, powerWeapon: Float = 30f) {
//        this.angle = calcAngle(position, player.center)
//        this.animateShotgun(position)
//        var killCount = 0
//        this.fire(
//            player = player,
//            enemies = engine.enemies,
//            bodies = engine.bodies,
//            onHitEnemy = { enemy ->
//                if (enemy.killable) {
//                    engine.killEnemy(enemy)
//                    kills += enemy.killWeight
//                    killCount++
//                    if (Random.nextFloat() < 0.1f) {
//                        val ammunition = Ammunition(
//                            value = 12,
//                            type = AmmunitionType.Cartridges,
//                            startPosition = enemy.center
//                        )
//                        engine.addAmmunition(ammunition)
//                    }
//                } else {
//                    scope.launch { enemy.onHitEffect() }
//                }
//            },
//            onHitBody = { body, angle, power ->
//                body.addBias(
//                    angle = angle,
//                    power = power,
//                    powerWeapon = powerWeapon
//                )
//                engine.addBlood(
//                    Blood(
//                        power = power,
//                        position = body.position + Offset(
//                            0f,
//                            -body.dstSize.height.toFloat()
//                        ),
//                        pivot = body.center,
//                        angle = angle
//                    )
//                )
//            }
//        )
//        onKill(killCount)
//        if (killCount > 1) {
//            val newKillmark = Killmark(
//                view = gameAssets.killmark,
//                text = "X$killCount",
//            )
//            killmarkController.startAnimation(newKillmark)
//        }
//    }
//
//    fun revolverTap(offset: Offset) {
//        (weapon as? Revolver)?.let { it.doFire(offset, isHoly = false) }
//    }
//
//    fun revolverOnLongPress(offset: Offset) {
//        (weapon as? Revolver)?.let {
//            it.doFire(offset, isHoly = true)
//        }
//    }
//
//    fun shotgunOnDragStart(offset: Offset) {
//        (weapon as? Shotgun)?.let { shotgun ->
//            shotgun.targetMagnet = offset / zoom - Offset(scrollX, scrollY)
//            shotgun.startAction(
//                enemies = engine.enemies,
//                bodies = engine.bodies,
//                onHitEnemy = { enemy, offset ->
//                    enemy.position += offset
//                },
//                onHitBody = { body, offset ->
//                    body.position += offset
//                }
//            )
//        }
//    }
//
//    fun shotgunOnDrag(position: Offset) {
//        (weapon as? Shotgun)?.let { shotgun ->
//            shotgun.targetMagnet =
//                position / zoom - Offset(scrollX, scrollY)
//        }
//    }
//
//    fun shotgunOnDragEnd() {
//        (weapon as? Shotgun)?.let { shotgun ->
//            shotgun.stopAction()
//            shotgun.doFire(
//                position = shotgun.targetMagnet ?: Offset.Zero,
//                powerWeapon = 50f
//            )
//            shotgun.targetMagnet = null
//        }
//    }
//
//    fun shotgunOnDragCancel() {
//        (weapon as? Shotgun)?.let { shotgun ->
//            shotgun.stopAction()
//            shotgun.targetMagnet = null
//        }
//    }
//
//    fun shotgunOnTap(offset: Offset) {
//        (weapon as? Shotgun)?.let { shotgun ->
//            shotgun.doFire(
//                position = offset / zoom - Offset(scrollX, scrollY)
//            )
//        }
//    }
//
//    fun axeOnDragStart() {
//        (weapon as? Axe)?.let { axe ->
//            axe.startSlice()
//        }
//    }
//
//    fun axeOnDrag(position: Offset) {
//        (weapon as? Axe)?.let { axe ->
//            if (axe.holyModeProgress >= axe.minHolyModeUsable) {
//                axe.slicePoints.add(
//                    position / zoom - Offset(
//                        scrollX,
//                        scrollY
//                    )
//                )
//            }
//        }
//    }
//
//    fun axeOnDragEnd() {
//        (weapon as? Axe)?.let { axe ->
//            axe.slice(
//                onSuccessAction = { trigger2++ },
//                bodies = engine.bodies,
//                enemies = engine.enemies,
//                onHitEnemy = { enemy: Enemy ->
//                    if (enemy.killable) {
//                        AudioManager.play(AudioManager.SoundType.AXE_HIT)
//                        engine.killEnemy(enemy)
//                        kills += enemy.killWeight
//                        axe.onKill(1)
//                        if (Random.nextFloat() < 0.3f) {
//                            val newKillmark = Killmark(
//                                view = gameAssets.axe_killmark,
//                            )
//                            killmarkController.startAnimation(newKillmark)
//                            val ammunition =
//                                when (Random.nextFloat() > 0.5f) {
//                                    true -> Ammunition(
//                                        value = 12,
//                                        type = AmmunitionType.Cartridges,
//                                        startPosition = enemy.center
//                                    )
//
//                                    false -> Ammunition(
//                                        value = 5,
//                                        type = AmmunitionType.Pellets,
//                                        startPosition = enemy.center
//                                    )
//                                }
//                            engine.addAmmunition(ammunition)
//                        }
//                    } else scope.launch { enemy.onHitEffect() }
//                },
//                onHitBody = ::onHitBody
//            )
//            axe.speed = 30f
//            axe.rotationAxe(
//                isHold = false,
//                player = player,
//                enemies = engine.enemies,
//                bodies = engine.bodies,
//                onHitEnemy = { enemy: Enemy ->
//                    if (enemy.killable) {
//                        AudioManager.play(AudioManager.SoundType.AXE_HIT)
//                        engine.killEnemy(enemy)
//                        kills += enemy.killWeight
//                        axe.onKill(1)
//                        if (Random.nextFloat() < 0.3f) {
//                            val newKillmark = Killmark(
//                                view = gameAssets.axe_killmark,
//                            )
//                            killmarkController.startAnimation(newKillmark)
//                            val ammunition = when (axe.trend) {
//                                true -> Ammunition(
//                                    value = 12,
//                                    type = AmmunitionType.Cartridges,
//                                    startPosition = enemy.center
//                                )
//
//                                false -> Ammunition(
//                                    value = 5,
//                                    type = AmmunitionType.Pellets,
//                                    startPosition = enemy.center
//                                )
//                            }
//                            engine.addAmmunition(ammunition)
//                        }
//                    } else scope.launch { enemy.onHitEffect() }
//                },
//                onHitBody = ::onHitBody,
//            )
//        }
//    }
//
//    fun axeOnDragCancel() {
//        axe.slicePoints.clear()
//    }
//
//    fun axeOnRotate(_isHold: Boolean) {
//        axe.rotationAxe(
//            isHold = _isHold,
//            player = player,
//            enemies = engine.enemies,
//            bodies = engine.bodies,
//            onHitEnemy = { enemy: Enemy ->
//                if (enemy.killable) {
//                    AudioManager.play(AudioManager.SoundType.AXE_HIT)
//                    engine.killEnemy(enemy)
//                    kills += enemy.killWeight
//                    axe.onKill(1)
//                    if (Random.nextFloat() < 0.3f) {
//                        val newKillmark = Killmark(
//                            view = gameAssets.axe_killmark,
//                        )
//                        killmarkController.startAnimation(newKillmark)
//                        val ammunition = when (axe.trend) {
//                            true -> Ammunition(
//                                value = 12,
//                                type = AmmunitionType.Cartridges,
//                                startPosition = enemy.center
//                            )
//
//                            false -> Ammunition(
//                                value = 5,
//                                type = AmmunitionType.Pellets,
//                                startPosition = enemy.center
//                            )
//                        }
//                        engine.addAmmunition(ammunition)
//                    }
//                } else scope.launch { enemy.onHitEffect() }
//            },
//            onHitBody = ::onHitBody,
//        )
//    }

//    suspend fun startSpawn() = with(levelStates) {
//        if (mode == 0) {
//            player.hitPoint = 100
//            scope.launch {
//                for (i in 0 until enemyScarecrowCounts) {
//                    TimeManager.delay(spawnTime)
//                    engine.add(
//                        enemy =
//                        Scarecrow(
//                            spawnAnimationSize = gameAssets.spawnAnimation.size,
//                            startPosition = Offset(
//                                (0..mapSize.width).random().toFloat(),
//                                (0..mapSize.height).random().toFloat()
//                            ),
//                            speed = speedScarecrowRange.random().toFloat(),
//                            animationSize = gameAssets.scarecrowAnimation.size
//                        )
//                    )
//                }
//            }
//            scope.launch {
//                for (i in 0 until enemyCrowCounts) {
//                    TimeManager.delay(spawnTime)
//                    engine.add(
//                        enemy =
//                        Crow(
//                            spawnAnimationSize = gameAssets.spawnAnimation.size,
//                            startPosition = Offset(
//                                (0..mapSize.width).random().toFloat(),
//                                (0..mapSize.height).random().toFloat()
//                            ),
//                            speed = speedCrowRange.random().toFloat(),
//                            animationSize = gameAssets.crowAnimation.size,
//                            fixedPlayerPosition = player.center
//                        )
//                    )
//                }
//            }
//            scope.launch {
//                for (i in 0 until enemySpawnerCounts) {
//                    TimeManager.delay(spawnTime)
//                    engine.add(
//                        enemy =
//                        Spawner(
//                            spawnAnimationSize = gameAssets.spawnAnimation.size,
//                            startPosition = Offset(
//                                (0..mapSize.width).random().toFloat(),
//                                (0..mapSize.height).random().toFloat()
//                            ),
//                            action = { self ->
//                                engine.add(
//                                    enemy = CrowMinion(
//                                        spawnAnimationSize = gameAssets.spawnAnimation.size,
//                                        startPosition = self.center,
//                                        speed = speedMinionRange.random().toFloat(),
//                                        animationSize = gameAssets.crowAnimation.size,
//                                    )
//                                )
//                            }
//                        )
//                    )
//                }
//            }
//            scope.launch {
//                for (i in 0 until enemySmokeCounts) {
//                    TimeManager.delay(spawnTime)
//                    engine.add(
//                        enemy = Smoke(
//                            spawnAnimationSize = gameAssets.spawnAnimation.size,
//                            startPosition = Offset(
//                                (0..mapSize.width).random().toFloat(),
//                                (0..mapSize.height).random().toFloat()
//                            ),
//                            speed = speedSmokeRange.random().toFloat(),
//                            animationSize = gameAssets.smokeAnimation.size
//                        )
//                    )
//                }
//            }
//        }
//        if (mode == 1) {
//            scope.launch {
//                val speedRange = (600..900)
//                while (true) {
//                    val random = Random.nextFloat()
//                    TimeManager.delay(1000L)
//                    if (random < 0.8f) {
//                        engine.add(
//                            enemy =
//                            Scarecrow(
//                                spawnAnimationSize = gameAssets.spawnAnimation.size,
//                                startPosition = Offset(
//                                    (0..mapSize.width).random().toFloat(),
//                                    (0..mapSize.height).random().toFloat()
//                                ),
//                                speed = speedRange.random().toFloat(),
//                                animationSize = gameAssets.scarecrowAnimation.size
//                            )
//                        )
//                    }
//                    if (random < 0.4f) {
//                        engine.add(
//                            enemy =
//                            Crow(
//                                spawnAnimationSize = gameAssets.spawnAnimation.size,
//                                startPosition = Offset(
//                                    (0..mapSize.width).random().toFloat(),
//                                    (0..mapSize.height).random().toFloat()
//                                ),
//                                speed = speedRange.random().toFloat(),
//                                animationSize = gameAssets.crowAnimation.size,
//                                fixedPlayerPosition = player.center
//                            )
//                        )
//                    }
//                    if (random < 0.3f) {
//                        engine.add(
//                            enemy = Smoke(
//                                spawnAnimationSize = gameAssets.spawnAnimation.size,
//                                startPosition = Offset(
//                                    (0..mapSize.width).random().toFloat(),
//                                    (0..mapSize.height).random().toFloat()
//                                ),
//                                speed = speedRange.random().toFloat(),
//                                animationSize = gameAssets.smokeAnimation.size
//                            )
//                        )
//                    }
//                    if (random < 0.1f) {
//                        engine.add(
//                            enemy =
//                            Spawner(
//                                spawnAnimationSize = gameAssets.spawnAnimation.size,
//                                startPosition = Offset(
//                                    (0..mapSize.width).random().toFloat(),
//                                    (0..mapSize.height).random().toFloat()
//                                ),
//                                action = { self ->
//                                    engine.add(
//                                        enemy = CrowMinion(
//                                            spawnAnimationSize = gameAssets.spawnAnimation.size,
//                                            startPosition = self.center,
//                                            speed = speedRange.random().toFloat(),
//                                            animationSize = gameAssets.crowAnimation.size,
//                                        )
//                                    )
//                                }
//                            )
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    private fun initSpawnLogic() {
//        scope.launch {
//            snapshotFlow { level }.distinctUntilChanged().collectLatest {
//                startSpawn()
//            }
//        }
//    }

//    private fun initMusicController() {
//        scope.launch {
//            val levelCompleteFlow = snapshotFlow { isLevelCompleted }
//            combine(
//                levelCompleteFlow,
//                TimeManager.timeScale,
//                TimeManager.isTimePaused
//            ) { levelCompleted, scale, paused ->
//                Triple(levelCompleted, scale, paused)
//            }.distinctUntilChanged().collectLatest { (levelCompleted, scale, paused) ->
//                if (levelCompleted) {
//                    AudioManager.stopMusic()
//                } else {
//                    delay(30L)
//                    if (!paused) AudioManager.setMusicSpeed(scale)
//                    AudioManager.playMusic(paused)
//                }
//            }
//        }
//    }

//    fun changeWeapon(index: Int) {
//        weapon = when (index) {
//            1 -> revolver
//            2 -> shotgun
//            else -> axe
//        }
//    }

    fun startGame(newMode: Int) {
        if (isActualGame) return
        mode = newMode
        engine = GameEngine(gameAssets,scope,DeathPlayer,WinPlayer,canvasWidth,canvasWidth,newMode)
        isActualGame = true
    }

    fun resetGame() {
        engine.resetGame()
        isActualGame = false
    }

    override fun onCleared() {
        super.onCleared()
        AudioManager.release()
        TimeManager.setPaused(false)
    }
}