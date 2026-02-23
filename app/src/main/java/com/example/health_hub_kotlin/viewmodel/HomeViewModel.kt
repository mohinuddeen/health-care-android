package com.example.health_hub_kotlin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health_hub_kotlin.models.HomeResponse
import com.example.health_hub_kotlin.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException

enum class HomeState {
    LOADING,
    LOADED,
    ERROR,
    REFRESHING
}

class HomeViewModel : ViewModel() {

    private val _homeData = MutableLiveData<HomeResponse>()
    val homeData: LiveData<HomeResponse> = _homeData

    private val _state = MutableLiveData<HomeState>()
    val state: LiveData<HomeState> = _state

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _showLongLoading = MutableLiveData(false)
    val showLongLoading: LiveData<Boolean> = _showLongLoading

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _state.value = HomeState.LOADING
            _showLongLoading.value = false

            // Simulate long loading check
            viewModelScope.launch {
                kotlinx.coroutines.delay(5000)
                if (_state.value == HomeState.LOADING) {
                    _showLongLoading.value = true
                }
            }

            try {
                val response = RetrofitClient.instance.getHomeData()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _homeData.value = it
                        _state.value = HomeState.LOADED
                        _error.value = null
                    }
                } else {
                    _state.value = HomeState.ERROR
                    _error.value = "Failed to load data: ${response.code()}"
                }
            } catch (e: IOException) {
                _state.value = HomeState.ERROR
                _error.value = "Network error: ${e.message}"
            } catch (e: Exception) {
                _state.value = HomeState.ERROR
                _error.value = "Unexpected error: ${e.message}"
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _state.value = HomeState.REFRESHING

            try {
                val response = RetrofitClient.instance.getHomeData()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _homeData.value = it
                        _state.value = HomeState.LOADED
                        _error.value = null
                    }
                } else {
                    _state.value = HomeState.ERROR
                    _error.value = "Failed to refresh data: ${response.code()}"
                }
            } catch (e: IOException) {
                _state.value = HomeState.ERROR
                _error.value = "Network error: ${e.message}"
            }
        }
    }
}