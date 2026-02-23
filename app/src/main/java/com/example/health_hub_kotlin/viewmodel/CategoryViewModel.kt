package com.example.health_hub_kotlin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health_hub_kotlin.models.CategoryData
import com.example.health_hub_kotlin.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException

enum class CategoryState {
    LOADING,
    LOADED,
    ERROR
}

class CategoryViewModel : ViewModel() {

    private val _categoryData = MutableLiveData<CategoryData>()
    val categoryData: LiveData<CategoryData> = _categoryData

    private val _state = MutableLiveData<CategoryState>()
    val state: LiveData<CategoryState> = _state

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val tag = "CategoryViewModel"

    fun fetchCategoryData(categoryId: Int) {
        viewModelScope.launch {
            _state.value = CategoryState.LOADING
            Log.d(tag, "Fetching category data for ID: $categoryId")

            try {
                val response = RetrofitClient.instance.getCategoryData(categoryId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(tag, "Data received successfully. Subcategories: ${it.subcategories.size}")
                        // Log first item to verify
                        if (it.subcategories.isNotEmpty()) {
                            val first = it.subcategories[0]
                            Log.d(tag, "First item: ${first.title}, price: ${first.price}")
                        }
                        _categoryData.value = it
                        _state.value = CategoryState.LOADED
                        _error.value = null
                    } ?: run {
                        Log.e(tag, "Response body is null")
                        _state.value = CategoryState.ERROR
                        _error.value = "Empty response"
                    }
                } else {
                    Log.e(tag, "Error response: ${response.code()}")
                    _state.value = CategoryState.ERROR
                    _error.value = "Failed to load data: ${response.code()}"
                }
            } catch (e: IOException) {
                Log.e(tag, "Network error: ${e.message}")
                _state.value = CategoryState.ERROR
                _error.value = "Network error: ${e.message}"
            } catch (e: Exception) {
                Log.e(tag, "Unexpected error: ${e.message}")
                _state.value = CategoryState.ERROR
                _error.value = "Unexpected error: ${e.message}"
            }
        }
    }

    fun clearData() {
        _categoryData.value = null
    }
}