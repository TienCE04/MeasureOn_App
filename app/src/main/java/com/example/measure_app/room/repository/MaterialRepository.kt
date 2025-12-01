package com.example.measure_app.room.repository

import android.util.Log
import com.example.measure_app.room.dao.MaterialDao
import com.example.measure_app.room.entity.Material
import kotlinx.coroutines.flow.Flow

class MaterialRepository(private val materialDao: MaterialDao) {

    fun getListMaterial(idPhoto: Int): Flow<List<Material>> {
        return materialDao.getListMaterial(idPhoto)
    }

    suspend fun createMaterial(material: Material): Boolean {
        try {
            Log.d("DEBUG", "create material success")
            return materialDao.createMaterial(material) > 0
        } catch (e: Exception) {
            Log.d("DEBUG", "create material fail")
            throw Exception("$e")
        }
    }

    suspend fun updateIdPhotoForMaterial(idPhoto: Int, idMaterial: Int): Boolean {
        return materialDao.updateIdPhotoForMaterial(idPhoto, idMaterial) > 0
    }

    suspend fun updateMaterial(material: Material): Boolean {
        return materialDao.updateMaterial(material) > 0
    }

    suspend fun deleteMaterial(material: Material): Boolean {
        return materialDao.deleteMaterial(material) > 0
    }

    suspend fun deleteMaterialFromId(idMaterial: Int): Boolean {
        return materialDao.deleteMaterialFromId(idMaterial) > 0
    }

    suspend fun deleteAllMaterial(idPhoto: Int): Boolean {
        return materialDao.deleteAllMaterial(idPhoto) > 0
    }
}