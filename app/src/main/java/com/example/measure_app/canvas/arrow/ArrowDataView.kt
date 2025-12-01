package com.example.measure_app.canvas.arrow

import android.graphics.Paint

data class ArrowDataView(
    // Các thuộc tính có thể thay đổi trong khi tương tác (dùng 'var')
    var startX: Float,
    var startY: Float,
    var endX: Float,
    var endY: Float,

    // Các thuộc tính vẽ
    val color: Int,
    val strokeWidth: Float,

    // Trạng thái View
    var isSelected: Boolean = false,

    var valueMeasure: Float=0.0f,
    var nameMaterial:String="",
    var attribute1:String="",
    var attribute2:String="",
    var attribute3:String="",
    var description:String="",

    // Đối tượng Paint được tạo ra từ color và strokeWidth
    val paint: Paint = Paint().apply {
        this.color = color
        this.strokeWidth = strokeWidth
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
) {
}