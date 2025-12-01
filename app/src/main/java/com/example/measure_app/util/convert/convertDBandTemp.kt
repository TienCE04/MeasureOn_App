package com.example.measure_app.util.convert

import com.example.measure_app.canvas.arrow.ArrowDataView
import com.example.measure_app.canvas.arrow.DataArrow


fun ArrowDataView.toDataArrow(): DataArrow {
    return DataArrow(
        startX = this.startX,
        startY = this.startY,
        endX = this.endX,
        endY = this.endY,
        color = this.color,
        strokeWidth = this.strokeWidth,
        valueMeasure = this.valueMeasure,
        nameMaterial = this.nameMaterial,
        attribute1 = this.attribute1,
        attribute2 = this.attribute2,
        attribute3 = this.attribute3,
        description = this.description
    )
}
fun DataArrow.toArrowDataView(): ArrowDataView {
    return ArrowDataView(
        startX = this.startX,
        startY = this.startY,
        endX = this.endX,
        endY = this.endY,
        color = this.color,
        strokeWidth = this.strokeWidth,
        valueMeasure = this.valueMeasure,
        nameMaterial = this.nameMaterial,
        attribute1 = this.attribute1,
        attribute2 = this.attribute2,
        attribute3 = this.attribute3,
        description = this.description
    )
}