package com.example.measure_app.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.measure_app.room.entity.Home
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

    @Query("SELECT * FROM home WHERE idHome = :homeId")
    suspend fun getDataHome(homeId: Int):Home

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createHome(home: Home): Long

    @Update
    suspend fun updateHome(home: Home): Int

    //xóa nhà theo object
    @Delete
    suspend fun deleteHome(home: Home): Int

    //xóa nhà theo id
    @Query("DELETE FROM Home WHERE idHome = :homeId")
    suspend fun deleteHomeFromId(homeId: Int): Int

    //dùng flow, livedata tự động chuyển sang IO nên ko cần suspend
    @Query("SELECT * FROM Home")
    fun getListHome(): Flow<List<Home>>
}