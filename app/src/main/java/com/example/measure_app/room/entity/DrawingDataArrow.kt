package com.example.measure_app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.measure_app.canvas.arrow.DataArrow
import com.example.measure_app.util.convert.ArrowListConverter

@Entity(tableName = "drawing_data")
@TypeConverters(ArrowListConverter::class)
data class DrawingDataArrow(
    @PrimaryKey val idPhoto:Int,

    val arrowList: List<DataArrow> =listOf()
)
