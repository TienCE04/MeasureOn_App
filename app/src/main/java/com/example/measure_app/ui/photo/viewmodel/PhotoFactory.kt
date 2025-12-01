package com.example.measure_app.ui.photo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.measure_app.room.repository.PhotoRepository
import com.example.measure_app.ui.home.viewmodel.HomeViewModel

class PhotoFactory(private val photoRepository: PhotoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            return PhotoViewModel(photoRepository) as T
        }
        throw IllegalArgumentException("Unknow Viewmodel class")
    }
}