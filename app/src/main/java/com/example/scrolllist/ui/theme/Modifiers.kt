package com.example.scrolllist.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

val LocalShakeTrigger = compositionLocalOf { 0 }

fun Modifier.shaking(localTrigger: Int? = null): Modifier = composed {
    val trigger = localTrigger ?: LocalShakeTrigger.current
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            val target = (10..30).random().toFloat()
            println(target)
            rotation.snapTo(target)
            rotation.animateTo(0f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium))
        }
    }
    this.graphicsLayer{
        rotationY = rotation.value
        cameraDistance = 8f * density
    }
}

fun Modifier.jiggle(trigger: Int): Modifier = composed {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            val target = (10..30).random().toFloat()
            println(target)
            offset.snapTo(target)
            offset.animateTo(0f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium))
        }
    }
    this.graphicsLayer{translationX = offset.value}
}