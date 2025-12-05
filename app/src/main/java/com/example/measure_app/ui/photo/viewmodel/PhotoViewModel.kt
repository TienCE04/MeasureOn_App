package com.example.measure_app.ui.photo.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.measure_app.room.entity.Photo
import com.example.measure_app.room.entity.RoomInHome
import com.example.measure_app.room.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class PhotoViewModel(private val photoRepository: PhotoRepository) : ViewModel() {

    private val _idRoom = MutableStateFlow(-1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val photos: StateFlow<List<Photo>> = _idRoom
        .filter { it != -1 }
        .flatMapLatest { idRoom ->
            photoRepository.getListPhoto(idRoom)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setIdRoom(id: Int) {
        _idRoom.value = id
    }

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    private val _photo = MutableSharedFlow<Photo>()
    val photo = _photo.asSharedFlow()

    //2025 sử dụng event để emit thay vì callback
    fun createPhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = photoRepository.createPhoto(photo)
            if (response) {
                _event.emit("Thêm thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun updatePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = photoRepository.updatePhoto(photo)
            if (response) {
                _event.emit("Cập nhật thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun updateNewThumbPath(idPhoto: Int, thumbPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = photoRepository.updateThumbPath(idPhoto, thumbPath)
            if (response) {
                _event.emit("Cập nhật thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }


    fun deletePhoto(idPhoto: Int,deleteImage:()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = photoRepository.deletePhoto(idPhoto)
            if (response) {
                _event.emit("Xóa thành công!")
                deleteImage()
            } else {
                _event.emit("fail")
            }
        }
    }

    fun getDataPhoto(idPhoto: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = photoRepository.getDataPhoto(idPhoto)
            _photo.emit(response)
        }
    }

}