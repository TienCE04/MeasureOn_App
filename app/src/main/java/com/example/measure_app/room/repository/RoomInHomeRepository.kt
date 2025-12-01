package com.example.measure_app.room.repository

import android.util.Log
import com.example.measure_app.room.dao.RoomDao
import com.example.measure_app.room.entity.RoomInHome
import kotlinx.coroutines.flow.Flow

class RoomInHomeRepository(private val roomDao: RoomDao) {

    //ko cần suspend nhưng vẫn cần để IO trong viewmodel
    suspend fun getListRoom(idHome: Int): Flow<List<RoomInHome>> {
        return roomDao.getListRoom(idHome)
    }

    suspend fun createRoomInHome(room: RoomInHome): Boolean {
        try {
            Log.d("DEBUG", "create room in home success")
            return roomDao.createRoom(room) > 0
        } catch (e: Exception) {
            Log.d("DEBUG", "error room")
            throw Exception("Lỗi bảng dữ liệu!")
        }
    }

    suspend fun updateRoomInHome(room: RoomInHome): Boolean {
        return roomDao.updateRoom(room) > 0
    }

    suspend fun deleteRoomInHome(idRoom: Int): Boolean {
        return roomDao.deleteRoomFromId(idRoom) > 0
    }

    suspend fun deleteAllRoom(homeId: Int): Boolean {
        return roomDao.deleteAllRoom(homeId) > 0
    }

    suspend fun getDataRoom(idRoom: Int): RoomInHome {
        return roomDao.getDataRoom(idRoom)
    }

}