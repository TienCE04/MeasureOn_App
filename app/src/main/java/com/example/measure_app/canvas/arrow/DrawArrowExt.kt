package com.example.measure_app.canvas.arrow

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// --- Hàm kiểm tra điểm gần arrow ---
fun isNearLine(arrow: ArrowDataView, x: Float, y: Float, tolerance: Float = 30f): Boolean {
    val dx = arrow.endX - arrow.startX
    val dy = arrow.endY - arrow.startY
    val lengthSq = dx * dx + dy * dy

    if (lengthSq == 0f) return distance(arrow.startX, arrow.startY, x, y) < tolerance

    val t = ((x - arrow.startX) * dx + (y - arrow.startY) * dy) / lengthSq
    val closestX = when {
        t < 0 -> arrow.startX
        t > 1 -> arrow.endX
        else -> arrow.startX + t * dx
    }
    val closestY = when {
        t < 0 -> arrow.startY
        t > 1 -> arrow.endY
        else -> arrow.startY + t * dy
    }

    return distance(x, y, closestX, closestY) < tolerance
}

fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

// --- Paint chuẩn ---
val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    isAntiAlias = true
    isDither = true
    isSubpixelText = true
    color = Color.BLACK
    textSize = 48f
    textAlign = Paint.Align.CENTER
    typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
}

val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.YELLOW
    style = Paint.Style.FILL
}

val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.DKGRAY
    style = Paint.Style.STROKE
    strokeWidth = 2f
}

// --- Hằng số ---
const val padding = 10f
const val offsetDistance = 0f // Dịch hộp ra ngoài arrow nếu muốn

// --- Hàm vẽ hộp info box ---
fun drawArrowInfoBox(canvas: Canvas, arrow: ArrowDataView) {
    // 1. Chuẩn bị nội dung
    val infoLines = mutableListOf<String>()
    if (arrow.nameMaterial.isNotBlank() && arrow.valueMeasure != 0.0f){
        infoLines.add("${arrow.nameMaterial}: [${String.format(Locale.getDefault(), "%.2f", arrow.valueMeasure)}m]")
    }
    else{
        if(arrow.nameMaterial.isNotBlank()){
            infoLines.add(arrow.nameMaterial)
        }
        if(arrow.valueMeasure!=0.0f){
            infoLines.add("[${String.format(Locale.getDefault(), "%.2f", arrow.valueMeasure)}m]")
        }
    }

    if (infoLines.isEmpty()) return

    // 2. Tính kích thước box
    val maxTextWidth = infoLines.maxOf { textPaint.measureText(it) }
    val totalTextHeight = textPaint.fontSpacing * infoLines.size
    val boxWidth = maxTextWidth + 2 * padding
    val boxHeight = totalTextHeight + 2 * padding

    // 3. Tọa độ giữa arrow
    val midX = (arrow.startX + arrow.endX) / 2
    val midY = (arrow.startY + arrow.endY) / 2

    // Góc của arrow
    val angleRad = atan2(arrow.endY - arrow.startY, arrow.endX - arrow.startX)
    val angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()

    // Góc vuông góc để dịch hộp
    val perpendicularAngle = angleRad + (Math.PI / 2).toFloat()

    // Khoảng cách dịch chuyển hộp: nửa chiều cao + offset
    val translationDistance = (boxHeight / 2) + offsetDistance

    // --- Có thể chọn phía trên hoặc dưới arrow ---
    val side = 0.5f // 1 = trên arrow, -1 = dưới arrow (thay đổi nếu muốn)
    val centerX = midX + cos(perpendicularAngle) * translationDistance * side
    val centerY = midY + sin(perpendicularAngle) * translationDistance * side

    // 4. Vẽ box và text bằng KTX
    canvas.withTranslation(centerX, centerY) {
        withRotation(angleDeg) {
            // Box căn giữa tại 0,0
            val rect = RectF(-boxWidth / 2, -boxHeight / 2, boxWidth / 2, boxHeight / 2)

            drawRoundRect(rect, 5f, 5f, backgroundPaint)
            drawRoundRect(rect, 5f, 5f, borderPaint)

            // Vẽ chữ căn giữa
            var currentY = -boxHeight / 2 + padding + textPaint.textSize * 0.75f
            for (line in infoLines) {
                drawText(line, 0f, currentY, textPaint)
                currentY += textPaint.fontSpacing
            }
        }
    }
}
