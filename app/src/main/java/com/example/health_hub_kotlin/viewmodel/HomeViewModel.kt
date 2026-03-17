package com.example.health_hub_kotlin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health_hub_kotlin.models.HomeResponse
import com.example.health_hub_kotlin.network.RetrofitClient
import com.example.health_hub_kotlin.repository.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showLongLoading: Boolean = false,
    val data: HomeResponse? = null,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val repository = HomeRepository(RetrofitClient.instance)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        fetchHomeData()
    }
    fun fetchHomeData() {
        viewModelScope.launch {

//            _uiState.value = HomeUiState(isLoading = true)
            _uiState.value = _uiState.value.copy(isLoading = true)
            val longLoadingJob = launch {
                delay(5000)
                _uiState.value = _uiState.value?.copy(showLongLoading = true)!!
            }

            try {
                val data = repository.getHomeData()
                longLoadingJob.cancel()

                _uiState.value = HomeUiState(
                    data = data
                )

            } catch (e: Exception) {
                longLoadingJob.cancel()

                _uiState.value = HomeUiState(
                    error = e.message
                )
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val data = repository.getHomeData()

                _uiState.value = HomeUiState(
                    data = data
                )

            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    error = e.message
                )
            }
        }
    }
}