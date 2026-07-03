package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface ProductListRemoteSource {
    suspend fun getProducts(
        first: Int,
        after: String?,
        sortKey: String,
        reverse: Boolean,
    ): PocketResult<ProductListPage, PocketDataError.Remote>
}
