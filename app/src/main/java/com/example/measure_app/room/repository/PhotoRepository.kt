package com.example.measure_app.room.repository

import android.util.Log
import com.example.measure_app.room.dao.PhotoDao
import com.example.measure_app.room.entity.Photo
import kotlinx.coroutines.flow.Flow

class PhotoRepository(private val photoDao: PhotoDao) {

    fun getListPhoto(roomId: Int): Flow<List<Photo>> {
        return photoDao.getListPhoto(roomId)
    }

    suspend fun createPhoto(photo: Photo): Boolean {
        try {
            Log.d("DEBUG", "create photo success")
            return photoDao.createPhoto(photo) > 0
        } catch (e: Exception) {
            Log.d("DEBUG", "create photo fail")
            throw Exception("$e")
        }
    }

    suspend fun updatePhoto(photo: Photo): Boolean {
        return photoDao.updatePhoto(photo) > 0
    }

    suspend fun updateThumbPath(idPhoto: Int,thumbPath:String): Boolean{
        return photoDao.updateThumbPath(idPhoto,thumbPath)>0
    }

    suspend fun deletePhoto(idPhoto: Int): Boolean {
        return photoDao.deletePhotoFromId(idPhoto) > 0
    }

    suspend fun deleteAllPhoto(idRoom: Int): Boolean {
        return photoDao.deleteAllPhoto(idRoom) > 0
    }

    suspend fun getDataPhoto(idPhoto: Int): Photo {
        return photoDao.getDataPhoto(idPhoto)
    }
}