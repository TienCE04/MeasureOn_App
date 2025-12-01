package com.example.measure_app.ui.material.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.measure_app.room.entity.DrawingDataArrow
import com.example.measure_app.room.repository.ArrowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MaterialViewModel(private val arrowRepository: ArrowRepository) : ViewModel() {

    private val _arrowList = MutableStateFlow<DrawingDataArrow?>(null)
    val arrowList: StateFlow<DrawingDataArrow?> = _arrowList

    fun loadArrows(photoId: Int) {
        viewModelScope.launch {
            arrowRepository.getListArrow(photoId)
                .catch { e -> Log.e("ArrowVM", "Load fail: ${e.message}") }
                .collect { data ->
                    _arrowList.value = data
                }
        }
    }

    fun saveArrows(drawingDataArrow: DrawingDataArrow) {
        viewModelScope.launch {
            try {
                val success = arrowRepository.createListArrow(drawingDataArrow)
                if (success) {
                    Log.d("ArrowVM", "Save arrows success")
                }
            } catch (e: Exception) {
                Log.e("ArrowVM", "Save arrows fail: ${e.message}")
            }
        }
    }
}