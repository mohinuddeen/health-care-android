package com.example.health_hub_kotlin.repository

import com.example.health_hub_kotlin.models.Service
import com.example.health_hub_kotlin.network.ApiService
import retrofit2.Response

class ServiceRepository(private val api: ApiService) {

    suspend fun getServiceData(serviceId: Int): Response<Service> {
        return api.getServiceData(serviceId)

    }
}