package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface ProductListRepository {
    suspend fun getProducts(
        first: Int = 20,
        after: String? = null,
        sortKey: String,
        reverse: Boolean,
    ): PocketResult<ProductListPage, PocketDataError.Remote>
}
