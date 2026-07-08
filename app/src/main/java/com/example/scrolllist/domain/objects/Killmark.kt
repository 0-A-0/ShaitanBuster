package com.example.scrolllist.domain.objects

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import com.example.scrolllist.R

@Immutable
class Killmark(
    val view:KillmarkView? = null,
    val text: String? = null,
    val dstSize: IntSize = IntSize(200,200),
    ) {
    companion object {
        val KillmarkStyle = TextStyle(
            fontFamily = FontFamily(Font(R.font.kelly_slab)),
            color = Color(0xFFFFF3E0),
            fontSize = 40.sp,
            shadow = Shadow(color = Color(0xFFC03B11), blurRadius = 25f),
            textAlign = TextAlign.Center
        )
    }

//    fun DrawScope.draw(
//        textMeasurer: TextMeasurer,
//        position: IntOffset,
//        alpha: Float,
//        scale: Float
//    ){
//        view?.let {
//            scale(scale) {
//                drawImage(
//                    dstOffset = position,
//                    image = it,
//                    dstSize = dstSize,
//                    alpha = alpha,
//                    filterQuality = FilterQuality.None
//                )
//            }
//        }
//        text?.let {
//            val text = textMeasurer.measure(
//                text = it,
//                style = KillmarkStyle
//            )
//            drawText(
//                textLayoutResult = text,
//                topLeft = (position + IntOffset(dstSize.width/2 - text.size.width/2, 0)).toOffset(),
//            )
//        }
//    }
}