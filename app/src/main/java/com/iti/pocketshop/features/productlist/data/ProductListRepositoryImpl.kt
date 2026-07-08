package com.iti.pocketshop.features.productlist.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.productlist.domain.ProductListPage
import com.iti.pocketshop.features.productlist.domain.ProductListRemoteSource
import com.iti.pocketshop.features.productlist.domain.ProductListRepository
import javax.inject.Inject

class ProductListRepositoryImpl @Inject constructor(
    private val remoteSource: ProductListRemoteSource,
) : ProductListRepository {

    override suspend fun getProducts(
        first: Int,
        after: String?,
        sortKey: String,
        reverse: Boolean,
        query: String?,
    ): PocketResult<ProductListPage, PocketDataError.Remote> {
        return remoteSource.getProducts(
            first = first,
            after = after,
            sortKey = sortKey,
            reverse = reverse,
            query = query,
        )
    }

    override suspend fun getCollectionProducts(
        handle: String,
        first: Int,
        after: String?,
    ): PocketResult<ProductListPage, PocketDataError.Remote> {
        return remoteSource.getCollectionProducts(
            handle = handle,
            first = first,
            after = after,
        )
    }
}
