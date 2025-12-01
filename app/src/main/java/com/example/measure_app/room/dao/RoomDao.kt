package com.example.measure_app.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.measure_app.room.entity.RoomInHome
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Query("SELECT * FROM RoomInHome WHERE idRoom = :roomId")
    suspend fun getDataRoom(roomId: Int): RoomInHome

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createRoom(room: RoomInHome): Long

    @Update
    suspend fun updateRoom(room: RoomInHome): Int

    @Delete
    suspend fun deleteRoom(room: RoomInHome): Int

    //xóa phòng theo id phòng
    @Query("DELETE FROM RoomInHome WHERE idRoom = :roomId")
    suspend fun deleteRoomFromId(roomId: Int): Int

    //xóa tất cả phòng trong 1 nhà
    @Query("DELETE FROM RoomInHome WHERE idHome = :homeId")
    suspend fun deleteAllRoom(homeId: Int): Int

    @Query("SELECT * FROM RoomInHome WHERE idHome = :homeId")
    fun getListRoom(homeId: Int): Flow<List<RoomInHome>>

}