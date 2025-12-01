package com.example.measure_app.ui.workspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.measure_app.room.entity.RoomInHome
import com.example.measure_app.room.repository.RoomInHomeRepository
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

class RoomViewModel(private val roomInHomeRepository: RoomInHomeRepository): ViewModel() {
    // database có cơ chế báo cho flow :))
    private val _idHome = MutableStateFlow(-1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val rooms: StateFlow<List<RoomInHome>> = _idHome
        .filter { it != -1 }
        .flatMapLatest { idHome ->
            roomInHomeRepository.getListRoom(idHome)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setIdHome(id:Int){
        _idHome.value=id
    }

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    private val _room = MutableSharedFlow<RoomInHome>()
    val room = _room.asSharedFlow()

    //2025 sử dụng event để emit thay vì callback
    fun createRoom(room: RoomInHome) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = roomInHomeRepository.createRoomInHome(room)
            if (response) {
                _event.emit("Thêm thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun updateRoom(room: RoomInHome) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = roomInHomeRepository.updateRoomInHome(room)
            if (response) {
                _event.emit("Cập nhật thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun deleteRoom(idRoom: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = roomInHomeRepository.deleteRoomInHome(idRoom)
            if (response) {
                _event.emit("Xóa thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun getDataRoom(idRoom: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = roomInHomeRepository.getDataRoom(idRoom)
            _room.emit(response)
        }
    }

}