package com.example.health_hub_kotlin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health_hub_kotlin.models.Service
import com.example.health_hub_kotlin.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException

enum class ServiceState {
    LOADING,
    LOADED,
    ERROR
}

class ServiceViewModel : ViewModel() {

    private val _serviceData = MutableLiveData<Service>()
    val serviceData: LiveData<Service> = _serviceData

    private val _state = MutableLiveData<ServiceState>()
    val state: LiveData<ServiceState> = _state

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val tag = "ServiceViewModel"

    fun fetchServiceData(serviceId: Int) {
        viewModelScope.launch {
            _state.value = ServiceState.LOADING
            Log.d(tag, "Fetching service data for ID: $serviceId")

            try {
                val response = RetrofitClient.instance.getServiceData(serviceId)
                Log.d(tag, "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(tag, "Service data received: ${it.title}")
                        Log.d(tag, "Service ID: ${it.id}, Price: ${it.price}")

                        _serviceData.value = it
                        _state.value = ServiceState.LOADED
                        _error.value = null
                    } ?: run {
                        Log.e(tag, "Response body is null")
                        _state.value = ServiceState.ERROR
                        _error.value = "Empty response"
                    }
                } else {
                    Log.e(tag, "Error response: ${response.code()} - ${response.message()}")
                    _state.value = ServiceState.ERROR
                    _error.value = "Failed to load data: ${response.code()}"
                }
            } catch (e: IOException) {
                Log.e(tag, "Network error: ${e.message}")
                _state.value = ServiceState.ERROR
                _error.value = "Network error: ${e.message}"
            } catch (e: Exception) {
                Log.e(tag, "Unexpected error: ${e.message}")
                _state.value = ServiceState.ERROR
                _error.value = "Unexpected error: ${e.message}"
            }
        }
    }

    fun clearData() {
        _serviceData.value = null
    }
}