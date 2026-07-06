package com.iti.pocketshop.features.brands.domain

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.brands.domain.models.BrandItem

interface BrandsRepository {
    suspend fun getBrands(
        first: Int = 20
    ): PocketResult<List<BrandItem>, PocketDataError.Remote>
}
