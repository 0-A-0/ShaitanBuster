package com.example.scrolllist.game

import android.graphics.RectF
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.AudioManager
import com.example.scrolllist.GlobalGameSettings
import com.example.scrolllist.LevelStates
import com.example.scrolllist.TimeManager
import com.example.scrolllist.domain.badBlazingRemove
import com.example.scrolllist.domain.blazingReflectFor
import com.example.scrolllist.domain.blazingRemove
import com.example.scrolllist.domain.calcAngle
import com.example.scrolllist.domain.calcDistanceForComparison
import com.example.scrolllist.domain.checkNotCollision
import com.example.scrolllist.domain.objects.Ammunition
import com.example.scrolllist.domain.objects.AmmunitionType
import com.example.scrolllist.domain.objects.BoxObject
import com.example.scrolllist.domain.objects.FirePoint
import com.example.scrolllist.domain.objects.Killmark
import com.example.scrolllist.domain.objects.KillmarkController
import com.example.scrolllist.domain.objects.KillmarkView
import com.example.scrolllist.domain.units.Player
import com.example.scrolllist.domain.units.enemy.Blood
import com.example.scrolllist.domain.units.enemy.Body
import com.example.scrolllist.domain.units.enemy.BodyType
import com.example.scrolllist.domain.units.enemy.Crow
import com.example.scrolllist.domain.units.enemy.CrowMinion
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.FixedBody
import com.example.scrolllist.domain.units.enemy.FlyableBody
import com.example.scrolllist.domain.units.enemy.GhostBody
import com.example.scrolllist.domain.units.enemy.Scarecrow
import com.example.scrolllist.domain.units.enemy.SimpleBody
import com.example.scrolllist.domain.units.enemy.Smoke
import com.example.scrolllist.domain.units.enemy.Spawner
import com.example.scrolllist.domain.units.weapon.Axe
import com.example.scrolllist.domain.units.weapon.Revolver
import com.example.scrolllist.domain.units.weapon.Shotgun
import com.example.scrolllist.domain.units.weapon.Weapon
import com.example.scrolllist.ui.DrawableWithZ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
import kotlin.random.Random

