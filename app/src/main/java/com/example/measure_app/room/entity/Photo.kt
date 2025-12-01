package com.example.measure_app.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = RoomInHome::class,
        parentColumns = ["idRoom"],
        childColumns = ["idRoom"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class Photo(
    @PrimaryKey(autoGenerate = true) val idPhoto:Int=0,
    val name:String="Ảnh Mới",
    val path:String,
    val thumbnailPath:String?=null,
    val idRoom:Int,
    val createAt:String
) {
}