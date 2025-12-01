package com.example.measure_app.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.measure_app.room.dao.ArrowDao
import com.example.measure_app.room.dao.HomeDao
import com.example.measure_app.room.dao.MaterialDao
import com.example.measure_app.room.dao.PhotoDao
import com.example.measure_app.room.dao.RoomDao
import com.example.measure_app.room.entity.DrawingDataArrow
import com.example.measure_app.room.entity.Home
import com.example.measure_app.room.entity.Material
import com.example.measure_app.room.entity.Photo
import com.example.measure_app.room.entity.RoomInHome


//abstract vì khi build room nó tự tạo annotation
@Database(entities = [Home::class, Material::class, Photo::class, RoomInHome::class, DrawingDataArrow::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
    abstract fun materialDao(): MaterialDao
    abstract fun photoDao(): PhotoDao
    abstract fun roomDao(): RoomDao

    abstract fun arrowDao(): ArrowDao

    companion object {
        //thread safe
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}