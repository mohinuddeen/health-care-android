package com.example.health_hub_kotlin.repository

import com.example.health_hub_kotlin.models.CategoryData
import com.example.health_hub_kotlin.network.ApiService
import retrofit2.Response

class CategoryRepository(private val api: ApiService) {

    suspend fun getCategoryData(categoryId: Int): Response<CategoryData> {
        return api.getCategoryData(categoryId)
    }
}