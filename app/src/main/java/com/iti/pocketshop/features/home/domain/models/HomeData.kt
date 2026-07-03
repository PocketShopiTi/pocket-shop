package com.iti.pocketshop.features.home.domain.models


data class HomeData(
    val brands: List<Brand>,
    val featuredProducts: List<Product>,
    val bestSellers: List<Product>,
    val newArrivals: List<Product>,
)
