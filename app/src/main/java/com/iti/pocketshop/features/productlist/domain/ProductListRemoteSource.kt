package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface ProductListRemoteSource {
    suspend fun getProducts(
        first: Int,
        after: String?,
        sortKey: String,
        reverse: Boolean,
        query: String? = null,
    ): PocketResult<ProductListPage, PocketDataError.Remote>

    suspend fun getCollectionProducts(
        handle: String,
        first: Int,
        after: String?,
    ): PocketResult<ProductListPage, PocketDataError.Remote>
}
