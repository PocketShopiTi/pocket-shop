package com.iti.pocketshop.features.productlist.presentation

sealed interface ProductListAction {
    data object LoadMore : ProductListAction
    data object Refresh : ProductListAction
}
