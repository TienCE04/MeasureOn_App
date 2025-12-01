package com.example.measure_app.util.convert

import androidx.room.TypeConverter
import com.example.measure_app.canvas.arrow.DataArrow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ArrowListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromArrowList(list: List<DataArrow>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toArrowList(data: String?): List<DataArrow> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<DataArrow>>() {}.type
        return gson.fromJson(data, type)
    }
}

