package com.example.scrolllist.units.enemy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import com.example.scrolllist.DrawableWithZ
import com.example.scrolllist.GlobalGameSettings
import com.example.scrolllist.TimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EnemiesList(
    private val scope: CoroutineScope
) {
//    private val _listOfDrawableWithZ = ArrayList<DrawableWithZ>()
    private val _enemies = ArrayList<Enemy>()
    private val _bloods = ArrayList<Blood>()
    private val _bodies = ArrayList<Body>()

//    val listOfDrawableWithZ: List<DrawableWithZ> get() = _listOfDrawableWithZ
    val enemies: List<Enemy> get() = _enemies
    val bloods: List<Blood> get() = _bloods
    val bodies: List<Body> get() = _bodies

    var frame by mutableLongStateOf(0L)
        private set

    fun updateTick() {
        frame++
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
                    view = enemy.bodyView,
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
                BodyType.Flyable_Body -> FlyableBody(
                    view = enemy.bodyView,
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
                BodyType.Fixed_Body -> FixedBody(
                    view = enemy.bodyView,
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
                BodyType.Ghost_body -> GhostBody(
                    view = enemy.bodyView,
                    position = enemy.position,
                    dstSize = enemy.dstSize
                )
            }
            if (_bodies.size > 50) _bodies.removeAt(0)
            _bodies.add(body)
            deleteBodyForTime(body)
        }
    }

    fun addBlood(blood: Blood) {
        if (!GlobalGameSettings.useBlood.value) return
        if (_bloods.size > 50) _bloods.removeAt(0)
        _bloods.add(blood)
        deleteBloodForTime(blood)
    }

    private fun deleteBloodForTime(blood: Blood) {
        scope.launch {
            TimeManager.delay(10_000L)
            _bloods.remove(blood)
        }
    }

    private fun deleteBodyForTime(body: Body) {
        scope.launch {
            TimeManager.delay(1_500L)
            _bodies.remove(body)
        }
    }
}
