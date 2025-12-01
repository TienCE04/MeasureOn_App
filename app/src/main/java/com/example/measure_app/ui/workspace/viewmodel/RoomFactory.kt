package com.example.measure_app.ui.workspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.measure_app.room.repository.RoomInHomeRepository

class RoomFactory(private val roomInHomeRepository: RoomInHomeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(roomInHomeRepository) as T
        }
        throw IllegalArgumentException("Unknow Viewmodel class")
    }
}