package com.example.measure_app.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.measure_app.room.entity.DrawingDataArrow
import kotlinx.coroutines.flow.Flow

@Dao
interface ArrowDao {
    @Query("SELECT * FROM drawing_data WHERE idPhoto = :photoId")
    fun getListArrow(photoId:Int): Flow<DrawingDataArrow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createListArrow(arrowDataView: DrawingDataArrow): Long
}