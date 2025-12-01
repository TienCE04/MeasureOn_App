package com.example.measure_app.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.measure_app.room.entity.Material
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {

    @Query("SELECT * FROM Material WHERE idMaterial = :materialId")
    suspend fun getDataMaterial(materialId: Int): Material

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMaterial(material: Material): Long

    @Update
    suspend fun updateMaterial(material: Material): Int

    @Delete
    suspend fun deleteMaterial(material: Material): Int

    //xóa vật liệu theo id
    @Query("DELETE FROM Material WHERE idMaterial = :materialId")
    suspend fun deleteMaterialFromId(materialId: Int): Int

    //xóa tất cả vật liệu trong ảnh
    @Query("DELETE FROM Material WHERE idPhoto = :photoId")
    suspend fun deleteAllMaterial(photoId: Int): Int

    @Query("SELECT * FROM Material WHERE idPhoto = :photoId")
    fun getListMaterial(photoId: Int): Flow<List<Material>>

    @Query("UPDATE Material SET idPhoto = :newIdPhoto WHERE idMaterial = :materialId")
    suspend fun updateIdPhotoForMaterial(newIdPhoto: Int, materialId: Int): Int
}