class GameEngine(
    val gameAssetsSize: GameAssetsSizeProvider,
    val scope: CoroutineScope,
    val DeathPlayer: (Int) -> Unit,
    val WinPlayer: () -> Unit,
    var canvasWidth: Float,
    var canvasHeight: Float,
    val mode: Int,
) {
    init {
        initMusicController()
        initSpawnLogic()
    }
    
    private val _listOfDrawableWithZ = ArrayList<DrawableWithZ>()
//    private val _enemies = ArrayList<Enemy>()
    private val _enemies = CopyOnWriteArrayList<Enemy>()
    private val _bloods = ArrayList<Blood>()
    private val _bodies = ArrayList<Body>()
    private val _ammunition = ArrayList<Ammunition>()

    val listOfDrawableWithZ: List<DrawableWithZ> get() = _listOfDrawableWithZ
    val enemies: List<Enemy> get() = _enemies
    val bloods: List<Blood> get() = _bloods
    val bodies: List<Body> get() = _bodies
    val ammunition: List<Ammunition> get() = _ammunition

    var zoom by mutableStateOf(1f)
        //    var zoom = 1f
        private set
    var isLevelCompleted by mutableStateOf(false)
        private set
    var scrollX by mutableStateOf(0f)
        //    var scrollX = 0f
        private set
    var scrollY by mutableStateOf(0f)
        //    var scrollY = 0f
        private set
    var level by mutableStateOf(1)
        private set
    var trigger by mutableStateOf(0)
    var trigger2 by mutableStateOf(0)
    val levelStates: LevelStates
        get() = when (level) {
            // 1: Вступление. Скорость 300-500 пикс/сек.
            1 -> LevelStates(400L, 15, 0, 0, 0, (300..500))

            // 2: Плотность выше.
            2 -> LevelStates(100L, 30, 1, 0, 0, (300..550), (400..600))

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
            10 -> LevelStates(
                700L,
                30,
                10,
                10,
                1,
                (750..950),
                (1000..1200),
                (900..1100),
                (950..1150)
            )

            // 11: Испытание. Максимальная сложность.
            11 -> LevelStates(
                750L,
                40,
                20,
                20,
                2,
                (800..1000),
                (1100..1300),
                (1000..1200),
                (1050..1250)
            )

            else -> LevelStates(
                600L,
                50,
                30,
                30,
                3,
                (850..1050),
                (1150..1350),
                (1100..1300),
                (1100..1300)
            )
        }
    var kills = 0
    val mapSize = IntSize(2000, 2500)
    val epsilon = 15f
    var left = canvasWidth / 2f / zoom - epsilon
    var right = (canvasWidth - canvasWidth / 2f) / zoom + epsilon
    var top = canvasHeight / 2f / zoom - epsilon
    var bottom = (canvasHeight - canvasHeight / 2f) / zoom + epsilon
    var player = Player(
        position = Offset(mapSize.width / 2f, mapSize.height / 2f),
        animationFrontSize = gameAssetsSize.playerAnimationFrontSize,
        animationBackSize = gameAssetsSize.playerAnimationBackSize,
        animationRightSize = gameAssetsSize.playerAnimationRightSize,
        animationLeftSize = gameAssetsSize.playerAnimationLeftSize
    )
    val right_wall = (mapSize.width - player.dstSize.width).toFloat()
    val bottom_wall = (mapSize.height - player.dstSize.height).toFloat()
    val left_wall = 0f
    val top_wall = 0f
    var lastNanosTime = 0L
    val killmarkController = KillmarkController(scope)
    val firePoint = FirePoint(
        position = Offset(mapSize.width / 2f, mapSize.height / 2f),
        animationSize = gameAssetsSize.fireAnimationSize
    )
    val axe =
        Axe(
            scope = scope,
        )
    val revolver =
        Revolver(
            scope = scope,
            animationSize = gameAssetsSize.revolverAnimationSize
        )
    val shotgun =
        Shotgun(
            scope = scope,
        )
    var weapon: Weapon by mutableStateOf(axe)
    val mapBox = RectF(-100f, -100f, mapSize.width + 100f, mapSize.height + 100f)

    val boxes: MutableList<BoxObject> =
        run {
            val list = List<BoxObject>(10) { index ->
                BoxObject(
                    startPosition = Offset(
                        (0..mapSize.width).random().toFloat(),
                        (0..mapSize.height).random().toFloat()
                    )
                )
            }.toMutableList()
            // удаление ящиков в важных точках
            for (i in list.indices.reversed()) {
                if (list[i].collisionRect.overlaps(player.collisionRect) || list[i].collisionRect.overlaps(
                        firePoint.collisionRect
                    )
                ) list.removeAt(i)
            }
            list
        }

    fun checkScroll() {
        val playerOnScreenX = player.position.x + scrollX
        val playerOnScreenY = player.position.y + scrollY
        var newScrollX = scrollX
        var newScrollY = scrollY
        if (playerOnScreenX < left) {
            newScrollX += (left - playerOnScreenX)
        } else if (playerOnScreenX > right) {
            newScrollX += (right - playerOnScreenX)
        }
        if (playerOnScreenY < top) {
            newScrollY += (top - playerOnScreenY)
        } else if (playerOnScreenY > bottom) {
            newScrollY += (bottom - playerOnScreenY)
        }
        newScrollX = newScrollX.coerceIn(canvasWidth / zoom - mapSize.width,0f)
        newScrollY = newScrollY.coerceIn(canvasHeight / zoom - mapSize.height,0f)
        if (abs(scrollX - newScrollX) > 0.001f){
            scrollX = newScrollX
        }
        if (abs(scrollY - newScrollY) > 0.001f){
            scrollY = newScrollY
        }
    }

    fun updateGameScreenSize(width: Float, height: Float) {
        if (width == canvasWidth && height == canvasHeight) return
        canvasWidth = width
        canvasHeight = height
        left = canvasWidth / 2f / zoom
        right = (canvasWidth - canvasWidth / 2f) / zoom
        top = canvasHeight / 2f / zoom
        bottom = (canvasHeight - canvasHeight / 2f) / zoom
    }
    
    fun update(currentTime:Long) {
        if (canvasWidth == 0f || canvasHeight == 0f) return
        if (!TimeManager.isTimePaused.value) {
            if (lastNanosTime == 0L) {
                lastNanosTime = currentTime
                return
            }
            val deltaTime = (currentTime - lastNanosTime) / 1_000_000f * TimeManager.timeScale.value
            lastNanosTime = currentTime
            player.update(deltaTime, boxes)
            player.position =
                player.position.copy(x = player.position.x.coerceIn(left_wall, right_wall))
            player.position =
                player.position.copy(y = player.position.y.coerceIn(top_wall, bottom_wall))
            checkScroll()
            _ammunition.blazingRemove { ammunition ->
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
                    true
                } else false
            }
            player.checkDeath(DeathPlayer, _enemies, kills)
            _enemies.blazingReflectFor { enemy ->
                enemy.update(deltaTime, player.position)
                if (!mapBox.contains(enemy.position.x, enemy.position.y)) {
                    removeEnemy(enemy)
                    kills += enemy.killWeight
                }
            }
            _bloods.blazingRemove { blood ->
                blood.update(deltaTime)
                blood.lifeTimeMl <= 0f
            }
            _bodies.blazingRemove { body ->
                body.update(deltaTime)
                body.lifeTimeMl <= 0f
            }
            shotgun.update(deltaTime)
            revolver.update(deltaTime)
            checkLevelProgress()
            checkIsLevelComplete(deltaTime)
            _listOfDrawableWithZ.apply {
                clear()
                addAll(enemies)
                addAll(bodies)
                addAll(ammunition)
                addAll(boxes)
                add(player)
                if (isLevelCompleted) add(firePoint)
                sortBy { it.indexZ }
            }
        } else {
            lastNanosTime = 0L
        }
    }

    private fun checkIsLevelComplete(delta: Float) {
        if (isLevelCompleted) {
            firePoint.update(delta)
            if (calcDistanceForComparison(
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

    private fun checkLevelProgress() {
        if (kills == levelStates.killableCount) {
            AudioManager.play(AudioManager.SoundType.HOLY_MOMENT)
            kills = 0
            if (level == 12) WinPlayer() else isLevelCompleted = true
        }
    }

    fun onHitBody(body: Body, angle: Float, power: Int, weaponPower: Float? = null) {
        body.addBias(angle = angle, power = power)
        addBlood(
            Blood(
                power = power,
                position = body.position + Offset(0f, -body.dstSize.height.toFloat()),
                pivot = body.center,
                angle = angle
            )
        )
    }

    fun Revolver.doFire(offset: Offset, isHoly: Boolean) {
        val position = offset / zoom - Offset(scrollX, scrollY)
        this.angle = calcAngle(position, player.center)
        var killCount = 0
        this.fire(
            onSuccessAction = { trigger++ },
            position = position,
            player = player,
            enemies = _enemies,
            bodies = _bodies,
            holyMode = isHoly,
            onHitEnemy = { enemy: Enemy ->
                if (enemy.killable) {
                    killEnemy(enemy)
                    kills += enemy.killWeight
                    killCount++
                    this.checkSeries(
                        createKillmark = {
                            val newKillmark = Killmark(
                                view = KillmarkView.revolver_killmark,
                                text = "$it подряд"
                            )
                            killmarkController.startAnimation(newKillmark)
                        }
                    )

                    if (Random.nextFloat() < 0.1f) {
                        val ammunition = Ammunition(
                            value = 5,
                            type = AmmunitionType.Pellets,
                            startPosition = enemy.center
                        )
                        addAmmunition(ammunition)
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
            enemies = _enemies,
            bodies = _bodies,
            onHitEnemy = { enemy ->
                if (enemy.killable) {
                    killEnemy(enemy)
                    kills += enemy.killWeight
                    killCount++
                    if (Random.nextFloat() < 0.1f) {
                        val ammunition = Ammunition(
                            value = 12,
                            type = AmmunitionType.Cartridges,
                            startPosition = enemy.center
                        )
                        addAmmunition(ammunition)
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
                addBlood(
                    Blood(
                        power = power,
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
                view = KillmarkView.killmark,
                text = "X$killCount",
            )
            killmarkController.startAnimation(newKillmark)
        }
    }

    fun revolverTap(offset: Offset) {
        (weapon as? Revolver)?.let { it.doFire(offset, isHoly = false) }
    }

    fun revolverOnLongPress(offset: Offset) {
        (weapon as? Revolver)?.let {
            it.doFire(offset, isHoly = true)
        }
    }

    fun shotgunOnDragStart(offset: Offset) {
        (weapon as? Shotgun)?.let { shotgun ->
            shotgun.targetMagnet = offset / zoom - Offset(scrollX, scrollY)
            shotgun.startAction(
                enemies = _enemies,
                bodies = _bodies,
                onHitEnemy = { enemy, offset ->
                    enemy.position += offset
                },
                onHitBody = { body, offset ->
                    body.position += offset
                }
            )
        }
    }

    fun shotgunOnDrag(position: Offset) {
        (weapon as? Shotgun)?.let { shotgun ->
            shotgun.targetMagnet =
                position / zoom - Offset(scrollX, scrollY)
        }
    }

    fun shotgunOnDragEnd() {
        (weapon as? Shotgun)?.let { shotgun ->
            shotgun.stopAction()
            shotgun.doFire(
                position = shotgun.targetMagnet ?: Offset.Zero,
                powerWeapon = 50f
            )
            shotgun.targetMagnet = null
        }
    }

    fun shotgunOnDragCancel() {
        (weapon as? Shotgun)?.let { shotgun ->
            shotgun.stopAction()
            shotgun.targetMagnet = null
        }
    }

    fun shotgunOnTap(offset: Offset) {
        (weapon as? Shotgun)?.let { shotgun ->
            shotgun.doFire(
                position = offset / zoom - Offset(scrollX, scrollY)
            )
        }
    }

    fun axeOnDragStart() {
        (weapon as? Axe)?.let { axe ->
            axe.startSlice()
        }
    }

    fun axeOnDrag(position: Offset) {
        (weapon as? Axe)?.let { axe ->
            if (axe.holyModeProgress >= axe.minHolyModeUsable) {
                axe.slicePoints.add(
                    position / zoom - Offset(
                        scrollX,
                        scrollY
                    )
                )
            }
        }
    }

    fun axeOnDragEnd() {
        (weapon as? Axe)?.let { axe ->
            axe.slice(
                onSuccessAction = { trigger2++ },
                bodies = _bodies,
                enemies = _enemies,
                onHitEnemy = { enemy: Enemy ->
                    if (enemy.killable) {
                        AudioManager.play(AudioManager.SoundType.AXE_HIT)
                        killEnemy(enemy)
                        kills += enemy.killWeight
                        axe.onKill(1)
                        if (Random.nextFloat() < 0.3f) {
                            val newKillmark = Killmark(
                                view = KillmarkView.axe_killmark,
                            )
                            killmarkController.startAnimation(newKillmark)
                            val ammunition =
                                when (Random.nextFloat() > 0.5f) {
                                    true -> Ammunition(
                                        value = 12,
                                        type = AmmunitionType.Cartridges,
                                        startPosition = enemy.center
                                    )

                                    false -> Ammunition(
                                        value = 5,
                                        type = AmmunitionType.Pellets,
                                        startPosition = enemy.center
                                    )
                                }
                            addAmmunition(ammunition)
                        }
                    } else scope.launch { enemy.onHitEffect() }
                },
                onHitBody = ::onHitBody
            )
            axe.speed = 30f
            axe.rotationAxe(
                isHold = false,
                player = player,
                enemies = _enemies,
                bodies = _bodies,
                onHitEnemy = { enemy: Enemy ->
                    if (enemy.killable) {
                        AudioManager.play(AudioManager.SoundType.AXE_HIT)
                        killEnemy(enemy)
                        kills += enemy.killWeight
                        axe.onKill(1)
                        if (Random.nextFloat() < 0.3f) {
                            val newKillmark = Killmark(
                                view = KillmarkView.axe_killmark,
                            )
                            killmarkController.startAnimation(newKillmark)
                            val ammunition = when (axe.trend) {
                                true -> Ammunition(
                                    value = 12,
                                    type = AmmunitionType.Cartridges,
                                    startPosition = enemy.center
                                )

                                false -> Ammunition(
                                    value = 5,
                                    type = AmmunitionType.Pellets,
                                    startPosition = enemy.center
                                )
                            }
                            addAmmunition(ammunition)
                        }
                    } else scope.launch { enemy.onHitEffect() }
                },
                onHitBody = ::onHitBody,
            )
        }
    }

    fun axeOnDragCancel() {
        axe.slicePoints.clear()
    }

    fun axeOnRotate(_isHold: Boolean) {
        axe.rotationAxe(
            isHold = _isHold,
            player = player,
            enemies = _enemies,
            bodies = _bodies,
            onHitEnemy = { enemy: Enemy ->
                if (enemy.killable) {
                    AudioManager.play(AudioManager.SoundType.AXE_HIT)
                    killEnemy(enemy)
                    kills += enemy.killWeight
                    axe.onKill(1)
                    if (Random.nextFloat() < 0.3f) {
                        val newKillmark = Killmark(
                            view = KillmarkView.axe_killmark,
                        )
                        killmarkController.startAnimation(newKillmark)
                        val ammunition = when (axe.trend) {
                            true -> Ammunition(
                                value = 12,
                                type = AmmunitionType.Cartridges,
                                startPosition = enemy.center
                            )

                            false -> Ammunition(
                                value = 5,
                                type = AmmunitionType.Pellets,
                                startPosition = enemy.center
                            )
                        }
                        addAmmunition(ammunition)
                    }
                } else scope.launch { enemy.onHitEffect() }
            },
            onHitBody = ::onHitBody,
        )
    }

    suspend fun startSpawn() = with(levelStates) {
        if (mode == 0) {
            player.hitPoint = 100
            scope.launch {
                for (i in 0 until enemyScarecrowCounts) {
                    TimeManager.delay(spawnTime)
                    add(
                        enemy =
                        Scarecrow(
                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = speedScarecrowRange.random().toFloat(),
                            animationSize = gameAssetsSize.scarecrowAnimationSize
                        )
                    )
                }
            }
            scope.launch {
                for (i in 0 until enemyCrowCounts) {
                    TimeManager.delay(spawnTime)
                    add(
                        enemy =
                        Crow(
                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = speedCrowRange.random().toFloat(),
                            animationSize = gameAssetsSize.crowAnimationSize,
                            fixedPlayerPosition = player.center
                        )
                    )
                }
            }
            scope.launch {
                for (i in 0 until enemySpawnerCounts) {
                    TimeManager.delay(spawnTime)
                    add(
                        enemy =
                        Spawner(
                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            action = { self ->
                                add(
                                    enemy = CrowMinion(
                                        spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                        startPosition = self.center,
                                        speed = speedMinionRange.random().toFloat(),
                                        animationSize = gameAssetsSize.crowAnimationSize,
                                    )
                                )
                            }
                        )
                    )
                }
            }
            scope.launch {
                for (i in 0 until enemySmokeCounts) {
                    TimeManager.delay(spawnTime)
                    add(
                        enemy = Smoke(
                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                            startPosition = Offset(
                                (0..mapSize.width).random().toFloat(),
                                (0..mapSize.height).random().toFloat()
                            ),
                            speed = speedSmokeRange.random().toFloat(),
                            animationSize = gameAssetsSize.smokeAnimationSize
                        )
                    )
                }
            }
        }
        if (mode == 1) {
            scope.launch {
                val speedRange = (600..900)
                while (true) {
                    val random = Random.nextFloat()
                    TimeManager.delay(1000L)
                    if (random < 0.8f) {
                        add(
                            enemy =
                            Scarecrow(
                                spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                startPosition = Offset(
                                    (0..mapSize.width).random().toFloat(),
                                    (0..mapSize.height).random().toFloat()
                                ),
                                speed = speedRange.random().toFloat(),
                                animationSize = gameAssetsSize.scarecrowAnimationSize
                            )
                        )
                    }
                    if (random < 0.4f) {
                        add(
                            enemy =
                            Crow(
                                spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                startPosition = Offset(
                                    (0..mapSize.width).random().toFloat(),
                                    (0..mapSize.height).random().toFloat()
                                ),
                                speed = speedRange.random().toFloat(),
                                animationSize = gameAssetsSize.crowAnimationSize,
                                fixedPlayerPosition = player.center
                            )
                        )
                    }
                    if (random < 0.3f) {
                        add(
                            enemy = Smoke(
                                spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                startPosition = Offset(
                                    (0..mapSize.width).random().toFloat(),
                                    (0..mapSize.height).random().toFloat()
                                ),
                                speed = speedRange.random().toFloat(),
                                animationSize = gameAssetsSize.smokeAnimationSize
                            )
                        )
                    }
                    if (random < 0.1f) {
                        add(
                            enemy =
                            Spawner(
                                spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                startPosition = Offset(
                                    (0..mapSize.width).random().toFloat(),
                                    (0..mapSize.height).random().toFloat()
                                ),
                                action = { self ->
                                    add(
                                        enemy = CrowMinion(
                                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                            startPosition = self.center,
                                            speed = speedRange.random().toFloat(),
                                            animationSize = gameAssetsSize.crowAnimationSize,
                                        )
                                    )
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    private fun initSpawnLogic() {
        scope.launch {
            snapshotFlow { level }.distinctUntilChanged().collectLatest {
                startSpawn()
            }
        }
    }

    private fun initMusicController() {
        scope.launch {
            val levelCompleteFlow = snapshotFlow { isLevelCompleted }
            combine(
                levelCompleteFlow,
                TimeManager.timeScale,
                TimeManager.isTimePaused
            ) { levelCompleted, scale, paused ->
                Triple(levelCompleted, scale, paused)
            }.distinctUntilChanged().collectLatest { (levelCompleted, scale, paused) ->
                if (levelCompleted) {
                    AudioManager.stopMusic()
                } else {
                    delay(30L)
                    if (!paused) AudioManager.setMusicSpeed(scale)
                    AudioManager.playMusic(paused)
                }
            }
        }
    }

    fun changeWeapon(index: Int) {
        weapon = when (index) {
            1 -> revolver
            2 -> shotgun
            else -> axe
        }
    }
    

    fun resetGame() {
        scope.coroutineContext.cancelChildren()
        AudioManager.stopMusic()
        TimeManager.setPaused(false)
    }

    fun addAmmunition(ammunition: Ammunition) {
        _ammunition.add(ammunition)
    }
    fun removeAmmunition(ammunition: Ammunition) {
        _ammunition.remove(ammunition)
    }
    fun add(enemy: Enemy) {
        _enemies.add(enemy)
    }

    fun removeEnemy(enemy: Enemy) {
        _enemies.remove(enemy)
    }

    fun killEnemy(enemy: Enemy) {
        _enemies.remove(enemy)
        if (GlobalGameSettings.useBody.value) {
            val body = when (enemy.bodyType) {
                BodyType.Simple_Body -> SimpleBody(
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
                BodyType.Flyable_Body -> FlyableBody(
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
                BodyType.Fixed_Body -> FixedBody(
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
                BodyType.Ghost_body -> GhostBody(
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
            }
            if (_bodies.size > 50) _bodies.removeAt(0)
            _bodies.add(body)
        }
    }

    fun addBlood(blood: Blood) {
        if (!GlobalGameSettings.useBlood.value) return
        if (_bloods.size > 50) _bloods.removeAt(0)
        _bloods.add(blood)
    }
}
