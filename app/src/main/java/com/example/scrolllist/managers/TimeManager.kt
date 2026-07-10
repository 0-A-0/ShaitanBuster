package com.example.scrolllist.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

object TimeManager {
    private val _timeScale = MutableStateFlow(1.0f)
    val timeScale: StateFlow<Float> = _timeScale.asStateFlow()

    private val _isTimePaused = MutableStateFlow(false)
    val isTimePaused: StateFlow<Boolean> = _isTimePaused.asStateFlow()

    fun getScaledTime(time:Long):Long{
        return (time / _timeScale.value).toLong()
    }

    fun setTimeScale(scale: Float) {
        if (scale <= 0f) return
        _timeScale.value = scale
    }

    fun setPaused(isPaused: Boolean) {
        _isTimePaused.value = isPaused
    }
    suspend fun delay(durationMillis: Long) {
        _isTimePaused.first { isPaused -> !isPaused } //ждет пока пауза не станет ложной
        val currentScale = _timeScale.value

        val actualDelay = (durationMillis / currentScale).toLong()
        kotlinx.coroutines.delay(actualDelay)
    }
}