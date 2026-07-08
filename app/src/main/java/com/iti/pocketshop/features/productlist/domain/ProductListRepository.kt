package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface ProductListRepository {
    suspend fun getProducts(
        first: Int = 20,
        after: String? = null,
        sortKey: String,
        reverse: Boolean,
        query: String? = null,
    ): PocketResult<ProductListPage, PocketDataError.Remote>

    suspend fun getCollectionProducts(
        handle: String,
        first: Int = 20,
        after: String? = null,
    ): PocketResult<ProductListPage, PocketDataError.Remote>
}
