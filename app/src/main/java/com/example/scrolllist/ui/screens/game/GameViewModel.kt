package com.example.scrolllist.ui.screens.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.example.scrolllist.data.database.AppDatabase
import com.example.scrolllist.managers.AudioManager
import com.example.scrolllist.managers.TimeManager
import com.example.scrolllist.game.GameEngine
import com.example.scrolllist.ui.assets.GameAssets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    val WinPlayer: () -> Unit,
    var canvasWidth: Float,
    var canvasHeight: Float,
    var mode: Int,
) : AndroidViewModel(application) {
    val gameAssets = GameAssets()

    val scope = CoroutineScope(AndroidUiDispatcher.Main + SupervisorJob())
    var axeTrend by mutableStateOf(true)
    var isActualGame by mutableStateOf(false)
    val userTrackDao = AppDatabase.getInstance(application).userTrackDao()
    lateinit var engine: GameEngine

    init {
        gameAssets.loadAll(application)
        AudioManager.init(application)
    }

    fun startGame(newMode: Int, DeathPlayer: (Int) -> Unit) {
        if (isActualGame) return
        mode = newMode
        scope.launch {
            val uriString = userTrackDao.getChosenTrack()?.uri
            AudioManager.setActualUserTrack(application, uriString)
        }
        engine = GameEngine(
            gameAssets,
            scope,
            DeathPlayer,
            WinPlayer,
            canvasWidth,
            canvasHeight,
            newMode
        )
        axeTrend = engine.axe.trend
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