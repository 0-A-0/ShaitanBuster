package com.example.scrolllist.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

val LocalShakeTrigger = compositionLocalOf { 0 }
fun Modifier.shaking(localTrigger: (() -> Int)? = null): Modifier = composed {
    val trigger = rememberUpdatedState(LocalShakeTrigger.current)
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow {
            localTrigger?.invoke() ?: trigger.value
        }.collect { trigger ->
            if (trigger > 0) {
                val target = (30..100).random().toFloat()
                println(target)
                rotation.snapTo(target)
                rotation.animateTo(
                    0f,
                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium)
                )
            }
        }
    }
    this.graphicsLayer {
        rotationY = rotation.value
        cameraDistance = 8f * density
    }
}

// 0 рекомпозиций
fun Modifier.jiggle(trigger: () -> Int): Modifier = composed {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow(trigger).collect { trigger ->
            if (trigger > 0) {
                val target = (10..30).random().toFloat()
                println(target)
                offset.snapTo(target)
                offset.animateTo(0f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium))
            }
        }
    }
    this.graphicsLayer { translationX = offset.value }
}

@Composable
fun Modifier.burning(durationMillis: Int = 3000, delayDuration:Long = 0L): Modifier {
    val radius = remember { Animatable(0.001f) }
    LaunchedEffect(Unit) {
        delay(delayDuration)
        radius.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing),
        )
    }
    return this
        .graphicsLayer(alpha = 0.99f)
        .drawWithContent {
            drawContent()
            val burnGradient = Brush.radialGradient(
                0.0f to Color.Gray,
                0.1f to Color.Black,
                0.80f to Color.White,
                0.88f to Color(0xFFFFD700),
                0.94f to Color(0xFFFF4500),
                0.98f to Color(0xFF8B0000),
                1.0f to Color.Transparent,
                center = center,
                radius = radius.value * size.width
            )
            drawRect(
                brush = burnGradient,
                blendMode = BlendMode.SrcIn
            )
        }
}