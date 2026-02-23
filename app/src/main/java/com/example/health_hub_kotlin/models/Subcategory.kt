package com.example.health_hub_kotlin.models

import com.google.gson.annotations.SerializedName

data class CategoryData(
    val subcategories: List<Subcategory>
)

data class Subcategory(
    val id: Int,
    val title: String,
    val image: String,
    val price: String,
    val duration: String,
    val tag: String,
    val rating: Double,
    val description: String
)