package com.example.health_hub_kotlin.network

import com.example.health_hub_kotlin.models.CategoryData
import com.example.health_hub_kotlin.models.HomeResponse
import com.example.health_hub_kotlin.models.Service
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("home/homePage.json")
    suspend fun getHomeData(): Response<HomeResponse>

    @GET("categories/{categoryId}.json")
    suspend fun getCategoryData(@Path("categoryId") categoryId: Int): Response<CategoryData>

    @GET("services/{serviceId}.json")
    suspend fun getServiceData(@Path("serviceId") serviceId: Int): Response<Service>
}