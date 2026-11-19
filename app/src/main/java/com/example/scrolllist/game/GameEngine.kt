package com.example.scrolllist.game

import android.graphics.RectF
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.scrolllist.managers.AudioManager
import com.example.scrolllist.data.GlobalGameSettings
import com.example.scrolllist.domain.LevelStates
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.domain.utils.blazingReflectFor
import com.example.scrolllist.domain.utils.blazingRemove
import com.example.scrolllist.domain.utils.calcAngle
import com.example.scrolllist.domain.utils.calcDistanceForComparison
import com.example.scrolllist.domain.utils.checkNotCollision
import com.example.scrolllist.domain.objects.Ammunition
import com.example.scrolllist.domain.objects.AmmunitionType
import com.example.scrolllist.domain.objects.BoxObject
import com.example.scrolllist.domain.objects.FirePoint
import com.example.scrolllist.domain.objects.Killmark
import com.example.scrolllist.domain.objects.KillmarkController
import com.example.scrolllist.domain.objects.KillmarkView
import com.example.scrolllist.domain.units.Player
import com.example.scrolllist.domain.units.enemy.Blood
import com.example.scrolllist.domain.units.enemy.bodies.Body
import com.example.scrolllist.domain.units.enemy.bodies.BodyType
import com.example.scrolllist.domain.units.enemy.Crow
import com.example.scrolllist.domain.units.enemy.CrowMinion
import com.example.scrolllist.domain.units.enemy.Enemy
import com.example.scrolllist.domain.units.enemy.bodies.FixedBody
import com.example.scrolllist.domain.units.enemy.bodies.FlyableBody
import com.example.scrolllist.domain.units.enemy.bodies.GhostBody
import com.example.scrolllist.domain.units.enemy.Scarecrow
import com.example.scrolllist.domain.units.enemy.bodies.SimpleBody
import com.example.scrolllist.domain.units.enemy.Smoke
import com.example.scrolllist.domain.units.enemy.Spawner
import com.example.scrolllist.domain.units.weapon.Axe
import com.example.scrolllist.domain.units.weapon.Revolver
import com.example.scrolllist.domain.units.weapon.Shotgun
import com.example.scrolllist.domain.units.weapon.Weapon
import com.example.scrolllist.domain.DrawableWithZ
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

    val baseCanvasWidth = 1080f
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
            1 -> LevelStates(
                spawnTime = 200L,
                spawnTimeCrow = 2000L,
                spawnTimeSpawner = 0L,
                enemyScarecrowCounts = 40,
                enemyCrowCounts = 0,
                enemySmokeCounts = 0,
                enemySpawnerCounts = 0,
                speedScarecrowRange = (100..200),
                speedMinionRange = (100..200)
            )

            2 -> LevelStates(
                spawnTime = 150L,
                spawnTimeCrow = 1200L,
                spawnTimeSpawner = 5000L,
                enemyScarecrowCounts = 60,
                enemyCrowCounts = 10,
                enemySmokeCounts = 0,
                enemySpawnerCounts = 0,
                speedScarecrowRange = (200..250),
                speedCrowRange = (350..450)
            )

            3 -> LevelStates(
                spawnTime = 400L,
                spawnTimeCrow = 450L,
                spawnTimeSpawner = 5000L,
                enemyScarecrowCounts = 50,
                enemyCrowCounts = 30,
                enemySmokeCounts = 0,
                enemySpawnerCounts = 0,
                speedScarecrowRange = (200..300),
                speedCrowRange = (350..500)
            )

            4 -> LevelStates(
                spawnTime = 150L,
                spawnTimeCrow = 1500L,
                spawnTimeSpawner = 5000L,
                enemyScarecrowCounts = 120,
                enemyCrowCounts = 12,
                enemySmokeCounts = 8,
                enemySpawnerCounts = 0,
                speedScarecrowRange = (200..300),
                speedCrowRange = (200..300),
                speedSmokeRange = (350..450)
            )

            5 -> LevelStates(
                //до сюда хорошо, просто количество прибавить
                spawnTime = 2000L,
                spawnTimeCrow = 1000L,
                spawnTimeSpawner = 3700L,
                enemyScarecrowCounts = 0,
                enemyCrowCounts = 0,
                enemySmokeCounts = 16,
                enemySpawnerCounts = 6,
                speedScarecrowRange = (450..600),
                speedSmokeRange = 400..500,
                speedCrowRange = (500..650),
                speedMinionRange = (300..450)
            )

            6 -> LevelStates(
                spawnTime = 150L,
                spawnTimeCrow = 2000L,
                spawnTimeSpawner = 5000L,
                enemyScarecrowCounts = 100,
                enemyCrowCounts = 10,
                enemySmokeCounts = 40,
                enemySpawnerCounts = 0,
                speedScarecrowRange = (200..300),
                speedCrowRange = (350..400),
                speedSmokeRange = (450..500)
            )

            7 -> LevelStates(
                spawnTime = 100L,
                spawnTimeCrow = 1000L,
                spawnTimeSpawner = 3000L,
                enemyScarecrowCounts = 100,
                enemyCrowCounts = 30,
                enemySmokeCounts = 15,
                enemySpawnerCounts = 2,
                speedScarecrowRange = (300..400),
                speedCrowRange = (300..450),
                speedSmokeRange = (300..450),
                speedMinionRange = (300..400)
            )

            8 -> LevelStates(
                // интересный уровень
                spawnTime = 600L,
                spawnTimeCrow = 250L,
                spawnTimeSpawner = 7000L,
                enemyScarecrowCounts = 0,
                enemyCrowCounts = 100,
                enemySmokeCounts = 0,
                enemySpawnerCounts = 1,
                speedScarecrowRange = (500..650),
                speedCrowRange = (650..800),
                speedMinionRange = (400..400)
            )

            9 -> LevelStates(
                spawnTime = 80L,
                spawnTimeCrow = 4000L,
                spawnTimeSpawner = 1000L,
                enemyScarecrowCounts = 0,
                enemyCrowCounts = 2,
                enemySmokeCounts = 25,
                enemySpawnerCounts = 10,
                speedScarecrowRange = (300..400),
                speedCrowRange = (400..550),
                speedSmokeRange = (400..500),
                speedMinionRange = (300..400)
            )

            10 -> LevelStates(
                // Можно оставить
                spawnTime = 20L,
                spawnTimeCrow = 600L,
                spawnTimeSpawner = 3000L,
                enemyScarecrowCounts = 1000,
                enemyCrowCounts = 0,
                enemySmokeCounts = 0,
                enemySpawnerCounts = 0,
                speedScarecrowRange = (200..300),
                speedCrowRange = (650..800),
                speedSmokeRange = (600..800),
                speedMinionRange = (350..500)
            )

            11 -> LevelStates(
                spawnTime = 40L,
                spawnTimeCrow = 450L,
                spawnTimeSpawner = 2500L,
                enemyScarecrowCounts = 300,
                enemyCrowCounts = 65,
                enemySmokeCounts = 60,
                enemySpawnerCounts = 8,
                speedScarecrowRange = (200..300),
                speedCrowRange = (400..700),
                speedSmokeRange = (200..350),
                speedMinionRange = (350..400)
            )

            12 -> LevelStates(
                spawnTime = 30L,
                spawnTimeCrow = 350L,
                spawnTimeSpawner = 2000L,
                enemyScarecrowCounts = 400,
                enemyCrowCounts = 80,
                enemySmokeCounts = 100,
                enemySpawnerCounts = 6,
                speedScarecrowRange = (200..350),
                speedCrowRange = (200..300),
                speedSmokeRange = (250..450),
                speedMinionRange = (400..550)
            )

            else -> LevelStates(
                spawnTime = 30L,
                spawnTimeCrow = 350L,
                spawnTimeSpawner = 2000L,
                enemyScarecrowCounts = 400,
                enemyCrowCounts = 80,
                enemySmokeCounts = 100,
                enemySpawnerCounts = 6,
                speedScarecrowRange = (650..850),
                speedCrowRange = (650..800),
                speedSmokeRange = (650..850),
                speedMinionRange = (400..550)
            )
        }
    var kills = 0
    val mapSize = IntSize(2000, 2500)
    val spawnFieldX = (0..mapSize.width-300)
    val spawnFieldY = (0..mapSize.height-300)
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
                        spawnFieldX.random().toFloat(),
                        spawnFieldY.random().toFloat()
                    )
                )
            }.toMutableList()
            // удаление ящиков в важных точках
            list.blazingRemove { box ->
                box.collisionRect.overlaps(player.collisionRect) || box.collisionRect.overlaps(
                        firePoint.collisionRect
                    )
            }
            list
        }

    fun changeZoom() {
        val newZoom = canvasWidth/baseCanvasWidth
        if (canvasWidth / newZoom <= mapSize.width && canvasHeight / newZoom <= mapSize.height) {
            zoom = newZoom
        } else {
            val zoomByWidth = canvasWidth / mapSize.width
            val zoomByHeight = canvasHeight / mapSize.height
            zoom = maxOf(zoomByWidth, zoomByHeight)
        }
        checkScroll()
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
        changeZoom()
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
//            player.hitByRecoil()
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
            if (mode == 0) {
                checkLevelProgress()
                checkIsLevelComplete(deltaTime)
            }
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

    fun onHitBody(body: Body, angle: Float, power: Int, weaponPower: Float = 1f) {
        body.addBias(angle = angle, power = power, powerWeapon = weaponPower)
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
            onSuccessAction = {  },
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
                    enemy.onHitEffect()
                }
            },
            onHitBody = ::onHitBody
        )
        if(!isHoly) onKill(killCount)
    }
