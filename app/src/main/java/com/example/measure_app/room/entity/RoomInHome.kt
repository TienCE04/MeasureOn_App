package com.example.measure_app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RoomInHome(
    @PrimaryKey(autoGenerate = true) val idRoom: Int = 0,
    val name: String = "Phòng mới",
    val idHome: Int? = null,
    val countPhoto: Int = 0,
    val createAt: String,
) {
}
//biến countPhoto dùng về sau các thao tác thêm sửa xóa material sẽ phải cập nhật count trong room nhiều \
//nên chưa cần thiết
//hoặc done các thao tác back lại room thì mới cập nhật