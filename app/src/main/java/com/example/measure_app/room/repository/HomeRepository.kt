package com.example.measure_app.room.repository

import android.util.Log
import com.example.measure_app.room.dao.HomeDao
import com.example.measure_app.room.entity.Home
import kotlinx.coroutines.flow.Flow

class HomeRepository(private val homeDao: HomeDao) {
    val allHome: Flow<List<Home>> = homeDao.getListHome()

    suspend fun createHome(home: Home): Boolean {
        try {
            val id = homeDao.createHome(home)
            Log.d("DEBUG", "create home success")
            return id > 0
        } catch (e: Exception) {
            Log.d("DEBUG", "create home fail")
            throw Exception("create home fail")
        }
    }

    suspend fun deleteHome(idHome: Int): Boolean {
        return homeDao.deleteHomeFromId(idHome) > 0
    }

    suspend fun updateHome(home: Home): Boolean {
        return homeDao.updateHome(home) > 0
    }

    suspend fun getDataHome(idHome:Int): Home{
        return homeDao.getDataHome(idHome)
    }
}