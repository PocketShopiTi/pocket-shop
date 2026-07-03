package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.features.home.domain.models.Product

data class ProductListPage(
    val products: List<Product>,
    val hasNextPage: Boolean,
    val endCursor: String?
)
