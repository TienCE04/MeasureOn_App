package com.example.measure_app.ui.material.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.measure_app.room.repository.ArrowRepository

class MaterialFactory(private val arrowRepository: ArrowRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if(modelClass.isAssignableFrom(MaterialViewModel::class.java)){
            return MaterialViewModel(arrowRepository) as T
        }
        throw IllegalArgumentException("Unknow Viewmodel class")
    }
}