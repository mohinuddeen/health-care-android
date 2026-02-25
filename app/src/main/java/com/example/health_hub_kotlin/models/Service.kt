package com.example.health_hub_kotlin.models

import com.google.gson.annotations.SerializedName

data class Service(
    val id: Int,
    val title: String,
    val subtitle: String,
    val image: String,
    val price: Int,
    val duration: String,
    val rating: Double,
    val tag: String,
    val description: String,
    @SerializedName("tests_include")
    val testsInclude: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    @SerializedName("fasting_required")
    val fastingRequired: String,
    @SerializedName("report_time")
    val reportTime: String,
    @SerializedName("suitable_for")
    val suitableFor: List<String> = emptyList(),
    val preparation: String,
    @SerializedName("key_ingredients")
    val keyIngredients: List<String> = emptyList()
)