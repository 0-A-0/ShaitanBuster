package com.example.scrolllist.ui.screens.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.AndroidViewModel
import com.example.scrolllist.managers.AudioManager
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.game.GameEngine
import com.example.scrolllist.ui.assets.GameAssets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class GameViewModel(
    application: Application,
    val DeathPlayer: (Int) -> Unit,
    val WinPlayer: () -> Unit,
    var canvasWidth: Float,
    var canvasHeight: Float,
    var mode: Int,
) : AndroidViewModel(application) {
    val gameAssets = GameAssets()

    val scope = CoroutineScope(AndroidUiDispatcher.Main + SupervisorJob())
    var axeTrend by mutableStateOf(true)
    var isActualGame by mutableStateOf(false)
    lateinit var engine: GameEngine

    init {
        gameAssets.loadAll(application)
        AudioManager.init(application)
    }
    fun ChangeZoom(newZoom: Float, unsucsessAction: () -> Unit) {
//        if (canvasWidth / newZoom <= mapSize.width && canvasHeight / newZoom <= mapSize.height) {
//            zoom = newZoom
//            checkScroll()
//        } else {
//            unsucsessAction()
//        }
    }

    fun startGame(newMode: Int) {
        if (isActualGame) return
        mode = newMode
        engine = GameEngine(gameAssets,scope,DeathPlayer,WinPlayer,canvasWidth,canvasHeight,newMode)
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