package com.example.measure_app.canvas.arrow

data class DataArrow(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Int,
    val strokeWidth: Float,
    val valueMeasure: Float=0.0f,
    val nameMaterial:String="",
    val attribute1:String="",
    val attribute2:String="",
    val attribute3:String="",
    val description:String="",
)