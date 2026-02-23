package com.example.health_hub_kotlin.models

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val banners: List<Banner>,
    val categories: List<Category>,
    val features: List<Feature>,
    @SerializedName("trending_services")
    val trendingServices: List<TrendingService>,
    val packages: List<Package>
)

data class Banner(
    val id: Int,
    val image: String
)

data class Category(
    val id: Int,
    val title: String,
    val image: String
)

data class Feature(
    val id: Int,
    val icon: String,
    val title: String,
    val subtitle: String
)

data class TrendingService(
    val id: Int,
    val title: String,
    val price: Int,
    @SerializedName("discount_price")
    val discountPrice: Int,
    val rating: Double,
    val reviews: Int
)

data class Package(
    val id: Int,
    val title: String,
    @SerializedName("service_count")
    val serviceCount: Int,
    val price: Int,
    val image: String
)