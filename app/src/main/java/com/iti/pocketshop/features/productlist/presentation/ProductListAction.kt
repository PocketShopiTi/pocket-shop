package com.iti.pocketshop.features.productlist.presentation

sealed interface ProductListAction {
    data class UpdateRouteInfo(val routeInfo: ProductListRouteInfo) : ProductListAction
    data object LoadMore : ProductListAction
    data object Refresh : ProductListAction
    data class SearchProducts(val query: String) : ProductListAction
}
