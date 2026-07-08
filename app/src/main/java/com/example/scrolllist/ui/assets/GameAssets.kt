package com.example.scrolllist.ui.assets

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.scrolllist.R
import com.example.scrolllist.game.GameAssetsSizeProvider

class GameAssets() : GameAssetsSizeProvider {
    var isLoadedAssets by mutableStateOf(false)
        private set
    lateinit var box_: ImageBitmap
        private set
    lateinit var killmark: ImageBitmap
        private set
    lateinit var axe_killmark: ImageBitmap
        private set
    lateinit var revolver_killmark: ImageBitmap
        private set
    lateinit var playerAssets: PlayerAssets
    var spawnAnimation:List<ImageBitmap> = emptyList()
    lateinit var scarecrowAssets: EnemyAssets
    lateinit var crowAssets: EnemyAssets
    lateinit var smokeAssets: EnemyAssets
    lateinit var spawnerAssets: EnemyAssets
    var scarecrowAnimation: List<ImageBitmap> = emptyList()
        private set
    var crowAnimation: List<ImageBitmap> = emptyList()
        private set
    var crowMirroredAnimation: List<ImageBitmap> = emptyList()
        private set
    var smokeAnimation: List<ImageBitmap> = emptyList()
        private set


    lateinit var playerBack: ImageBitmap
        private set
    var playerBackAnimation: List<ImageBitmap> = emptyList()
        private set

    lateinit var playerFront: ImageBitmap
        private set
    var playerFrontAnimation: List<ImageBitmap> = emptyList()
        private set

    lateinit var playerRight: ImageBitmap
        private set
    var playerRightAnimation: List<ImageBitmap> = emptyList()
        private set

    lateinit var playerLeft: ImageBitmap
        private set
    var playerLeftAnimation: List<ImageBitmap> = emptyList()
        private set

    var bloodAnimation: List<ImageBitmap> = emptyList()
        private set
    var fireAnimation: List<ImageBitmap> = emptyList()
        private set

    lateinit var back: ImageBitmap
        private set
    lateinit var cartridges: ImageBitmap
        private set
    lateinit var pellets: ImageBitmap
        private set
    lateinit var hurt_frame: ImageBitmap
        private set
    lateinit var axe_image: ImageBitmap
        private set
    lateinit var axe_image_mirror: ImageBitmap
        private set
    lateinit var axe_low_effect: ImageBitmap
        private set
    lateinit var axe_low_effect_mirror: ImageBitmap
        private set
    lateinit var axe_strong_effect_mirror: ImageBitmap
        private set
    lateinit var axe_strong_effect: ImageBitmap
        private set
    lateinit var revolver_left: ImageBitmap
        private set
    lateinit var revolver_left_1: ImageBitmap
        private set
    lateinit var revolver_right: ImageBitmap
        private set
    lateinit var revolver_right_1: ImageBitmap
        private set
    var revolverRightAnimation: List<ImageBitmap> = emptyList()
        private set
    var revolverLeftAnimation: List<ImageBitmap> = emptyList()
        private set
    lateinit var shotgun_left: ImageBitmap
        private set
    lateinit var shotgun_right: ImageBitmap
        private set
    lateinit var weaponAssets: WeaponAssets
        private set
    override val crowAnimationSize: Int
        get() = crowAnimation.size
    override val playerAnimationBackSize: Int
        get() = playerBackAnimation.size
    override val playerAnimationFrontSize: Int
        get() = playerFrontAnimation.size
    override val playerAnimationLeftSize: Int
        get() = playerLeftAnimation.size
    override val playerAnimationRightSize: Int
        get() = playerRightAnimation.size
    override val scarecrowAnimationSize: Int
        get() = scarecrowAnimation.size
    override val smokeAnimationSize: Int
        get() = smokeAnimation.size
    override val spawnAnimationSize: Int
        get() = spawnAnimation.size
    override val fireAnimationSize: Int
        get() =fireAnimation.size
    override val revolverAnimationSize: Int
        get() = revolverRightAnimation.size

