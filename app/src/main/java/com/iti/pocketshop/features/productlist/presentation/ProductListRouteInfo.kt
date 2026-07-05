package com.iti.pocketshop.features.productlist.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iti.pocketshop.R
import com.iti.pocketshop.features.home.domain.models.CategoryName

sealed interface ProductListRouteInfo {
    data object None : ProductListRouteInfo
    data object Featured : ProductListRouteInfo
    data object Trending : ProductListRouteInfo
    data object NewArrivals : ProductListRouteInfo
    data class Brands(val brandTitle: String) : ProductListRouteInfo
    data class Category(val categoryName: CategoryName) : ProductListRouteInfo
}

@Composable
fun ProductListRouteInfo.screenTitle(): String {
    return when (this) {
        ProductListRouteInfo.None -> stringResource(R.string.empty_string)
        ProductListRouteInfo.Featured -> stringResource(R.string.featured)
        ProductListRouteInfo.Trending -> stringResource(R.string.trending)
        ProductListRouteInfo.NewArrivals -> stringResource(R.string.new_arrivals)
        is ProductListRouteInfo.Brands -> this.brandTitle
        is ProductListRouteInfo.Category -> stringResource(this.categoryName.titleId)
    }
}

fun ProductListRouteInfo.sortKey(): String {
    return when (this) {
        ProductListRouteInfo.None -> ""
        ProductListRouteInfo.Featured -> "ID"
        ProductListRouteInfo.Trending -> "BEST_SELLING"
        ProductListRouteInfo.NewArrivals -> "CREATED_AT"
        is ProductListRouteInfo.Brands -> "ID"
        is ProductListRouteInfo.Category -> "ID"
    }
}


fun ProductListRouteInfo.query(): String? {
    return when (this) {
        is ProductListRouteInfo.Brands -> "vendor:$brandTitle"
        is ProductListRouteInfo.Category -> "tag:$categoryName"
        else -> null
    }
}

fun ProductListRouteInfo.isReverse(): Boolean {
    return this == ProductListRouteInfo.NewArrivals
}