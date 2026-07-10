package com.example.scrolllist.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.scrolllist.R
import kotlinx.coroutines.delay

@Composable
fun MyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val buttonShape = remember {
        GenericShape { size, _ ->
            val propW = 0.2f
            val firstHalf = Path().apply {
                val spikeHeight = 0f
                val topLineY = 15f
                moveTo(0f, size.height / 2f)
                quadraticTo(
                    size.width * 0.05f,
                    size.height * 0.3f,
                    size.width * 0.08f,
                    spikeHeight
                )
                quadraticTo(size.width * 0.12f, topLineY, size.width * propW, topLineY)
                lineTo(size.width * (1f - propW), topLineY)
                quadraticTo(
                    size.width * (1f - 0.12f),
                    topLineY,
                    size.width * (1f - 0.08f),
                    spikeHeight
                )
                quadraticTo(
                    size.width * (1f - 0.05f),
                    size.height * 0.3f,
                    size.width,
                    size.height / 2f
                )
                close()
            }
            val matrix = Matrix().apply {
                translate(0f, size.height / 2f)
                scale(1f, -1f)
                translate(0f, -size.height / 2f)
            }
            val secondHalf = Path().apply {
                addPath(firstHalf)
                transform(matrix)
            }
            addPath(firstHalf)
            addPath(secondHalf)
        }
    }
    Button(
        shape = buttonShape,
        onClick = onClick,
        modifier = modifier
            .sizeIn(
                minWidth = 100.dp,
                minHeight = 48.dp,
                maxWidth = 300.dp,
                maxHeight = 100.dp
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(R.color.back_content),
//                        colorResource(R.color.back_content),
                        Color(0xFF67210A)
                    )
                ),
                shape = buttonShape
            ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
//            containerColor = colorResource(R.color.back_content),
            containerColor = Color.Transparent,
            contentColor = colorResource(R.color.content)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySlider(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    colors: SliderColors = SliderDefaults.colors(
        thumbColor = Color.Transparent,
        activeTrackColor = Color.Transparent
    ),
    thumbContent: @Composable () -> Unit
) {
    val clipShape = remember {
        GenericShape { size, _ ->
            moveTo(0f, size.height / 2f)
            quadraticTo(size.width / 2f, 0f, size.width, size.height / 2f)
            quadraticTo(size.width / 2f, size.height, 0f, size.height / 2f)
            close()
        }
    }
    var targetRotation by remember { mutableStateOf(0f) }
    LaunchedEffect(value) {
        delay(100)
        targetRotation = 0f
    }
    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = {
            targetRotation = when {
                (it - value) > 0.001f -> -20f
                (it - value) < -0.001f -> 20f
                else -> 0f
            }
            onValueChange(it)
        },
        valueRange = valueRange,
        steps = steps,
        colors = colors,
        thumb = {
            Box(modifier = Modifier.graphicsLayer(rotationZ = rotation)) {
                thumbContent()
            }
        },
        track = {
            val brush = Brush.linearGradient(
                colors = listOf(
                    colorResource(R.color.back_content),
                    colorResource(R.color.content)
                )
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(clipShape)
            ) {
                drawRect(
                    color = Color.DarkGray,
                )
                drawRect(
                    brush = brush,
                    size = size.copy(width = size.width * ((it.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)))
                )
            }
        }
    )
}

@Composable
fun MyCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val QuatrefoilShape = remember {
        GenericShape { size, _ ->
            val r = size.minDimension / 4f
            val cx = size.width / 2f
            val cy = size.height / 2f

            addOval(Rect(cx - r, cy - 2 * r, cx + r, cy))
            addOval(Rect(cx - r, cy, cx + r, cy + 2 * r))
            addOval(Rect(cx - 2 * r, cy - r, cx, cy + r))
            addOval(Rect(cx, cy - r, cx + 2 * r, cy + r))
        }
    }
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkedColor = colorResource(R.color.back_content),
            checkmarkColor = colorResource(R.color.content),
            uncheckedColor = colorResource(R.color.back_content)
        )
    )
}

@Composable
fun VerticalSlider(
    text: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    onChangeValue: (Float) -> Unit,
) {
    val currentValue by rememberUpdatedState(value)
    val color = colorResource(R.color.back_content)
    val rangeLength = remember { range.endInclusive - range.start }
    val previewValue = ((value - range.start) / (rangeLength) * 100f).toInt().coerceIn(0, 100)
    Text(
        text = text.plus(" $previewValue")
    )
    Canvas(
        modifier = modifier
            .drawWithCache {
                val path = Path()
                path.moveTo(size.width / 2f, 0f)
                path.quadraticTo(size.width * 3f, size.height / 2f, size.width / 2f, size.height)
                path.quadraticTo(-size.width * 2f, size.height / 2f, size.width / 2f, 0f)
                path.close()
                onDrawBehind {
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 6f)
                    )
                    clipRect(
                        left = 0f,
                        top = size.height - (size.height * (currentValue / rangeLength)),
                        right = size.width,
                        bottom = size.height,
                    ) {
                        drawPath(
                            path = path,
                            brush = Brush.radialGradient(
                                0f to Color.Red,
                                0.1f to Color.Red.copy(0.7f),
                                0.4f to color,
                                1f to color,
                                center = size.center,
                                radius = size.height,
                            )
                        )
                    }
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val valuePixel = rangeLength / size.height
                    val newValue = currentValue - (dragAmount * valuePixel)
                    onChangeValue(newValue.coerceIn(range))
                }
            }
    ) {

    }
}