//    делает игрока неуязвимым при абЪюзе(
//    fun Player.hitByRecoil(){
//        if(recoil != Offset.Zero) {
//            val hitDistance = recoilHitRadius * recoilHitRadius
//            val playerCenter = center
//            _enemies.blazingReflectFor { enemy ->
//                val enemyCenter = enemy.center
//                if(calcDistanceForComparison(enemyCenter,playerCenter) <= hitDistance){
//                    if (enemy.killable) {
//                        killEnemy(enemy)
//                        kills += enemy.killWeight
//                        if (Random.nextFloat() < 0.1f) {
//                            val ammunition = Ammunition(
//                                value = 12,
//                                type = AmmunitionType.Cartridges,
//                                startPosition = enemyCenter
//                            )
//                            addAmmunition(ammunition)
//                        }
//                    } else {
//                        enemy.onHitEffect()
//                    }
//                }
//            }
//            _bodies.blazingReflectFor { body ->
//                val bodyCenter = body.center
//                if(calcDistanceForComparison(bodyCenter,playerCenter) <= hitDistance){
//                    val angle = calcAngle(bodyCenter,playerCenter)
//                    onHitBody(body,angle,5,5f)
//                }
//            }
//        }
//    }
    fun Shotgun.doFire(position: Offset) {
        this.angle = calcAngle(position, player.center)
        this.animateShotgun(position)
        var killCount = 0
        this.fire(
            onSuccessAction = { player.startRecoil(position)},
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
                    enemy.onHitEffect()
                }
            },
            onHitBody = ::onHitBody
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
            )
            shotgun.targetMagnet = null
            trigger++
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
                    } else enemy.onHitEffect()
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
                    } else enemy.onHitEffect()
                },
                onHitBody = ::onHitBody,
            )
        }
    }

    fun axeOnDragCancel() {
        axe.slicePoints.clear()
    }

    fun changeAxeRotate(newTrend: Boolean) {
        axe.trend = newTrend
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
                                spawnFieldX.random().toFloat(),
                                spawnFieldY.random().toFloat()
                            ),
                            speed = speedScarecrowRange.random().toFloat(),
                            animationSize = gameAssetsSize.scarecrowAnimationSize
                        )
                    )
                }
            }
            scope.launch {
                for (i in 0 until enemyCrowCounts) {
                    TimeManager.delay(spawnTimeCrow)
                    add(
                        enemy =
                        Crow(
                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                            startPosition = Offset(
                                spawnFieldX.random().toFloat(),
                                spawnFieldY.random().toFloat()
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
                    TimeManager.delay(spawnTimeSpawner)
                    val spawner = Spawner(
                        spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                        startPosition = Offset(
                            spawnFieldX.random().toFloat(),
                            spawnFieldY.random().toFloat()
                        ),
                        speedMinionRange = levelStates.speedMinionRange,
                        crowAnimationSize = gameAssetsSize.crowAnimationSize
                    )
                    add(enemy = spawner)
                    add(enemy = spawner.minion)
                }
            }
            scope.launch {
                for (i in 0 until enemySmokeCounts) {
                    TimeManager.delay(spawnTime)
                    add(
                        enemy = Smoke(
                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                            startPosition = Offset(
                                spawnFieldX.random().toFloat(),
                                spawnFieldY.random().toFloat()
                            ),
                            speed = speedSmokeRange.random().toFloat(),
                            animationSize = gameAssetsSize.smokeAnimationSize
                        )
                    )
                }
            }
        }
        if (mode == 1) {
            var speedRange = (200..300)
            var spawnTime = 1000L
            val speedRangeMinion = (300..400)
            var bloodyTime = false
            scope.launch {
                while (true) {
                    TimeManager.delay(10_000L)
                    if(spawnTime >= 100L) {
                        spawnTime = (spawnTime - 300L).coerceAtLeast(40L)
                        if (spawnTime <= 100L) bloodyTime = true
                    } else {
                        spawnTime = 1000L
                        bloodyTime = false
                        speedRange = speedRange.start+50..speedRange.endInclusive+50
                    }
                }
            }
            scope.launch {
                while (true) {
                    val random = Random.nextFloat()
                    TimeManager.delay(spawnTime)
//                    if (random < 0.8f) {
                        add(
                            enemy =
                            Scarecrow(
                                spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                startPosition = Offset(
                                    spawnFieldX.random().toFloat(),
                                    spawnFieldY.random().toFloat()
                                ),
                                speed = speedRange.random().toFloat(),
                                animationSize = gameAssetsSize.scarecrowAnimationSize
                            )
                        )
//                    }
                    if (!bloodyTime && random < 0.15f) {
                        add(
                            enemy =
                            Crow(
                                spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                                startPosition = Offset(
                                    spawnFieldX.random().toFloat(),
                                    spawnFieldY.random().toFloat()
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
                                    spawnFieldX.random().toFloat(),
                                    spawnFieldY.random().toFloat()
                                ),
                                speed = speedRange.random().toFloat(),
                                animationSize = gameAssetsSize.smokeAnimationSize
                            )
                        )
                    }
                    if (!bloodyTime && random < 0.01f) {
                        val spawner = Spawner(
                            spawnAnimationSize = gameAssetsSize.spawnAnimationSize,
                            startPosition = Offset(
                                spawnFieldX.random().toFloat(),
                                spawnFieldY.random().toFloat()
                            ),
                            speedMinionRange = speedRangeMinion,
                            crowAnimationSize = gameAssetsSize.crowAnimationSize
                        )
                        add(enemy = spawner)
                        add(enemy = spawner.minion)
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
                TimeManager.isTimePaused
            ) { levelCompleted, paused ->
                Pair(levelCompleted, paused)
            }.distinctUntilChanged().collectLatest { (levelCompleted, paused) ->
                if (levelCompleted) {
                    AudioManager.stopMusic()
                } else {
                    delay(30L)
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
        if (enemy is Spawner) {
            killEnemy(enemy.minion)
        }
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
