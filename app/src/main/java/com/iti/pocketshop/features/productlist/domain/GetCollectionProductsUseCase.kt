package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import javax.inject.Inject

class GetCollectionProductsUseCase @Inject constructor(
    private val repository: ProductListRepository,
) {
    suspend operator fun invoke(
        handle: String,
        first: Int = 20,
        after: String? = null,
    ): PocketResult<ProductListPage, PocketDataError.Remote> {
        return repository.getCollectionProducts(
            handle = handle,
            first = first,
            after = after,
        )
    }
}