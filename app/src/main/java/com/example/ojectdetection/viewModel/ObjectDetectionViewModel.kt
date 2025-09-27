package com.example.ojectdetection.viewModel

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ojectdetection.data.repo.ObjectRepository
import com.example.ojectdetection.data_class.DetectedItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObjectDetectionViewModel @Inject constructor(
    private val repo : ObjectRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<DetectedItem>>(emptyList())
    val items: StateFlow<List<DetectedItem>> = _items

    fun onImageProxy(imageProxy: ImageProxy) {
        viewModelScope.launch {
            val results = repo.detect(imageProxy)
            _items.value = results
        }
    }
}