package com.example.scrolllist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object GlobalGameSettings {
    var brightness by mutableStateOf(0.1f)
        private set
    private val _useBody = MutableStateFlow(true)
    val useBody: StateFlow<Boolean> = _useBody.asStateFlow()

    private val _useBlood = MutableStateFlow(true)
    val useBlood: StateFlow<Boolean> = _useBlood.asStateFlow()

    fun setUseBody(boolean: Boolean){
        _useBody.value = boolean
    }

    fun setUseBlood(boolean: Boolean){
        _useBlood.value = boolean
    }

    fun changeBrightness(volume:Float){
        brightness = volume
    }
}