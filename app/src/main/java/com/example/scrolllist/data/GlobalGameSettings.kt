package com.example.scrolllist.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import com.example.scrolllist.gameDataStore
import com.example.scrolllist.managers.AudioManager
import com.example.scrolllist.managers.TimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object GlobalGameSettings {
    private val BRIGHTNESS = floatPreferencesKey("brightness")
    private val USE_BODY = booleanPreferencesKey("use_body")
    private val USE_BLOOD = booleanPreferencesKey("use_blood")
    private val SOUND_VALUE = floatPreferencesKey("sound_value")
    private val MUSIC_VALUE = floatPreferencesKey("music_value")
    private val TIME_SCALE = floatPreferencesKey("time_scale")

    private val scope = CoroutineScope(Dispatchers.IO)
    var brightness by mutableStateOf(0.1f)
        private set
    private val _useBody = MutableStateFlow(true)
    val useBody: StateFlow<Boolean> = _useBody.asStateFlow()

    private val _useBlood = MutableStateFlow(true)
    val useBlood: StateFlow<Boolean> = _useBlood.asStateFlow()

    fun init(context: Context){
        scope.launch {
            context.gameDataStore.data.collect{
                brightness = it[BRIGHTNESS] ?: 0.1f
                _useBody.value = it[USE_BODY] ?: true
                _useBlood.value = it[USE_BLOOD] ?: true
                AudioManager.setMusicVolume(it[MUSIC_VALUE] ?: 0.3f)
                AudioManager.setSoundVolume(it[SOUND_VALUE] ?: 1f)
                TimeManager.setTimeScale(it[TIME_SCALE] ?: 1f)
            }
        }
    }

    fun setUseBody(boolean: Boolean){
        _useBody.value = boolean
    }

    fun setUseBlood(boolean: Boolean){
        _useBlood.value = boolean
    }

    fun changeBrightness(volume:Float){
        brightness = volume
    }

    fun save(context: Context){
        scope.launch {
            context.gameDataStore.edit {
                it[BRIGHTNESS] = brightness
                it[USE_BODY] = useBody.value
                it[USE_BLOOD] = useBlood.value
                it[SOUND_VALUE] = AudioManager._soundVolume
                it[MUSIC_VALUE] = AudioManager._musicVolume
                it[TIME_SCALE] = TimeManager.timeScale.value
            }
        }
    }
}