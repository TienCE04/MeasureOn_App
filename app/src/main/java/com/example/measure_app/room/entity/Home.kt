package com.example.measure_app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Home(
    @PrimaryKey(autoGenerate = true) val idHome: Int = 0,
    val name: String,
    val createAt: String,
    val countRoom: Int = 0,
) {
}