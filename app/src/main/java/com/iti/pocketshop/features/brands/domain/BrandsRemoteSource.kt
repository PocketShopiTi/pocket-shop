package com.iti.pocketshop.features.brands.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.brands.domain.models.BrandItem

interface BrandsRemoteSource {
    suspend fun getBrands(
        first: Int
    ): PocketResult<List<BrandItem>, PocketDataError.Remote>
}
