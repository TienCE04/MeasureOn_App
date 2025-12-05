package com.example.measure_app.room.repository

import android.util.Log
import com.example.measure_app.room.dao.ArrowDao
import com.example.measure_app.room.entity.DrawingDataArrow
import kotlinx.coroutines.flow.Flow

class ArrowRepository(private val arrowDao: ArrowDao) {

    suspend fun createListArrow(arr: DrawingDataArrow): Boolean{
        try {
            val id=arrowDao.createListArrow(arr)
            Log.d("DEBUG", "createListArrow success")
            return id>0
        }catch (e: Exception){
            Log.d("DEBUG", "createListArrow fail")
            throw Exception("createListArrow fail")
        }
    }

    fun getListArrow(idPhoto:Int): Flow<DrawingDataArrow?>{
        return arrowDao.getListArrow(idPhoto)
    }
}