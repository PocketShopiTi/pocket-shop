package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult

interface ProductListRemoteSource {
    suspend fun getProducts(
        first: Int,
        after: String?,
        sortKey: String,
        reverse: Boolean,
        query: String? = null,
    ): PocketResult<ProductListPage, PocketDataError.Remote>
}
