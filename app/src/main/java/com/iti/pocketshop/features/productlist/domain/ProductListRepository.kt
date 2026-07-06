package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult

interface ProductListRepository {
    suspend fun getProducts(
        first: Int = 20,
        after: String? = null,
        sortKey: String,
        reverse: Boolean,
        query: String? = null,
    ): PocketResult<ProductListPage, PocketDataError.Remote>
}
