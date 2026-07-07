package com.iti.pocketshop.features.productlist.presentation

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct

sealed interface ProductListAction {
    data class UpdateRouteInfo(val routeInfo: ProductListRouteInfo) : ProductListAction
    data object LoadMore : ProductListAction
    data object Refresh : ProductListAction
    data class SearchProducts(val query: String) : ProductListAction
    data class ToggleFavorite(val product: FavoriteProduct) : ProductListAction
}
