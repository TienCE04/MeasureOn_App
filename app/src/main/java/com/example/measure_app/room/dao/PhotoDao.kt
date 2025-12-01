package com.example.measure_app.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.measure_app.room.entity.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM Photo WHERE idPhoto = :photoId")
    suspend fun getDataPhoto(photoId: Int): Photo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPhoto(photo: Photo): Long

    @Update
    suspend fun updatePhoto(photo: Photo): Int

    @Delete
    suspend fun deletePhoto(photo: Photo): Int

    //xóa ảnh theo id ảnh
    @Query("DELETE FROM Photo WHERE idPhoto = :photoId")
    suspend fun deletePhotoFromId(photoId: Int): Int

    //xóa tất cả trong 1 phòng
    @Query("DELETE FROM Photo WHERE idRoom = :roomId")
    suspend fun deleteAllPhoto(roomId: Int): Int

    @Query("SELECT * FROM Photo WHERE idRoom = :roomId")
    fun getListPhoto(roomId: Int): Flow<List<Photo>>

    @Query("UPDATE Photo SET thumbnailPath = :newThumbPath WHERE idPhoto = :photoId")
    suspend fun updateThumbPath(photoId:Int,newThumbPath:String): Int
}