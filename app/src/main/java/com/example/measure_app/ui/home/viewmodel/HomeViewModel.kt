package com.example.measure_app.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.measure_app.room.entity.Home
import com.example.measure_app.room.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    // database có cơ chế báo cho flow :))
    val homes: StateFlow<List<Home>> = homeRepository.allHome.stateIn(
        viewModelScope,
        SharingStarted.Lazily, emptyList()
    )


    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    private val _home = MutableSharedFlow<Home>()
    val home = _home.asSharedFlow()

    //2025 sử dụng event để emit thay vì callback
    fun createHome(home: Home) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = homeRepository.createHome(home)
            if (response) {
                _event.emit("Thêm thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun updateHome(home: Home) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = homeRepository.updateHome(home)
            if (response) {
                _event.emit("Cập nhật thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun deleteHome(idHome: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = homeRepository.deleteHome(idHome)
            if (response) {
                _event.emit("Xóa thành công!")
            } else {
                _event.emit("fail")
            }
        }
    }

    fun getDataHome(idHome: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = homeRepository.getDataHome(idHome)
            _home.emit(response)
        }
    }

}