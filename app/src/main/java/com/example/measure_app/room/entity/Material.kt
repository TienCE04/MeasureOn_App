package com.example.measure_app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Material(
    @PrimaryKey(autoGenerate = true) val idMaterial: Int = 0,
    val name: String,
    val attribute1: String = "",
    val attribute2: String = "",
    val attribute3: String = "",
    val valueMeasure: Float = 0.0f,
    val price: String? = null,
    val idPhoto: Int? = null,
    val description: String = "",
) {
}