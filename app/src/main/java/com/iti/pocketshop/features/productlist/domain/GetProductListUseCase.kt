package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import javax.inject.Inject

class GetProductListUseCase @Inject constructor(
    private val repository: ProductListRepository,
) {
    suspend operator fun invoke(
        first: Int = 20,
        after: String? = null,
        sortKey: String,
        reverse: Boolean,
        query: String? = null,
    ): PocketResult<ProductListPage, PocketDataError.Remote> {
        return repository.getProducts(
            first = first,
            after = after,
            sortKey = sortKey,
            reverse = reverse,
            query = query,
        )
    }
}