    fun loadAll(contex: Context) {
        if (isLoadedAssets) return
        fun loadListOfFrames(ids: List<Int>): List<ImageBitmap> {
            val cacheIds = ids.distinct().associateWith { id ->
                BitmapFactory.decodeResource(contex.resources, id).asImageBitmap()
            }
            return ids.map { id -> cacheIds[id]!! }
        }

        fun loadImageBitmap(id: Int): ImageBitmap {
            return BitmapFactory.decodeResource(contex.resources, id).asImageBitmap()
        }
        box_ = loadImageBitmap(R.drawable.box)
        killmark = loadImageBitmap(R.drawable.killmark)
        axe_killmark = loadImageBitmap(R.drawable.axe_killmark)
        revolver_killmark = loadImageBitmap(R.drawable.revolver_killmark)

        scarecrowAnimation = loadListOfFrames(
            listOf(
                R.drawable.scarecrow_front_1,
                R.drawable.scarecrow_front_2,
                R.drawable.scarecrow_front_3,
                R.drawable.scarecrow_front_4,
                R.drawable.scarecrow_front_5,
                R.drawable.scarecrow_front_6
            )
        )

        playerBack = loadImageBitmap(R.drawable.player_back)
        playerBackAnimation = loadListOfFrames(
            listOf(
                R.drawable.player_back_1,
                R.drawable.player_back_2,
                R.drawable.player_back_3,
                R.drawable.player_back_4,
                R.drawable.player_back_5
            )
        )

        playerFront = loadImageBitmap(R.drawable.player_front)
        playerFrontAnimation = loadListOfFrames(
            listOf(
                R.drawable.player_front_1,
                R.drawable.player_front_2,
                R.drawable.player_front_3,
                R.drawable.player_front_4,
                R.drawable.player_front_5,
                R.drawable.player_front_6
            )
        )

        playerRight = loadImageBitmap(R.drawable.player_right)
        playerRightAnimation = loadListOfFrames(
            listOf(
                R.drawable.player_right_1,
                R.drawable.player_right_2,
                R.drawable.player_right_3,
                R.drawable.player_right_4,
                R.drawable.player_right_5,
                R.drawable.player_right_6
            )
        )

        playerLeft = loadImageBitmap(R.drawable.player_left)
        playerLeftAnimation = loadListOfFrames(
            listOf(
                R.drawable.player_left_1,
                R.drawable.player_left_2,
                R.drawable.player_left_3,
                R.drawable.player_left_4,
                R.drawable.player_left_5,
                R.drawable.player_left_6
            )
        )

        bloodAnimation = loadListOfFrames(
            listOf(
                R.drawable.blood_1, R.drawable.blood_2, R.drawable.blood_3,
                R.drawable.blood_4, R.drawable.blood_5, R.drawable.blood_6,
                R.drawable.blood_7, R.drawable.blood_8, R.drawable.blood_9,
                R.drawable.blood_10
            )
        )

        fireAnimation = loadListOfFrames(
            listOf(
                R.drawable.fire_0, R.drawable.fire_1, R.drawable.fire_2,
                R.drawable.fire_3, R.drawable.fire_4, R.drawable.fire_5,
                R.drawable.fire_6, R.drawable.fire_7, R.drawable.fire_8,
                R.drawable.fire_9, R.drawable.fire_10,
                R.drawable.fire_9, R.drawable.fire_8, R.drawable.fire_7,
                R.drawable.fire_6, R.drawable.fire_5, R.drawable.fire_4,
                R.drawable.fire_3, R.drawable.fire_2, R.drawable.fire_1
            )
        )

        back = loadImageBitmap(R.drawable.map)
        cartridges = loadImageBitmap(R.drawable.cartridges)
        pellets = loadImageBitmap(R.drawable.pellets)
        hurt_frame = loadImageBitmap(R.drawable.hurt_frame)
        axe_image = loadImageBitmap(R.drawable.axe)
        axe_image_mirror = loadImageBitmap(R.drawable.axe_mirror)
        axe_low_effect = loadImageBitmap(R.drawable.axe_low_effect)
        axe_low_effect_mirror = loadImageBitmap(R.drawable.axe_low_effect_mirror)
        axe_strong_effect_mirror = loadImageBitmap(R.drawable.axe_strong_effect_mirror)
        axe_strong_effect = loadImageBitmap(R.drawable.axe_strong_effect)
        revolver_left = loadImageBitmap(R.drawable.revolver_left)
        revolver_left_1 = loadImageBitmap(R.drawable.revolver_left_1)
        revolver_right = loadImageBitmap(R.drawable.revolver_right)
        revolver_right_1 = loadImageBitmap(R.drawable.revolver_right_1)

        revolverRightAnimation = listOf(revolver_right, revolver_right_1)
        revolverLeftAnimation = listOf(revolver_left, revolver_left_1)

        shotgun_left = loadImageBitmap(R.drawable.shotgun_left)
        shotgun_right = loadImageBitmap(R.drawable.shotgun_right)
        crowAnimation = loadListOfFrames(listOf(
            R.drawable.crow_0, R.drawable.crow_1, R.drawable.crow_2,
            R.drawable.crow_3, R.drawable.crow_4, R.drawable.crow_5,
            R.drawable.crow_6, R.drawable.crow_7, R.drawable.crow_8,
            R.drawable.crow_7, R.drawable.crow_6, R.drawable.crow_5,
            R.drawable.crow_4, R.drawable.crow_3, R.drawable.crow_2, R.drawable.crow_1
        ))
        crowMirroredAnimation = loadListOfFrames(
            listOf(
                R.drawable.crow_mirror_0, R.drawable.crow_mirror_1, R.drawable.crow_mirror_2,
                R.drawable.crow_mirror_3, R.drawable.crow_mirror_4, R.drawable.crow_mirror_5,
                R.drawable.crow_mirror_6, R.drawable.crow_mirror_7, R.drawable.crow_mirror_8,
                R.drawable.crow_mirror_7, R.drawable.crow_mirror_6, R.drawable.crow_mirror_5,
                R.drawable.crow_mirror_4, R.drawable.crow_mirror_3, R.drawable.crow_mirror_2,
                R.drawable.crow_mirror_1
            )
        )
        spawnAnimation = loadListOfFrames(
            listOf(
                R.drawable.spawnanim_0, R.drawable.spawnanim_1, R.drawable.spawnanim_2,
                R.drawable.spawnanim_3, R.drawable.spawnanim_4, R.drawable.spawnanim_5,
                R.drawable.spawnanim_6
            )
        )
        smokeAnimation = loadListOfFrames(
            listOf(
                R.drawable.smoke,
                R.drawable.smoke_1, R.drawable.smoke_2, R.drawable.smoke_3, R.drawable.smoke_4,
                R.drawable.smoke_3, R.drawable.smoke_2, R.drawable.smoke_1
            )
        )
        val spawner = loadImageBitmap(R.drawable.totem)
        playerAssets = PlayerAssets(
            viewRight = playerRight,
            viewFront = playerFront,
            viewLeft = playerLeft,
            viewBack = playerBack,
            animation = Animation(
                back = playerBackAnimation,
                front = playerFrontAnimation,
                right = playerRightAnimation,
                left = playerLeftAnimation,
            )
        )
        scarecrowAssets = EnemyAssets(
            frontAnimation = scarecrowAnimation,
            spawnAnimation = spawnAnimation,
            bodyView = scarecrowAnimation.first()
        )
        crowAssets = EnemyAssets(
            frontAnimation = crowAnimation,
            frontAnimationMirrored = crowMirroredAnimation,
            bodyView = crowAnimation.first(),
            spawnAnimation = spawnAnimation,
        )
        spawnerAssets = EnemyAssets(
            bodyView = spawner,
            view = spawner,
            spawnAnimation = spawnAnimation
        )
        smokeAssets = EnemyAssets(
            frontAnimation = smokeAnimation,
            bodyView = smokeAnimation.first(),
            spawnAnimation = spawnAnimation,
        )
        weaponAssets = WeaponAssets(
            axe = axe_image,
            axe_mirror = axe_image_mirror,
            axe_low_effect_mirror = axe_low_effect_mirror,
            axe_low_effect = axe_low_effect,
            axe_strong_effect = axe_strong_effect,
            axe_strong_effect_mirror = axe_strong_effect_mirror,
            present_view_axe = axe_image,
            viewLeftAnimation = revolverLeftAnimation,
            viewRightAnimation = revolverRightAnimation,
            present_view_revolver = revolver_right,
            viewRightShotgun = shotgun_right,
            viewLeftShotgun = shotgun_left,
            present_view_shotgun = shotgun_right
        )
        isLoadedAssets = true
    }
}
