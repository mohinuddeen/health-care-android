package com.example.health_hub_kotlin.repository

import com.example.health_hub_kotlin.models.HomeResponse
import com.example.health_hub_kotlin.network.ApiService
import retrofit2.Response

class HomeRepository(private val api: ApiService) {

    suspend fun getHomeData(): HomeResponse {
        val response = api.getHomeData()
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("API Error: ${response.code()}")
        }
    }
}