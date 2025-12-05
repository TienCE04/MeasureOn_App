package com.example.measure_app.ui.material.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.measure_app.room.entity.DrawingDataArrow
import com.example.measure_app.room.repository.ArrowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MaterialViewModel(private val arrowRepository: ArrowRepository) : ViewModel() {

    private val _arrowList = MutableStateFlow<DrawingDataArrow?>(null)
    val arrowList: StateFlow<DrawingDataArrow?> = _arrowList

    private val _arrowAllList = MutableStateFlow<List<DrawingDataArrow?>>(emptyList())
    val arrowAllList: StateFlow<List<DrawingDataArrow?>> = _arrowAllList


    fun loadArrows(photoId: Int) {
        viewModelScope.launch {
            arrowRepository.getListArrow(photoId)
                .catch { e -> Log.e("ArrowVM", "Load fail: ${e.message}") }
                .collect { data ->
                    if(data!=null){
                        Log.d("DEBUGtt",data.toString())
                        _arrowList.value = data
                    }
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
    fun loadArrowsFromPhotos(photoIds: List<Int>) {
        viewModelScope.launch {
            try {
                val allArrows = mutableListOf<DrawingDataArrow>()
                for (id in photoIds) {
                    val arrows = arrowRepository.getListArrow(id)
                        .catch { e -> Log.e("ArrowVM", "Load fail: ${e.message}") }
                        .firstOrNull() // lấy giá trị đầu tiên từ Flow
                    if (arrows != null) {
                        allArrows.add(arrows)
                    }
                }
                _arrowAllList.value = allArrows
            } catch (e: Exception) {
                Log.e("ArrowVM", "Load arrows from photos fail: ${e.message}")
            }
        }
    }
}