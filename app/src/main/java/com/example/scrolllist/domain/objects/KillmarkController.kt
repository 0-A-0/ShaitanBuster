package com.example.scrolllist.domain.objects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
class KillmarkController(
    val scope: CoroutineScope,
) {
    private var currentAlphaJob: Job? = null
    var killmark by mutableStateOf<Killmark?>(null)
        private set
    val alpha = Animatable(0f)
    val scale = Animatable(1f)
    fun startAnimation(newKillmark: Killmark) {
        currentAlphaJob?.cancel()
        currentAlphaJob = scope.launch {
            killmark = newKillmark
            alpha.snapTo(0f)
            scale.snapTo(1f)
            launch {
                scale.animateTo(
                    targetValue = 1.5f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 500)
            )
            killmark = null
        }
    }
}