package com.example.scrolllist

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.atan2
import kotlin.math.sqrt

fun calcDistance(startPoint: Offset, endPoint: Offset): Float {
    return sqrt(
        (startPoint.x - endPoint.x) * (startPoint.x - endPoint.x) + (startPoint.y - endPoint.y) * (startPoint.y - endPoint.y)
    )
}
fun calcDistanceForСomparison(startPoint: Offset, endPoint: Offset): Float {
       return (startPoint.x - endPoint.x) * (startPoint.x - endPoint.x) + (startPoint.y - endPoint.y) * (startPoint.y - endPoint.y)
}
fun calcAngle(enemy: Offset, player: Offset): Float {
    val dx = enemy.x - player.x
    val dy = player.y - enemy.y
    val angleRad = atan2(dx, dy)
    var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
    if (angleDeg < 0) {
        angleDeg += 360f
    }
    return angleDeg
}

fun caclAxePower(speed: Float): Int {
    return ((speed - 10f) / 20f * 9f + 1f).toInt()
}

fun calculatePowerByHit(hitCount: Int, maxHits: Int = 5): Int {
    val p = 10 - (hitCount * (8 / (maxHits - 1)))
    return p.coerceIn(1, 10)
}

fun calculatePowerByDist(distSq: Float, maxDistSq: Float): Int {
    val ratio = distSq / maxDistSq
    val p = (1.0f - ratio) * 10f
    return p.toInt().coerceIn(0, 10)
}

fun isIntersectWithLine(r: Rect, start: Offset, end: Offset, isInfinityLine: Boolean = false): Boolean {
    val (x1, y1) = start
    val (x2, y2) = end

    if(!isInfinityLine) {
        if (r.left > maxOf(x1, x2) || r.right < minOf(x1, x2) ||
            r.top > maxOf(y1, y2) || r.bottom < minOf(y1, y2)
        ) {
            return false
        }
    } else if((x2 - x1) * (r.center.x - x1) + (y2 - y1) * (r.center.y - y1) < 0) return false

    fun side(px: Float, py: Float) = (y1 - y2) * px + (x2 - x1) * py + (x1 * y2 - x2 * y1)

    val s1 = side(r.left, r.top)
    val s2 = side(r.right, r.top)
    val s3 = side(r.right, r.bottom)
    val s4 = side(r.left, r.bottom)

    return !(s1 > 0 && s2 > 0 && s3 > 0 && s4 > 0 ||
            s1 < 0 && s2 < 0 && s3 < 0 && s4 < 0)
}

fun checkNotCollision(checkRect: Rect, rect: Rect): Boolean {
    return checkRect.intersect(rect).isEmpty
}

